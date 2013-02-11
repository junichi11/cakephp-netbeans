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
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = "text/x-php5", service = HyperlinkProvider.class)
public class CakePhpHyperlinkProvider implements HyperlinkProvider {

    private static final String ELEMENTS_DIR_NAME = "Elements";
    private FileObject element;
    private String target;
    private int targetStart;
    private int targetEnd;

    @Override
    public boolean isHyperlinkPoint(Document doc, int offset) {
        return verifyState(doc, offset);
    }

    @SuppressWarnings("unchecked")
    public boolean verifyState(Document doc, int offset) {
        AbstractDocument ad = (AbstractDocument) doc;
        ad.readLock();
        try {
            TokenHierarchy hierarchy = TokenHierarchy.get(doc);
            TokenSequence<PHPTokenId> ts = hierarchy.tokenSequence(PHPTokenId.language());
            if (ts == null) {
                return false;
            }

            ts.move(offset);
            ts.moveNext();
            Token<PHPTokenId> token = ts.token();
            target = token.text().toString();
            PHPTokenId id = token.id();
            int newOffset = ts.offset();

            if (!isElement(ts)) {
                return false;
            }

            if (id == PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                target = token.text().toString();
                if (target.length() > 2) {
                    target = target.substring(1, target.length() - 1);
                } else {
                    target = ""; // NOI18N
                    return false;
                }
                PhpModule pm = PhpModule.inferPhpModule();
                CakePhpModule module = CakePhpModule.forPhpModule(pm);
                element = module.getViewFile(DIR_TYPE.APP, ELEMENTS_DIR_NAME, target);
                if (element == null) {
                    element = module.getViewFile(DIR_TYPE.CORE, ELEMENTS_DIR_NAME, target);
                }
                if (element != null) {
                    targetStart = newOffset + 1;
                    targetEnd = targetStart + target.length();
                    return true;
                }
            }
        } finally {
            ad.readUnlock();
        }

        return false;
    }

    @Override
    public int[] getHyperlinkSpan(Document doc, int offset) {
        if (element != null) {
            return new int[]{targetStart, targetEnd};
        } else {
            return null;
        }
    }

    @Override
    public void performClickAction(Document doc, int offset) {
        if (element != null) {
            UiUtils.open(element, 0);
        }
    }

    /**
     * Check element method
     *
     * @param ts
     * @return boolean
     */
    private boolean isElement(TokenSequence<PHPTokenId> ts) {
        ts.movePrevious();
        ts.movePrevious();
        Token<PHPTokenId> method = ts.token();
        String methodName = method.text().toString();
        PHPTokenId methodId = method.id();
        if (!methodName.equals("element") || methodId != PHPTokenId.PHP_STRING) { // NOI18N
            return false;
        }
        return true;
    }
}
