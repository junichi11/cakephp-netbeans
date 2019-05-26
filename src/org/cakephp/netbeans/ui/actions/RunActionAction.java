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

import java.awt.Dialog;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.ui.wizards.RunActionPanel;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpType.Method;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.FormalParameter;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
@ActionID(
        category = "PHP",
        id = "org.cakephp.netbeans.ui.actions.RunActionAction")
@ActionRegistration(
        displayName = "#CTL_RunActionAction")
@NbBundle.Messages("CTL_RunActionAction=Run Action")
public class RunActionAction extends BaseAction {

    private static final long serialVersionUID = -7304154690195265892L;
    private static final RunActionAction INSTANCE = new RunActionAction();
    private static final String SLASH = "/"; // NOI18N

    private RunActionAction() {
    }

    public static RunActionAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @Override
    protected String getPureName() {
        return Bundle.CTL_RunActionAction();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        // called via shortcut
        if (!CakePhpUtils.isCakePHP(phpModule)) {
            return;
        }

        // get editor
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor == null) {
            return;
        }

        // get current file
        FileObject targetFile = NbEditorUtilities.getFileObject(editor.getDocument());

        if (!CakePhpUtils.isController(targetFile)) {
            return;
        }

        // get caret position
        int caretPosition = editor.getCaretPosition();

        // get EditorSupport
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);

        // get controller name
        String controllerName = getControllerName(editorSupport, targetFile);

        // get action name
        final String actionName = getActionName(editorSupport, targetFile, caretPosition);
        if (StringUtils.isEmpty(controllerName) || StringUtils.isEmpty(actionName)) {
            return;
        }

        // plugin?
        CakePhpModule cakePhpModule = CakePhpModule.forPhpModule(phpModule);
        if (cakePhpModule == null) {
            return;
        }
        String pluginName = cakePhpModule.getCurrentPluginName(targetFile);

        // get arguments
        List<FormalParameter> arguments = getArguments(targetFile, actionName);
        String controllerId = CakePhpUtils.toUnderscoreCase(controllerName);
        String actionId = CakePhpUtils.toUnderscoreCase(actionName);

        // run action
        runAction(arguments, phpModule, controllerId, actionId, pluginName);
    }

    /**
     * Get action name.
     *
     * @param editorSupport EditorSupport
     * @param targetFile current file
     * @param caretPosition caret position
     * @return action name.
     */
    private String getActionName(EditorSupport editorSupport, FileObject targetFile, int caretPosition) {
        PhpBaseElement element = editorSupport.getElement(targetFile, caretPosition);
        String actionName = ""; // NOI18N
        if (element instanceof PhpClass.Method) {
            element = (Method) element;
            actionName = element.getName();
        }
        return actionName;

    }

    /**
     * Get controller name. Return the name that controller suffix is removed.
     *
     * @param editorSupport EditorSupport
     * @param targetFile current file
     * @return controller name.
     */
    private String getControllerName(EditorSupport editorSupport, FileObject targetFile) {
        String controllerName = ""; // NOI18N
        Collection<PhpClass> classes = editorSupport.getClasses(targetFile);
        for (PhpClass phpClass : classes) {
            controllerName = phpClass.getName();
            break;
        }
        controllerName = controllerName.replace("Controller", ""); // NOI18N
        return controllerName;
    }

    /**
     * Get arguments of action method.
     *
     * @param targetFile FileObject
     * @param actionName action name
     * @return argment list
     */
    private List<FormalParameter> getArguments(FileObject targetFile, final String actionName) {
        final List<FormalParameter> params = new ArrayList<>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(targetFile)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    if (resultIterator == null) {
                        return;
                    }
                    ParserResult result = (ParserResult) resultIterator.getParserResult();
                    ControllerMethodVisitor visitor = new ControllerMethodVisitor(actionName);
                    visitor.scan(Utils.getRoot(result));
                    params.addAll(visitor.getParams());
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        return params;
    }

    /**
     * Run action.
     *
     * @param params arguments
     * @param pm PhpModule
     * @param controllerId controller name
     * @param actionId action name
     */
    private void runAction(final List<FormalParameter> params, final PhpModule pm, final String controllerId, final String actionId, final String pluginName) {
        if (params.isEmpty()) {
            openBrowser(pm, controllerId, actionId, pluginName);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // create dialog
                    RunActionPanel panel = new RunActionPanel(params);
                    DialogDescriptor descriptor = new DialogDescriptor(panel, Bundle.CTL_RunActionAction(), true, null);
                    Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);

                    dialog.pack();
                    dialog.validate();
                    dialog.setVisible(true);

                    // open browser
                    if (descriptor.getValue() == DialogDescriptor.OK_OPTION) {
                        Map<String, String> requests = panel.getGetRequest();
                        openBrowser(pm, controllerId, actionId, requests, pluginName);
                    }
                    dialog.dispose();
                }
            });
        }
    }

    /**
     * Open Browser.
     *
     * @param phpModule
     * @param controllerId
     * @param actionId
     */
    private void openBrowser(PhpModule phpModule, String controllerId, String actionId, String pluginName) {
        openBrowser(phpModule, controllerId, actionId, new HashMap<>(), pluginName);
    }

    /**
     * Open Browser.
     *
     * @param phpModule
     * @param controllerId
     * @param actionId
     * @param getRequests
     */
    private void openBrowser(PhpModule phpModule, String controllerId, String actionId, Map<String, String> getRequests, String pluginName) {
        // build url
        StringBuilder sb = new StringBuilder();
        PhpModuleProperties properties = phpModule.getLookup().lookup(PhpModuleProperties.Factory.class).getProperties();
        String urlPath = properties.getUrl();
        if (!StringUtils.isEmpty(urlPath)) {
            sb.append(urlPath);
            if (!urlPath.endsWith(SLASH)) {
                sb.append(SLASH);
            }
        }

        // plugin
        if (!StringUtils.isEmpty(pluginName)) {
            sb.append(CakePhpUtils.toUnderscoreCase(pluginName)).append(SLASH);
        }

        sb.append(controllerId)
                .append(SLASH)
                .append(actionId);

        // add GET requests
        for (String value : getRequests.values()) {
            sb.append(SLASH)
                    .append(value);
        }

        // open URL
        try {
            URL url = new URL(sb.toString());
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    //~ inner class
    private static class ControllerMethodVisitor extends DefaultVisitor {

        private String targetMethodName;
        private final List<FormalParameter> params = new ArrayList<>();

        public ControllerMethodVisitor(String targetMethodName) {
            this.targetMethodName = targetMethodName;
        }

        @Override
        public void visit(MethodDeclaration node) {
            super.visit(node);
            String methodName = CodeUtils.extractMethodName(node);
            if (!methodName.equals(targetMethodName)) {
                return;
            }
            FunctionDeclaration function = node.getFunction();
            List<FormalParameter> formalParameters = function.getFormalParameters();
            params.addAll(formalParameters);
        }

        public List<FormalParameter> getParams() {
            return params;
        }
    }
}
