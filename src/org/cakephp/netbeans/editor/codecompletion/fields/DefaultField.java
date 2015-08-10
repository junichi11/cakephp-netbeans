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
package org.cakephp.netbeans.editor.codecompletion.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.text.Document;
import org.cakephp.netbeans.editor.codecompletion.CakePhpCompletionItem;
import org.cakephp.netbeans.util.CakePhpDocUtils;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.filesystems.FileObject;

public class DefaultField extends FieldImpl {

    public DefaultField(String name, Document doc, int offset, PhpModule phpModule) {
        super(name, doc, offset, phpModule);
    }

    @Override
    protected List<CompletionItem> getCompletionItems() {
        FileObject fileObject = NbEditorUtilities.getFileObject(getDocument());
        if (fileObject == null) {
            return Collections.emptyList();
        }

        List<String> fieldList = Collections.emptyList();
        if (CakePhpUtils.isController(fileObject)) {
            fieldList = Arrays.asList(Field.USES, Field.HELPERS, Field.COMPONENTS);
        } else if (CakePhpUtils.isModel(fileObject)) {
            fieldList = Arrays.asList(Field.USES, Field.ACTS_AS, Field.VALIDATE);
        } else if (CakePhpUtils.isComponent(fileObject)) {
            fieldList = Arrays.asList(Field.COMPONENTS);
        } else if (CakePhpUtils.isHelper(fileObject)) {
            fieldList = Arrays.asList(Field.VALIDATE);
        }

        ArrayList<CompletionItem> items = new ArrayList<>();
        TokenSequence<PHPTokenId> ts = CakePhpDocUtils.getTokenSequence(getDocument(), getOffset());
        ts.movePrevious();
        Token<PHPTokenId> token = ts.token();
        TokenId id = token.id();
        String caretPosigionText = ""; // NOI18N
        int insertStart = getOffset();
        if (id != PHPTokenId.PHP_PUBLIC) {
            insertStart = ts.offset();
            caretPosigionText = token.text().toString();
        }
        for (String field : fieldList) {
            if (field.startsWith(caretPosigionText)) {
                items.add(new CakePhpCompletionItem(field, insertStart, caretPosigionText.length()));
            }
        }

        return items;
    }

}
