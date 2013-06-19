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
package org.cakephp.netbeans.ui.actions;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.util.CakePhpDocUtils;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.CakeVersion;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Support for CakePHP 3.x
 *
 * @author junichi11
 */
public class FixNamespaceAction extends BaseAction {

    private static final long serialVersionUID = 3308857827173002135L;
    private boolean hasNamespace;
    private int addingOffset;
    private PhpModule phpModule;
    private static final String NAMESPACE_FORMAT = "namespace %s;\n"; // NOI18N

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @NbBundle.Messages("FixNamespaceAction.Name=Fix namespace")
    @Override
    protected String getPureName() {
        return Bundle.FixNamespaceAction_Name();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        // support for CakePHP3.x
        if (!isCakePHP3(phpModule)) {
            return;
        }
        this.phpModule = phpModule;

        // get editor
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor == null) {
            return;
        }

        Document document = editor.getDocument();
        if (document == null) {
            return;
        }

        // check namespace
        TokenSequence ts = CakePhpDocUtils.getTokenSequence(document, 0);
        hasNamespace = false;
        ts.moveStart();
        while (ts.moveNext()) {
            Token token = ts.token();
            TokenId id = token.id();
            if (id == PHPTokenId.PHP_CLASS) {
                break;
            }
            if (id == PHPTokenId.PHP_NAMESPACE) {
                hasNamespace = true;
                break;
            }
            if (id == PHPTokenId.PHP_OPENTAG) {
                addingOffset = ts.offset() + token.length() + 1;
            }
        }

        if (!hasNamespace) {
            try {
                FileObject fileObject = NbEditorUtilities.getFileObject(document);
                document.insertString(addingOffset, getNamespace(fileObject), null);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }

    /**
     * Get namespace.
     *
     * @param target
     * @return namespace
     */
    private String getNamespace(FileObject target) {
        String namespace = ""; // NOI18N
        if (target.isFolder()) {
            return namespace;
        }
        FileObject parent = target.getParent();
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        FileObject appDirectory = cakeModule.getDirectory(CakePhpModule.DIR_TYPE.APP);
        FileObject coreDirectory = cakeModule.getDirectory(CakePhpModule.DIR_TYPE.CORE);
        if (appDirectory == null || coreDirectory == null || parent == null) {
            return namespace;
        }
        String appPath = appDirectory.getPath();
        String corePath = coreDirectory.getPath();
        String parentPath = parent.getPath();
        String path = ""; // NOI18N

        // App
        if (parentPath.startsWith(appPath)) {
            path = parentPath.replace(appPath, ""); // NOI18N
            path = path.replaceAll("/", "\\\\"); // NOI18N
            path = "App" + path; // NOI18N
        }

        // Core
        if (parentPath.startsWith(corePath)) {
            path = parentPath.replace(corePath, ""); // NOI18N
            path = path.replaceAll("/", "\\\\"); // NOI18N
            path = "Cake" + path; // NOI18N
        }

        if (!path.isEmpty()) {
            namespace = String.format(NAMESPACE_FORMAT, path);
        }
        return namespace;
    }

    /**
     * Check whether PhpModule is CakePHP3.
     *
     * @param phpModule
     * @return true if CakePHP3, otherwiese false;
     */
    private boolean isCakePHP3(PhpModule phpModule) {
        if (!CakePhpUtils.isCakePHP(phpModule)) {
            return false;
        }
        CakeVersion version = CakeVersion.getInstance(phpModule);
        if (!version.isCakePhp(3)) {
            return false;
        }

        return true;
    }
}
