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

import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.cakephp.netbeans.module.CakePhpModule;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = "text/x-php5", service = CompletionProvider.class)
public class ElementCompletionProvider extends CakePhpCompletionProvider {

    private static final String ELEMENT = "element"; // NOI18N

    @Override
    public CompletionTask createTask(int queryType, JTextComponent jtc, PhpModule phpModule) {

        final CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);

        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @Override
            @SuppressWarnings("unchecked")
            protected void query(CompletionResultSet completionResultSet, Document doc, int caretOffset) {
                // check $this->element()
                AbstractDocument ad = (AbstractDocument) doc;
                ad.readLock();
                try {
                    TokenHierarchy hierarchy = TokenHierarchy.get(doc);
                    TokenSequence<PHPTokenId> ts = hierarchy.tokenSequence(PHPTokenId.language());
                    ts.move(caretOffset);
                    ts.moveNext();
                    Token<PHPTokenId> token = ts.token();
                    if (token.id() != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                        completionResultSet.finish();
                        return;
                    }
                    String caretInput = ts.token().text().toString();

                    int startOffset = ts.offset() + 1;
                    int removeLength = caretInput.length() - 2;
                    if (removeLength < 0) {
                        removeLength = 0;
                    }
                    // brace?
                    ts.movePrevious();
                    // element?
                    ts.movePrevious();
                    String imageMethod = ts.token().text().toString();
                    if (!imageMethod.equals(ELEMENT) || ts.token().id() != PHPTokenId.PHP_STRING) {
                        completionResultSet.finish();
                        return;
                    }

                    String filter = caretInput.substring(1, caretInput.length() - 1);

                    // get webroot/img files
                    // to a CompletionResultSet
                    FileObject view = cakeModule.getViewDirectory(CakePhpModule.DIR_TYPE.APP);
                    if (view == null) {
                        completionResultSet.finish();
                        return;
                    }
                    FileObject elementDirectory = view.getFileObject("Elements"); // NOI18N
                    if (elementDirectory == null) {
                        completionResultSet.finish();
                        return;
                    }
                    FileObject[] elements = elementDirectory.getChildren();
                    for (int i = 0; i < elements.length; i++) {
                        final FileObject element = elements[i];
                        String ext = element.getExt();
                        if (ext.isEmpty()) {
                            continue;
                        }
                        final String elementName = element.getName();
                        if (!elementName.isEmpty() // NOI18N
                            && !element.isFolder()
                            && elementName.startsWith(filter)) {
                            completionResultSet.addItem(new CakePhpCompletionItem(elementName, startOffset, removeLength));
                        }
                    }
                } finally {
                    ad.readUnlock();
                }

                completionResultSet.finish();
            }
        }, jtc);

    }
}
