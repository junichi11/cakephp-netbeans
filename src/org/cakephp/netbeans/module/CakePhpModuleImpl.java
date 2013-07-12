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
package org.cakephp.netbeans.module;

import java.io.IOException;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class CakePhpModuleImpl {

    protected PhpModule phpModule;
    protected static String PHP_EXT = "php";
    protected static String CTP_EXT = "ctp";

    public CakePhpModuleImpl(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    public static String getExt(FILE_TYPE type) {
        if (type == FILE_TYPE.VIEW) {
            return CTP_EXT;
        }
        return PHP_EXT;
    }

    public FileObject getConfigFile() {
        FileObject configDirectory = getConfigDirectory(DIR_TYPE.APP);
        if (configDirectory != null) {
            return configDirectory.getFileObject("core.php"); // NOI18N
        }
        return null;
    }

    public FileObject getViewDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.VIEW);
    }

    public FileObject getViewDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.VIEW, pluginName);
    }

    public FileObject getViewFile(DIR_TYPE type, String directoryName, String fileName) {
        return getFile(type, FILE_TYPE.VIEW, fileName, directoryName);
    }

    public FileObject getViewFile(DIR_TYPE type, String directoryName, String fileName, String pluginName) {
        return getFile(pluginName, type, FILE_TYPE.VIEW, fileName, directoryName);
    }

    public FileObject getControllerDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.CONTROLLER);
    }

    public FileObject getControllerDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.CONTROLLER, pluginName);
    }

    public FileObject getControllerFile(DIR_TYPE type, String fileName) {
        return getFile(type, FILE_TYPE.CONTROLLER, fileName);
    }

    public FileObject getControllerFile(DIR_TYPE type, String fileName, String pluginName) {
        return getFile(pluginName, type, FILE_TYPE.CONTROLLER, fileName);
    }

    public FileObject getModelDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.MODEL);
    }

    public FileObject getModelDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.MODEL, pluginName);
    }

    public FileObject getModelFile(DIR_TYPE type, String fileName) {
        return getFile(type, FILE_TYPE.MODEL, fileName);
    }

    public FileObject getModelFile(DIR_TYPE type, String fileName, String pluginName) {
        return getFile(pluginName, type, FILE_TYPE.MODEL, fileName);
    }

    public FileObject getComponentDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.COMPONENT);
    }

    public FileObject getComponentDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.COMPONENT, pluginName);
    }

    public FileObject getComponentFile(DIR_TYPE type, String fileName) {
        return getFile(type, FILE_TYPE.COMPONENT, fileName);
    }

    public FileObject getComponentFile(DIR_TYPE type, String fileName, String pluginName) {
        return getFile(pluginName, type, FILE_TYPE.COMPONENT, fileName);
    }

    public FileObject getHelperDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.HELPER);
    }

    public FileObject getHelperDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.HELPER, pluginName);
    }

    public FileObject getHelperFile(DIR_TYPE type, String fileName) {
        return getFile(type, FILE_TYPE.HELPER, fileName);
    }

    public FileObject getHelperFile(DIR_TYPE type, String fileName, String pluginName) {
        return getFile(pluginName, type, FILE_TYPE.HELPER, fileName);
    }

    public FileObject getBehaviorDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.BEHAVIOR);
    }

    public FileObject getBehaviorDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.BEHAVIOR, pluginName);
    }

    public FileObject getBehaviorFile(DIR_TYPE type, String fileName) {
        return getFile(type, FILE_TYPE.BEHAVIOR, fileName);
    }

    public FileObject getBehaviorFile(DIR_TYPE type, String fileName, String pluginName) {
        return getFile(pluginName, type, FILE_TYPE.BEHAVIOR, fileName);
    }

    public FileObject getConfigDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.CONFIG);
    }

    public FileObject getConfigDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.CONFIG, pluginName);
    }

    public FileObject getTestDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.TEST);
    }

    public FileObject getTestDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.TEST, pluginName);
    }

    public FileObject getWebrootDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.WEBROOT);
    }

    public FileObject getWebrootDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.WEBROOT, pluginName);
    }

    public FileObject getConsoleDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.CONSOLE);
    }

    public FileObject getConsoleDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.CONSOLE, pluginName);
    }

    public FileObject getFixtureDirectory(DIR_TYPE type) {
        return getDirectory(type, FILE_TYPE.FIXTURE);
    }

    public FileObject getFixtureDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(type, FILE_TYPE.FIXTURE, pluginName);
    }

    public FileObject getFixtureFile(DIR_TYPE type, String fileName) {
        return getFile(type, FILE_TYPE.FIXTURE, fileName);
    }

    public FileObject getFixtureFile(DIR_TYPE type, String fileName, String pluginName) {
        return getFile(pluginName, type, FILE_TYPE.FIXTURE, fileName);
    }

    protected FileObject getDirectory(DIR_TYPE type, FILE_TYPE fileType) {
        return getDirectory(type, fileType, null);
    }

    public abstract FileObject getDirectory(DIR_TYPE type, FILE_TYPE fileType, String pluginName);

    public abstract FileObject getDirectory(DIR_TYPE type);

    public abstract boolean isView(FileObject fo);

    public abstract FileObject getView(FileObject controller, String viewName);

    public abstract FileObject getView(FileObject controller, String viewName, FileObject theme);

    public abstract boolean isController(FileObject fo);

    public abstract FileObject getController(FileObject view);

    public abstract boolean isModel(FileObject fo);

    public abstract boolean isBehavior(FileObject fo);

    public abstract boolean isComponent(FileObject fo);

    public abstract boolean isHelper(FileObject fo);

    public abstract boolean isTest(FileObject fo);

    public abstract String getTestCaseClassName(FileObject fo);

    public abstract String getTestedClassName(FileObject testCase);

    public abstract String getViewFolderName(String controllerFileName);

    public abstract FileObject createView(FileObject controller, PhpBaseElement phpElement) throws IOException;

    /**
     * Get file name
     *
     * @param fileType FILE_TYPE
     * @param name e.g. TreeBehavior->Tree, HtmlHelper->Html,...
     * @return file name with extension | null
     */
    public abstract String getFileNameWithExt(FILE_TYPE type, String name);

    public abstract String toViewDirectoryName(String name);

    protected final FileObject getCakePhpDirectory() {
        return CakePhpModule.getCakePhpDirectory(phpModule);
    }

    protected boolean isSpecifiedFile(FileObject fo, String directoryName) {
        return fo != null
                && fo.isData()
                && fo.getParent().getNameExt().equals(directoryName)
                && FileUtils.isPhpFile(fo);
    }

    protected FileObject getFile(String pluginName, DIR_TYPE dirType, FILE_TYPE fileType, String fileName) {
        return getFile(pluginName, dirType, fileType, fileName, null);
    }

    protected FileObject getFile(DIR_TYPE dirType, FILE_TYPE fileType, String fileName) {
        return getFile(null, dirType, fileType, fileName, null);
    }

    protected FileObject getFile(DIR_TYPE dirType, FILE_TYPE fileType, String fileName, String directoryName) {
        return getFile(null, dirType, fileType, fileName, directoryName);
    }

    /**
     * Get specified file
     *
     * @param dirType
     * @param fileType
     * @param fileName file name without feature type. (e.g. PostsController ->
     * Posts, SessionHelpser -> Session, ...)
     * @param directoryName Camelcase e.g. DebugKit
     * @param pluginName Camelcase e.g. DebugKit
     * @return
     */
    protected FileObject getFile(String pluginName, DIR_TYPE dirType, FILE_TYPE fileType, String fileName, String directoryName) {
        FileObject targetDirectory = null;
        FileObject targetFile = null;
        switch (fileType) {
            case VIEW:
                targetDirectory = getViewDirectory(dirType, pluginName);
                break;
            case CONTROLLER:
                targetDirectory = getControllerDirectory(dirType, pluginName);
                break;
            case MODEL:
                targetDirectory = getModelDirectory(dirType, pluginName);
                break;
            case HELPER:
                targetDirectory = getHelperDirectory(dirType, pluginName);
                break;
            case COMPONENT:
                targetDirectory = getComponentDirectory(dirType, pluginName);
                break;
            case BEHAVIOR:
                targetDirectory = getBehaviorDirectory(dirType, pluginName);
                break;
            case FIXTURE:
                targetDirectory = getFixtureDirectory(dirType, pluginName);
                break;
            // TODO add other files
            default:
                throw new AssertionError();
        }

        if (targetDirectory == null || fileName == null || fileName.isEmpty()) {
            return null;
        }

        String targetPath = ""; // NOI18N
        if (directoryName != null && !directoryName.isEmpty()) {
            targetPath = toViewDirectoryName(directoryName) + "/"; // NOI18N
        }
        targetPath = targetPath + getFileNameWithExt(fileType, fileName);
        targetFile = targetDirectory.getFileObject(targetPath);
        return targetFile;
    }
}
