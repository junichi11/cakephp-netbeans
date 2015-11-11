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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.cakephp.netbeans.editor.visitors.CakePhpFieldsVisitor.HELPERS;
import org.cakephp.netbeans.util.CakePhpCodeUtils;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.astnodes.Assignment;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.ExpressionStatement;
import org.netbeans.modules.php.editor.parser.astnodes.FieldAccess;
import org.netbeans.modules.php.editor.parser.astnodes.FieldsDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.VariableBase;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public final class CakePhpControllerVisitor extends CakePhpFieldsVisitor {

    private final Set<PhpVariable> fields = new HashSet<>();
    private final Set<String> viewNames = new HashSet<>();
    private final Set<String> allViewNames = new HashSet<>();
    private final Set<String> themeNames = new HashSet<>();
    private final Set<String> allThemeNames = new HashSet<>();
    private String className = null;
    private String methodName = "";
    private String viewName = null;
    private boolean isController = false;
    private boolean isView = false;
    private boolean isTheme = false;

    public CakePhpControllerVisitor(FileObject fo, PhpClass phpClass) {
        super(fo, phpClass);
        setFileInfo(targetFile);
    }

    public CakePhpControllerVisitor(FileObject targetFile) {
        super(targetFile);
        setFileInfo(targetFile);
    }

    public CakePhpControllerVisitor(FileObject targetFile, int currentCaretPosition) {
        super(targetFile);
        setFileInfo(targetFile);
        if (isController) {
            // get PhpBaseElement(Method) for current positon
            EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
            // XXX
            int startClassOffset = 0;
            Collection<PhpClass> classes = editorSupport.getClasses(targetFile);
            for (PhpClass phpClass : classes) {
                startClassOffset = phpClass.getOffset();
                for (PhpClass.Method method : phpClass.getMethods()) {
                    allViewNames.add(method.getName());
                }
                break;
            }
            // FIXME exception might be occurred
            // if user run action at outside php class.
            // e.g. document area.
            //
            if (currentCaretPosition > startClassOffset) {
                PhpBaseElement phpElement = editorSupport.getElement(targetFile, currentCaretPosition);
                if (phpElement != null && phpElement instanceof PhpClass.Method) {
                    PhpClass.Method method = (PhpClass.Method) phpElement;
                    viewName = method.getName();
                    viewNames.add(viewName);
                }
            }
        }
    }

    private void setFileInfo(FileObject targetFile) {
        if (CakePhpUtils.isView(targetFile)) {
            viewName = CakePhpUtils.getActionName(targetFile);
            isView = true;
        } else if (CakePhpUtils.isController(targetFile)) {
            isController = true;
        }
    }

    @Override
    public void visit(FieldsDeclaration node) {
        super.visit(node);
        List<SingleFieldDeclaration> controllerFields = node.getFields();
        for (SingleFieldDeclaration field : controllerFields) {
            String fieldName = CodeUtils.extractVariableName(field.getName());
            if ("$theme".equals(fieldName)) { // NOI18N
                isTheme = true;
            }
        }
    }

    @Override
    public void visit(ExpressionStatement node) {
        super.visit(node);
        if (methodName == null) {
            return;
        }

        Expression expression = node.getExpression();
        if (expression instanceof Assignment) {
            Assignment assignment = (Assignment) expression;
            Assignment.Type operator = assignment.getOperator();
            if (operator != Assignment.Type.EQUAL) {
                return;
            }

            VariableBase leftHandSide = assignment.getLeftHandSide();
            if (leftHandSide instanceof FieldAccess) {
                // e.g. $this->view = 'add';
                // left
                FieldAccess f = (FieldAccess) leftHandSide;
                Variable v = f.getField();
                String variableName = CodeUtils.extractVariableName(v);
                if (!"view".equals(variableName) && !"theme".equals(variableName)) { // NOI18N
                    return;
                }

                // right
                Expression rightHandSide = assignment.getRightHandSide();
                String rightValue = CakePhpCodeUtils.getStringValue(rightHandSide);
                if (rightValue.isEmpty()) {
                    return;
                }

                if ("view".equals(variableName)) { // NOI18N
                    if (methodName.equals(viewName)) {
                        viewNames.add(rightValue);
                    } else {
                        allViewNames.add(rightValue);
                    }
                } else if ("theme".equals(variableName)) { // NOI18N
                    if (methodName.equals(viewName)) {
                        themeNames.add(rightValue);
                        isTheme = true;
                    } else {
                        allThemeNames.add(rightValue);
                    }
                }
            }
        }
    }

    @Override
    public Set<String> getFieldNames() {
        Set<String> fieldNames = new HashSet<>();

        // get AppController fields info.
        String name = CakePhpUtils.toUnderscoreCase(targetFile.getName());
        if ("app_controller".equals(name)) { // NOI18N
            FileObject currentFileObject = CakePhpUtils.getCurrentFileObject();
            if (currentFileObject != null && CakePhpUtils.isView(currentFileObject)) {
                fieldNames.add(HELPERS);
                return fieldNames;
            }
        }

        if (isController) {
            fieldNames.add(USES);
            fieldNames.add(COMPONENTS);
            if (getPhpClass() == null) {
                fieldNames.add(HELPERS);
            }
        } else if (isView) {
            fieldNames.add(HELPERS);
        }
        return fieldNames;
    }

    public Set<PhpVariable> getPhpVariables() {
        Set<PhpVariable> phpVariables = new HashSet<>();
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
        String viewVarName = ""; // NOI18N
        if (e instanceof Scalar) {
            Scalar s = (Scalar) e;
            if (s.getScalarType() == Scalar.Type.STRING) {
                viewVarName = prepareViewVar(s.getStringValue());
            }
        }

        // in controller
        if (isController && invokedMethodName.equals("render")) { // NOI18N
            String viewPath = ""; // NOI18N
            if (e instanceof Scalar) {
                viewPath = CakePhpCodeUtils.getStringValue(e);
            }
            if (!StringUtils.isEmpty(viewPath)) {
                if (methodName.equals(viewName)) {
                    viewNames.add(viewPath);
                } else {
                    allViewNames.add(viewPath);
                }
            }
        }

        // in view
        if (isView) {
            if (methodName.equals(viewName) && invokedMethodName.equals("set") && CakePhpUtils.isControllerName(className) && !viewVarName.isEmpty()) {
                synchronized (fields) {
                    fields.add(new PhpVariable("$" + viewVarName, new PhpClass("stdClass", "stdClass"), targetFile, 0)); // NOI18N
                }
            }
        }
    }

    public Set<String> getViewNames() {
        return viewNames;
    }

    public Set<String> getAllViewNames() {
        allViewNames.addAll(viewNames);
        return allViewNames;
    }

    public boolean isTheme() {
        return isTheme;
    }

    public Set<String> getThemeNames() {
        return themeNames;
    }

    public Set<String> getAllThemeNames() {
        return allThemeNames;
    }

    private String prepareViewVar(String viewVarName) {
        if (!viewVarName.isEmpty()) {
            viewVarName = viewVarName.substring(1, viewVarName.length() - 1).trim();
            if (!viewVarName.matches("[A-Za-z_][A-Za-z0-9_]*")) { // NOI18N
                viewVarName = ""; // NOI18N
            }
        }
        return viewVarName;
    }
}
