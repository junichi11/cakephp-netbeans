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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;

/**
 *
 * @author junichi11
 */
public class CakePhpModule {

    private PhpModule phpModule;
    private CakePhpModuleImpl impl;
    private FileObject app;

    public CakePhpModule(PhpModule phpModule, CakePhpModuleImpl impl) {
        this.phpModule = phpModule;
        this.impl = impl;
        app = impl.getDirectory(DIR_TYPE.APP);
        final PhpModule pm = phpModule;
        app.addFileChangeListener(new FileChangeAdapter() {
            @Override
            public void fileRenamed(FileRenameEvent fe) {
                String changeName = fe.getFile().getName();
                CakePreferences.setAppName(pm, changeName);
            }
        });
    }

    public enum DIR_TYPE {

        APP,
        CORE,
        PLUGIN,
        APP_PLUGIN,
        VENDOR,
        APP_VENDOR,
        APP_LIB,;
    }

    public enum FILE_TYPE {

        NONE,
        MODEL,
        VIEW,
        CONTROLLER,
        BEHAVIOR,
        HELPER,
        COMPONENT,
        WEBROOT,
        TEST,
        FIXTURE,
        CONSOLE,
        CONFIG,;

        @Override
        public String toString() {
            String name = name().toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            return name;
        }
    }

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

    public static FileObject getCakePhpDirectory(PhpModule phpModule) {
        FileObject cakePhpDirectory = null;
        if (CakePreferences.useProjectDirectory(phpModule)) {
            cakePhpDirectory = phpModule.getProjectDirectory().getFileObject(CakePreferences.getCakePhpDirPath(phpModule));
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

    public FileObject getFile(DIR_TYPE dirType, FILE_TYPE fileType, String fileName, String pluginName) {
        return impl.getFile(pluginName, dirType, fileType, fileName);
    }

    public List<FileObject> getFiles(FileObject targetDirectory, FileFilter filter) {
        if (targetDirectory == null || !targetDirectory.isFolder()) {
            return Collections.emptyList();
        }
        if (filter == null) {
            filter = new DefaultFileFilter();
        }
        List<FileObject> list = new ArrayList<FileObject>();
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

    public static CakePhpModule forPhpModule(PhpModule phpModule) {
        CakePhpModuleFactory factory = CakePhpModuleFactory.getInstance();
        return factory.create(phpModule);
    }
}
