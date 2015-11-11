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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.versions.CakeVersion;
import org.cakephp.netbeans.versions.Versionable;
import org.cakephp.netbeans.versions.Versionable.VERSION_TYPE;
import org.cakephp.netbeans.versions.Versions;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;

/**
 *
 * @author junichi11
 */
public class CakePhpModule implements ChangeListener {

    private final PhpModule phpModule;
    private CakePhpModuleImpl impl;
    private FileObject appDirectory = null;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final FileChangeAdapter fileChangeAdapter = new FileChangeAdapter() {
        @Override
        public void fileRenamed(FileRenameEvent fe) {
            String changeName = fe.getFile().getName();
            CakePreferences.setAppName(phpModule, changeName);
        }
    };
    public static String PROPERTY_CHANGE_CAKE = "property-change-cake"; // NOI18N

    public CakePhpModule(PhpModule phpModule, CakePhpModuleImpl impl) {
        this.phpModule = phpModule;
        this.impl = impl;
        setAppDirectory();
    }

    private void setAppDirectory() {
        FileObject newAppDirectory = impl.getDirectory(DIR_TYPE.APP);
        if (newAppDirectory == null) {
            return;
        }
        if (appDirectory == null || appDirectory != newAppDirectory) {
            if (appDirectory != null) {
                appDirectory.removeFileChangeListener(fileChangeAdapter);
            }
            appDirectory = newAppDirectory;
            appDirectory.addFileChangeListener(fileChangeAdapter);
        }
    }

    public PhpModule getPhpModule() {
        return phpModule;
    }

    public String getAppName() {
        FileObject appName = getDirectory(DIR_TYPE.APP);
        if (appName != null) {
            return appName.getName();
        }
        return null;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        impl.refresh();
    }

    void setImpl(CakePhpModuleImpl impl) {
        this.impl = impl;
    }

    public enum DIR_TYPE {

        NONE,
        APP,
        CORE,
        BASER,
        PLUGIN,
        APP_PLUGIN,
        BASER_PLUGIN,
        VENDOR,
        APP_VENDOR,
        APP_LIB,;

        public boolean isPlugin() {
            return this == APP_PLUGIN || this == PLUGIN || this == BASER_PLUGIN;
        }

        public boolean isCake() {
            return this == CORE
                    || this == APP
                    || this == PLUGIN
                    || this == APP_PLUGIN
                    || this == APP_VENDOR
                    || this == APP_LIB;
        }

    }

    public enum FILE_TYPE {

        NONE,
        MODEL,
        VIEW,
        ELEMENT,
        LAYOUT,
        CONTROLLER,
        BEHAVIOR,
        HELPER,
        COMPONENT,
        WEBROOT,
        TEST,
        TESTCASE,
        FIXTURE,
        CONSOLE,
        CONFIG,
        TMP,;

        @Override
        public String toString() {
            String name = name().toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            return name;
        }
    }

    public static final List<DIR_TYPE> ALL_PLUGINS = Arrays.asList(DIR_TYPE.APP_PLUGIN, DIR_TYPE.PLUGIN, DIR_TYPE.BASER_PLUGIN);

    public FileObject getConfigFile() {
        return impl.getConfigFile();
    }

    public FileObject getViewDirectory(DIR_TYPE type) {
        return impl.getViewDirectory(type);
    }

    public FileObject getViewDirectory(DIR_TYPE type, String pluginName) {
        return impl.getViewDirectory(type, pluginName);
    }

    public FileObject getViewFile(DIR_TYPE type, String directoryName, String fileName) {
        return impl.getViewFile(type, directoryName, fileName);
    }

    public FileObject getViewFile(DIR_TYPE type, String directoryName, String fileName, String pluginName) {
        return impl.getViewFile(type, directoryName, fileName, pluginName);
    }

    public FileObject getControllerDirectory(DIR_TYPE type) {
        return impl.getControllerDirectory(type);
    }

    public FileObject getControllerDirectory(DIR_TYPE type, String pluginName) {
        return impl.getControllerDirectory(type, pluginName);
    }

    public FileObject getControllerFile(DIR_TYPE type, String fileName) {
        return impl.getControllerFile(type, fileName);
    }

    public FileObject getControllerFile(DIR_TYPE type, String fileName, String pluginName) {
        return impl.getControllerFile(type, fileName, pluginName);
    }

    public FileObject getModelDirectory(DIR_TYPE type) {
        return impl.getModelDirectory(type);
    }

    public FileObject getModelDirectory(DIR_TYPE type, String pluginName) {
        return impl.getModelDirectory(type, pluginName);
    }

    public FileObject getModelFile(DIR_TYPE type, String fileName) {
        return impl.getModelFile(type, fileName);
    }

    public FileObject getModelFile(DIR_TYPE type, String fileName, String pluginName) {
        return impl.getModelFile(type, fileName, pluginName);
    }

    public FileObject getComponentDirectory(DIR_TYPE type) {
        return impl.getComponentDirectory(type);
    }

    public FileObject getComponentDirectory(DIR_TYPE type, String pluginName) {
        return impl.getComponentDirectory(type, pluginName);
    }

    public FileObject getComponentFile(DIR_TYPE type, String fileName) {
        return impl.getComponentFile(type, fileName);
    }

    public FileObject getComponentFile(DIR_TYPE type, String fileName, String pluginName) {
        return impl.getComponentFile(type, fileName, pluginName);
    }

    public FileObject getHelperDirectory(DIR_TYPE type) {
        return impl.getHelperDirectory(type);
    }

    public FileObject getHelperDirectory(DIR_TYPE type, String pluginName) {
        return impl.getHelperDirectory(type, pluginName);
    }

    public FileObject getHelperFile(DIR_TYPE type, String fileName) {
        return impl.getHelperFile(type, fileName);
    }

    public FileObject getHelperFile(DIR_TYPE type, String fileName, String pluginName) {
        return impl.getHelperFile(type, fileName, pluginName);
    }

    public FileObject getBehaviorDirectory(DIR_TYPE type) {
        return impl.getBehaviorDirectory(type);
    }

    public FileObject getBehaviorDirectory(DIR_TYPE type, String pluginName) {
        return impl.getBehaviorDirectory(type, pluginName);
    }

    public FileObject getBehaviorFile(DIR_TYPE type, String fileName) {
        return impl.getBehaviorFile(type, fileName);
    }

    public FileObject getBehaviorFile(DIR_TYPE type, String fileName, String pluginName) {
        return impl.getBehaviorFile(type, fileName, pluginName);
    }

    public FileObject getConfigDirectory(DIR_TYPE type) {
        return impl.getConfigDirectory(type);
    }

    public FileObject getConfigDirectory(DIR_TYPE type, String pluginName) {
        return impl.getConfigDirectory(type, pluginName);
    }

    public FileObject getTestDirectory(DIR_TYPE type) {
        return impl.getTestDirectory(type);
    }

    public FileObject getTestDirectory(DIR_TYPE type, String pluginName) {
        return impl.getTestDirectory(type, pluginName);
    }

    public FileObject getFixtureDirectory(DIR_TYPE type) {
        return impl.getFixtureDirectory(type);
    }

    public FileObject getFixtureDirectory(DIR_TYPE type, String pluginName) {
        return impl.getFixtureDirectory(type, pluginName);
    }

    public FileObject getFixtureFile(DIR_TYPE type, String fileName) {
        return impl.getFixtureFile(type, fileName);
    }

    public FileObject getFixtureFile(DIR_TYPE type, String fileName, String pluginName) {
        return impl.getFixtureFile(type, fileName, pluginName);
    }

    public FileObject getWebrootDirectory(DIR_TYPE type) {
        return impl.getWebrootDirectory(type);
    }

    public FileObject getConsoleDirectory(DIR_TYPE type) {
        return impl.getConsoleDirectory(type);
    }

    public FileObject getConsoleDirectory(DIR_TYPE type, String pluginName) {
        return impl.getConsoleDirectory(type, pluginName);
    }

    public FileObject getCurrentPluginDirectory(FileObject currentFile) {
        return impl.getCurrentPluginDirectory(currentFile);
    }

    public String getCurrentPluginName(FileObject currentFile) {
        return impl.getCurrentPluginName(currentFile);
    }

    @CheckForNull
    public static FileObject getCakePhpDirectory(PhpModule phpModule) {
        if (phpModule == null) {
            return null;
        }
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            return null;
        }

        String cakePhpDirRelativePath = CakePreferences.getCakePhpDirPath(phpModule);
        FileObject cakePhpDirectory;
        if (!StringUtils.isEmpty(cakePhpDirRelativePath)) {
            cakePhpDirectory = sourceDirectory.getFileObject(cakePhpDirRelativePath);
        } else {
            cakePhpDirectory = phpModule.getSourceDirectory();
        }
        return cakePhpDirectory;
    }

    public FileObject getDirectory(DIR_TYPE type) {
        return impl.getDirectory(type);
    }

    public FileObject getDirectory(DIR_TYPE dirType, FILE_TYPE fileType, String pluginName) {
        return impl.getDirectory(dirType, fileType, pluginName);
    }

    public List<FileObject> getDirectories(DIR_TYPE dirType, FILE_TYPE fileType, String pluginName) {
        return impl.getDirectories(dirType, fileType, pluginName);
    }

    public DIR_TYPE getDirectoryType(FileObject currentFile) {
        return impl.getDirectoryType(currentFile);
    }

    public FILE_TYPE getFileType(FileObject currentFile) {
        return impl.getFileType(currentFile);
    }

    public FileObject getFile(DIR_TYPE dirType, FILE_TYPE fileType, String fileName, String pluginName) {
        return impl.getFile(pluginName, dirType, fileType, fileName);
    }

    public FileObject getFile(List<DIR_TYPE> dirTypes, FILE_TYPE fileType, String fileName, String pluginName) {
        FileObject file = null;
        for (DIR_TYPE dirType : dirTypes) {
            file = getFile(dirType, fileType, fileName, pluginName);
            if (file != null) {
                break;
            }
        }
        return file;
    }

    public List<FileObject> getFiles(FileObject targetDirectory, FileFilter filter) {
        if (targetDirectory == null || !targetDirectory.isFolder()) {
            return Collections.emptyList();
        }
        if (filter == null) {
            filter = new DefaultFileFilter();
        }
        List<FileObject> list = new ArrayList<>();
        FileObject[] children = targetDirectory.getChildren();
        for (FileObject child : children) {
            if (child.isFolder()) {
                continue;
            }
            if (filter.accept(child)) {
                list.add(child);
            }
        }
        return list;
    }

    public String getFileNameWithExt(FILE_TYPE type, String name) {
        return impl.getFileNameWithExt(type, name);
    }

    public boolean isController(FileObject fo) {
        return impl.isController(fo);
    }

    public FileObject getController(FileObject view) {
        return impl.getController(view);
    }

    public boolean isModel(FileObject fo) {
        return impl.isModel(fo);
    }

    public boolean isBehavior(FileObject fo) {
        return impl.isBehavior(fo);
    }

    public boolean isView(FileObject fo) {
        return impl.isView(fo);
    }

    public boolean isElement(FileObject fo) {
        return impl.isElement(fo);
    }

    public boolean isLayout(FileObject fo) {
        return impl.isLayout(fo);
    }

    public FileObject getView(FileObject controller, String viewName) {
        return impl.getView(controller, viewName);
    }

    public FileObject getView(FileObject controller, String viewName, FileObject theme) {
        return impl.getView(controller, viewName, theme);
    }

    public boolean isHelper(FileObject fo) {
        return impl.isHelper(fo);
    }

    public boolean isComponent(FileObject fo) {
        return impl.isComponent(fo);
    }

    public boolean isTest(FileObject fo) {
        return impl.isTest(fo);
    }

    public String getTestCaseClassName(FileObject fo) {
        return impl.getTestCaseClassName(fo);
    }

    public String getTestedClassName(FileObject testCase) {
        return impl.getTestedClassName(testCase);
    }

    public String getViewFolderName(String controllerFileName) {
        return impl.getViewFolderName(controllerFileName);
    }

    public FileObject createView(FileObject controller, PhpBaseElement phpElement) throws IOException {
        return impl.createView(controller, phpElement);
    }

    public boolean isInCakePhp() {
        return impl.isInCakePhp();
    }

    public Set<String> getAllPluginNames() {
        return impl.getAllPluginNames();
    }

    /**
     * Get {@link Versions}.
     *
     * @return {@link Versions}
     */
    public Versions getVersions() {
        return impl.getVersions();
    }

    /**
     * Get {@link Versionable} for {@code VERSION_TYPE}.
     *
     * @param versionType {@code VERSION_TYPE}
     * @return {@link Versionable}
     */
    public Versionable getVersion(Versionable.VERSION_TYPE versionType) {
        return impl.getVersions().getVersion(versionType);
    }

    /**
     * Get {@link CakeVersion}.
     *
     * @return {@link CakeVersion}
     */
    @CheckForNull
    public CakeVersion getCakeVersion() {
        return (CakeVersion) getVersion(VERSION_TYPE.CAKEPHP);
    }

    /**
     * Check CakePHP major version.
     *
     * @param majorVersion major version number
     * @return {@code true} if specified version, {@code false} otherwise.
     */
    public boolean isCakePhp(int majorVersion) {
        CakeVersion version = getCakeVersion();
        if (version == null) {
            return false;
        }
        return version.isCakePhp(majorVersion);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void notifyPropertyChanged(PropertyChangeEvent event) {
        if (PROPERTY_CHANGE_CAKE.equals(event.getPropertyName())) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    refreshNodes();
                    reset();
                }
            });
        }
    }

    void reset() {
        CakePhpModuleFactory.getInstance().reset(this);
    }

    void refreshNodes() {
        propertyChangeSupport.firePropertyChange(PROPERTY_CHANGE_CAKE, null, null);
    }

    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        return impl.getPhpModuleProperties(phpModule);
    }

    @CheckForNull
    public static CakePhpModule forPhpModule(PhpModule phpModule) {
        if (phpModule == null) {
            phpModule = PhpModule.Factory.inferPhpModule();
        }
        if (phpModule == null) {
            return null;
        }
        CakePhpModuleFactory factory = CakePhpModuleFactory.getInstance();
        return factory.create(phpModule);
    }
}
