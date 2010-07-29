/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cakephp.netbeans.editor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.openide.filesystems.FileObject;

/**
 *
 * @author igorf
 */
public class CakePhpEditorExtender extends EditorExtender {
    static final Logger LOGGER = Logger.getLogger(CakePhpEditorExtender.class.getName());
    private static final List<PhpBaseElement> ELEMENTS = Arrays.<PhpBaseElement>asList(
            new PhpVariable("$this", new PhpClass("View", "View"))); // NOI18N

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        if (CakePhpUtils.isView(fo)) {
            List<PhpBaseElement> elements = new LinkedList<PhpBaseElement>(ELEMENTS);
            elements.addAll(parseAction(fo));
            return elements;
        }
        return Collections.emptyList();
    }

    private Set<PhpVariable> parseAction(final FileObject view) {
        final FileObject controller = CakePhpUtils.getController(view);
        if (controller == null) {
            return Collections.emptySet();
        }
        final Set<PhpVariable> phpVariables = new HashSet<PhpVariable>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(controller)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                    final CakePhpControllerVisitor controllerVisitor = new CakePhpControllerVisitor(view, (PHPParseResult) parseResult);
                    controllerVisitor.scan(Utils.getRoot(parseResult));
                    phpVariables.addAll(controllerVisitor.getPhpVariables());
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return phpVariables;
    }

    private static final class CakePhpControllerVisitor extends DefaultVisitor {
        private final Set<PhpVariable> fields = new HashSet<PhpVariable>();

        private String className = null;
        private String methodName = null;
        private String viewName = null;
        
        public CakePhpControllerVisitor(FileObject view, PHPParseResult actionParseResult) {
            viewName = CakePhpUtils.getActionName(view);
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

            if(!(node.getDispatcher() instanceof Variable)
                || !"$this".equals(CodeUtils.extractVariableName((Variable) node.getDispatcher()))) {
                return;
            }

            FunctionInvocation fi = node.getMethod();
            String invokedMethodName = CodeUtils.extractFunctionName(fi);

            List<Expression> params = fi.getParameters();
            Expression e = null;

            if(!params.isEmpty()) {
                e = params.get(0);
            }

            String viewVarName = "";
            if(e instanceof Scalar) {
                Scalar s = (Scalar)e;
                if(s.getScalarType() == Scalar.Type.STRING) {
                    viewVarName = prepareViewVar(s.getStringValue());
                }
            }

            if(methodName.equals(viewName)
                    && invokedMethodName.equals("set")
                    && CakePhpUtils.isControllerName(className)
                    && !viewVarName.isEmpty()) {
                synchronized (fields) {
                    fields.add(new PhpVariable("$" + viewVarName, new PhpClass("stdClass", "stdClass")));
                }
            }
        }
        
        private String prepareViewVar(String viewVarName) {
            if(!viewVarName.isEmpty()) {
                viewVarName = viewVarName.substring(1, viewVarName.length() - 1).trim();
                if(!viewVarName.matches("[A-Za-z][A-Za-z0-9]*")) {
                    viewVarName = "";
                }
            }
            return viewVarName;
        }
    }
}
