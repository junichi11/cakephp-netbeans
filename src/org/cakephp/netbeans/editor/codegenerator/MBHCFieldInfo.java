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
package org.cakephp.netbeans.editor.codegenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.JTextComponent;
import static org.cakephp.netbeans.editor.codegenerator.FieldInfo.Type.ACTS_AS;
import static org.cakephp.netbeans.editor.codegenerator.FieldInfo.Type.COMPONENTS;
import static org.cakephp.netbeans.editor.codegenerator.FieldInfo.Type.HELPERS;
import static org.cakephp.netbeans.editor.codegenerator.FieldInfo.Type.USES;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.util.CakePhpCodeUtils;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FieldsDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public class MBHCFieldInfo extends FieldInfo {

    protected MBHCFieldInfo(FieldInfo.Type type, JTextComponent textComponent) {
        super(type, textComponent);
    }
    private static final String CONTENTS = "${contents}"; // NOI18N
    private static final String ARRAY_FIELD_TEMPLATE = "public $%s = array(${contents});${cursor}"; // NOI18N
    private static final String COMPONENTS_TEMPLATE = String.format(ARRAY_FIELD_TEMPLATE, COMPONENTS_NAME);
    private static final String HELPERS_TEMPLATE = String.format(ARRAY_FIELD_TEMPLATE, HELPERS_NAME);
    private static final String USES_TEMPLATE = String.format(ARRAY_FIELD_TEMPLATE, USES_NAME);
    private static final String ACTSAS_TEMPLATE = String.format(ARRAY_FIELD_TEMPLATE, ACTS_AS_NAME);
    // ignore list
    private static final String[] FILTERS = {
        "JqueryEngine", // NOI18N
        "PrototypeEngine", // NOI18N
        "MootoolsEngine", // NOI18N
        "JsBaseEngine", // NOI18N
        "App", // NOI18N
        "AppModel" // NOI18N
    };

    /**
     * Get string for insert.
     *
     * @return
     */
    @Override
    public String getInsertString() {
        StringBuilder sb = new StringBuilder();
        for (Property property : getPossibleProperties()) {
            if (property.isSelected()) {
                sb.append(", '").append(property.getName()).append("'"); // NOI18N
            }
        }
        String insertString = sb.toString();
        if (isExist) {
            return insertString;
        }
        insertString = insertString.replaceFirst(", ", ""); // NOI18N
        if (insertString == null || insertString.isEmpty()) {
            return ""; // NOI18N
        }

        switch (type) {
            case COMPONENTS:
                insertString = COMPONENTS_TEMPLATE.replace(CONTENTS, insertString);
                break;
            case HELPERS:
                insertString = HELPERS_TEMPLATE.replace(CONTENTS, insertString);
                break;
            case USES:
                insertString = USES_TEMPLATE.replace(CONTENTS, insertString);
                break;
            case ACTS_AS:
                insertString = ACTSAS_TEMPLATE.replace(CONTENTS, insertString);
                break;
            default:
                break;
        }

        return insertString;
    }

    @Override
    protected void parse(PHPParseResult result) {
        // scan document
        final MBHCFileldsVisitor visitor = new MBHCFileldsVisitor();
        visitor.scan(Utils.getRoot(result));
        Map<Type, Integer> insertOffsetMap = visitor.getInsertOffsetMap();
        List<String> existings = visitor.getExistings(type);
        List<String> allProperties = getAllCommonNames(type);

        // add properties
        for (String property : allProperties) {
            if (existings.contains(property) || Arrays.asList(FILTERS).contains(property)) {
                continue;
            }
            getPossibleProperties().add(new Property(property, 0, type.toString()));
            Integer offset = insertOffsetMap.get(type);
            if (offset != null) {
                setInsertOffset(offset);
                isExist = true;
            }
        }
    }

    /**
     * Update insert position
     */
    @Override
    public void updateInsertPosition() {
        try {
            ParserManager.parse(Collections.singleton(Source.create(textComponent.getDocument())), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    PHPParseResult info = (PHPParseResult) resultIterator.getParserResult();
                    if (info != null) {
                        final MBHCFileldsVisitor visitor = new MBHCFileldsVisitor();
                        visitor.scan(Utils.getRoot(info));
                        Map<Type, Integer> insertOffsetMap = visitor.getInsertOffsetMap();
                        Integer offset = insertOffsetMap.get(type);
                        if (offset != null) {
                            setInsertOffset(offset);
                            isExist = true;
                        }
                    }
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Get all common names for specified type.
     *
     * @param type Type
     * @return common names
     */
    private List<String> getAllCommonNames(Type type) {
        FILE_TYPE fileType = toFileType(type);
        if (fileType == FILE_TYPE.NONE) {
            return Collections.emptyList();
        }

        PhpModule phpModule = getPhpModule();
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module == null) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>();
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        FileObject appDir = module.getDirectory(DIR_TYPE.APP, fileType, null);
        list.addAll(getCommonNames(appDir, editorSupport, null));
        if (fileType != FILE_TYPE.MODEL) {
            FileObject coreDir = module.getDirectory(DIR_TYPE.CORE, fileType, null);
            list.addAll(getCommonNames(coreDir, editorSupport, null));
            for (DIR_TYPE dirType : Arrays.asList(DIR_TYPE.APP_PLUGIN, DIR_TYPE.PLUGIN)) {
                FileObject appPlugin = module.getDirectory(dirType);
                if (appPlugin == null) {
                    continue;
                }
                for (FileObject child : appPlugin.getChildren()) {
                    if (!child.isFolder()) {
                        continue;
                    }
                    FileObject appPluginObject = module.getDirectory(dirType, fileType, child.getName());
                    if (appPluginObject != null) {
                        list.addAll(getCommonNames(appPluginObject, editorSupport, child.getName()));
                    }
                }
            }
        }
        return list;
    }

    /**
     * Convert to FILE_TYPE from Type.
     *
     * @param type Type
     * @return FILE_TYPE if exists it for Type, otherwise throw AssertionError.
     */
    private FILE_TYPE toFileType(Type type) {
        FILE_TYPE fileType = FILE_TYPE.NONE;
        switch (type) {
            case COMPONENTS:
                fileType = FILE_TYPE.COMPONENT;
                break;
            case HELPERS:
                fileType = FILE_TYPE.HELPER;
                break;
            case USES:
                fileType = FILE_TYPE.MODEL;
                break;
            case ACTS_AS:
                fileType = FILE_TYPE.BEHAVIOR;
                break;
            default:
                throw new AssertionError();
        }
        return fileType;
    }

    /**
     * Get common names.
     *
     * @param targetDirectory FileObject (directory)
     * @param editorSupport EditorSupport
     * @param plugin plugin name: e.g. DebugKit
     * @return common names
     */
    private List<String> getCommonNames(FileObject targetDirectory, EditorSupport editorSupport, String plugin) {
        List<String> list = new LinkedList<>();
        if (targetDirectory.isFolder()) {
            for (FileObject child : targetDirectory.getChildren()) {
                if (child.isFolder()) {
                    continue;
                }
                String name = getCommonName(child, editorSupport);
                if (plugin != null && !plugin.isEmpty()) {
                    name = plugin + "." + name; // NOI18N
                }
                if (!StringUtils.isEmpty(name)) {
                    list.add(name);
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    /**
     * Get common name(for example:Html(HtmlHelper), Session(SessionComponent)).
     *
     * @param target FileObject (file)
     * @param editorSupport EditorSupport
     * @return common name
     */
    private String getCommonName(FileObject target, EditorSupport editorSupport) {
        String name = null;
        for (PhpClass phpClass : editorSupport.getClasses(target)) {
            name = phpClass.getName();
            break;
        }
        if (name == null) {
            return null;
        }
        if (CakePhpUtils.isComponent(target)) {
            name = name.replace(CakePhpModule.FILE_TYPE.COMPONENT.toString(), ""); // NOI18N
        } else if (CakePhpUtils.isHelper(target)) {
            name = name.replace(CakePhpModule.FILE_TYPE.HELPER.toString(), ""); // NOI18N
        } else if (CakePhpUtils.isBehavior(target)) {
            name = name.replace(CakePhpModule.FILE_TYPE.BEHAVIOR.toString(), ""); // NOI18N
        }
        return name;
    }

    //~ inner class
    private static final class MBHCFileldsVisitor extends DefaultVisitor {

        private final List<String> existingComponents = new ArrayList<>();
        private final List<String> existingHelpers = new ArrayList<>();
        private final List<String> existingUses = new ArrayList<>();
        private final List<String> existingActsAs = new ArrayList<>();
        private final Map<Type, Integer> insertOffsetMap = new EnumMap<>(Type.class);

        public MBHCFileldsVisitor() {
        }

        @Override
        public void visit(FieldsDeclaration node) {
            List<SingleFieldDeclaration> fields = node.getFields();
            for (SingleFieldDeclaration field : fields) {
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
                String valueName;
                for (ArrayElement element : arrayCreation.getElements()) {
                    Expression key = element.getKey();
                    if (key != null) {
                        valueName = CakePhpCodeUtils.getStringValue(key);
                    } else {
                        valueName = CakePhpCodeUtils.getStringValue(element.getValue());
                    }
                    if (name != null) {
                        switch (name) {
                            case "$components": // NOI18N
                                existingComponents.add(valueName);
                                insertOffsetMap.put(Type.COMPONENTS, field.getEndOffset() - 1);
                                break;
                            case "$helpers": // NOI18N
                                existingHelpers.add(valueName);
                                insertOffsetMap.put(Type.HELPERS, field.getEndOffset() - 1);
                                break;
                            case "$uses": // NOI18N
                                existingUses.add(valueName);
                                insertOffsetMap.put(Type.USES, field.getEndOffset() - 1);
                                break;
                            case "$actsAs": // NOI18N
                                existingActsAs.add(valueName);
                                insertOffsetMap.put(Type.ACTS_AS, field.getEndOffset() - 1);
                                break;
                            default:
                        }
                    }
                }
            }
        }

        private List<String> getExistings(Type type) {
            switch (type) {
                case COMPONENTS:
                    return existingComponents;
                case HELPERS:
                    return existingHelpers;
                case USES:
                    return existingUses;
                case ACTS_AS:
                    return existingActsAs;
                default:
                    throw new AssertionError();
            }
        }

        private Map<Type, Integer> getInsertOffsetMap() {
            return insertOffsetMap;
        }
    }
}
