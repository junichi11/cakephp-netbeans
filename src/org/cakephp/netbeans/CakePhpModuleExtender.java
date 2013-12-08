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
package org.cakephp.netbeans;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.cakephp.netbeans.commands.CakeScript;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.options.CakePhpOptions;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.ui.wizards.DBConfigPanel;
import org.cakephp.netbeans.ui.wizards.NewProjectConfigurationDetailPanel;
import org.cakephp.netbeans.ui.wizards.NewProjectConfigurationPanel;
import org.cakephp.netbeans.util.CakePhpFileUtils;
import org.cakephp.netbeans.util.CakePhpSecurityString;
import org.cakephp.netbeans.util.CakeVersion;
import org.cakephp.netbeans.util.CakeZipEntryFilter;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.composer.api.Composer;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender.ExtendingException;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * @author juncihi11
 */
public class CakePhpModuleExtender extends PhpModuleExtender {

    static final Logger LOGGER = Logger.getLogger(CakePhpModuleExtender.class.getName());
    private static final String GIT_COMMAND = "Git Command : "; // NOI18N
    private static final String GIT_GITHUB_COM_CAKEPHP_CAKEPHP_GIT = "git://github.com/cakephp/cakephp.git"; // NOI18N
    private static final String ADD_COMMAND = "add"; // NOI18N
    private static final String BRANCH_MASTER_MERGE = "branch.master.merge"; // NOI18N
    private static final String BRANCH_MASTER_REMOTE = "branch.master.remote"; // NOI18N
    private static final String CONFIG_COMMAND = "config"; // NOI18N
    private static final String GIT = "git"; // NOI18N
    private static final String GIT_REPO = "/.git"; // NOI18N
    private static final String INIT_COMMAND = "init"; // NOI18N
    private static final String ORIGIN = "origin"; // NOI18N
    private static final String PULL_COMMAND = "pull"; // NOI18N
    private static final String REFS_HEADS = "refs/heads/master"; // NOI18N
    private static final String REMOTE_COMMAND = "remote"; // NOI18N
    private static final String UTF8 = "UTF-8"; // NOI18N
    private NewProjectConfigurationPanel panel = null;
    // CakePHP 1.x, 2.x : app, CakePHP 3.x : App
    private String defaultAppName;

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        boolean isValid = getErrorMessage() == null;
        Container parent = getPanel().getParent();
        if (parent != null) {
            parent = parent.getParent();
        }
        if (parent != null) {
            setComponentsEnabled(parent, isValid);
        }
        return isValid;
    }

    @Override
    public String getErrorMessage() {
        return getPanel().getErrorMessage();
    }

    @Override
    public String getWarningMessage() {
        return null;
    }

    @NbBundle.Messages({
        "# {0} - name",
        "CakePhpModuleExtender.extending.exception=This project might be broken: {0}"
    })
    @Override
    public Set<FileObject> extend(PhpModule phpModule) throws ExtendingException {
        FileObject targetDirectory = phpModule.getSourceDirectory();
        if (targetDirectory == null) {
            // broken project
            throw new ExtendingException(Bundle.CakePhpModuleExtender_extending_exception(phpModule.getName()));
        }

        // disabled components
        Container parent = getPanel().getParent().getParent();
        setComponentsEnabled(parent, false);
        getPanel().getProgressTextField().setVisible(true);

        // install CakePHP
        installCakePHP(phpModule, targetDirectory);
        targetDirectory.refresh(true);

        if (getPanel().isComposer()) {
            return Collections.emptySet();
        }

        setAppName(targetDirectory);

        // change tmp directory permission
        FileObject tmp = targetDirectory.getFileObject(defaultAppName + "/tmp"); // NOI18N
        if (tmp != null) {
            CakePhpFileUtils.chmodTmpDirectory(tmp);
        }

        // set default configurations
        setIgnoreTmpDirectory(phpModule);
        setAutoCreateViewFile(phpModule);

        // set enabled
        CakePreferences.setEnabled(phpModule, Boolean.TRUE);

        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        FileObject config = null;
        if (module != null) {
            config = module.getConfigFile();
        }

        if (config != null) {
            // change security string
            changeSecurityString(config);
        } else {
            LOGGER.log(Level.WARNING, "Not found: config file(core.php)");
        }

        // create database file
        createDBFile(phpModule);

        return getInitialFiles(config, targetDirectory);
    }

    /**
     * Change enabled componets
     *
     * @param parent root container
     * @param enabled enabled true, disable false
     */
    private void setComponentsEnabled(Container parent, boolean enabled) {
        parent.setEnabled(enabled);
        for (Component component : parent.getComponents()) {
            if (component instanceof Container) {
                setComponentsEnabled((Container) component, enabled);
            } else {
                component.setEnabled(false);
            }
        }
    }

    private void createDBFile(PhpModule phpModule) {
        // create database.php
        if (CakeVersion.getInstance(phpModule).isCakePhp(3)) {
            createDatasourcesFile(phpModule);
        } else {
            createDatabaseFile(phpModule);
        }
    }

    /**
     * Create database.php file (CakePHP 1.x, 2.x)
     *
     * @param phpModule
     */
    @NbBundle.Messages({
        "CakePhpModuleExtender.not.found.configdir=Not found: app config directoy"
    })
    private void createDatabaseFile(PhpModule phpModule) {
        // create database.php file
        NewProjectConfigurationPanel p = getPanel();
        NewProjectConfigurationDetailPanel detailPanel = NewProjectConfigurationDetailPanel.getDefault();
        if (p.isDatabasePhp()) {
            CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
            if (cakeModule == null) {
                return;
            }
            FileObject configDirectory = cakeModule.getConfigDirectory(CakePhpModule.DIR_TYPE.APP);
            if (configDirectory == null) {
                LOGGER.log(Level.WARNING, Bundle.CakePhpModuleExtender_not_found_configdir());
                return;
            }
            try {
                OutputStream outputStream = configDirectory.createAndOpen("database.php"); // NOI18N
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream, UTF8), true); // NOI18N
                try {
                    pw.println("<?php"); // NOI18N
                    pw.println("class DATABASE_CONFIG {\n"); // NOI18N

                    if (detailPanel.isDbDefault()) {
                        writeDbSettings(phpModule, "default", pw, detailPanel.getDbDefaultPanel()); // NOI18N
                    }
                    pw.println();
                    if (detailPanel.isDbTest()) {
                        writeDbSettings(phpModule, "test", pw, detailPanel.getDbTestPanel()); // NOI18N
                    }
                    pw.println("}"); // NOI18N
                } finally {
                    outputStream.close();
                    pw.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void writeDbSettings(PhpModule phpModule, String variableName, PrintWriter pw, DBConfigPanel dbConfigPanel) {
        pw.println("\tpublic $" + variableName + " = array("); // NOI18N
        if (CakeVersion.getInstance(phpModule).isCakePhp(2)) {
            pw.println("\t\t'datasource' => 'Database/" + dbConfigPanel.getDatasource() + "',"); // NOI18N
        } else {
            pw.println("\t\t'driver' => '" + dbConfigPanel.getDatasource().toLowerCase() + "',"); // NOI18N
        }
        pw.println("\t\t'persistent' => " + String.valueOf(dbConfigPanel.isPersistent()) + ","); // NOI18N
        pw.println("\t\t'host' => '" + dbConfigPanel.getHost() + "',"); // NOI18N
        pw.println("\t\t'login' => '" + dbConfigPanel.getLogin() + "',"); // NOI18N
        pw.println("\t\t'password' => '" + String.valueOf(dbConfigPanel.getPassword()) + "',"); // NOI18N
        pw.println("\t\t'database' => '" + dbConfigPanel.getDatabase() + "',"); // NOI18N
        pw.println("\t\t'prefix' => '" + dbConfigPanel.getPrefix() + "',"); // NOI18N
        String encoding = dbConfigPanel.getEncoding();
        if (!encoding.isEmpty()) {
            pw.println("\t\t'encoding' => '" + dbConfigPanel.getEncoding() + "'"); // NOI18N
        }

        pw.println("\t);"); // NOI18N
    }

    /**
     * Create datasources.php (CakePHP 3.x)
     *
     * @param phpModule
     */
    private void createDatasourcesFile(PhpModule phpModule) {
        // TODO use app.php
//        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
//        FileObject configDirectory = cakeModule.getConfigDirectory(CakePhpModule.DIR_TYPE.APP);
//        if (configDirectory == null) {
//            return;
//        }
//        FileObject datasourcesDefault = configDirectory.getFileObject("datasources.default.php"); // NOI18N
//        if (datasourcesDefault == null) {
//            return;
//        }
//
//        // write file
//        try {
//            final OutputStream outputStream = configDirectory.createAndOpen("datasources.php"); // NOI18N
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, UTF8);
//            PrintWriter pw = new PrintWriter(outputStreamWriter);
//            NewProjectConfigurationPanel p = getPanel();
//            boolean defaultEndFlag = false;
//            try {
//                for (String line : datasourcesDefault.asLines(UTF8)) {
//                    if (defaultEndFlag == true) {
//                        pw.println(line);
//                        continue;
//                    }
//
//                    // Datasource.default
//                    if (line.contains("'datasources'")) { // NOI18N
//                        pw.println("\t'datasources' => 'Cake\\Database\\Driver\\" + p.getDatasourceTextField().getText() + "',");
//                    } else if (line.contains("'persistent'")) { // NOI18N
//                        pw.println("\t'persistent' => " + String.valueOf(p.getPersistentCheckBox().isSelected()) + ","); // NOI18N
//                    } else if (line.contains("'host'")) { // NOI18N
//                        pw.println("\t'host' => '" + p.getHostTextField().getText() + "',"); // NOI18N
//                    } else if (line.contains("'login'")) { // NOI18N
//                        pw.println("\t'login' => '" + p.getLoginTextField().getText() + "',"); // NOI18N
//                    } else if (line.contains("'password'")) { // NOI18N
//                        pw.println("\t'password' => '" + String.valueOf(p.getPasswordField().getPassword()) + "',"); // NOI18N
//                    } else if (line.contains("'database'")) { // NOI18N
//                        pw.println("\t'database' => '" + p.getDatabaseTextField().getText() + "',"); // NOI18N
//                    } else if (line.contains("'prefix'")) { // NOI18N
//                        pw.println("\t'prefix' => '" + p.getPrefixTextField().getText() + "',"); // NOI18N
//                    } else if (line.contains("'encoding'")) { // NOI18N
//                        defaultEndFlag = true;
//                        String encoding = p.getEncodingTextField().getText();
//                        if (!encoding.isEmpty()) {
//                            pw.println("\t'encoding' => '" + p.getEncodingTextField().getText() + "'"); // NOI18N
//                        } else {
//                            pw.println(line);
//                        }
//                    } else {
//                        pw.println(line);
//                    }
//                }
//            } finally {
//                pw.close();
//            }
//        } catch (IOException ex) {
//            LOGGER.log(Level.WARNING, "Fail: can't create datasources.php");
//        }
    }

    /**
     * Create cakephp directorys from git command
     *
     * @param localPath Source directory
     */
    private void createProjectFromGitCommand(FileObject localPath) {
        try {
            String repoPath = localPath.getPath();
            // env parameter
            String[] envp = {"GIT_DIR=" + repoPath + GIT_REPO, "GIT_WORK_TREE=" + repoPath}; // NOI18N
            // git commands
            String[] initCommand = {GIT, INIT_COMMAND, repoPath};
            String[] remoteAddCommand = {GIT, REMOTE_COMMAND, ADD_COMMAND, ORIGIN, GIT_GITHUB_COM_CAKEPHP_CAKEPHP_GIT};
            String[] configMergeCommand = {GIT, CONFIG_COMMAND, BRANCH_MASTER_MERGE, REFS_HEADS};
            String[] configRemoteCommand = {GIT, CONFIG_COMMAND, BRANCH_MASTER_REMOTE, ORIGIN};
            String[] pullCommand = {GIT, PULL_COMMAND};

            // Run git Command
            JTextField progressTextField = getPanel().getProgressTextField();
            progressTextField.setText(GIT_COMMAND + INIT_COMMAND);
            Process initProcess = Runtime.getRuntime().exec(initCommand, envp);
            initProcess.waitFor();
            progressTextField.setText(GIT_COMMAND + REMOTE_COMMAND + " " + ADD_COMMAND); // NOI18N
            Process remoteProcess = Runtime.getRuntime().exec(remoteAddCommand, envp);
            remoteProcess.waitFor();
            progressTextField.setText(GIT_COMMAND + CONFIG_COMMAND + " " + BRANCH_MASTER_MERGE); // NOI18N
            Process configMergeProcess = Runtime.getRuntime().exec(configMergeCommand, envp);
            configMergeProcess.waitFor();
            progressTextField.setText(GIT_COMMAND + CONFIG_COMMAND + " " + BRANCH_MASTER_REMOTE); // NOI18N
            Process configRemoteProcess = Runtime.getRuntime().exec(configRemoteCommand, envp);
            configRemoteProcess.waitFor();
            progressTextField.setText(GIT_COMMAND + PULL_COMMAND);
            Process pullProcess = Runtime.getRuntime().exec(pullCommand, envp);
            pullProcess.waitFor();
            progressTextField.setText("Complete"); // NOI18N
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Change security string. Security.salt and Security.cipherSeed.
     *
     * @param config app/Config/core.php
     */
    private void changeSecurityString(FileObject config) {
        if (config == null) {
            return;
        }
        if (!config.getNameExt().equals("core.php") && !config.getNameExt().equals("app.php")) { // NOI18N
            LOGGER.log(Level.WARNING, "Not Found core.php or app.php");
            return;
        }
        try {
            CakePhpSecurityString.changeSecurityString(config);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (NoSuchAlgorithmException nsaex) {
            // do nothing
            LOGGER.log(Level.WARNING, null, nsaex);
        }
    }

    /**
     * Get opend files. Get files to be opened When New CakePHP Project is
     * opened.
     *
     * @param config
     * @param targetDirectory
     * @return file set if plugin can find app/Config/core.php or
     * app/webroot/index.php, otherwize return empty set.
     */
    private Set<FileObject> getInitialFiles(FileObject config, FileObject targetDirectory) {
        if (config == null) {
            return Collections.emptySet();
        }

        Set<FileObject> files = new HashSet<FileObject>();
        files.add(config);
        if (files.isEmpty()) {
            FileObject index = targetDirectory.getFileObject(defaultAppName + "/webroot/index.php"); // NOI18N
            if (index != null) {
                files.add(index);
            }
        }
        return files;
    }

    /**
     * Get NewProjectConfigurationPanel
     *
     * @return panel
     */
    public NewProjectConfigurationPanel getPanel() {
        if (panel == null) {
            panel = new NewProjectConfigurationPanel();
        }
        panel.setError();
        return panel;
    }

    private void setIgnoreTmpDirectory(PhpModule phpModule) {
        boolean isIgnore = CakePhpOptions.getInstance().isIgnoreTmpDirectory();
        if (!isIgnore) {
            CakePreferences.setIgnoreTmpDirectory(phpModule, isIgnore);
        }
    }

    private void setAutoCreateViewFile(PhpModule phpModule) {
        boolean isAutoCreate = CakePhpOptions.getInstance().isAutoCreateView();
        if (isAutoCreate) {
            CakePreferences.setAutoCreateView(phpModule, isAutoCreate);
        }
    }

    /**
     * Set app name.
     *
     * @param targetDirectory
     */
    private void setAppName(FileObject targetDirectory) {
        FileObject fileObject = targetDirectory.getFileObject("App"); // NOI18N
        if (fileObject != null) {
            defaultAppName = "App"; // NOI18N
        } else {
            defaultAppName = "app"; // NOI18N
        }
    }

    private void installCakePHP(PhpModule phpModule, FileObject targetDirectory) throws ExtendingException {
        // create cakephp files
        NewProjectConfigurationPanel p = getPanel();
        if (p.isUnzip()) {
            unzipFromGitHub(p, targetDirectory);
        } else if (p.isLocalFile()) {
            unzipFromLocalZip(targetDirectory);
        } else if (p.isComposer()) {
            installWithComposer(phpModule, targetDirectory);
        } else if (p.isGit()) {
            // Linux Mac ... run git command
            createProjectFromGitCommand(targetDirectory);
        }
    }

    private void unzipFromGitHub(NewProjectConfigurationPanel p, FileObject targetDirectory) {
        // unzip
        String url = p.getSelectedUrl();
        File target = FileUtil.toFile(targetDirectory);
        boolean deleteEmpty = false;
        try {
            CakePhpFileUtils.unzip(url, target, new CakeZipEntryFilter(deleteEmpty, p.getProgressTextField()));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void unzipFromLocalZip(FileObject targetDirectory) {
        // local zip file
        String path = CakePhpOptions.getInstance().getLocalZipFilePath();
        try {
            FileUtils.unzip(path, FileUtil.toFile(targetDirectory), new ZipEntryFilterImpl());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Installing with Composer.
     *
     * @see
     * http://book.cakephp.org/2.0/en/installation/advanced-installation.html#installing-cakephp-with-composer
     * @param phpModule
     * @param targetDirectory
     * @throws
     * org.netbeans.modules.php.spi.framework.PhpModuleExtender.ExtendingException
     */
    private void installWithComposer(final PhpModule phpModule, FileObject targetDirectory) throws ExtendingException {
        // prevent NPE
        CakePreferences.setEnabled(phpModule, Boolean.FALSE);

        // run composer install command
        if (!composerInstall(targetDirectory, phpModule)) {
            return;
        }

        // run bake project command
        bakeProject(phpModule);
    }

    /**
     * Run composer install command.
     *
     * @param targetDirectory source directory
     * @param phpModule
     * @throws
     * org.netbeans.modules.php.spi.framework.PhpModuleExtender.ExtendingException
     */
    @NbBundle.Messages({
        "# {0} - name",
        "CakePhpModuleExtender.extending.exception.composer.json=failed creating composer.json: {0}",
        "# {0} - name",
        "CakePhpModuleExtender.extending.exception.composer.install=failed installing composer: {0}"
    })
    private boolean composerInstall(FileObject targetDirectory, final PhpModule phpModule) throws ExtendingException {
        boolean isSuccess = true;
        try {
            // create composer.json
            createComposerJson(targetDirectory);
        } catch (IOException ex) {
            isSuccess = false;
            throw new ExtendingException(Bundle.CakePhpModuleExtender_extending_exception_composer_json(phpModule.getName()));
        }
        if (!isSuccess) {
            return false;
        }

        // install with composer
        try {
            Composer composer = Composer.getDefault();
            composer.setWorkDir(FileUtil.toFile(targetDirectory));
            Future<Integer> result = composer.install(phpModule);
            if (result != null) {
                try {
                    result.get();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } catch (InvalidPhpExecutableException ex) {
            isSuccess = false;
            throw new ExtendingException(Bundle.CakePhpModuleExtender_extending_exception_composer_install(phpModule.getName()));
        }
        return isSuccess;
    }

    /**
     * Run bake project.
     *
     * @param phpModule
     * @throws
     * org.netbeans.modules.php.spi.framework.PhpModuleExtender.ExtendingException
     */
    @NbBundle.Messages({
        "# {0} - name",
        "CakePhpModuleExtender.extending.exception.bake.project=failed bake project - please check your php interpreter settings on Options: {0}"
    })
    private void bakeProject(final PhpModule phpModule) throws ExtendingException {
        try {
            CakeScript cakeScript = CakeScript.forComposer(phpModule);
            final String appName = getPanel().getAppName();
            CakePreferences.setAppDirectoryPath(phpModule, appName);

            // post-execution create database.php, set default settings
            Runnable postExecution = new Runnable() {
                @Override
                public void run() {
                    refreshSourceDirectory(phpModule);
                    CakePreferences.setEnabled(phpModule, Boolean.TRUE);
                    setAutoCreateViewFile(phpModule);
                    setIgnoreTmpDirectory(phpModule);
                    createDBFile(phpModule);
                    addComposerAutoload(phpModule);
                    // change core include path define in webroot/index.php, test.php
                    changeCoreIncludePath(phpModule, appName);
                }
            };

            Future<Integer> result = cakeScript.bakeProject(phpModule, appName, getPanel().useEmptyOption(), postExecution); // NOI18N
            try {
                if (result != null) {
                    result.get();
                }
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        } catch (InvalidPhpExecutableException ex) {
            throw new ExtendingException(Bundle.CakePhpModuleExtender_extending_exception_bake_project(phpModule.getName()));
        }
        refreshSourceDirectory(phpModule);
    }

    /**
     * Refresh source directory.
     *
     * @param phpModule
     */
    private void refreshSourceDirectory(PhpModule phpModule) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory != null) {
            sourceDirectory.refresh(true);
        }
    }

    /**
     * Create composer.json. Use contents of Options panel.
     *
     * @param targetDirectory
     * @throws IOException
     */
    private void createComposerJson(FileObject targetDirectory) throws IOException {
        String appName = getPanel().getAppName();

        OutputStream outputStream = null; // NOI18N
        try {
            outputStream = targetDirectory.createAndOpen("composer.json");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream, UTF8), true);
            try {
                String composerJson = CakePhpOptions.getInstance().getComposerJson();
                String placeholder = "\\{\\$nb-app-name\\}"; // NOI18N
                if (appName.isEmpty()) {
                    composerJson = composerJson.replaceAll(placeholder + "/", ""); // NOI18N
                    composerJson = composerJson.replaceAll(placeholder, ""); // NOI18N
                } else {
                    composerJson = composerJson.replaceAll(placeholder, appName); // NOI18N
                }
                pw.println(composerJson);
            } finally {
                pw.close();
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }

    /**
     * Add Composer autoload settings to bootstrap.php.
     *
     * @param phpModule
     */
    private void addComposerAutoload(PhpModule phpModule) {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return;
        }
        FileObject configDirectory = cakeModule.getConfigDirectory(CakePhpModule.DIR_TYPE.APP);
        if (configDirectory == null) {
            return;
        }
        FileObject bootstrap = configDirectory.getFileObject("bootstrap.php"); // NOI18N
        try {
            DataObject bootstrapDataObject = DataObject.find(bootstrap);
            EditorCookie ec = bootstrapDataObject.getLookup().lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }
            StyledDocument document = ec.openDocument();
            if (document == null) {
                return;
            }

            document.insertString(document.getLength(), getComposerAutoloadSettings(), null);
            ec.saveDocument();
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Change <code>CORE_INCLUDE_PATH</code> define from hard-coding to relative
     * path. In case of installing composer, we have to do this change.Target
     * file:
     * <ul>
     * <li>webroot/index.php</li>
     * <li>webroot/test.</li>
     * </ul>
     * <pre>
     *     // sourcedirectory/app
     *     define('CAKE_CORE_INCLUDE_PATH', ROOT . DS . 'Vendor' . DS. 'pear-pear.cakephp.org' . DS . 'CakePHP');
     *     // app directory is source directory
     *     define('CAKE_CORE_INCLUDE_PATH', ROOT . DS . APP_DIR . DS 'Vendor' . DS. 'pear-pear.cakephp.org' . DS . 'CakePHP');
     * </pre>
     *
     * @param phpModule
     */
    private void changeCoreIncludePath(PhpModule phpModule, String appName) {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return;
        }
        FileObject webrootDirectory = cakeModule.getWebrootDirectory(DIR_TYPE.APP);
        if (webrootDirectory == null) {
            LOGGER.log(Level.WARNING, "Not found: webroot directory({0})", phpModule.getDisplayName());
            return;
        }
        ArrayList<FileObject> files = new ArrayList<FileObject>(2);
        FileObject index = webrootDirectory.getFileObject("index.php"); // NOI18N
        if (index != null) {
            files.add(index);
        }
        FileObject test = webrootDirectory.getFileObject("test.php"); // NOI18N
        if (test != null) {
            files.add(test);
        }
        for (FileObject file : files) {
            changeCoreIncludePath(file, appName);
        }
    }

    /**
     * Change <code>CORE_INCLUDE_PATH</code> to relative path.
     *
     * @param fileObject
     * @param appName
     */
    private void changeCoreIncludePath(FileObject fileObject, String appName) {
        try {
            DataObject dataObject = DataObject.find(fileObject);
            EditorCookie ec = dataObject.getLookup().lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }
            final StyledDocument document = ec.openDocument();
            if (document == null) {
                return;
            }
            String text = document.getText(0, document.getLength());
            String replacement = "define('CAKE_CORE_INCLUDE_PATH', ROOT . DS . APP_DIR . DS . 'Vendor' . DS. 'pear-pear.cakephp.org' . DS . 'CakePHP');"; // NOI18N
            if (!StringUtils.isEmpty(appName)) {
                replacement = "define('CAKE_CORE_INCLUDE_PATH', ROOT . DS . 'Vendor' . DS. 'pear-pear.cakephp.org' . DS . 'CakePHP');"; // NOI18N
            }
            final String insertString = text.replaceFirst("define\\('CAKE_CORE_INCLUDE_PATH',(.*?;)", replacement); // NOI18N
            NbDocument.runAtomic(document, new Runnable() {
                @Override
                public void run() {
                    try {
                        document.remove(0, document.getLength());
                        document.insertString(0, insertString, null);
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
            ec.saveDocument();
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Get Composer autoload settings.
     *
     * @see
     * @return String for composer autoload settings.
     */
    private String getComposerAutoloadSettings() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n// Load Composer autoload.\n"); // NOI18N
        if (getPanel().getAppName().isEmpty()) {
            sb.append("require APP . '/Vendor/autoload.php';\n\n"); // NOI18N
        } else {
            sb.append("require ROOT . '/Vendor/autoload.php';\n\n"); // NOI18N
        }
        sb.append("// Remove and re-prepend CakePHP's autoloader as Composer thinks it is the most important.\n"); // NOI18N
        sb.append("//See https://github.com/composer/composer/commit/c80cb76b9b5082ecc3e5b53b1050f76bb27b127b\n"); // NOI18N
        sb.append("spl_autoload_unregister(array('App', 'load'));\n"); // NOI18N
        sb.append("spl_autoload_register(array('App', 'load'), true, true);\n"); // NOI18N
        return sb.toString();
    }

    //~ Inner class
    private static class ZipEntryFilterImpl implements FileUtils.ZipEntryFilter {

        private static final String CAKEPHP = "cakephp"; // NOI18N

        public ZipEntryFilterImpl() {
        }

        @Override
        public boolean accept(ZipEntry ze) {
            if (ze.isDirectory() && ze.getName().startsWith(CAKEPHP)) {
                return false;
            }
            return true;
        }

        @Override
        public String getName(ZipEntry ze) {
            String name = ze.getName();
            if (name.startsWith(CAKEPHP)) {
                int start = name.indexOf("/"); // NOI118N
                name = name.substring(start);
            }
            return name;
        }
    }
}
