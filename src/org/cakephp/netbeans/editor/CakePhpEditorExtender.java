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
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.module.DefaultFileFilter;
import org.cakephp.netbeans.util.CakePhpCodeUtils;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.CakeVersion;
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
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FieldsDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.openide.filesystems.FileObject;

/**
 *
 * @author igorf
 */
public class CakePhpEditorExtender extends EditorExtender {

    static final Logger LOGGER = Logger.getLogger(CakePhpEditorExtender.class.getName());

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        boolean isView = CakePhpUtils.isView(fo);
        boolean isController = CakePhpUtils.isController(fo);
        boolean isComponent = CakePhpUtils.isComponent(fo);
        boolean isHelper = CakePhpUtils.isHelper(fo);

        if (!isView && !isController && !isComponent && !isHelper) {
            return Collections.emptyList();
        }

        List<PhpBaseElement> elements;
        elements = new LinkedList<PhpBaseElement>();

        for (PhpClass phpClass : parseFields(fo)) {
            PhpModule pm = PhpModule.forFileObject(fo);
            CakePhpModule module = CakePhpModule.forPhpModule(pm);
            if (isView || isHelper) {
                if (!(isView && CakeVersion.getInstance(pm).isCakePhp(2))) {
                    FileObject helperDirectory = module.getHelperDirectory(DIR_TYPE.CORE);
                    if (helperDirectory != null) {
                        List<FileObject> helpers = module.getFiles(helperDirectory, new DefaultFileFilter());
                        for (FileObject helper : helpers) {
                            String className = CakePhpUtils.getClassName(helper);
                            String name = className.replace(CakePhpModule.FILE_TYPE.HELPER.toString(), ""); // NOI18N
                            phpClass.addField(name, new PhpClass(name, className), fo, 0);
                        }
                    }
                }
            } else {
                if (!(isController && CakeVersion.getInstance(pm).isCakePhp(2))) {
                    FileObject componentDirectory = module.getComponentDirectory(DIR_TYPE.CORE);
                    if (componentDirectory != null) {
                        List<FileObject> components = module.getFiles(componentDirectory, new DefaultFileFilter());
                        for (FileObject component : components) {
                            String className = CakePhpUtils.getClassName(component);
                            String name = className.replace(CakePhpModule.FILE_TYPE.COMPONENT.toString(), ""); // NOI18N
                            phpClass.addField(name, new PhpClass(name, className), fo, 0);
                        }
                    }
                }
            }
            elements.add(new PhpVariable("$this", phpClass, fo, 0)); // NOI18N
        }

        if (isView) {
            elements.addAll(parseAction(fo));
        }

        return elements;
    }

    private Set<PhpVariable> parseAction(final FileObject view) {
        final FileObject controller = CakePhpUtils.getController(view);
        if (controller == null) {
            return Collections.emptySet();
        }
        final Set<PhpVariable> phpVariables = new HashSet<PhpVariable>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(controller)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                    final CakePhpControllerVisitor controllerVisitor = new CakePhpControllerVisitor(view, (PHPParseResult) parseResult);
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
        if (CakePhpUtils.isView(fo)) {
            tmp = CakePhpUtils.getController(fo);
        }

        final FileObject target = tmp;
        if (target == null) {
            return Collections.emptySet();
        }

        final Set<PhpClass> phpClasses = new HashSet<PhpClass>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(target)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                    if (CakePhpUtils.isView(fo) || CakePhpUtils.isController(fo)) {
                        final CakePhpControllerVisitor controllerVisitor = new CakePhpControllerVisitor(fo, (PHPParseResult) parseResult);
                        controllerVisitor.scan(Utils.getRoot(parseResult));
                        phpClasses.addAll(Collections.singleton(controllerVisitor.getPhpClasses()));
                    } else if (CakePhpUtils.isComponent(fo)) {
                        final CakePhpComponentVisitor componentVisitor = new CakePhpComponentVisitor(fo);
                        componentVisitor.scan(Utils.getRoot(parseResult));
                        phpClasses.addAll(Collections.singleton(componentVisitor.getPhpClass()));
                    } else if (CakePhpUtils.isHelper(fo)) {
                        final CakePhpHelperVisitor helperVisitor = new CakePhpHelperVisitor(fo);
                        helperVisitor.scan(Utils.getRoot(parseResult));
                        phpClasses.addAll(Collections.singleton(helperVisitor.getPhpClass()));
                    }
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return phpClasses;
    }

    private static final class CakePhpControllerVisitor extends DefaultVisitor {

        private final Set<PhpVariable> fields = new HashSet<PhpVariable>();
        private final PhpClass controllerClasses = new PhpClass("Controller", "Controller"); // NOI18N
        private final PhpClass viewClasses = new PhpClass("View", "View"); // NOI18N
        private String className = null;
        private String methodName = null;
        private String viewName = null;
        private PhpModule pm = null;
        private FileObject target = null;

        public CakePhpControllerVisitor(FileObject fo, PHPParseResult actionParseResult) {
            this.target = fo;
            pm = PhpModule.forFileObject(fo);
            if (CakePhpUtils.isView(fo)) {
                viewName = CakePhpUtils.getActionName(fo);
            }
        }

        public Set<PhpVariable> getPhpVariables() {
            Set<PhpVariable> phpVariables = new HashSet<PhpVariable>();
            synchronized (fields) {
                phpVariables.addAll(fields);
            }
            return phpVariables;
        }

        public PhpClass getPhpClasses() {
            PhpClass phpClasses = null;
            if (viewName != null) {
                synchronized (viewClasses) {
                    phpClasses = viewClasses;
                }
            } else {
                synchronized (controllerClasses) {
                    phpClasses = controllerClasses;
                }
            }
            return phpClasses;
        }

        @Override
        public void visit(ClassDeclaration node) {
            className = CodeUtils.extractClassName(node);
            super.visit(node);
        }

        @Override
        public void visit(MethodDeclaration node) {
            methodName = CodeUtils.extractMethodName(node);
            super.visit(node);
        }

        @Override
        public void visit(MethodInvocation node) {
            super.visit(node);

            if (!(node.getDispatcher() instanceof Variable)
                || !"$this".equals(CodeUtils.extractVariableName((Variable) node.getDispatcher()))) { // NOI18N
                return;
            }

            FunctionInvocation fi = node.getMethod();
            String invokedMethodName = CodeUtils.extractFunctionName(fi);

            List<Expression> params = fi.getParameters();
            Expression e = null;

            if (!params.isEmpty()) {
                e = params.get(0);
            }

            String viewVarName = ""; // NOI18N
            if (e instanceof Scalar) {
                Scalar s = (Scalar) e;
                if (s.getScalarType() == Scalar.Type.STRING) {
                    viewVarName = prepareViewVar(s.getStringValue());
                }
            }

            if (methodName.equals(viewName)
                && invokedMethodName.equals("set") // NOI18N
                && CakePhpUtils.isControllerName(className)
                && !viewVarName.isEmpty()) {
                synchronized (fields) {
                    fields.add(new PhpVariable("$" + viewVarName, new PhpClass("stdClass", "stdClass"), target, 0)); // NOI18N
                }
            }
        }

        @Override
        public void visit(FieldsDeclaration node) {
            List<SingleFieldDeclaration> controllerFields = node.getFields();
            for (SingleFieldDeclaration field : controllerFields) {
                String name = CodeUtils.extractVariableName(field.getName());
                // get ArrayCreation
                ArrayCreation arrayCreation = null;
                Expression value = field.getValue();
                if (value instanceof ArrayCreation) {
                    arrayCreation = (ArrayCreation) value;
                }
                if (arrayCreation == null) {
                    continue;
                }

                FileObject object = null;
                for (ArrayElement element : arrayCreation.getElements()) {
                    String aliasName = null;
                    Expression entity = null;
                    Expression e = element.getKey();
                    if (e != null) {
                        Expression elementValue = element.getValue();
                        if (elementValue != null && elementValue instanceof ArrayCreation) {
                            ArrayCreation ac = (ArrayCreation) elementValue;
                            entity = CakePhpCodeUtils.getEntity(ac);
                        }

                        // check entity
                        if (entity != null) {
                            aliasName = CakePhpCodeUtils.getStringValue(e);
                            e = entity;
                        }
                    } else {
                        e = element.getValue();
                    }
                    if (e == null) {
                        continue;
                    }

                    String elementName = CakePhpCodeUtils.getStringValue(e);
                    CakePhpModule module = CakePhpModule.forPhpModule(pm);
                    // model
                    if (viewName == null && name.equals("$uses")) { // NOI18N
                        object = module.getModelFile(DIR_TYPE.APP, elementName);

                        if (object != null) {
                            synchronized (controllerClasses) {
                                controllerClasses.addField(elementName, new PhpClass(elementName, elementName), object, 0);
                            }
                        }
                    }

                    // check app or plugin component
                    if (viewName == null && name.equals("$components")) { // NOI18N
                        String[] split = elementName.split("[.]"); // NOI18N
                        int len = split.length;
                        switch (len) {
                            case 1:
                                object = module.getComponentFile(DIR_TYPE.APP, elementName);
                                break;
                            case 2:
                                String pluginName = split[0];
                                String componentName = split[1];
                                object = module.getComponentFile(DIR_TYPE.APP_PLUGIN, componentName, pluginName);
                                if (object == null) {
                                    object = module.getComponentFile(DIR_TYPE.PLUGIN, componentName, pluginName);
                                }
                                elementName = componentName;
                                break;
                            default:
                                break;
                        }
                        String componentClassName = elementName + "Component"; // NOI18N
                        if (object != null) {
                            synchronized (controllerClasses) {
                                if (aliasName == null) {
                                    controllerClasses.addField(elementName, new PhpClass(elementName, componentClassName), object, 0);
                                } else {
                                    controllerClasses.addField(aliasName, new PhpClass(elementName, componentClassName), object, 0);
                                }
                            }
                        }
                    }

                    // check app or plugin helper
                    if (viewName != null && name.equals("$helpers")) { // NOI18N
                        String[] split = elementName.split("[.]"); // NOI18N
                        int len = split.length;
                        switch (len) {
                            case 1:
                                object = module.getHelperFile(DIR_TYPE.APP, elementName);
                                break;
                            case 2:
                                String pluginName = split[0];
                                String helperName = split[1];
                                object = module.getHelperFile(DIR_TYPE.APP_PLUGIN, helperName, pluginName);
                                if (object == null) {
                                    object = module.getHelperFile(DIR_TYPE.PLUGIN, helperName, pluginName);
                                }
                                elementName = helperName;
                                break;
                            default:
                                break;
                        }
                        String helperClassName = elementName + "Helper"; // NOI18N
                        if (object != null) {
                            synchronized (viewClasses) {
                                if (aliasName == null) {
                                    viewClasses.addField(elementName, new PhpClass(elementName, helperClassName), object, 0);
                                } else {
                                    viewClasses.addField(aliasName, new PhpClass(elementName, helperClassName), object, 0);
                                }
                            }
                        }
                    }
                }
            }
            super.visit(node);
        }

        private String prepareViewVar(String viewVarName) {
            if (!viewVarName.isEmpty()) {
                viewVarName = viewVarName.substring(1, viewVarName.length() - 1).trim();
                if (!viewVarName.matches("[A-Za-z_][A-Za-z0-9_]*")) { // NOI18N
                    viewVarName = ""; // NOI18N
                }
            }
            return viewVarName;
        }
    }

    private static final class CakePhpComponentVisitor extends DefaultVisitor {

        private final PhpClass componentClass;
        private PhpModule pm;

        public CakePhpComponentVisitor(FileObject fo) {
            pm = PhpModule.forFileObject(fo);
            CakeVersion version = CakeVersion.getInstance(pm);

            if (version.isCakePhp(1)) {
                componentClass = new PhpClass("Object", "Object"); // NOI18N
            } else if (version.isCakePhp(2)) {
                componentClass = new PhpClass("Component", "Component"); // NOI18N
            } else {
                componentClass = null;
            }
        }

        /**
         * Get component class fields
         *
         * @return component php class
         */
        public PhpClass getPhpClass() {
            PhpClass component;
            synchronized (componentClass) {
                component = componentClass;
            }
            return component;
        }

        @Override
        public void visit(FieldsDeclaration node) {
            super.visit(node);
            List<SingleFieldDeclaration> componentFields = node.getFields();
            for (SingleFieldDeclaration field : componentFields) {
                String name = CodeUtils.extractVariableName(field.getName());
                if (componentClass == null || !name.equals("$components")) { // NOI18N
                    return;
                }
                // get ArrayCreation
                ArrayCreation arrayCreation = null;
                Expression value = field.getValue();
                if (value instanceof ArrayCreation) {
                    arrayCreation = (ArrayCreation) value;
                }
                if (arrayCreation == null) {
                    continue;
                }
                for (ArrayElement element : arrayCreation.getElements()) {
                    Expression entity = null;
                    String aliasName = null;
                    Expression e = element.getKey();
                    if (e != null) {
                        // get value
                        Expression elementValue = element.getValue();
                        if (elementValue != null && elementValue instanceof ArrayCreation) {
                            ArrayCreation ac = (ArrayCreation) elementValue;
                            entity = CakePhpCodeUtils.getEntity(ac);
                        }
                        if (entity != null) {
                            aliasName = CakePhpCodeUtils.getStringValue(e);
                            e = entity;
                        }
                    } else {
                        e = element.getValue();
                    }
                    if (e == null) {
                        continue;
                    }

                    String elementName = CakePhpCodeUtils.getStringValue(e);

                    FileObject object = null;
                    CakePhpModule module = CakePhpModule.forPhpModule(pm);
                    // check app or plugin component
                    String[] split = elementName.split("[.]"); // NOI18N
                    int len = split.length;
                    switch (len) {
                        case 1:
                            object = module.getComponentFile(DIR_TYPE.APP, elementName);
                            break;
                        case 2:
                            String pluginName = split[0];
                            String componentName = split[1];
                            object = module.getComponentFile(DIR_TYPE.APP_PLUGIN, componentName, pluginName);
                            if (object == null) {
                                object = module.getComponentFile(DIR_TYPE.PLUGIN, componentName, pluginName);
                            }
                            elementName = componentName;
                            break;
                        default:
                            break;
                    }
                    String componentClassName = elementName + "Component"; // NOI18N
                    if (object != null) {
                        synchronized (componentClass) {
                            if (aliasName == null) {
                                componentClass.addField(elementName, new PhpClass(elementName, componentClassName), object, 0);
                            } else {
                                componentClass.addField(aliasName, new PhpClass(elementName, componentClassName), object, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    private static final class CakePhpHelperVisitor extends DefaultVisitor {

        private final PhpClass helperClass = new PhpClass("AppHelper", "AppHelper"); // NOI18N
        private PhpModule pm;

        public CakePhpHelperVisitor(FileObject fo) {
            pm = PhpModule.forFileObject(fo);
        }

        /**
         * Get component class fields
         *
         * @return component php class
         */
        public PhpClass getPhpClass() {
            PhpClass component;
            synchronized (helperClass) {
                component = helperClass;
            }
            return component;
        }

        @Override
        public void visit(FieldsDeclaration node) {
            super.visit(node);
            List<SingleFieldDeclaration> helperFields = node.getFields();
            for (SingleFieldDeclaration field : helperFields) {
                String name = CodeUtils.extractVariableName(field.getName());
                if (helperClass == null || !name.equals("$helpers")) { // NOI18N
                    return;
                }
                // get ArrayCreation
                ArrayCreation arrayCreation = null;
                Expression value = field.getValue();
                if (value instanceof ArrayCreation) {
                    arrayCreation = (ArrayCreation) value;
                }
                if (arrayCreation == null) {
                    continue;
                }
                for (ArrayElement element : arrayCreation.getElements()) {
                    String aliasName = null;
                    Expression entity = null;
                    Expression e = element.getKey();
                    if (e != null) {
                        // get value
                        Expression elementValue = element.getValue();
                        if (elementValue != null && elementValue instanceof ArrayCreation) {
                            ArrayCreation ac = (ArrayCreation) elementValue;
                            entity = CakePhpCodeUtils.getEntity(ac);
                        }
                        if (entity != null) {
                            aliasName = CakePhpCodeUtils.getStringValue(e);
                            e = entity;
                        }
                    } else {
                        e = element.getValue();
                    }
                    if (e == null) {
                        continue;
                    }

                    String elementName = CakePhpCodeUtils.getStringValue(e);

                    FileObject object = null;
                    // check app or plugin helper
                    CakePhpModule module = CakePhpModule.forPhpModule(pm);
                    String[] split = elementName.split("[.]"); // NOI18N
                    int len = split.length;
                    switch (len) {
                        case 1:
                            object = module.getHelperFile(DIR_TYPE.APP, elementName);
                            break;
                        case 2:
                            String pluginName = split[0];
                            String helperName = split[1];
                            object = module.getHelperFile(DIR_TYPE.APP_PLUGIN, helperName, pluginName);
                            if (object == null) {
                                object = module.getHelperFile(DIR_TYPE.PLUGIN, helperName, pluginName);
                            }
                            elementName = helperName;
                            break;
                        default:
                            break;
                    }
                    String helperClassName = elementName + "Helper"; // NOI18N
                    if (object != null) {
                        synchronized (helperClass) {
                            if (aliasName == null) {
                                helperClass.addField(elementName, new PhpClass(elementName, helperClassName), object, 0);
                            } else {
                                helperClass.addField(aliasName, new PhpClass(elementName, helperClassName), object, 0);
                            }
                        }
                    }
                }
            }
        }
    }
}
