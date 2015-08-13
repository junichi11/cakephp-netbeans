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
package org.cakephp.netbeans.ui.actions;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.cakephp.netbeans.CakePhp;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.options.CakePhpOptions;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.ProjectPropertiesSupport;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public class PHPUnitInitAction extends BaseAction {

    private static final long serialVersionUID = -8991367529276697723L;
    private static final PHPUnitInitAction INSTANCE = new PHPUnitInitAction();
    private static final Logger LOGGER = Logger.getLogger(PHPUnitInitAction.class.getName());
    private static final String BOOTSTRAP_PHPUNIT = "bootstrap_phpunit"; // NOI18N
    private static final String BOOTSTRAP_PHPUNIT_PHP = BOOTSTRAP_PHPUNIT + ".php"; // NOI18N
    private static final String NET_BEANS_SUITE = "NetBeansSuite"; // NOI18N
    private static final String NET_BEANS_SUITE_PHP = NET_BEANS_SUITE + ".php"; // NOI18N
    private static final String CONFIG_PATH = "org-cakephp-netbeans/"; // NOI18N
    private static final String CONFIG_NET_BEANS_SUITE_PHP = CONFIG_PATH + NET_BEANS_SUITE_PHP;
    private static final String CONFIG_BOOTSTRAP_PHPUNIT_PHP = CONFIG_PATH + BOOTSTRAP_PHPUNIT_PHP;
    private static final String PHPUNIT = "phpunit"; // NOI18N
    private static final String PHPUNIT_PHP = PHPUNIT + ".php"; // NOI18N
    private static final Map<String, String> MESSAGES = new HashMap<>();
    private static final String SUCCESS_MSG = "success";
    private static final String FAIL_MSG = "fail";
    private static final String BOOTSTRAP = "bootstrap";
    private FileObject webroot;

    private PHPUnitInitAction() {
    }

    public static PHPUnitInitAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return NbBundle.getMessage(ClearCacheAction.class, "LBL_CakePhpAction", getPureName());
    }

    @Override
    @NbBundle.Messages("LBL_PHPUnitInitAction=PHPUnit Test Init")
    protected String getPureName() {
        return Bundle.LBL_PHPUnitInitAction();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        // via shortcut
        if (!CakePhpUtils.isCakePHP(phpModule)) {
            // do nothing
            return;
        }
        // check version
        // CakePHP 1.x uses Simple Test
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module == null || module.getCakeVersion() == null || module.isCakePhp(1)) {
            return;
        }
        webroot = module.getWebrootDirectory(DIR_TYPE.APP);
        if (webroot == null) {
            return;
        }

        // create files
        createBootstrap();
        createNetBeansSuite(phpModule);
        createScript(phpModule);

        setPhpProjectProperties(phpModule);
        StringBuilder notifyMessage = new StringBuilder();
        for (String key : MESSAGES.keySet()) {
            notifyMessage.append(key);
            notifyMessage.append(":"); // NOI18N
            notifyMessage.append(MESSAGES.get(key));
            notifyMessage.append(" \n"); // NOI18N
        }
        NotificationDisplayer.getDefault().notify(getFullName(), ImageUtilities.loadImageIcon(CakePhp.CAKE_ICON_16, true), notifyMessage.toString(), null);
    }

    /**
     * Create webroot/bootstrap_phpunit.php file
     *
     * @return void
     */
    private void createBootstrap() {
        FileObject bootstrapPhpunit = webroot.getFileObject(BOOTSTRAP_PHPUNIT_PHP);
        if (bootstrapPhpunit == null) {
            try {
                FileObject configBootstrap = FileUtil.getConfigFile(CONFIG_BOOTSTRAP_PHPUNIT_PHP);
                configBootstrap.copy(webroot, BOOTSTRAP_PHPUNIT, "php"); // NOI18N
                MESSAGES.put(BOOTSTRAP, SUCCESS_MSG);
            } catch (IOException ex) {
                MESSAGES.put(BOOTSTRAP, FAIL_MSG);
            }
        }
    }

    /**
     * Create NetBeansSuite.php
     *
     * @param phpModule
     */
    private void createNetBeansSuite(PhpModule phpModule) {
        FileObject nbproject = CakePhpUtils.getNbproject(phpModule);
        if (nbproject == null) {
            return;
        }
        FileObject nbSuite = nbproject.getFileObject(NET_BEANS_SUITE_PHP);
        if (nbSuite != null) {
            try {
                nbSuite.delete();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        FileObject suite = FileUtil.getConfigFile(CONFIG_NET_BEANS_SUITE_PHP);
        try {
            suite.copy(nbproject, NET_BEANS_SUITE, "php"); // NOI18N
            MESSAGES.put(NET_BEANS_SUITE, SUCCESS_MSG);
        } catch (IOException ex) {
            MESSAGES.put(NET_BEANS_SUITE, FAIL_MSG);
        }
    }

    /**
     * Create Script file
     *
     * @param phpModule
     */
    @NbBundle.Messages({
        "PHPUnitInitAction.phpunit.script.overwrite.confirmation=phpunit script file already exists. Do you want to overwrite?"
    })
    private void createScript(PhpModule phpModule) {
        FileObject nbproject = CakePhpUtils.getNbproject(phpModule);

        // get phpunit path
        String phpUnit = getPHPUnitPath();
        if (phpUnit == null || phpUnit.isEmpty()) {
            MESSAGES.put(PHPUNIT, FAIL_MSG + "(isn't set phpunit option)");
            return;
        }

        String scriptFileName = getScriptFileName();

        FileObject phpUnitScript = nbproject.getFileObject(scriptFileName);
        // overwrite confirmation
        if (phpUnitScript != null) {
            NotifyDescriptor.Confirmation confirmation = new NotifyDescriptor.Confirmation(
                    Bundle.PHPUnitInitAction_phpunit_script_overwrite_confirmation(),
                    NotifyDescriptor.YES_NO_OPTION);
            if (DialogDisplayer.getDefault().notify(confirmation) != NotifyDescriptor.YES_OPTION) {
                return;
            }
        }

        FileObject script = FileUtil.getConfigFile(CONFIG_PATH + scriptFileName);
        try {
            String commandAndOptions = phpUnit;
            if (CakePhpOptions.getInstance().isTestStderr()) {
                commandAndOptions = commandAndOptions.concat(" --stderr"); // NOI18N
            }
            String format = String.format(script.asText("UTF-8"), commandAndOptions); // NOI18N

            // write file
            PrintWriter pw;
            if (phpUnitScript == null) {
                pw = new PrintWriter(new OutputStreamWriter(nbproject.createAndOpen(scriptFileName), "UTF-8"), true); // NOI18N
            } else {
                pw = new PrintWriter(new OutputStreamWriter(phpUnitScript.getOutputStream(), "UTF-8"), true); // NOI18N
            }
            try {
                pw.print(format);
            } finally {
                pw.close();
            }
            MESSAGES.put(PHPUNIT, SUCCESS_MSG);
        } catch (IOException ex) {
            MESSAGES.put(PHPUNIT, FAIL_MSG);
        }
        FileObject createdFile = nbproject.getFileObject(scriptFileName);
        FileUtil.toFile(createdFile).setExecutable(true);
    }

    private String getPHPUnitPath() {
        Preferences preference = NbPreferences.root().node("/org/netbeans/modules/php/phpunit/phpunit"); // NOI18N
        return preference.get("phpUnit.path", null); // NOI18N
    }

    /**
     * Set PHP Project Properties. Set bootstrap, script
     *
     * @param phpModule
     */
    private void setPhpProjectProperties(PhpModule phpModule) {
        Project project = ProjectPropertiesSupport.getProject(webroot);
        if (project != null) {
            String bootstrapPath = webroot.getPath() + "/" + BOOTSTRAP_PHPUNIT_PHP; // NOI18N
            String scriptPath = CakePhpUtils.getNbproject(phpModule).getPath() + "/" + getScriptFileName(); // NOI18N
            ProjectPropertiesSupport.setPHPUnit(phpModule, bootstrapPath, scriptPath);
        }
    }

    /**
     * Get script file name. php intepreter is used in NetBeans 7.4. we have to
     * use php script. #70 https://gist.github.com/nojimage/7655837
     *
     * @return phpunit.php
     */
    private String getScriptFileName() {
        return PHPUNIT_PHP;
    }
}
