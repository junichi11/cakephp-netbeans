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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CheckDefaultAction extends BaseAction {

    private static final Logger LOGGER = Logger.getLogger(CheckDefaultAction.class.getName());
    private static final long serialVersionUID = 3438128008537517533L;
    private static final String CAKE_FAVICON_ICO = "org-cakephp-netbeans/favicon.ico";
    private static final CheckDefaultAction INSTANCE = new CheckDefaultAction();
    private static final String OK_NOT_FOUND = "OK:Not Found";
    private static final String OK_CHANGED = "OK:Changed";
    private JPanel panel;

    private CheckDefaultAction() {
    }

    public static CheckDefaultAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return NbBundle.getMessage(CheckDefaultAction.class, "LBL_CakePhpAction", getPureName());
    }

    @NbBundle.Messages("LBL_VerifyCakePHP=Check Default")
    @Override
    protected String getPureName() {
        return Bundle.LBL_VerifyCakePHP();
    }

    @NbBundle.Messages({
        "CheckDefaultAction.message.webroot.dir.error=Not found: webroot directory"
    })
    @Override
    protected void actionPerformed(PhpModule phpModule) {
        // is it cake module? called via shortcut
        if (!CakePhpUtils.isCakePHP(phpModule)) {
            return;
        }
        // get panel
        CheckDefaultPanel verifyPanel = getPanel();
        verifyPanel.reset();
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module == null) {
            return;
        }
        FileObject webroot = module.getWebrootDirectory(CakePhpModule.DIR_TYPE.APP);
        if (webroot == null) {
            LOGGER.log(Level.WARNING, Bundle.CheckDefaultAction_message_webroot_dir_error());
            NotifyDescriptor.Message message = new NotifyDescriptor.Message(Bundle.CheckDefaultAction_message_webroot_dir_error(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(message);
            return;
        }

        // favicon.ico
        if (isChangedFavicon(phpModule)) {
            verifyPanel.setFaviconStatusLabel(OK_CHANGED);
        }
        if (!existFile(webroot, "favicon.ico")) { // NOI18N
            verifyPanel.setFaviconStatusLabel(OK_NOT_FOUND);
        }

        // files/empty
        if (!existFile(webroot, "files/empty")) { // NOI18N
            verifyPanel.setFilesEmptyStatusLabel(OK_NOT_FOUND);
        }

        // img/...
        if (!existFile(webroot, "img/cake.icon.png")) { // NOI18N
            verifyPanel.setCakeIconStatusLabel(OK_NOT_FOUND);
        }
        if (!existFile(webroot, "img/cake.power.gif")) { // NOI18N
            verifyPanel.setCakePowerStatusLabel(OK_NOT_FOUND);
        }
        if (!existFile(webroot, "img/test-error-icon.png")) { // NOI18N
            verifyPanel.setTestErrorIconStatusLabel(OK_NOT_FOUND);
        }
        if (!existFile(webroot, "img/test-fail-icon.png")) { // NOI18N
            verifyPanel.setTestFailIconStatusLabel(OK_NOT_FOUND);
        }
        if (!existFile(webroot, "img/test-pass-icon.png")) { // NOI18N
            verifyPanel.setTestPassIconStatusLabel(OK_NOT_FOUND);
        }
        if (!existFile(webroot, "img/test-skip-icon.png")) { // NOI18N
            verifyPanel.setTestSkipIconStatusLabel(OK_NOT_FOUND);
        }

        // css/cake.generic.css
        if (!existFile(webroot, "css/cake.generic.css")) { // NOI18N
            verifyPanel.setCakeGenericCssStatusLabel(OK_NOT_FOUND);
        }

        // test.php
        if (!existFile(webroot, "test.php")) { // NOI18N
            verifyPanel.setTestPhpStatusLabel(OK_NOT_FOUND);
        }

        // Config/core.php :Session name
        if (isChangedSessionName(phpModule)) {
            verifyPanel.setSessionCookieStatusLabel(OK_CHANGED);
        }

        // Config/core.php :debug level
        // open panel
        verifyPanel.showDialog();
    }

    /**
     * Verify favicon.ico
     *
     * @param phpModule
     * @return true if it has been changed or not exist, otherwise false.
     */
    private boolean isChangedFavicon(PhpModule phpModule) {
        boolean isChanged = false;
        FileObject favicon = FileUtil.getConfigFile(CAKE_FAVICON_ICO);
        assert favicon != null;
        FileObject targetFavicon = null;
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return false;
        }
        FileObject webrootDirectory = cakeModule.getWebrootDirectory(CakePhpModule.DIR_TYPE.APP);
        if (webrootDirectory != null) {
            targetFavicon = webrootDirectory.getFileObject("favicon.ico"); // NOI18N
        }
        // not found
        if (targetFavicon == null) {
            return true;
        }
        try (InputStream originalInputStream = favicon.getInputStream(); InputStream targetInputStream = targetFavicon.getInputStream()) {
            int originalData;
            int targetData;
            do {
                originalData = originalInputStream.read();
                targetData = targetInputStream.read();
                if (originalData != targetData) {
                    originalInputStream.close();
                    targetInputStream.close();
                    return true;
                }
            } while ((originalData != -1) && (targetData != -1));
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return isChanged;
    }

    /**
     * Verify whether specified files exist
     *
     * @param webroot
     * @param path
     * @return
     */
    private boolean existFile(FileObject webroot, String path) {
        if (webroot == null) {
            return false;
        }
        FileObject fileObject = webroot.getFileObject(path);
        return fileObject != null;
    }

    /**
     * Verify Session.cookie(default CAKEPHP)
     *
     * @param phpModule
     * @return
     */
    private boolean isChangedSessionName(PhpModule phpModule) {
        FileObject config;
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return false;
        }

        // CakePHP 1.x, 2.x core.php
        config = cakeModule.getConfigFile();

        if (config == null) {
            return false;
        }

        try {
            List<String> lines = config.asLines("UTF-8"); // NOI18N
            boolean existSessionCookie = false;
            for (String line : lines) {
                if (line.matches("^.*'Session\\.cookie', *'CAKEPHP'.*$")) { // NOI18N
                    return false;
                }
                if (line.matches("^.*'Session\\.cookie',.*$")) { // NOI18N
                    existSessionCookie = true;
                }
                if (line.matches("^.*'cookie' *=> *'CAKEPHP'.*$")) { // NOI18N
                    return false;
                }
                if (line.matches("^.*'cookie' *=>.*$")) { // NOI18N
                    existSessionCookie = true;
                }
            }
            if (!existSessionCookie) {
                return false;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    /**
     * Get CheckDefaultPanel
     *
     * @return
     */
    private CheckDefaultPanel getPanel() {
        if (panel == null) {
            panel = new CheckDefaultPanel();
        }
        return (CheckDefaultPanel) panel;
    }
}
