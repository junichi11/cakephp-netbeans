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
import java.io.*;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.ui.wizards.NewProjectConfigurationPanel;
import org.cakephp.netbeans.util.CakePhpFileUtils;
import org.cakephp.netbeans.util.CakePhpSecurity;
import org.cakephp.netbeans.util.CakeVersion;
import org.cakephp.netbeans.util.CakeZipEntryFilter;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

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
    private NewProjectConfigurationPanel panel = null;

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
        return getErrorMessage() == null;
    }

    @Override
    public String getErrorMessage() {
        return getPanel().getErrorMessage();
    }

    @Override
    public String getWarningMessage() {
        return null;
    }

    @Override
    public Set<FileObject> extend(PhpModule phpModule) throws ExtendingException {
        FileObject targetDirectory = phpModule.getSourceDirectory();
        if (targetDirectory == null) {
            return Collections.emptySet();
        }

        // disabled components
        Container parent = getPanel().getParent().getParent();
        enabledComponents(parent, false);

        // create cakephp files
        if (getPanel().getUnzipRadioButton().isSelected()) {
            // unzip
            Map<String, String> tagsMap = getPanel().getTagsMap();
            String url = tagsMap.get(getPanel().getVersionList().getSelectedValue().toString());
            File target = FileUtil.toFile(targetDirectory);
            boolean deleteEmpty = false;
            try {
                CakePhpFileUtils.unzip(url, target, new CakeZipEntryFilter(deleteEmpty, getPanel().getUnzipFileNameTextField()));
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            // Linux Mac ... run git command
            createProjectFromGitCommand(targetDirectory);
        }

        // change tmp directory permission
        FileObject tmp = targetDirectory.getFileObject("app/tmp"); // NOI18N
        if (tmp != null) {
            CakePhpFileUtils.chmodTmpDirectory(tmp);
        }

        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        FileObject config = module.getConfigDirectory(CakePhpModule.DIR_TYPE.APP).getFileObject("core.php"); // NOI18N
        // change security string
        changeSecurityString(config);

        // create database.php
        createDatabaseFile(phpModule);
        Set<FileObject> files = getOpenedFiles(config, targetDirectory);
        return files;
    }

    /**
     * Change enabled componets
     *
     * @param parent root container
     * @param enabled enabled true, disable false
     */
    private void enabledComponents(Container parent, boolean enabled) {
        parent.setEnabled(enabled);
        for (Component component : parent.getComponents()) {
            if (component instanceof Container) {
                enabledComponents((Container) component, enabled);
            } else {
                component.setEnabled(false);
            }
        }
    }

    /**
     * Create database.php file
     *
     * @param phpModule
     */
    private void createDatabaseFile(PhpModule phpModule) {
        // create database.php file
        FileObject configDirectory;
        NewProjectConfigurationPanel p = getPanel();
        if (p.getDatabaseCheckBox().isSelected()) {

            configDirectory = CakePhpModule.forPhpModule(phpModule).getConfigDirectory(CakePhpModule.DIR_TYPE.APP);
            try {
                PrintWriter pw = new PrintWriter(configDirectory.createAndOpen("database.php")); // NOI18N
                pw.println("<?php"); // NOI18N
                pw.println("class DATABASE_CONFIG {\n"); // NOI18N

                pw.println("\tpublic $default = array("); // NOI18N
                if (CakeVersion.getInstance(phpModule).isCakePhp(2)) {
                    pw.println("\t\t'datasource' => 'Database/" + p.getDatasourceTextField().getText() + "',"); // NOI18N
                } else {
                    pw.println("\t\t'driver' => '" + p.getDatasourceTextField().getText().toLowerCase() + "',"); // NOI18N
                }
                pw.println("\t\t'persistent' => " + String.valueOf(p.getPersistentCheckBox().isSelected()) + ","); // NOI18N
                pw.println("\t\t'host' => '" + p.getHostTextField().getText() + "',"); // NOI18N
                pw.println("\t\t'login' => '" + p.getLoginTextField().getText() + "',"); // NOI18N
                pw.println("\t\t'password' => '" + String.valueOf(p.getPasswordField().getPassword()) + "',"); // NOI18N
                pw.println("\t\t'database' => '" + p.getDatabaseTextField().getText() + "',"); // NOI18N
                pw.println("\t\t'prefix' => '" + p.getPrefixTextField().getText() + "',"); // NOI18N
                String encoding = p.getEncodingTextField().getText();
                if (!encoding.isEmpty()) {
                    pw.println("\t\t'encoding' => '" + p.getEncodingTextField().getText() + "'"); // NOI18N
                }

                pw.println("\t);"); // NOI18N
                pw.println("}"); // NOI18N

                pw.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
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
            getPanel().setGitCommandLabel(GIT_COMMAND + INIT_COMMAND);
            Process initProcess = Runtime.getRuntime().exec(initCommand, envp);
            initProcess.waitFor();
            getPanel().setGitCommandLabel(GIT_COMMAND + REMOTE_COMMAND + " " + ADD_COMMAND); // NOI18N
            Process remoteProcess = Runtime.getRuntime().exec(remoteAddCommand, envp);
            remoteProcess.waitFor();
            getPanel().setGitCommandLabel(GIT_COMMAND + CONFIG_COMMAND + " " + BRANCH_MASTER_MERGE); // NOI18N
            Process configMergeProcess = Runtime.getRuntime().exec(configMergeCommand, envp);
            configMergeProcess.waitFor();
            getPanel().setGitCommandLabel(GIT_COMMAND + CONFIG_COMMAND + " " + BRANCH_MASTER_REMOTE); // NOI18N
            Process configRemoteProcess = Runtime.getRuntime().exec(configRemoteCommand, envp);
            configRemoteProcess.waitFor();
            getPanel().setGitCommandLabel(GIT_COMMAND + PULL_COMMAND);
            Process pullProcess = Runtime.getRuntime().exec(pullCommand, envp);
            pullProcess.waitFor();
            getPanel().setGitCommandLabel("Complete"); // NOI18N
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
        if (!config.getNameExt().equals("core.php")) { // NOI18N
            LOGGER.log(Level.WARNING, "Not Found core.php");
            return;
        }
        try {
            CakePhpSecurity.changeSecurityString(config);
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
    private Set<FileObject> getOpenedFiles(FileObject config, FileObject targetDirectory) {
        if (config == null) {
            return Collections.emptySet();
        }

        Set<FileObject> files = new HashSet<FileObject>();
        if (config != null) {
            files.add(config);
        }
        if (files.isEmpty()) {
            FileObject index = targetDirectory.getFileObject("app/webroot/index.php"); // NOI18N
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
        return panel;
    }
}
