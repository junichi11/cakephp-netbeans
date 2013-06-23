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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.CakePhp;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.module.DefaultFileFilter;
import org.cakephp.netbeans.util.CakePhpCodeUtils;
import org.cakephp.netbeans.util.CakePhpUtils;
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
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FieldsDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.NamespaceName;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.StaticMethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.openide.filesystems.FileObject;

/**
 *
 * @author igorf
 */
public abstract class CakePhpEditorExtender extends EditorExtender {

    static final Logger LOGGER = Logger.getLogger(CakePhpEditorExtender.class.getName());
    private static final Map<String, FILE_TYPE> FILE_TYPES = new HashMap<String, FILE_TYPE>();
    private static final String USES = "$uses"; // NOI18N
    private static final String COMPONENTS = "$components"; // NOI18N
    private static final String HELPERS = "$helpers"; // NOI18N
    private static final String ACTS_AS = "$actsAs"; // NOI18N
    private boolean isView = false;
    private boolean isController = false;
    private boolean isComponent = false;
    private boolean isHelper = false;
    private PhpModule phpModule;

    public CakePhpEditorExtender(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    static {
        FILE_TYPES.put(USES, FILE_TYPE.MODEL);
        FILE_TYPES.put(COMPONENTS, FILE_TYPE.COMPONENT);
        FILE_TYPES.put(HELPERS, FILE_TYPE.HELPER);
        FILE_TYPES.put(ACTS_AS, FILE_TYPE.BEHAVIOR);
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
        elements = new LinkedList<PhpBaseElement>();

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
        final Set<PhpVariable> phpVariables = new HashSet<PhpVariable>();
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
        if (target == null) {
            return Collections.emptySet();
        }

        final Set<PhpClass> phpClasses = new HashSet<PhpClass>();
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
    private PhpClass getPhpClass(FileObject fo) {
        if (CakePhpUtils.isComponent(fo)) {
            return getComponentPhpClass();
        } else if (CakePhpUtils.isController(fo)) {
            return getControllerPhpClass();
        } else if (CakePhpUtils.isView(fo)) {
            return getViewPhpClass();
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

    //~ inner classes
    private final class CakePhpControllerVisitor extends CakePhpFieldsVisitor {

        private final Set<PhpVariable> fields = new HashSet<PhpVariable>();
        private String className = null;
        private String methodName = null;
        private String viewName = null;

        public CakePhpControllerVisitor(FileObject fo, PhpClass phpClass) {
            super(fo, phpClass);
            if (CakePhpUtils.isView(fo)) {
                viewName = CakePhpUtils.getActionName(fo);
            }
        }

        @Override
        public Set<String> getFieldNames() {
            Set<String> fieldNames = new HashSet<String>();
            if (CakePhpUtils.isController(targetFile)) {
                fieldNames.add(USES);
                fieldNames.add(COMPONENTS);
            } else if (CakePhpUtils.isView(targetFile)) {
                fieldNames.add(HELPERS);
            }
            return fieldNames;
        }

        public Set<PhpVariable> getPhpVariables() {
            Set<PhpVariable> phpVariables = new HashSet<PhpVariable>();
            synchronized (fields) {
                phpVariables.addAll(fields);
            }
            return phpVariables;
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
                    fields.add(new PhpVariable("$" + viewVarName, new PhpClass("stdClass", "stdClass"), targetFile, 0)); // NOI18N
                }
            }
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

    abstract class CakePhpFieldsVisitor extends DefaultVisitor {

        private final PhpClass phpClass;
        protected FileObject targetFile;
        protected PhpModule phpModule;

        public CakePhpFieldsVisitor(FileObject targetFile, PhpClass phpClass) {
            this.phpClass = phpClass;
            this.targetFile = targetFile;
            phpModule = PhpModule.forFileObject(targetFile);
        }

        /**
         * Get class fields for target.
         *
         * @return php class for target
         */
        public PhpClass getPhpClass() {
            PhpClass pc;
            synchronized (phpClass) {
                pc = phpClass;
            }
            return pc;
        }

        /**
         * Get filed name. (e.g. $helpers)
         *
         * @return
         */
        public abstract Set<String> getFieldNames();

        @Override
        public void visit(StaticMethodInvocation node) {
            super.visit(node);
            // get class name
            String methodClassName = getClassName(node);
            if (methodClassName == null || !methodClassName.equals("ClassRegistry")) { // NOI18N
                return;
            }

            // get method name
            FunctionInvocation method = node.getMethod();
            String methodName = CodeUtils.extractFunctionName(method);
            if (!methodName.equals("init")) { // NOI18N
                return;
            }

            // add field to PhpClass
            List<Expression> parameters = method.getParameters();
            CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
            for (Expression parameter : parameters) {
                String entityName = CakePhpCodeUtils.getStringValue(parameter);
                addField(entityName, USES, module, null);
                break;
            }
        }

        @Override
        public void visit(FieldsDeclaration node) {
            super.visit(node);
            if (phpClass == null) {
                return;
            }
            // get fields
            List<SingleFieldDeclaration> fields = node.getFields();
            for (SingleFieldDeclaration field : fields) {
                // check filed name
                String fieldName = CodeUtils.extractVariableName(field.getName());
                if (FILE_TYPES.get(fieldName) == null || !containsFieldNames(fieldName, getFieldNames())) {
                    continue;
                }

                // get ArrayCreation
                ArrayCreation arrayCreation = CakePhpCodeUtils.getArrayCreation(field);
                if (arrayCreation == null) {
                    continue;
                }

                // set classes
                setFieldClasses(arrayCreation, fieldName);
            }
        }

        private String getClassName(StaticMethodInvocation node) {
            Expression className = node.getClassName();
            if (className instanceof NamespaceName) {
                return CodeUtils.extractQualifiedName((NamespaceName) className);
            }
            return null;
        }

        /**
         * Check whether field name is specified name.
         *
         * @param field
         * @param name
         * @return
         */
        private boolean containsFieldNames(String fieldName, Set<String> names) {
            if (fieldName != null && names.contains(fieldName)) {
                return true;
            }
            return false;
        }

        private Set<DIR_TYPE> getDirTypes(boolean isPlugin) {
            Set<DIR_TYPE> dirTypes;
            if (isPlugin) {
                dirTypes = EnumSet.of(DIR_TYPE.APP_PLUGIN, DIR_TYPE.PLUGIN);
            } else {
                dirTypes = EnumSet.of(DIR_TYPE.APP);
            }
            return dirTypes;
        }

        private void setFieldClasses(ArrayCreation arrayCreation, String fieldName) {
            CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
            List<ArrayElement> elements = arrayCreation.getElements();
            for (ArrayElement element : elements) {
                String aliasName = null;
                Expression entity = null;
                Expression value = element.getValue();
                if (value == null) {
                    continue;
                }
                if (value instanceof ArrayCreation) {
                    ArrayCreation className = (ArrayCreation) value;
                    entity = CakePhpCodeUtils.getEntity(className);
                }
                if (entity != null) {
                    value = entity;
                    aliasName = CakePhpCodeUtils.getStringValue(element.getKey());
                }
                // get entity name
                String entityName = CakePhpCodeUtils.getStringValue(value);

                // add field to PhpClass
                addField(entityName, fieldName, module, aliasName);
            }
        }

        private FileObject getEntityFile(boolean isPlugin, CakePhpModule module, FILE_TYPE fileType, String entityName, String pluginName) {
            FileObject object = null;
            Set<DIR_TYPE> dirTypes = getDirTypes(isPlugin);
            for (DIR_TYPE dirType : dirTypes) {
                object = module.getFile(dirType, fileType, entityName, pluginName);
                if (object != null) {
                    break;
                }
            }
            return object;
        }

        private void addField(String entityName, String fieldName, CakePhpModule module, String aliasName) {
            // check app or plugin
            boolean isPlugin = false;
            String pluginName = null;
            int dotPosition = entityName.indexOf("."); // NOI18N
            if (dotPosition > 0) {
                isPlugin = true;
                pluginName = entityName.substring(0, dotPosition);
                entityName = entityName.substring(dotPosition + 1);
            }

            //get entity file
            FILE_TYPE fileType = FILE_TYPES.get(fieldName);
            FileObject entityFile = getEntityFile(isPlugin, module, fileType, entityName, pluginName);
            if (entityFile == null) {
                return;
            }

            // add field
            addField(entityName, aliasName, entityFile);
        }

        private void addField(String entityName, String aliasName, FileObject entityFile) {
            String entityClassName = getFullyQualifiedClassName(entityFile);
            synchronized (phpClass) {
                if (aliasName == null) {
                    phpClass.addField(entityName, new PhpClass(entityName, entityClassName), entityFile, 0);
                } else {
                    phpClass.addField(aliasName, new PhpClass(entityName, entityClassName), entityFile, 0);
                }
            }
        }
    }

    private final class CakePhpComponentVisitor extends CakePhpFieldsVisitor {

        public CakePhpComponentVisitor(FileObject fo, PhpClass phpClass) {
            super(fo, phpClass);
        }

        @Override
        public Set<String> getFieldNames() {
            return Collections.singleton(COMPONENTS);
        }
    }

    private final class CakePhpHelperVisitor extends CakePhpFieldsVisitor {

        public CakePhpHelperVisitor(FileObject targetFile, PhpClass phpClass) {
            super(targetFile, phpClass);
        }

        @Override
        public Set<String> getFieldNames() {
            return Collections.singleton(HELPERS);
        }
    }
}
