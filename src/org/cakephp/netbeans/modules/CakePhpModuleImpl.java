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
package org.cakephp.netbeans.modules;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cakephp.netbeans.dotcake.Dotcake;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.versions.CakeVersion;
import org.cakephp.netbeans.versions.Versionable.VERSION_TYPE;
import org.cakephp.netbeans.versions.Versions;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class CakePhpModuleImpl {

    protected PhpModule phpModule;
    protected static final String PHP_EXT = "php"; // NOI18N
    protected static final String CTP_EXT = "ctp"; // NOI18N
    private FileObject appDirectory;
    private final Dotcake dotcake;
    private final Versions versions;

    public CakePhpModuleImpl(PhpModule phpModule, Versions versions, Dotcake dotcake) {
        this.phpModule = phpModule;
        this.versions = versions;
        this.dotcake = dotcake;
    }

    /**
     * Get {@link PhpModuleProperties}.
     *
     * @param phpModule {@link PhpModule}
     * @return
     */
    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        PhpModuleProperties properties = new PhpModuleProperties();
        FileObject webroot = getWebrootDirectory(DIR_TYPE.APP);
        if (webroot != null) {
            properties = properties.setWebRoot(webroot);
        }
        FileObject test = getTestDirectory(DIR_TYPE.APP);
        if (test != null) {
            properties = properties.setTests(test);
        }
        return properties;
    }

    public Versions getVersions() {
        return versions;
    }

    public Dotcake getDotcake() {
        return dotcake;
    }

    public static String getExt(FILE_TYPE type) {
        if (type == FILE_TYPE.VIEW
                || type == FILE_TYPE.ELEMENT
                || type == FILE_TYPE.LAYOUT) {
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
        PhpModuleProperties properties = phpModule.getLookup().lookup(PhpModuleProperties.Factory.class).getProperties();
        FileObject webroot = properties.getWebRoot();
        if (webroot == phpModule.getSourceDirectory()) {
            return getDirectory(type, FILE_TYPE.WEBROOT);
        }
        return webroot != null ? webroot : getDirectory(type, FILE_TYPE.WEBROOT);
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

    public FileObject getCurrentPluginDirectory(FileObject currentFile) {
        DIR_TYPE dirType = getDirectoryType(currentFile);
        FileObject pluginDirectory = getDirectory(dirType);
        if (pluginDirectory == null) {
            return null;
        }
        FileObject target = currentFile;
        FileObject parent = currentFile.getParent();
        while (parent != null) {
            if (parent.getName().equals("plugins") || parent.getName().equals("Plugin")) { // NOI18N
                return target;
            }
            target = parent;
            parent = target.getParent();
        }
        return null;
    }

    /**
     * Get current plugin name.
     *
     * @param currentFile current file
     * @return plugin name if current file is in Plugin, otherwise empty string.
     */
    public String getCurrentPluginName(FileObject currentFile) {
        if (currentFile == null) {
            return ""; // NOI18N
        }
        String pluginName = "";
        String currentPath = currentFile.getPath();
        Pattern pattern = Pattern.compile("/(plugins|Plugin)/(.+?)/"); // NOI18N
        Matcher matcher = pattern.matcher(currentPath);
        if (matcher.find()) {
            pluginName = matcher.group(2);
        }

        return pluginName;
    }

    protected FileObject getDirectory(DIR_TYPE type, FILE_TYPE fileType) {
        return getDirectory(type, fileType, null);
    }

    public abstract FileObject getDirectory(DIR_TYPE type, FILE_TYPE fileType, String pluginName);

    public abstract FileObject getDirectory(DIR_TYPE type);

    public abstract List<FileObject> getDirectories(DIR_TYPE type, FILE_TYPE fileType, String pluginName);

    protected FileObject getAppDirectory() {
        if (appDirectory != null) {
            return appDirectory;
        }
        setAppDirectory();

        return appDirectory;
    }

    protected void setAppDirectory() {
        String appDirectoryPath = CakePreferences.getAppDirectoryPath(phpModule, (CakeVersion) versions.getVersion(VERSION_TYPE.CAKEPHP));
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory != null && appDirectoryPath != null) {
            appDirectory = sourceDirectory.getFileObject(appDirectoryPath);
            return;
        }
        appDirectory = null;
    }

    /**
     * Get DIR_TYPE for current file.
     *
     * @param currentFile
     * @return DIR_TYPE
     */
    public DIR_TYPE getDirectoryType(FileObject currentFile) {
        String path = currentFile.getPath();
        // don't change order
        List<DIR_TYPE> allDirTypes = Arrays.asList(
                DIR_TYPE.APP_LIB, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP,
                DIR_TYPE.CORE, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR);
        for (DIR_TYPE dirType : allDirTypes) {
            FileObject directory = getDirectory(dirType);
            if (directory == null) {
                continue;
            }
            if (path.startsWith(directory.getPath())) {
                return dirType;
            }
        }
        return DIR_TYPE.NONE;
    }

    public abstract boolean isInCakePhp();

    public abstract FILE_TYPE getFileType(FileObject fileObject);

    public abstract boolean isView(FileObject fo);

    public abstract boolean isElement(FileObject fo);

    public abstract boolean isLayout(FileObject fo);

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

    protected boolean isSpecifiedFile(FileObject fo, FILE_TYPE fileType) {
        if (fo == null) {
            return false;
        }
        return fileType == getFileType(fo);
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
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }

        List<FileObject> directories = getDirectories(dirType, fileType, pluginName);
        for (FileObject directory : directories) {
            String targetPath = ""; // NOI18N
            if (directoryName != null && !directoryName.isEmpty()) {
                targetPath = toViewDirectoryName(directoryName) + "/"; // NOI18N
            }
            targetPath = targetPath + getFileNameWithExt(fileType, fileName);
            FileObject targetFile = directory.getFileObject(targetPath);
            if (targetFile != null) {
                return targetFile;
            }
        }

        // Not found target file
        return null;
    }

    public abstract void refresh();

    public abstract Set<String> getAllPluginNames();

}
