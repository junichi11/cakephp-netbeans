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
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.openide.filesystems.FileObject;

public final class CakePhpControllerVisitor extends CakePhpFieldsVisitor {

    private final Set<PhpVariable> fields = new HashSet<PhpVariable>();
    private String className = null;
    private String methodName = null;
    private String viewName = null;

    public CakePhpControllerVisitor(FileObject fo, PhpClass phpClass) {
        super(fo, phpClass);
        if (CakePhpUtils.isView(fo)) {
            viewName = CakePhpUtils.getActionName(fo);
        }
    }

    @Override
    public Set<String> getFieldNames() {
        Set<String> fieldNames = new HashSet<String>();
        if (CakePhpUtils.isController(targetFile)) {
            fieldNames.add(USES);
            fieldNames.add(COMPONENTS);
        } else if (CakePhpUtils.isView(targetFile)) {
            fieldNames.add(HELPERS);
        }
        return fieldNames;
    }

    public Set<PhpVariable> getPhpVariables() {
        Set<PhpVariable> phpVariables = new HashSet<PhpVariable>();
        synchronized (fields) {
            phpVariables.addAll(fields);
        }
        return phpVariables;
    }

    @Override
    public void visit(ClassDeclaration node) {
        className = CodeUtils.extractClassName(node);
        super.visit(node);
    }

    @Override
    public void visit(MethodDeclaration node) {
        methodName = CodeUtils.extractMethodName(node);
        super.visit(node);
    }

    @Override
    public void visit(MethodInvocation node) {
        super.visit(node);
        if (!(node.getDispatcher() instanceof Variable) || !"$this".equals(CodeUtils.extractVariableName((Variable) node.getDispatcher()))) {
            // NOI18N
            return;
        }
        FunctionInvocation fi = node.getMethod();
        String invokedMethodName = CodeUtils.extractFunctionName(fi);
        List<Expression> params = fi.getParameters();
        Expression e = null;
        if (!params.isEmpty()) {
            e = params.get(0);
        }
        String viewVarName = ""; // NOI18N
        if (e instanceof Scalar) {
            Scalar s = (Scalar) e;
            if (s.getScalarType() == Scalar.Type.STRING) {
                viewVarName = prepareViewVar(s.getStringValue());
            }
        }
        if (methodName.equals(viewName) && invokedMethodName.equals("set") && CakePhpUtils.isControllerName(className) && !viewVarName.isEmpty()) {
            synchronized (fields) {
                fields.add(new PhpVariable("$" + viewVarName, new PhpClass("stdClass", "stdClass"), targetFile, 0)); // NOI18N
            }
        }
    }

    private String prepareViewVar(String viewVarName) {
        if (!viewVarName.isEmpty()) {
            viewVarName = viewVarName.substring(1, viewVarName.length() - 1).trim();
            if (!viewVarName.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                // NOI18N
                viewVarName = ""; // NOI18N
            }
        }
        return viewVarName;
    }
}
