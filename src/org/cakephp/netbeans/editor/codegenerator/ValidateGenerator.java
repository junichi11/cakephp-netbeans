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

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.cakephp.netbeans.editor.codegenerator.FieldInfo.FieldInfoFactory;
import org.cakephp.netbeans.editor.codegenerator.FieldInfo.Type;
import org.cakephp.netbeans.editor.codegenerator.ui.FieldsGeneratorPanel;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.util.CakePhpDocUtils;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpType.Field;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.netbeans.spi.editor.codegen.CodeGeneratorContextProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class ValidateGenerator implements CodeGenerator {

    JTextComponent textComp;
    private final ArrayList<FieldInfo> fields;

    /**
     *
     * @param context containing JTextComponent and possibly other items
     * registered by {@link CodeGeneratorContextProvider}
     */
    private ValidateGenerator(Lookup context) {
        this(context, Collections.<Type>emptyList());
    }

    private ValidateGenerator(Lookup context, List<Type> types) {
        textComp = context.lookup(JTextComponent.class);
        // create FiledInfo
        fields = new ArrayList<>();
        for (Type type : types) {
            fields.add(FieldInfoFactory.create(type, textComp));
        }
    }

    /**
     * The name which will be inserted inside Insert Code dialog
     */
    @NbBundle.Messages("LBL_ValidationGenerator_Name=Validations...")
    @Override
    public String getDisplayName() {
        return Bundle.LBL_ValidationGenerator_Name();
    }

    /**
     * This will be invoked when user chooses this Generator from Insert Code
     * dialog
     */
    @Override
    public void invoke() {
        Document doc = textComp.getDocument();
        FileObject fo = CakePhpDocUtils.getFileObject(doc);
        Project project = FileOwnerQuery.getOwner(fo);
        if (project == null) {
            return;
        }

        // create dialog
        FieldsGeneratorPanel panel = new FieldsGeneratorPanel(fo, fields);
        DialogDescriptor descriptor = new DialogDescriptor(panel, "Generate validations"); // NOI18N
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.setVisible(true);
        dialog.dispose();
        if (descriptor.getValue() == DialogDescriptor.OK_OPTION) {
            for (final FieldInfo field : fields) {
                String inserString = field.getInsertString();
                if (inserString != null && !inserString.isEmpty() && !inserString.equals("${cursor}")) { // NOI18N
                    // insert code with CodeTemplateManager
                    CodeTemplateManager templateManager = CodeTemplateManager.get(doc);
                    CodeTemplate codeTemplate = templateManager.createTemporary(inserString);
                    codeTemplate.insert(textComp);
                }
            }
        }
    }

    @MimeRegistration(mimeType = "text/x-php5", service = CodeGenerator.Factory.class)
    public static class Factory implements CodeGenerator.Factory {

        @Override
        public List<? extends CodeGenerator> create(Lookup context) {
            JTextComponent textComponent = context.lookup(JTextComponent.class);
            FileObject fileObject = CakePhpDocUtils.getFileObject(textComponent.getDocument());
            PhpModule phpModule = PhpModule.Factory.forFileObject(fileObject);

            // check whether caret position is in validate array
            // XXX : check more exactly
            EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
            int validateStartOffset = 0;
            int validateEndOffset = 0;
            for (PhpClass phpClass : editorSupport.getClasses(fileObject)) {
                for (Field field : phpClass.getFields()) {
                    if (field.getName().equals("$validate")) { // NOI18N
                        validateStartOffset = field.getOffset();
                        validateEndOffset = CakePhpDocUtils.getEndStatementOffset(textComponent.getDocument(), validateStartOffset);
                    }
                }
                break;
            }
            int caretOffset = textComponent.getCaretPosition();
            if (validateStartOffset < caretOffset && caretOffset < validateEndOffset) {
                // check whether file is Model
                CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
                if (cakeModule == null) {
                    return Collections.emptyList();
                }
                CakeVersion cakeVersion = cakeModule.getCakeVersion();
                if (cakeVersion == null) {
                    return Collections.emptyList();
                }

                if (CakePhpUtils.isCakePHP(phpModule) && cakeVersion.getMajor() > 1) {
                    if (CakePhpUtils.isModel(fileObject)) {
                        return Collections.singletonList(new ValidateGenerator(context, Collections.singletonList(Type.VALIDATE)));
                    }
                }
            }
            return Collections.emptyList();
        }
    }
}
