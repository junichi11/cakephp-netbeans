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
package org.cakephp.netbeans.editor.visitors;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.util.CakePhpCodeUtils;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FieldsDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.NamespaceName;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.StaticMethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class CakePhpFieldsVisitor extends DefaultVisitor {

    private final PhpClass phpClass;
    protected FileObject targetFile;
    protected PhpModule phpModule;
    private final List<FileObject> models = new ArrayList<>();
    private final List<FileObject> components = new ArrayList<>();
    private final List<FileObject> helpers = new ArrayList<>();
    private final List<FileObject> behaviors = new ArrayList<>();
    private final List<FileObject> fixtures = new ArrayList<>();
    public static final Map<String, FILE_TYPE> FILE_TYPES = new HashMap<>();
    public static final String USES = "$uses"; // NOI18N
    public static final String COMPONENTS = "$components"; // NOI18N
    public static final String HELPERS = "$helpers"; // NOI18N
    public static final String ACTS_AS = "$actsAs"; // NOI18N
    public static final String FIXTURES = "$fixtures"; // NOI18N

    static {
        FILE_TYPES.put(USES, FILE_TYPE.MODEL);
        FILE_TYPES.put(COMPONENTS, FILE_TYPE.COMPONENT);
        FILE_TYPES.put(HELPERS, FILE_TYPE.HELPER);
        FILE_TYPES.put(ACTS_AS, FILE_TYPE.BEHAVIOR);
        FILE_TYPES.put(FIXTURES, FILE_TYPE.FIXTURE);
    }

    public CakePhpFieldsVisitor(FileObject targetFile, PhpClass phpClass) {
        this.phpClass = phpClass;
        this.targetFile = targetFile;
        phpModule = PhpModule.Factory.forFileObject(targetFile);
    }

    public CakePhpFieldsVisitor(FileObject targetFile) {
        this.phpClass = null;
        this.targetFile = targetFile;
        phpModule = PhpModule.Factory.forFileObject(targetFile);
    }

    /**
     * Get class fields for target.
     *
     * @return php class for target
     */
    public PhpClass getPhpClass() {
        PhpClass pc;
        if (phpClass == null) {
            return null;
        }

        synchronized (phpClass) {
            pc = phpClass;
        }
        return pc;
    }

    public List<FileObject> getModels() {
        return models;
    }

    public List<FileObject> getComponents() {
        return components;
    }

    public List<FileObject> getHelpers() {
        return helpers;
    }

    public List<FileObject> getBehaviors() {
        return behaviors;
    }

    public List<FileObject> getFixtures() {
        return fixtures;
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
        if (methodClassName == null || !methodClassName.equals("ClassRegistry")) {// NOI18N
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
        Expression className = node.getDispatcher();
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
        return fieldName != null && names.contains(fieldName);
    }

    private Set<CakePhpModule.DIR_TYPE> getDirTypes(boolean isPlugin) {
        Set<CakePhpModule.DIR_TYPE> dirTypes;
        if (isPlugin) {
            dirTypes = EnumSet.of(CakePhpModule.DIR_TYPE.APP_PLUGIN, CakePhpModule.DIR_TYPE.PLUGIN, CakePhpModule.DIR_TYPE.BASER_PLUGIN);
        } else {
            dirTypes = EnumSet.of(CakePhpModule.DIR_TYPE.APP, CakePhpModule.DIR_TYPE.BASER);
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
        Set<CakePhpModule.DIR_TYPE> dirTypes = getDirTypes(isPlugin);
        for (CakePhpModule.DIR_TYPE dirType : dirTypes) {
            object = module.getFile(dirType, fileType, entityName, pluginName);
            if (object != null) {
                break;
            }
        }
        return object;
    }

    private void addField(String entityName, String fieldName, CakePhpModule module, String aliasName) {
        // in TestCase
        if (CakePhpUtils.isTest(targetFile)) {
            FileObject entityFile = getFixtureFile(entityName, fieldName);
            if (entityFile != null) {
                addFile(entityFile, FILE_TYPE.FIXTURE);
            }
            return;
        }

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

        // add file
        addFile(entityFile, fileType);

        // add field
        if (phpClass != null) {
            addField(entityName, aliasName, entityFile);
        }
    }

    private FileObject getFixtureFile(String entityName, String fieldName) {
        if (!FIXTURES.equals(fieldName)) {
            return null;
        }
        String fixtureName = ""; // NOI18N
        String pluginName = null;
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return null;
        }

        DIR_TYPE dirType = null;
        if (entityName.startsWith("app.")) { // NOI18N
            // app
            fixtureName = entityName.replace("app.", ""); // NOI18N
            dirType = DIR_TYPE.APP;
        } else if (entityName.startsWith("plugin.")) { // NOI18N
            // plugin
            fixtureName = entityName.replace("plugin.", ""); // NOI18N
            int indexOfDot = fixtureName.indexOf("."); // NOI18N
            if (indexOfDot != -1) {
                pluginName = fixtureName.substring(0, indexOfDot);
                fixtureName = fixtureName.substring(indexOfDot + 1);
            } else {
                return null;
            }
            pluginName = CakePhpUtils.getCamelCaseName(pluginName);
            dirType = DIR_TYPE.APP_PLUGIN;
        } else if (entityName.startsWith("core.")) { // NOI18N
            // core
            fixtureName = entityName.replace("core.", ""); // NOI18N
            dirType = DIR_TYPE.CORE;
        }

        if (!StringUtils.isEmpty(fixtureName)) {
            if (fixtureName.length() > 1) {
                fixtureName = fixtureName.substring(0, 1).toUpperCase() + fixtureName.substring(1);
            }
        }

        FileObject fixture = null;
        if (!StringUtils.isEmpty(fieldName) && dirType != null) {
            fixture = cakeModule.getFixtureFile(dirType, fixtureName, pluginName);
        }

        if (fixture == null && !StringUtils.isEmpty(pluginName)) {
            fixture = cakeModule.getFixtureFile(DIR_TYPE.PLUGIN, fixtureName, pluginName);
        }
        return fixture;
    }

    public void addFile(FileObject file, FILE_TYPE fileType) {
        if (file == null) {
            return;
        }

        switch (fileType) {
            case MODEL:
                models.add(file);
                break;
            case HELPER:
                helpers.add(file);
                break;
            case COMPONENT:
                components.add(file);
                break;
            case BEHAVIOR:
                behaviors.add(file);
                break;
            case FIXTURE:
                fixtures.add(file);
                break;
            default:
                // do nothing
                break;
        }
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

    private String getFullyQualifiedClassName(FileObject entityFile) {
        String name = ""; // NOI18N
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return name;
        }
        CakeVersion version = cakeModule.getCakeVersion();
        if (version == null) {
            return name;
        }
        int major = version.getMajor();
        if (major >= 3) {
            name = CakePhpUtils.getFullyQualifiedClassName(entityFile);
        } else {
            name = CakePhpUtils.getClassName(entityFile);
        }

        return name;
    }
}
