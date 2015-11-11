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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cakephp.netbeans.util.CakePhpCodeUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class CakePhpViewVisitor extends DefaultVisitor {

    private final int caretPositon;
    private final Set<String> elementPaths = new HashSet<>();
    private final Set<String> extendPaths = new HashSet<>();
    private String caretPositionElementPath = ""; // NOI18N
    private String caretPositionExtendPath = ""; // NOI18N
    private static final String ELEMENT = "element"; // NOI18N
    private static final String EXTEND = "extend"; // NOI18N

    public CakePhpViewVisitor(FileObject view, int caretPositon) {
        this.caretPositon = caretPositon;
    }

    public Set<String> getElementPaths() {
        return elementPaths;
    }

    public String getElementPathForCaretPosition() {
        return caretPositionElementPath;
    }

    public Set<String> getExtendPaths() {
        return extendPaths;
    }

    public String getExtendPathForCaretPosition() {
        return caretPositionExtendPath;
    }

    @Override
    public void visit(MethodInvocation node) {
        super.visit(node);
        if (!(node.getDispatcher() instanceof Variable) || !"$this".equals(CodeUtils.extractVariableName((Variable) node.getDispatcher()))) { // NOI18N
            return;
        }
        FunctionInvocation fi = node.getMethod();
        String invokedMethodName = CodeUtils.extractFunctionName(fi);
        List<Expression> params = fi.getParameters();
        Expression e = null;
        if (!params.isEmpty()) {
            e = params.get(0);
        }

        if (e == null) {
            return;
        }

        String path = ""; // NOI18N
        if (e instanceof Scalar) {
            Scalar s = (Scalar) e;
            if (s.getScalarType() == Scalar.Type.STRING) {
                path = CakePhpCodeUtils.getStringValue(e);
            }
        }

        if (invokedMethodName.equals(ELEMENT)) {
            if (!StringUtils.isEmpty(path)) {
                elementPaths.add(path);
            }
            if (isCaretPosition(e.getStartOffset(), e.getEndOffset())) {
                caretPositionElementPath = path;
            }
        } else if (invokedMethodName.equals(EXTEND)) {
            if (!StringUtils.isEmpty(path)) {
                extendPaths.add(path);
            }
            if (isCaretPosition(e.getStartOffset(), e.getEndOffset())) {
                caretPositionExtendPath = path;
            }
        }
    }

    private boolean isCaretPosition(int startOffset, int endOffset) {
        return startOffset <= caretPositon && caretPositon <= endOffset;
    }
}
