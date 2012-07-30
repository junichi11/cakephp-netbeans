/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cakephp.netbeans.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.CakeVersion;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FieldsDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.openide.filesystems.FileObject;

/**
 *
 * @author igorf
 */
public class CakePhpEditorExtender extends EditorExtender {

    static final Logger LOGGER = Logger.getLogger(CakePhpEditorExtender.class.getName());

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        boolean isView = CakePhpUtils.isView(fo);
        boolean isController = CakePhpUtils.isController(fo);
        boolean isComponent = CakePhpUtils.isComponent(fo);
        boolean isHelper = CakePhpUtils.isHelper(fo);

        if (!isView && !isController && !isComponent && !isHelper) {
            return Collections.emptyList();
        }

        List<PhpBaseElement> elements;
        elements = new LinkedList<PhpBaseElement>();

        for (PhpClass phpClass : parseFields(fo)) {
            PhpModule pm = PhpModule.forFileObject(fo);
            if (isView || isHelper) {
                if (!(isView && CakeVersion.getInstance(pm).isCakePhp(2))) {
                    for (FileObject helper : CakePhpUtils.getFiles(pm, CakePhpUtils.DIR.CORE, CakePhpUtils.FILE.HELPER)) {
                        String className = CakePhpUtils.getClassName(helper);
                        String name = className.replace(CakePhpUtils.FILE.HELPER.toString(), "");
                        phpClass.addField(name, new PhpClass(name, className), fo, 0);
                    }
                }
            } else {
                if (!(isController && CakeVersion.getInstance(pm).isCakePhp(2))) {
                    for (FileObject component : CakePhpUtils.getFiles(pm, CakePhpUtils.DIR.CORE, CakePhpUtils.FILE.COMPONENT)) {
                        String className = CakePhpUtils.getClassName(component);
                        String name = className.replace(CakePhpUtils.FILE.COMPONENT.toString(), "");
                        phpClass.addField(name, new PhpClass(name, className), fo, 0);
                    }
                }
            }
            elements.add(new PhpVariable("$this", phpClass)); // NOI18N
        }

        if (isView) {
            elements.addAll(parseAction(fo));
        }

        return elements;
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

    private Set<PhpClass> parseFields(final FileObject fo) {
        FileObject tmp = fo;
        if (CakePhpUtils.isView(fo)) {
            tmp = CakePhpUtils.getController(fo);
        }

        final FileObject target = tmp;
        if (target == null) {
            return Collections.emptySet();
        }

        final Set<PhpClass> phpClasses = new HashSet<PhpClass>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(target)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                    if (CakePhpUtils.isView(fo) || CakePhpUtils.isController(fo)) {
                        final CakePhpControllerVisitor controllerVisitor = new CakePhpControllerVisitor(fo, (PHPParseResult) parseResult);
                        controllerVisitor.scan(Utils.getRoot(parseResult));
                        phpClasses.addAll(Collections.singleton(controllerVisitor.getPhpClasses()));
                    } else if (CakePhpUtils.isComponent(fo)) {
                        final CakePhpComponentVisitor componentVisitor = new CakePhpComponentVisitor(fo);
                        componentVisitor.scan(Utils.getRoot(parseResult));
                        phpClasses.addAll(Collections.singleton(componentVisitor.getPhpClass()));
                    } else if (CakePhpUtils.isHelper(fo)) {
                        final CakePhpHelperVisitor helperVisitor = new CakePhpHelperVisitor(fo);
                        helperVisitor.scan(Utils.getRoot(parseResult));
                        phpClasses.addAll(Collections.singleton(helperVisitor.getPhpClass()));
                    }
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return phpClasses;
    }

    private static final class CakePhpControllerVisitor extends DefaultVisitor {

        private final Set<PhpVariable> fields = new HashSet<PhpVariable>();
        private final PhpClass controllerClasses = new PhpClass("Controller", "Controller"); // NOI18N
        private final PhpClass viewClasses = new PhpClass("View", "View"); // NOI18N
        private String className = null;
        private String methodName = null;
        private String viewName = null;
        private PhpModule pm = null;

        public CakePhpControllerVisitor(FileObject fo, PHPParseResult actionParseResult) {
            pm = PhpModule.forFileObject(fo);
            if (CakePhpUtils.isView(fo)) {
                viewName = CakePhpUtils.getActionName(fo);
            }
        }

        public Set<PhpVariable> getPhpVariables() {
            Set<PhpVariable> phpVariables = new HashSet<PhpVariable>();
            synchronized (fields) {
                phpVariables.addAll(fields);
            }
            return phpVariables;
        }

        public PhpClass getPhpClasses() {
            PhpClass phpClasses = null;
            if (viewName != null) {
                synchronized (viewClasses) {
                    phpClasses = viewClasses;
                }
            } else {
                synchronized (controllerClasses) {
                    phpClasses = controllerClasses;
                }
            }
            return phpClasses;
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

            if (!(node.getDispatcher() instanceof Variable)
                || !"$this".equals(CodeUtils.extractVariableName((Variable) node.getDispatcher()))) {
                return;
            }

            FunctionInvocation fi = node.getMethod();
            String invokedMethodName = CodeUtils.extractFunctionName(fi);

            List<Expression> params = fi.getParameters();
            Expression e = null;

            if (!params.isEmpty()) {
                e = params.get(0);
            }

            String viewVarName = "";
            if (e instanceof Scalar) {
                Scalar s = (Scalar) e;
                if (s.getScalarType() == Scalar.Type.STRING) {
                    viewVarName = prepareViewVar(s.getStringValue());
                }
            }

            if (methodName.equals(viewName)
                && invokedMethodName.equals("set")
                && CakePhpUtils.isControllerName(className)
                && !viewVarName.isEmpty()) {
                synchronized (fields) {
                    fields.add(new PhpVariable("$" + viewVarName, new PhpClass("stdClass", "stdClass")));
                }
            }
        }

        @Override
        public void visit(FieldsDeclaration node) {
            List<SingleFieldDeclaration> controllerFields = node.getFields();
            for (SingleFieldDeclaration field : controllerFields) {
                String name = CodeUtils.extractVariableName(field.getName());
                // get ArrayCreation
                ArrayCreation arrayCreation = null;
                Expression value = field.getValue();
                if (value instanceof ArrayCreation) {
                    arrayCreation = (ArrayCreation) value;
                }
                if (arrayCreation == null) {
                    continue;
                }

                FileObject object = null;
                for (ArrayElement element : arrayCreation.getElements()) {
                    Expression e = null;
                    if (element.getKey() != null) {
                        e = element.getKey();
                    } else {
                        e = element.getValue();
                    }
                    if (e == null) {
                        continue;
                    }

                    String elementName = getStringName(e);

                    // model
                    if (viewName == null && name.equals("$uses")) { // NOI18N
                        object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.APP, CakePhpUtils.FILE.MODEL, elementName);

                        if (object != null) {
                            synchronized (controllerClasses) {
                                controllerClasses.addField(elementName, new PhpClass(elementName, elementName), object, 0);
                            }
                        }
                    }

                    // check app or plugin component
                    if (viewName == null && name.equals("$components")) { // NOI18N
                        String[] split = elementName.split("[.]"); // NOI18N
                        int len = split.length;
                        switch (len) {
                            case 1:
                                object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.APP, CakePhpUtils.FILE.COMPONENT, elementName);
                                break;
                            case 2:
                                object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.APP_PLUGIN, CakePhpUtils.FILE.COMPONENT, elementName);
                                if (object == null) {
                                    object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.PLUGIN, CakePhpUtils.FILE.COMPONENT, elementName);
                                }
                                elementName = split[1];
                                break;
                            default:
                                break;
                        }
                        String componentClassName = elementName + "Component"; // NOI18N
                        if (object != null) {
                            synchronized (controllerClasses) {
                                controllerClasses.addField(elementName, new PhpClass(elementName, componentClassName), object, 0);
                            }
                        }
                    }

                    // check app or plugin component
                    if (viewName != null && name.equals("$helpers")) { // NOI18N
                        String[] split = elementName.split("[.]"); // NOI18N
                        int len = split.length;
                        switch (len) {
                            case 1:
                                object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.APP, CakePhpUtils.FILE.HELPER, elementName);
                                break;
                            case 2:
                                object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.APP_PLUGIN, CakePhpUtils.FILE.HELPER, elementName);
                                if (object == null) {
                                    object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.PLUGIN, CakePhpUtils.FILE.HELPER, elementName);
                                }
                                elementName = split[1];
                                break;
                            default:
                                break;
                        }
                        String helperClassName = elementName + "Helper"; // NOI18N
                        if (object != null) {
                            synchronized (viewClasses) {
                                viewClasses.addField(elementName, new PhpClass(elementName, helperClassName), object, 0);
                            }
                        }
                    }
                }
            }
            super.visit(node);
        }

        private String prepareViewVar(String viewVarName) {
            if (!viewVarName.isEmpty()) {
                viewVarName = viewVarName.substring(1, viewVarName.length() - 1).trim();
                if (!viewVarName.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                    viewVarName = "";
                }
            }
            return viewVarName;
        }

        /**
         * Get string name from Expression
         *
         * @param e Expression
         * @return strin name
         */
        private String getStringName(Expression e) {
            String name = ""; // NOI18N
            if (e instanceof Scalar) {
                Scalar s = (Scalar) e;
                if (s.getScalarType() == Scalar.Type.STRING) {
                    name = s.getStringValue();
                    name = name.replaceAll("\"", ""); // NOI18N
                    name = name.replaceAll("'", ""); // NOI18N
                }
            }
            return name;
        }
    }

    private static final class CakePhpComponentVisitor extends DefaultVisitor {

        private final PhpClass componentClass;
        private PhpModule pm;

        public CakePhpComponentVisitor(FileObject fo) {
            pm = PhpModule.forFileObject(fo);
            CakeVersion version = CakeVersion.getInstance(pm);

            if (version.isCakePhp(1)) {
                componentClass = new PhpClass("Object", "Object"); // NOI18N
            } else if (version.isCakePhp(2)) {
                componentClass = new PhpClass("Component", "Component"); // NOI18N
            } else {
                componentClass = null;
            }
        }

        /**
         * Get component class fields
         *
         * @return component php class
         */
        public PhpClass getPhpClass() {
            PhpClass component;
            synchronized (componentClass) {
                component = componentClass;
            }
            return component;
        }

        @Override
        public void visit(FieldsDeclaration node) {
            super.visit(node);
            List<SingleFieldDeclaration> componentFields = node.getFields();
            for (SingleFieldDeclaration field : componentFields) {
                String name = CodeUtils.extractVariableName(field.getName());
                if (componentClass == null || !name.equals("$components")) { // NOI18N
                    return;
                }
                // get ArrayCreation
                ArrayCreation arrayCreation = null;
                Expression value = field.getValue();
                if (value instanceof ArrayCreation) {
                    arrayCreation = (ArrayCreation) value;
                }
                if (arrayCreation == null) {
                    continue;
                }
                for (ArrayElement element : arrayCreation.getElements()) {
                    Expression e = null;
                    if (element.getKey() != null) {
                        e = element.getKey();
                    } else {
                        e = element.getValue();
                    }
                    if (e == null) {
                        continue;
                    }

                    String elementName = ""; // NOI18N
                    if (e instanceof Scalar) {
                        Scalar s = (Scalar) e;
                        if (s.getScalarType() == Scalar.Type.STRING) {
                            elementName = s.getStringValue();
                            elementName = elementName.replaceAll("\"", ""); // NOI18N
                            elementName = elementName.replaceAll("'", ""); // NOI18N
                        }
                    }

                    FileObject object = null;
                    // check app or plugin component
                    String[] split = elementName.split("[.]"); // NOI18N
                    int len = split.length;
                    switch (len) {
                        case 1:
                            object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.APP, CakePhpUtils.FILE.COMPONENT, elementName);
                            break;
                        case 2:
                            object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.APP_PLUGIN, CakePhpUtils.FILE.COMPONENT, elementName);
                            if (object == null) {
                                object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.PLUGIN, CakePhpUtils.FILE.COMPONENT, elementName);
                            }
                            elementName = split[1];
                            break;
                        default:
                            break;
                    }
                    String componentClassName = elementName + "Component"; // NOI18N
                    if (object != null) {
                        synchronized (componentClass) {
                            componentClass.addField(elementName, new PhpClass(elementName, componentClassName), object, 0);
                        }
                    }
                }
            }
        }
    }

    private static final class CakePhpHelperVisitor extends DefaultVisitor {

        private final PhpClass helperClass = new PhpClass("AppHelper", "AppHelper");
        private PhpModule pm;

        public CakePhpHelperVisitor(FileObject fo) {
            pm = PhpModule.forFileObject(fo);
        }

        /**
         * Get component class fields
         *
         * @return component php class
         */
        public PhpClass getPhpClass() {
            PhpClass component;
            synchronized (helperClass) {
                component = helperClass;
            }
            return component;
        }

        @Override
        public void visit(FieldsDeclaration node) {
            super.visit(node);
            List<SingleFieldDeclaration> helperFields = node.getFields();
            for (SingleFieldDeclaration field : helperFields) {
                String name = CodeUtils.extractVariableName(field.getName());
                if (helperClass == null || !name.equals("$helpers")) { // NOI18N
                    return;
                }
                // get ArrayCreation
                ArrayCreation arrayCreation = null;
                Expression value = field.getValue();
                if (value instanceof ArrayCreation) {
                    arrayCreation = (ArrayCreation) value;
                }
                if (arrayCreation == null) {
                    continue;
                }
                for (ArrayElement element : arrayCreation.getElements()) {
                    Expression e = null;
                    if (element.getKey() != null) {
                        e = element.getKey();
                    } else {
                        e = element.getValue();
                    }
                    if (e == null) {
                        continue;
                    }

                    String elementName = ""; // NOI18N
                    if (e instanceof Scalar) {
                        Scalar s = (Scalar) e;
                        if (s.getScalarType() == Scalar.Type.STRING) {
                            elementName = s.getStringValue();
                            elementName = elementName.replaceAll("\"", ""); // NOI18N
                            elementName = elementName.replaceAll("'", ""); // NOI18N
                        }
                    }

                    FileObject object = null;
                    // check app or plugin helper
                    String[] split = elementName.split("[.]"); // NOI18N
                    int len = split.length;
                    switch (len) {
                        case 1:
                            object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.APP, CakePhpUtils.FILE.HELPER, elementName);
                            break;
                        case 2:
                            object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.APP_PLUGIN, CakePhpUtils.FILE.HELPER, elementName);
                            if (object == null) {
                                object = CakePhpUtils.getFile(pm, CakePhpUtils.DIR.PLUGIN, CakePhpUtils.FILE.HELPER, elementName);
                            }
                            elementName = split[1];
                            break;
                        default:
                            break;
                    }
                    String helperClassName = elementName + "Helper"; // NOI18N
                    if (object != null) {
                        synchronized (helperClass) {
                            helperClass.addField(elementName, new PhpClass(elementName, helperClassName), object, 0);
                        }
                    }
                }
            }
        }
    }
}
