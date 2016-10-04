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
package org.cakephp.netbeans.editor.codetemplates;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.util.CakePhpCodeUtils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateInsertRequest;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateParameter;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.ElementQueryFactory;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.QuerySupportFactory;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ClassName;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.NamespaceName;
import org.netbeans.modules.php.editor.parser.astnodes.StaticMethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

public class CakeAppUsesParameter extends CakePhpCodeTemplateParameter {

    private static final Set<String> IGNORED_SET = new HashSet<>();

    static {
        IGNORED_SET.add("App"); // NOI18N
        IGNORED_SET.add("Configure"); // NOI18N
        IGNORED_SET.add("Inflector"); // NOI18N
        IGNORED_SET.add("CakePlugin"); // NOI18N
        IGNORED_SET.add("NotFoundException"); // NOI18N
    }

    public CakeAppUsesParameter(CodeTemplateParameter parameter) {
        super(parameter);
    }

    @Override
    public void updateValue(CodeTemplateInsertRequest request, FileObject fileObject, CakePhpModule cakeModule) {
        // get default uses for current file class
        String appUsesDefault = getDefaultUses(cakeModule, fileObject);
        String parametrizedText = toParametrizeText(request, appUsesDefault);

        // scan current file
        final DefaultVisitorImpl defaultVisitor = new DefaultVisitorImpl();
        scan(fileObject, defaultVisitor);

        StringBuilder sb = new StringBuilder();
        if (!existDefaultClass(defaultVisitor.getExistingClasses())) {
            sb.append(parametrizedText);
        }
        sb.append(getOtherClassesText(fileObject, cakeModule, defaultVisitor));

        request.setParametrizedText(sb.toString());
    }

    @Override
    public void release(CodeTemplateInsertRequest request) {
    }

    /**
     * Get default App::uses values for current file class.
     *
     * @param cakeModule
     * @param fileObject current file
     * @return default value
     */
    private String getDefaultUses(CakePhpModule cakeModule, FileObject fileObject) {
        String appUsesDefault = "'${className}', '${location}'"; // NOI18N
        if (cakeModule.isController(fileObject)) {
            appUsesDefault = "'${AppController}', '${Controller}'"; // NOI18N
        } else if (cakeModule.isComponent(fileObject)) {
            appUsesDefault = "'${Component}', '${Controller}'"; // NOI18N
        } else if (cakeModule.isModel(fileObject)) {
            appUsesDefault = "'${AppModel}', '${Model}'"; // NOI18N
        } else if (cakeModule.isBehavior(fileObject)) {
            appUsesDefault = "'${ModelBehavior}', '${Model}'"; // NOI18N
        } else if (cakeModule.isHelper(fileObject)) {
            appUsesDefault = "'${AppHelper}', '${View/Helper}'"; // NOI18N
        }
        return appUsesDefault;
    }

    /**
     * Check whether default class is contained in existingClasses.
     *
     * @param existingClasses
     * @return true if exists, false otherwise.
     */
    private boolean existDefaultClass(Set<String> existingClasses) {
        return existingClasses.contains("AppController") // NOI18N
                || existingClasses.contains("Component") // NOI18N
                || existingClasses.contains("AppModel") // NOI18N
                || existingClasses.contains("ModelBehavior") // NOI18N
                || existingClasses.contains("AppHelper"); // NOI18N
    }

    /**
     * Change default to parametrize text.
     *
     * @param request
     * @param appUsesDefault default value
     * @return parametrize text
     */
    private String toParametrizeText(CodeTemplateInsertRequest request, String appUsesDefault) {
        String parametrizedText = request.getParametrizedText();
        parametrizedText = parametrizedText.replace("${CakeAppUses}", appUsesDefault); // NOI18N
        return parametrizedText;
    }

    /**
     * Get other classes text.
     *
     * @param fileObject
     * @param cakeModule
     * @return
     */
    private String getOtherClassesText(FileObject fileObject, CakePhpModule cakeModule, DefaultVisitorImpl visitor) {
        // add more
        PhpModule phpModule = PhpModule.Factory.forFileObject(fileObject);
        StringBuilder sb = new StringBuilder();
        if (phpModule == null) {
            return sb.toString();
        }

        // get core path
        String corePath = getCoreDirectoryPath(cakeModule);
        if (corePath == null) {
            return sb.toString();
        }

        ElementQuery.Index index = ElementQueryFactory.createIndexQuery(QuerySupportFactory.get(phpModule.getSourceDirectory()));
        Set<String> existingClasses = visitor.getExistingClasses();
        for (String className : visitor.getClasses()) {
            if (existingClasses.contains(className)) {
                continue;
            }

            for (ClassElement classElement : index.getClasses(NameKind.exact(className))) {
                FileObject target = classElement.getFileObject();
                if (target == null) {
                    continue;
                }
                String targetPath = target.getParent().getPath();
                // check whether it is cakephp
                if (!targetPath.startsWith(corePath)) {
                    continue;
                }
                String location = getLocation(targetPath, corePath);
                sb.append(String.format("\nApp::uses('%s', '%s');", className, location)); // NOI18N
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Get CakePHP Core directory path.
     *
     * @param cakeModule
     * @return core directory path if it exists, null otherwise.
     */
    @CheckForNull
    private String getCoreDirectoryPath(CakePhpModule cakeModule) {
        FileObject core = cakeModule.getDirectory(CakePhpModule.DIR_TYPE.CORE);
        if (core == null) {
            return null;
        }
        return core.getPath();
    }

    /**
     * Get classes. Detect all classes for current file.
     *
     * @param fileObject
     * @return all classe name
     */
    private void scan(FileObject fileObject, final DefaultVisitorImpl defaultVisitor) {
        // scan current file
        try {
            ParserManager.parse(Collections.singleton(Source.create(fileObject)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                    if (parseResult != null) {
                        defaultVisitor.scan(Utils.getRoot(parseResult));
                    }
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Get location.
     *
     * @param targetPath target file path
     * @param corePath core directory path
     * @return location
     */
    private String getLocation(String targetPath, String corePath) {
        String location = targetPath.replace(corePath, ""); // NOI18N
        if (location.startsWith("/")) { // NOI18N
            location = location.substring(1);
        }
        return location;
    }

    //~ inner class
    private static class DefaultVisitorImpl extends DefaultVisitor {

        public DefaultVisitorImpl() {
        }
        private final Set<String> classes = new HashSet<>();
        private final Set<String> existingClasses = new HashSet<>();

        @Override
        public void visit(StaticMethodInvocation node) {
            super.visit(node);
            Expression className = node.getDispatcher();
            if (className instanceof NamespaceName) {
                String name = CodeUtils.extractQualifiedName((NamespaceName) className);
                if (name.equals("App")) { // NOI18N
                    addExistingClass(node);
                    return;
                }
                if (!StringUtils.isEmpty(name) && !IGNORED_SET.contains(name)) {
                    classes.add(name);
                }
            }
        }

        /**
         * Add existing class.
         *
         * @param node
         */
        private void addExistingClass(StaticMethodInvocation node) {
            FunctionInvocation method = node.getMethod();
            String functionName = CodeUtils.extractFunctionName(method);
            if (functionName.equals("uses")) { // NOI18N
                List<Expression> parameters = method.getParameters();
                for (Expression parameter : parameters) {
                    String existClassName = CakePhpCodeUtils.getStringValue(parameter);
                    if (!StringUtils.isEmpty(existClassName)) {
                        existingClasses.add(existClassName);
                    }
                }
            }
        }

        @Override
        public void visit(ClassName node) {
            super.visit(node);
            String name = CodeUtils.extractClassName(node);
            if (!StringUtils.isEmpty(name) && !IGNORED_SET.contains(name)) {
                classes.add(name);
            }
        }

        public Set<String> getClasses() {
            return classes;
        }

        public Set<String> getExistingClasses() {
            return existingClasses;
        }

    }

}
