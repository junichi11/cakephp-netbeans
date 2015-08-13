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

import org.cakephp.netbeans.commands.CakeScript;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
@ActionID(
        category = "UnitTests",
        id = "org.cakephp.netbeans.ui.actions.RunBakeTestAction")
@ActionRegistration(
        displayName = "#CTL_RunBakeTestAction")
@NbBundle.Messages("CTL_RunBakeTestAction=Create Test with bake")
public class RunBakeTestAction extends BaseAction {

    private static final long serialVersionUID = -3482899506753626339L;
    private final DataObject context;
    private final FileObject targetFile;
    private final PhpModule phpModule;
    private FileObject test;

    public RunBakeTestAction(DataObject context) {
        this.context = context;
        targetFile = context.getPrimaryFile();
        phpModule = PhpModule.Factory.forFileObject(targetFile);
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @Override
    protected String getPureName() {
        return Bundle.CTL_RunBakeTestAction();
    }

    @Override
    public void actionPerformed(PhpModule phpModule) {
        if (!CakePhpUtils.isCakePHP(phpModule)) {
            // called via shortcut
            return;
        }
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        // support only CakePHP2.x and 3.x
        if (cakeModule == null) {
            return;
        }
        CakeVersion version = cakeModule.getCakeVersion();
        if (version == null || version.getMajor() <= 1) {
            return;
        }

        // already exist?
        if (existTest()) {
            UiUtils.open(test, 0);
            return;
        }

        try {
            DIR_TYPE dirType = getDirType();
            if (dirType != DIR_TYPE.APP && dirType != DIR_TYPE.APP_PLUGIN && dirType != DIR_TYPE.PLUGIN) {
                return;
            }
            String pluginName = null;
            if (dirType == DIR_TYPE.APP_PLUGIN || dirType == DIR_TYPE.PLUGIN) {
                pluginName = getPluginName(dirType);
                if (pluginName == null || pluginName.isEmpty()) {
                    return;
                }
            }

            FILE_TYPE fileType = getFileType();
            if (fileType != FILE_TYPE.NONE) {
                CakeScript.forPhpModule(phpModule, true).bakeTest(phpModule, fileType, targetFile.getName(), pluginName);
            }
        } catch (InvalidPhpExecutableException ex) {
            NotifyDescriptor descriptor = new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notifyLater(descriptor);
        }
    }

    /**
     * Get file tipe
     *
     * @return
     */
    private FILE_TYPE getFileType() {
        FILE_TYPE type = FILE_TYPE.NONE;
        if (CakePhpUtils.isController(targetFile)) {
            type = FILE_TYPE.CONTROLLER;
        }

        if (CakePhpUtils.isComponent(targetFile)) {
            type = FILE_TYPE.COMPONENT;
        }

        if (CakePhpUtils.isModel(targetFile)) {
            type = FILE_TYPE.MODEL;
        }

        if (CakePhpUtils.isBehavior(targetFile)) {
            type = FILE_TYPE.BEHAVIOR;
        }

        if (CakePhpUtils.isView(targetFile)) {
            type = FILE_TYPE.VIEW;
        }

        if (CakePhpUtils.isHelper(targetFile)) {
            type = FILE_TYPE.HELPER;
        }

        return type;
    }

    /**
     * Get directory type
     *
     * @return
     */
    private DIR_TYPE getDirType() {
        DIR_TYPE type = DIR_TYPE.APP;

        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module != null) {
            DIR_TYPE[] types = {DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
            for (DIR_TYPE t : types) {
                FileObject fo = module.getDirectory(t);
                if (fo == null) {
                    continue;
                }
                String path = fo.getPath();
                String targetPath = targetFile.getPath();
                if (targetPath.startsWith(path)) {
                    type = t;
                    break;
                }
            }
        }
        return type;
    }

    /**
     * Get plugin directory name
     *
     * @param type
     * @return
     */
    private String getPluginName(DIR_TYPE type) {
        if (type == DIR_TYPE.APP_PLUGIN || type == DIR_TYPE.PLUGIN) {
            CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
            if (module == null) {
                return null;
            }
            String path = module.getDirectory(type).getPath();
            String targetPath = targetFile.getPath();
            targetPath = targetPath.replace(path + "/", ""); // NOI18N
            String[] split = targetPath.split("/"); // NOI18N
            if (split.length > 0) {
                return split[0];
            }
        }
        return null;
    }

    private boolean existTest() {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return false;
        }
        DIR_TYPE dirType = getDirType();
        FILE_TYPE fileType = getFileType();
        String pluginName = getPluginName(dirType);
        FileObject testDirectory = cakeModule.getTestDirectory(dirType, pluginName);
        if (testDirectory == null) {
            return false;
        }
        String dirPath = fileType.toString() + "/"; // NOI18N
        switch (fileType) {
            case CONTROLLER: // no break
            case MODEL: // no break
            case VIEW:
                // do nothing
                break;
            case COMPONENT:
                dirPath = "Controller/" + dirPath; // NOI18N
                break;
            case BEHAVIOR:
                dirPath = "Model/" + dirPath; // NOI18N
                break;
            case HELPER:
                dirPath = "View/" + dirPath; // NOI18N
                break;
            default:
                return false;
        }
        dirPath = "Case/" + dirPath + targetFile.getName() + "Test." + targetFile.getExt(); // NOI18N

        test = testDirectory.getFileObject(dirPath);
        return test != null;
    }
}
