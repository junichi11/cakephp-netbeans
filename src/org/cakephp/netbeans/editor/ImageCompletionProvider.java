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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.util.CakePhpDocUtils;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
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
public class ImageCompletionProvider extends CakePhpCompletionProvider {

    @Override
    public CompletionTask createTask(int queryType, JTextComponent jtc, PhpModule phpModule) {
        final CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return new AsyncCompletionTask(new AsyncCompletionQueryImpl(cakeModule), jtc);
    }

    class AsyncCompletionQueryImpl extends AsyncCompletionQuery {

        private final CakePhpModule cakeModule;
        int startOffset;
        int removeLength;
        private String filter;
        private String directoryPath;
        private final List<String> imgExts = Arrays.asList("jpeg", "jpg", "png", "gif", "bmp", "ico"); // NOI18N
        private static final String SLASH = "/"; // NOI18N

        public AsyncCompletionQueryImpl(CakePhpModule cakeModule) {
            this.cakeModule = cakeModule;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void query(CompletionResultSet completionResultSet, Document doc, int caretOffset) {
            try {
                TokenSequence<PHPTokenId> ts = CakePhpDocUtils.getTokenSequence(doc, caretOffset);
                Token<PHPTokenId> token = ts.token();
                // check string ('' or "")
                if (token.id() != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                    return;
                }

                // set start offset and remove length
                String caretInput = ts.token().text().toString();
                setStartAndRemoveLength(ts, caretInput);

                // check $this->Html->image()
                int endIndex = caretOffset - startOffset + 1;
                if (!isSpecifiedMethod(ts, "image") || endIndex < 1) { // NOI18N
                    return;
                }

                // init
                filter = caretInput.substring(1, endIndex);
                directoryPath = ""; // NOI18N

                // get target directory
                FileObject targetDirectory = getTargetDirectory("img"); // NOI18N
                if (targetDirectory == null) {
                    return;
                }

                // exist subdirectory
                targetDirectory = getSubDirectory(targetDirectory);
                if (targetDirectory == null) {
                    return;
                }

                FileObject[] targets = targetDirectory.getChildren();
                for (FileObject target : targets) {
                    String targetFileName = target.getNameExt();
                    if (target.isFolder()) {
                        targetFileName = targetFileName + SLASH;
                    }
                    if (filter.startsWith(SLASH)) {
                        filter = filter.replaceFirst(SLASH, ""); // NOI18N
                        directoryPath = SLASH + directoryPath;
                    }
                    if (!targetFileName.isEmpty()
                            && targetFileName.startsWith(filter)) {
                        if (target.isFolder() || imgExts.contains(target.getExt().toLowerCase(Locale.ENGLISH))) {
                            completionResultSet.addItem(new ImageCompletionItem(directoryPath + targetFileName, startOffset, removeLength, target));
                        }
                    }
                }
            } finally {
                completionResultSet.finish();
            }
        }

        private void setStartAndRemoveLength(TokenSequence<PHPTokenId> ts, String caretInput) {
            startOffset = ts.offset() + 1;
            removeLength = caretInput.length() - 2;
            if (removeLength < 0) {
                removeLength = 0;
            }
        }

        private FileObject getSubDirectory(FileObject targetDirectory) {
            int lastIndexOfSlash = filter.lastIndexOf(SLASH);
            if (lastIndexOfSlash > 0) {
                directoryPath = filter.substring(0, lastIndexOfSlash + 1);
                filter = filter.substring(lastIndexOfSlash + 1);
                return targetDirectory.getFileObject(directoryPath);
            }
            return targetDirectory;
        }

        private boolean isSpecifiedMethod(TokenSequence<PHPTokenId> ts, String methodName) {
            while (ts.movePrevious()) {
                Token<PHPTokenId> token = ts.token();
                String text = token.text().toString();
                if (text.equals(methodName) && token.id() == PHPTokenId.PHP_STRING) {
                    return true;
                }
                if (ts.token().id() == PHPTokenId.PHP_SEMICOLON) {
                    break;
                }
            }
            return false;
        }

        /**
         * Get img directory.
         *
         * @param target
         * @return img directory webroot if directory name starts with "/",
         * otherwise webroot/img
         */
        private FileObject getTargetDirectory(String target) {
            FileObject webroot = cakeModule.getWebrootDirectory(CakePhpModule.DIR_TYPE.APP);
            if (webroot == null) {
                return null;
            }
            FileObject imgDirectory = null;
            if (filter.startsWith(SLASH)) {
                imgDirectory = webroot;
            } else {
                imgDirectory = webroot.getFileObject(target);
            }
            return imgDirectory;
        }
    }
}
