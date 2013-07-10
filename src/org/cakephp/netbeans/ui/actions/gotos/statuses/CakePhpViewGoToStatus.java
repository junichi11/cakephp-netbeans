/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.ui.actions.gotos.statuses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.editor.visitors.CakePhpControllerVisitor;
import org.cakephp.netbeans.editor.visitors.CakePhpViewVisitor;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.ui.GoToControllerItem;
import org.cakephp.netbeans.ui.GoToHelperItem;
import org.cakephp.netbeans.ui.GoToItem;
import org.cakephp.netbeans.ui.GoToViewItem;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * Behavior for current view file of Go To Action.
 *
 * - controller<br/>
 * - view (element, extend)<br/>
 * - helper
 *
 * @author junichi11
 */
public class CakePhpViewGoToStatus extends CakePhpGoToStatus {

    private static final String ELEMENTS = "Elements"; // NOI18N
    private final Set<FileObject> views = new HashSet<FileObject>();
    private final List<GoToItem> viewItems = new ArrayList<GoToItem>();
    private final Set<GoToItem> elementItems = new HashSet<GoToItem>();
    private final Set<GoToItem> extendItems = new HashSet<GoToItem>();
    private FileObject caretPositionElement;
    private FileObject caretPositionExtend;
    private final List<GoToItem> controllerItems = new ArrayList<GoToItem>();
    private final List<GoToItem> helperItems = new ArrayList<GoToItem>();
    private static final Logger LOGGER = Logger.getLogger(CakePhpViewGoToStatus.class.getName());
    private String caretPositionElementPath = ""; // NOI18N
    private String caretPositionExtendPath = ""; // NOI18N
    private static CakePhpViewGoToStatus INSTANCE = new CakePhpViewGoToStatus();

    private CakePhpViewGoToStatus() {
    }

    public static CakePhpViewGoToStatus getInstance() {
        return INSTANCE;
    }

    private void reset() {
        views.clear();
        viewItems.clear();
        elementItems.clear();
        extendItems.clear();
        controllerItems.clear();
        helperItems.clear();
        caretPositionElement = null;
        caretPositionExtend = null;
        caretPositionElementPath = ""; // NOI18N
        caretPositionExtendPath = ""; // NOI18N
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject view, int offset) {
        reset();
        final FileObject controller = getController();

        // scan
        // controller
        if (controller != null) {
            final CakePhpControllerVisitor controllerVisitor = new CakePhpControllerVisitor(view);
            try {
                scanController(controllerVisitor, controller);
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
            setHelpers(controllerVisitor.getHelpers());
            // sort
            sort(helperItems);
        }

        // view
        if (view != null) {
            final CakePhpViewVisitor viewVisitor = new CakePhpViewVisitor(view, getOffset());
            try {
                scanView(viewVisitor, view);
            } catch (ParseException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }

            // set
            // extends only 2.1+
            // don't change order
            CakePhpModule cakeModule = CakePhpModule.forPhpModule(getPhpModule());
            caretPositionElementPath = viewVisitor.getElementPathForCaretPosition();
            caretPositionExtendPath = viewVisitor.getExtendPathForCaretPosition();
            setElements(cakeModule, viewVisitor.getElementPaths());
            setExtends(cakeModule, viewVisitor.getExtendPaths());
        }
    }

    private void scanController(CakePhpControllerVisitor visitor, FileObject controller) throws ParseException {
        scan(visitor, controller);
    }

    private void scanView(CakePhpViewVisitor visitor, FileObject view) throws ParseException {
        scan(visitor, view);
    }

    private void setElements(CakePhpModule cakeModule, final Set<String> elementPaths) {
        if (cakeModule != null) {
            for (String elementPath : elementPaths) {
                FileObject element = getElementFile(cakeModule, elementPath);
                if (element != null) {
                    elementItems.add(new GoToViewItem(element, getCurrentOffset(element)));
                }
            }
        }

        if (!StringUtils.isEmpty(caretPositionElementPath)) {
            FileObject element = getElementFile(cakeModule, caretPositionElementPath);
            if (element != null) {
                caretPositionElement = element;
            }
        }
    }

    private void setExtends(CakePhpModule cakeModule, final Set<String> extendPaths) {
        if (cakeModule != null) {
            for (String extendPath : extendPaths) {
                FileObject extend = getExtendFile(cakeModule, extendPath);
                if (extend != null) {
                    extendItems.add(new GoToViewItem(extend, getCurrentOffset(extend)));
                }
            }
        }

        if (!StringUtils.isEmpty(caretPositionExtendPath)) {
            FileObject extend = getExtendFile(cakeModule, caretPositionExtendPath);
            if (extend != null) {
                caretPositionExtend = extend;
            }
        }
    }

    private void setHelpers(final List<FileObject> helperFiles) {
        for (FileObject helper : helperFiles) {
            helperItems.add(new GoToHelperItem(helper, getCurrentOffset(helper)));
        }
    }

    @Override
    public List<GoToItem> getControllers() {
        FileObject controller = getController();
        if (controller == null) {
            return Collections.emptyList();
        }

        List<GoToItem> items = new ArrayList<GoToItem>();
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : editorSupport.getClasses(controller)) {
            items.add(new GoToControllerItem(controller, getCurrentOffset(controller)));
            if (CakePhpUtils.isControllerName(phpClass.getName())) {
                for (PhpClass.Method method : phpClass.getMethods()) {
                    items.add(new GoToControllerItem(controller, method.getOffset(), method.getName()));
                }
            }
            break;
        }
        sort(items);
        return items;
    }

    private List<GoToItem> getSmartControllers() {
        FileObject view = getCurrentFile();
        FileObject controller = getController();
        if (controller == null) {
            return Collections.emptyList();
        }

        List<GoToItem> items = new ArrayList<GoToItem>();
        int actionMethodOffset = CakePhpUtils.getActionMethodOffset(controller, view);
        if (actionMethodOffset > 0) {
            items.add(new GoToControllerItem(controller, getCurrentOffset(controller)));
            items.add(new GoToControllerItem(controller, actionMethodOffset, view.getName()));
            return items;
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getViews() {
        if (caretPositionElement != null) {
            return Collections.singletonList((GoToItem) new GoToViewItem(caretPositionElement, getCurrentOffset(caretPositionElement)));
        }
        if (caretPositionExtend != null) {
            return Collections.singletonList((GoToItem) new GoToViewItem(caretPositionExtend, getCurrentOffset(caretPositionElement)));
        }

        if (!viewItems.isEmpty()) {
            return viewItems;
        }

        mergeViewItems();
        sort(viewItems);
        return viewItems;
    }

    private void mergeViewItems() {
        elementItems.addAll(extendItems);
        for (GoToItem item : elementItems) {
            FileObject fileObject = item.getFileObject();
            if (views.contains(fileObject)) {
                continue;
            }
            views.add(fileObject);
            viewItems.add(item);
        }
    }

    @Override
    public List<GoToItem> getHelpers() {
        return helperItems;
    }

    @Override
    public List<GoToItem> getSmart() {
        controllerItems.addAll(getSmartControllers());
        if (controllerItems.isEmpty()) {
            return getControllers();
        }

        return controllerItems;
    }

    private FileObject getElementFile(CakePhpModule cakeModule, String elementPath) {
        FileObject element = cakeModule.getViewFile(DIR_TYPE.APP, ELEMENTS, elementPath);
        if (element == null) {
            element = cakeModule.getViewFile(DIR_TYPE.CORE, ELEMENTS, elementPath);
        }
        return element;
    }

    private FileObject getExtendFile(CakePhpModule cakeModule, String extendPath) {
        FileObject controller = getController();
        if (controller == null) {
            return null;
        }

        FileObject extend = null;
        // starts with "/" slash
        if (CakePhpUtils.isAbsolutePath(extendPath)) {
            FileObject viewDirectory = cakeModule.getViewDirectory(DIR_TYPE.APP);
            if (viewDirectory != null) {
                if (extendPath.length() > 1) {
                    extend = viewDirectory.getFileObject(CakePhpUtils.appendCtpExt(extendPath.substring(1)));
                }
            }
        }

        if (extend == null) {
            String viewFolderName = cakeModule.getViewFolderName(controller.getName());
            extend = cakeModule.getViewFile(CakePhpModule.DIR_TYPE.APP, viewFolderName, extendPath);
        }

        return extend;
    }

    private FileObject getController() {
        FileObject view = getCurrentFile();
        if (view == null) {
            return null;
        }
        return CakePhpUtils.getController(view);
    }
}
