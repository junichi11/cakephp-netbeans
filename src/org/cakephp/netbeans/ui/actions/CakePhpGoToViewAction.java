/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.ui.actions;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.ui.GoToPopup;
import org.cakephp.netbeans.ui.PopupUtil;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToViewItem;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.GoToViewAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

public final class CakePhpGoToViewAction extends GoToViewAction {

    static final Logger LOGGER = Logger.getLogger(CakePhpGoToViewAction.class.getName());
    private static final long serialVersionUID = 9834759234756237L;
    private final FileObject controller;
    private final int offset;

    public CakePhpGoToViewAction(FileObject controller, int offset) {
        assert CakePhpUtils.isController(controller);
        this.controller = controller;
        this.offset = offset;
    }

    @Override
    public boolean goToView() {
        // get PhpBaseElement(Method) for current positon
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        PhpBaseElement phpElement = editorSupport.getElement(controller, offset);
        if (phpElement == null) {
            return false;
        }

        // go to theme view
        FileObject[] themes = getThemes();
        if (themes != null) {
            if (goToThemeView(editorSupport, themes, phpElement)) {
                return true;
            }
        }

        // go to view (app)
        FileObject view = CakePhpUtils.getView(controller, phpElement);
        if (view != null) {
            UiUtils.open(view, DEFAULT_OFFSET);
            return true;
        }

        // auto create a view file
        // support for only app view file
        // TODO support for theme?
        PhpModule phpModule = PhpModule.Factory.forFileObject(controller);
        if (phpModule == null) {
            return false;
        }
        if (CakePreferences.getAutoCreateView(phpModule)) {
            try {
                view = CakePhpUtils.createView(controller, phpElement);
                if (view != null) {
                    UiUtils.open(view, DEFAULT_OFFSET);
                    return true;
                }
            } catch (IOException ex) {
                // do nothing
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return false;
    }

    /**
     * Get theme directories.
     *
     * @return theme directories
     */
    public FileObject[] getThemes() {
        PhpModule phpModule = PhpModule.Factory.forFileObject(controller);
        FileObject[] themes = null;
        FileObject themeDirectory;

        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return new FileObject[0];
        }
        CakeVersion cakeVersion = cakeModule.getCakeVersion();
        if (cakeVersion == null) {
            return new FileObject[0];
        }
        if (cakeVersion.getMajor() >= 2) {
            themeDirectory = controller.getFileObject("../../View/Themed"); // NOI18N
        } else {
            themeDirectory = controller.getFileObject("../../views/themed"); // NOI18N
        }
        if (themeDirectory != null) {
            themes = themeDirectory.getChildren();
        }
        return themes;
    }

    /**
     * Go to theme view.
     *
     * @param editorSupport EditorSupport
     * @param themes theme directories
     * @param phpElement PhpBaseElement (Method)
     * @return true if there are theme files for each theme directories,
     * otherwise false.
     */
    private boolean goToThemeView(EditorSupport editorSupport, FileObject[] themes, PhpBaseElement phpElement) {
        for (PhpClass phpClass : editorSupport.getClasses(controller)) {
            if (CakePhpUtils.isControllerName(phpClass.getName())) {
                for (PhpClass.Field field : phpClass.getFields()) {
                    if (field.getName().equals("$theme")) { // NOI18N

                        // create items
                        final List<GoToItem> viewItems = new ArrayList<>();
                        for (FileObject themeDirectory : themes) {
                            if (themeDirectory.isFolder()) {
                                FileObject view = CakePhpUtils.getView(controller, phpElement, themeDirectory);
                                if (view != null) {
                                    viewItems.add(new GoToViewItem(view, 0, themeDirectory.getName()));
                                }
                            }
                        }
                        FileObject defaultView = CakePhpUtils.getView(controller, phpElement);
                        if (defaultView != null) {
                            viewItems.add(new GoToViewItem(defaultView, 0, null));
                        }

                        if (viewItems.isEmpty()) {
                            return false;
                        }

                        // editor
                        JTextComponent editor = EditorRegistry.lastFocusedComponent();

                        // show popup
                        if (editor != null) {
                            try {
                                Rectangle rectangle = editor.modelToView(offset);
                                final Point point = new Point(rectangle.x, rectangle.y + rectangle.height);
                                SwingUtilities.convertPointToScreen(point, editor);
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        PopupUtil.showPopup(new GoToPopup(" Go To Theme View ", viewItems), " Go To Theme View ", point.x, point.y, true, 0);
                                    }
                                });
                            } catch (BadLocationException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
