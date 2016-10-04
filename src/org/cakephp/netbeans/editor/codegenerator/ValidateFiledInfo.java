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
import java.util.List;
import javax.swing.text.JTextComponent;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpType.Method;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 * Support for only validations of "CORE"
 *
 * @author junichi11
 */
public class ValidateFiledInfo extends FieldInfo {

    private static final String VALIDATION_TEMPLATE = "'${name}' => array(\n" // NOI18N
            + "'rule' => '${name}',\n" // NOI18N
            + "'message' => '${${name}_message}'\n" // NOI18N
            + "),\n"; // NOI18N
    private static final String VALIDATION_TEMPLATE_ARG1 = "'${name}' => array(\n" // NOI18N
            + "'rule' => array('${name}', ${${name}_arg1}),\n" // NOI18N
            + "'message' => '${${name}_message}'\n" // NOI18N
            + "),\n"; // NOI18N
    private static final String VALIDATION_TEMPLATE_ARG2 = "'${name}' => array(\n" // NOI18N
            + "'rule' => array('${name}', ${${name}_arg1}, ${${name}_arg2}),\n" // NOI18N
            + "'message' => '${${name}_message}'\n" // NOI18N
            + "),\n"; // NOI18N
    private static final String NAME_PLACE = "${name}"; // NOI18N
    private static final String CURSOR = "${cursor}"; // NOI18N
    private static final List<String> HAS1ARGS = Arrays.asList("minLength", "maxLength", "equalTo"); // NOI18N
    private static final List<String> HAS2ARGS = Arrays.asList("between"); // NOI18N

    protected ValidateFiledInfo(Type type, JTextComponent textComponent) {
        super(type, textComponent);
    }

    @Override
    public String getInsertString() {
        StringBuilder sb = new StringBuilder();
        for (Property property : getPossibleProperties()) {
            if (property.isSelected()) {
                String name = property.getName();
                String template;
                if (HAS1ARGS.contains(name)) {
                    template = VALIDATION_TEMPLATE_ARG1.replace(NAME_PLACE, name);
                } else if (HAS2ARGS.contains(name)) {
                    template = VALIDATION_TEMPLATE_ARG2.replace(NAME_PLACE, name);
                } else {
                    template = VALIDATION_TEMPLATE.replace(NAME_PLACE, name);
                }
                sb.append(template);
            }
        }
        sb.append(CURSOR);
        return sb.toString();
    }

    @Override
    protected void parse(PHPParseResult result) {
        // add property
        for (String validation : getAllValidations()) {
            getPossibleProperties().add(new Property(validation, 0, type.toString()));
        }
    }

    @Override
    public void updateInsertPosition() {
    }

    @Override
    protected void setDefaultOffset() {
        insertOffset = textComponent.getCaretPosition();
    }

    private List<String> getAllValidations() {
        List<String> validations = new ArrayList<>();
        PhpModule phpModule = getPhpModule();
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return validations;
        }
        FileObject coreDirectory = cakeModule.getDirectory(DIR_TYPE.CORE);
        if (coreDirectory == null) {
            return validations;
        }

        // get validation method name in Core
        FileObject validation = coreDirectory.getFileObject("Utility/Validation.php"); // NOI18N
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : editorSupport.getClasses(validation)) {
            for (Method method : phpClass.getMethods()) {
                String name = method.getName();
                if (!name.startsWith("_")) {
                    validations.add(name);
                }
            }
            break;
        }

        // isUnique method exists in Model class
        validations.add("isUnique"); // NOI18N
        Collections.sort(validations);
        return validations;
    }
}
