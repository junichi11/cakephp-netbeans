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
package org.cakephp.netbeans.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.CakePhp;
import org.cakephp.netbeans.editor.visitors.CakePhpComponentVisitor;
import org.cakephp.netbeans.editor.visitors.CakePhpControllerVisitor;
import org.cakephp.netbeans.editor.visitors.CakePhpFieldsVisitor;
import org.cakephp.netbeans.editor.visitors.CakePhpHelperVisitor;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.DefaultFileFilter;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.openide.filesystems.FileObject;

/**
 *
 * @author igorf
 */
public abstract class CakePhpEditorExtender extends EditorExtender {

    static final Logger LOGGER = Logger.getLogger(CakePhpEditorExtender.class.getName());
    private boolean isView = false;
    private boolean isController = false;
    private boolean isComponent = false;
    private boolean isHelper = false;
    private final PhpModule phpModule;

    public CakePhpEditorExtender(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        isView = CakePhpUtils.isView(fo);
        isController = CakePhpUtils.isController(fo);
        isComponent = CakePhpUtils.isComponent(fo);
        isHelper = CakePhpUtils.isHelper(fo);
        if (CakePhpUtils.isCtpFile(fo)) {
            isView = true;
        }

        if (!isView && !isController && !isComponent && !isHelper) {
            return Collections.emptyList();
        }

        List<PhpBaseElement> elements;
        elements = new LinkedList<>();

        // get AppController
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule != null) {
            FileObject appController = cakeModule.getFile(DIR_TYPE.APP, CakePhpModule.FILE_TYPE.CONTROLLER, "App", null); // NOI18N
            if (appController != null) {
                for (PhpClass phpClass : parseFields(appController)) {
                    elements.add(new PhpVariable("$this", phpClass, fo, 0)); // NOI18N
                }
            }
        }

        for (PhpClass phpClass : parseFields(fo)) {
            if (isView || isHelper) {
                addDefaultHelpers(phpClass, fo);
            } else {
                addDefaultComponents(phpClass, fo);
            }
            elements.add(new PhpVariable("$this", phpClass, fo, 0)); // NOI18N
        }

        if (isView) {
            elements.addAll(parseAction(fo));
        }

        return elements;
    }

    public boolean isView() {
        return isView;
    }

    public boolean isController() {
        return isController;
    }

    public boolean isComponent() {
        return isComponent;
    }

    public boolean isHelper() {
        return isHelper;
    }

    public PhpModule getPhpModule() {
        return phpModule;
    }

    private Set<PhpVariable> parseAction(final FileObject view) {
        final FileObject controller = CakePhpUtils.getController(view);
        if (controller == null) {
            return Collections.emptySet();
        }
        final Set<PhpVariable> phpVariables = new HashSet<>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(controller)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                    final CakePhpControllerVisitor controllerVisitor = new CakePhpControllerVisitor(view, getPhpClass(view));
                    controllerVisitor.scan(Utils.getRoot(parseResult));
                    phpVariables.addAll(controllerVisitor.getPhpVariables());
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return phpVariables;
    }

    private Set<PhpClass> parseFields(final FileObject fo) {
        FileObject tmp = fo;
        if (CakePhpUtils.isView(fo) || fo.getExt().equals(CakePhp.CTP)) {
            tmp = CakePhpUtils.getController(fo);
            if (tmp == null) {
                return Collections.singleton(getViewPhpClass());
            }
        }

        final FileObject target = tmp;
        final Set<PhpClass> phpClasses = new HashSet<>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(target)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                    final CakePhpFieldsVisitor visitor;
                    if (isView || isController) {
                        visitor = new CakePhpControllerVisitor(fo, getPhpClass(fo));
                    } else if (isComponent) {
                        visitor = new CakePhpComponentVisitor(fo, getPhpClass(fo));
                    } else if (isHelper) {
                        visitor = new CakePhpHelperVisitor(fo, getPhpClass(fo));
                    } else {
                        visitor = null;
                    }

                    if (visitor != null) {
                        visitor.scan(Utils.getRoot(parseResult));
                        phpClasses.addAll(Collections.singleton(visitor.getPhpClass()));
                    }
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return phpClasses;
    }

    /**
     * Get extends class name. (e.g. component : Component, helper : AppHelper)
     *
     * @param fo
     * @param fileType
     * @return
     */
    @CheckForNull
    private PhpClass getPhpClass(FileObject fo) {
        if (CakePhpUtils.isComponent(fo)) {
            return getComponentPhpClass();
        } else if (CakePhpUtils.isController(fo)) {
            // get AppController fields info.
            String name = fo.getName();
            name = CakePhpUtils.toUnderscoreCase(name);
            if ("app_controller".equals(name)) { // NOI18N
                FileObject currentFileObject = CakePhpUtils.getCurrentFileObject();
                if (currentFileObject != null && CakePhpUtils.isView(currentFileObject)) {
                    return getViewPhpClass();
                }
            }
            return getControllerPhpClass();
        } else if (CakePhpUtils.isView(fo)) {
            if (CakePhpUtils.isCtpFile(fo) || FileUtils.isPhpFile(fo)) {
                return getViewPhpClass();
            }
        } else if (CakePhpUtils.isHelper(fo)) {
            return getHelperPhpClass();
        }
        return null;
    }

    public abstract PhpClass getViewPhpClass();

    public abstract PhpClass getControllerPhpClass();

    public abstract PhpClass getComponentPhpClass();

    public abstract PhpClass getHelperPhpClass();

    public void addDefaultHelpers(PhpClass phpClass, FileObject fo) {
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module == null) {
            return;
        }

        FileObject helperDirectory = module.getHelperDirectory(DIR_TYPE.CORE);
        if (helperDirectory != null) {
            List<FileObject> helpers = module.getFiles(helperDirectory, new DefaultFileFilter());
            for (FileObject helper : helpers) {
                String fullyQualifiedName = getFullyQualifiedClassName(helper);
                if (StringUtils.isEmpty(fullyQualifiedName)) {
                    continue;
                }
                String className = CakePhpUtils.getClassName(helper);
                String name = className.replace(CakePhpModule.FILE_TYPE.HELPER.toString(), ""); // NOI18N
                phpClass.addField(name, new PhpClass(name, fullyQualifiedName), fo, 0);
            }
        }
    }

    public void addDefaultComponents(PhpClass phpClass, FileObject fo) {
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module == null) {
            return;
        }

        FileObject componentDirectory = module.getComponentDirectory(DIR_TYPE.CORE);
        if (componentDirectory != null) {
            List<FileObject> components = module.getFiles(componentDirectory, new DefaultFileFilter());
            for (FileObject component : components) {
                String fullyQualifiedName = getFullyQualifiedClassName(component);
                if (StringUtils.isEmpty(fullyQualifiedName)) {
                    continue;
                }
                String className = CakePhpUtils.getClassName(component);
                String name = className.replace(CakePhpModule.FILE_TYPE.COMPONENT.toString(), ""); // NOI18N
                phpClass.addField(name, new PhpClass(name, fullyQualifiedName), fo, 0);
            }
        }
    }

    public abstract String getFullyQualifiedClassName(FileObject target);
}
