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
package org.cakephp.netbeans.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.lexer.TokenUtilities;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.netbeans.modules.editor.lib2.DocUtils;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public class CakePhpDocUtils {

    private static final Logger LOGGER = Logger.getLogger(CakePhpDocUtils.class.getName());

    private CakePhpDocUtils() {
    }

    public static int getLineIndex(Document doc, int position) {
        Element map = doc.getDefaultRootElement();
        return map.getElementIndex(position);
    }

    public static int getLineStartOffset(Document doc, int line) {
        Element element = getElement(doc, line);
        return element.getStartOffset();
    }

    public static int getLineEndOffset(Document doc, int line) {
        Element element = getElement(doc, line);
        return element.getEndOffset();
    }

    public static Element getElement(Document doc, int line) {
        Element map = doc.getDefaultRootElement();
        return map.getElement(line);
    }

    /**
     * Reformat with NetBeans format settings
     *
     * @param doc
     */
    public static void reformat(Document doc) {
        reformat(doc, 0, doc.getLength());
    }

    /**
     * Reformat with NetBeans format settings
     *
     * @param doc
     */
    public static void reformat(Document doc, final int start, final int end) {
        final BaseDocument baseDoc = (BaseDocument) doc;
        final Reformat reformat = Reformat.get(baseDoc);
        reformat.lock();
        try {
            baseDoc.runAtomic(new Runnable() {
                @Override
                public void run() {
                    try {
                        reformat.reformat(start, end);
                    } catch (BadLocationException ex) {
                        LOGGER.log(Level.WARNING, null, ex);
                    }
                }
            });
        } finally {
            reformat.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public static TokenSequence<PHPTokenId> getTokenSequence(Document doc, int offset) {
        DocUtils.atomicLock(doc);
        TokenSequence<PHPTokenId> tokenSequence = null;
        try {
            TokenHierarchy<Document> hierarchy = TokenHierarchy.get(doc);
            tokenSequence = hierarchy.tokenSequence(PHPTokenId.language());
        } finally {
            DocUtils.atomicUnlock(doc);
        }
        if (tokenSequence != null) {
            tokenSequence.move(offset);
            tokenSequence.moveNext();
        }
        return tokenSequence;

    }

    @SuppressWarnings("unchecked")
    public static String getContentsOfForwardArray(Document doc, int offset) {
        TokenSequence<PHPTokenId> tokenSequence = getTokenSequence(doc, offset);
        StringBuilder sb = new StringBuilder();
        boolean isArray = false;
        while (tokenSequence.moveNext()) {
            Token<PHPTokenId> token = tokenSequence.token();
            CharSequence text = token.text();
            if (TokenUtilities.endsWith(text, ";")) { // NOI18N
                isArray = false;
                break;
            }
            if (isArray) {
                if (TokenUtilities.equals(text, "(") || TokenUtilities.equals(text, ")")) { // NOI18N
                    continue;
                }
                sb.append(text);
                continue;
            }
            if (TokenUtilities.equals(text, "array")) { // NOI18N
                isArray = true;
            }
        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static int getEndStatementOffset(Document doc, int offset) {
        TokenSequence<PHPTokenId> tokenSequence = getTokenSequence(doc, offset);
        while (tokenSequence.moveNext()) {
            Token<PHPTokenId> token = tokenSequence.token();
            CharSequence text = token.text();
            if (TokenUtilities.equals(text, ";")) { // NOI18N
                return tokenSequence.offset();
            }
        }
        return -1;
    }

    /**
     * Get class name.
     *
     * @param document Document
     * @return class name
     */
    public static String getClassName(Document document) {
        String className = null;
        if (document == null) {
            return className;
        }
        FileObject fileObject = Source.create(document).getFileObject();
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : editorSupport.getClasses(fileObject)) {
            className = phpClass.getName();
        }
        return className;
    }

    /**
     * Get FileObject.
     *
     * @param document
     * @return FileObject
     */
    public static FileObject getFileObject(Document document) {
        Source source = Source.create(document);
        return source.getFileObject();
    }
}
