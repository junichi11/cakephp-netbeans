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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 *
 * @author junichi11
 */
public class CakePhp2ModuleImpl extends CakePhpModuleImpl {

    private static final Logger LOGGER = Logger.getLogger(CakePhp2ModuleImpl.class.getName());
    private static final String CONTROLLER_FILE_SUFIX = "Controller"; // NOI18N
    private static final String DIR_CONTROLLER = "Controller"; // NOI18N
    private static final String DIR_VIEW = "View"; // NOI18N
    private static final String FILE_VIEW_EXT = "ctp"; // NOI18N
    private static final String DIR_THEMED = "Themed"; // NOI18N
    private static final String FILE_CONTROLLER_RELATIVE = "../../" + DIR_CONTROLLER + "/%s.php"; // NOI18N
    private static final String FILE_THEME_CONTROLLER_RELATIVE = "../../../../" + DIR_CONTROLLER + "/%s.php"; // NOI18N
    private static final String FILE_VIEW_RELATIVE = "../" + DIR_VIEW + "/%s/%s." + FILE_VIEW_EXT; // NOI18N
    private static final String FILE_THEME_VIEW_RELATIVE = "../" + DIR_VIEW + "/" + DIR_THEMED + "/%s/%s/%s." + FILE_VIEW_EXT; // NOI18N
    private static final String DIR_COMPONENT = "Component";
    private static final String DIR_HELPER = "Helper";
    private static final String DIR_BEHAVIOR = "Behavior";
    private static final String DIR_FIXTURE = "Fixture";

    // XXX #66 (problem for Mac)
    private final boolean isMac = Utilities.isMac();

    public CakePhp2ModuleImpl(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public boolean isInCakePhp() {
        FileObject console = getDirectory(DIR_TYPE.APP, FILE_TYPE.CONSOLE, null);
        if (console == null) {
            return false;
        }

        FileObject cake = getDirectory(DIR_TYPE.CORE);
        return cake != null && cake.isFolder();
    }

    @Override
    public void refresh() {
        setAppDirectory();
    }

    @Override
    public FileObject getDirectory(DIR_TYPE type, FILE_TYPE fileType, String pluginName) {
        if (type == null) {
            return null;
        }
        if (fileType == null && pluginName == null) {
            return getDirectory(type);
        }
        if (pluginName != null) {
            switch (type) {
                case APP:
                case APP_LIB:
                case APP_VENDOR:
                case CORE:
                case VENDOR:
                    return null;
                default:
                    break;
            }
        }

        FileObject directory = null;
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case APP_LIB:
            case VENDOR:
            case APP_VENDOR:
                if (fileType == null || fileType == FILE_TYPE.NONE) {
                    return getDirectory(type);
                } else {
                    return null;
                }
            case PLUGIN: // no break
            case APP_PLUGIN: // no break
                if (pluginName == null || pluginName.isEmpty()) {
                    return null;
                }
                if (fileType == null) {
                    return getDirectory(type).getFileObject(pluginName);
                }
                sb.append(pluginName);
                sb.append("/"); // NOI18N
            case CORE: // no break
            case APP:
                switch (fileType) {
                    case CONTROLLER:
                    case VIEW:
                    case MODEL:
                        sb.append(fileType.toString());
                        break;
                    case COMPONENT:
                        sb.append("Controller/"); // NOI18
                        sb.append(DIR_COMPONENT);
                        break;
                    case HELPER:
                        sb.append("View/"); // NOI18
                        sb.append(DIR_HELPER);
                        break;
                    case ELEMENT:
                        sb.append("View/Elements"); // NOI18
                        break;
                    case LAYOUT:
                        sb.append("View/Layouts"); // NOI18
                        break;
                    case BEHAVIOR:
                        sb.append("Model/"); // NOI18
                        sb.append(DIR_BEHAVIOR);
                        break;
                    case TEST:
                        sb.append("Test"); // NOI18
                        break;
                    case TESTCASE:
                        sb.append("Test/Case"); // NOI18
                        break;
                    case FIXTURE:
                        sb.append("Test/"); // NOI18
                        sb.append(DIR_FIXTURE);
                        break;
                    case CONFIG:
                        // XXX #66 (Nb74 problem for Mac)
                        if (isMac) {
                            return getDirectoryForMac(getDirectory(type), sb.toString(), "Config"); // NOI18N
                        } else {
                            sb.append("Config"); // NOI18
                        }
                        break;
                    case CONSOLE:
                        // XXX #66 (Nb74 problem for Mac)
                        if (isMac) {
                            return getDirectoryForMac(getDirectory(type), sb.toString(), "Console"); // NOI18N
                        } else {
                            sb.append("Console"); // NOI18
                        }
                        break;
                    case WEBROOT:
                        if (type == DIR_TYPE.CORE) {
                            return null;
                        }
                        sb.append("webroot"); // NOI18
                        break;
                    case NONE:
                        if (type == DIR_TYPE.APP_PLUGIN || type == DIR_TYPE.PLUGIN) {
                            return getDirectory(type).getFileObject(pluginName);
                        }
                        return getDirectory(type);
                    default:
                        return null;
                }
                break;
            default:
                return null;
        }
        directory = getDirectory(type);
        if (directory == null) {
            return null;
        }
        return directory.getFileObject(sb.toString());
    }

    @Override
    public FileObject getDirectory(DIR_TYPE type) {
        if (type == null) {
            return null;
        }
        FileObject sourceDirectory = getCakePhpDirectory();
        if (sourceDirectory == null) {
            LOGGER.log(Level.WARNING, "Not found source directory");
            return null;
        }
        String path = ""; // NOI18N
        switch (type) {
            case APP:
            case APP_LIB:
            case APP_PLUGIN:
            case APP_VENDOR:
                return getAppDirectory(type);
            case CORE:
                return getCoreDirectory();
            case PLUGIN:
                path = "plugins"; // NOI18N
                break;
            case VENDOR:
                path = "vendors"; // NOI18N
                break;
            default:
                throw new AssertionError();
        }

        return sourceDirectory.getFileObject(path);
    }

    private FileObject getAppDirectory(DIR_TYPE dirType) {
        FileObject appDir = getAppDirectory();
        if (appDir == null) {
            return null;
        }

        switch (dirType) {
            case APP:
                return appDir;
            case APP_LIB:
                return appDir.getFileObject("Lib"); // NOI18N
            case APP_PLUGIN:
                return appDir.getFileObject("Plugin"); // NOI18N
            case APP_VENDOR:
                return appDir.getFileObject("Vendor"); // NOI18N
            default:
                throw new AssertionError();
        }
    }

    private FileObject getCoreDirectory() {
        FileObject cakePhpDirectory = getCakePhpDirectory();
        FileObject core = cakePhpDirectory.getFileObject("lib/Cake"); // NOI18N
        if (core != null) {
            return core;
        }

        // installing with Composer
        return cakePhpDirectory.getFileObject("Vendor/pear-pear.cakephp.org/CakePHP/Cake"); // NOI18N
    }

    // XXX #66 (Nb74 problem for Mac)
    private FileObject getDirectoryForMac(FileObject root, String subpath, String directoryName) {
        if (root == null) {
            return null;
        }
        FileObject subDirectory;
        if (!StringUtils.isEmpty(subpath)) {
            subDirectory = root.getFileObject(subpath);
            if (subDirectory == null) {
                return null;
            }
        } else {
            subDirectory = root;
        }

        FileObject result = subDirectory.getFileObject(directoryName);
        if (result == null) {
            result = subDirectory.getFileObject(directoryName.toLowerCase());
        }

        return result;
    }

    @Override
    public String getFileNameWithExt(FILE_TYPE type, String name) {
        if (type != null) {
            switch (type) {
                case BEHAVIOR:
                case HELPER:
                case CONTROLLER:
                case COMPONENT:
                case FIXTURE:
                    name += type.toString();
                    break;
                case VIEW:
                case ELEMENT:
                case LAYOUT:
                    // change only file name if name is file path
                    int lastIndexOfSlash = name.lastIndexOf("/");
                    if (lastIndexOfSlash != -1) {
                        name = name.substring(0, lastIndexOfSlash) + CakePhpUtils.toUnderscoreCase(name.substring(lastIndexOfSlash));
                    } else {
                        name = CakePhpUtils.toUnderscoreCase(name);
                    }
                    break;
                default:
                    break;
            }
        }
        return name + "." + getExt(type); // NOI18N
    }

    @Override
    public String toViewDirectoryName(String name) {
        return name;
    }

    @Override
    public boolean isView(FileObject fo) {
        return isSpecifiedFile(fo, FILE_TYPE.VIEW)
                || isElement(fo)
                || isLayout(fo);
    }

    @Override
    public boolean isElement(FileObject fo) {
        return isSpecifiedFile(fo, FILE_TYPE.ELEMENT);
    }

    @Override
    public boolean isLayout(FileObject fo) {
        return isSpecifiedFile(fo, FILE_TYPE.LAYOUT);
    }

    @Override
    public FileObject getView(FileObject controller, String viewName) {
        File parent = FileUtil.toFile(controller).getParentFile();
        File view = PropertyUtils.resolveFile(parent, String.format(FILE_VIEW_RELATIVE, getViewFolderName(controller.getName()), viewName));
        if (view.isFile()) {
            return FileUtil.toFileObject(view);
        }
        return null;
    }

    @Override
    public FileObject getView(FileObject controller, String viewName, FileObject theme) {
        File parent = FileUtil.toFile(controller).getParentFile();
        File view = PropertyUtils.resolveFile(parent, String.format(FILE_THEME_VIEW_RELATIVE, theme.getName(), getViewFolderName(controller.getName()), viewName));
        if (view.isFile()) {
            return FileUtil.toFileObject(view);
        }
        return null;
    }

    @Override
    public boolean isController(FileObject fo) {
        return isSpecifiedFile(fo, FILE_TYPE.CONTROLLER);
    }

    @Override
    public FileObject getController(FileObject view) {
        File parent = FileUtil.toFile(view).getParentFile();
        File cakePhpDirectory = FileUtil.toFile(CakePhpModule.getCakePhpDirectory(phpModule));
        // for sub directory view file
        File grand = parent.getParentFile();
        File child;
        while (!grand.getName().equals("View")) { // NOI18N
            child = parent;
            parent = grand;
            grand = parent.getParentFile();
            if (grand == null || grand == cakePhpDirectory) {
                return null;
            }
            if (grand.getName().equals("Themed")) { // NOI18N
                parent = child;
                break;
            }
            if (grand.getName().equals("View")) { // NOI18N
                break;
            }
        }

        File action = PropertyUtils.resolveFile(parent, String.format(FILE_CONTROLLER_RELATIVE, getControllerFileName(parent.getName())));
        // Theme view file
        if (!action.isFile()) {
            action = PropertyUtils.resolveFile(parent, String.format(FILE_THEME_CONTROLLER_RELATIVE, getControllerFileName(parent.getName())));
        }
        if (action.isFile()) {
            return FileUtil.toFileObject(action);
        }
        return null;
    }

    private static String getControllerFileName(String viewName) {
        return viewName + CONTROLLER_FILE_SUFIX;
    }

    @Override
    public boolean isModel(FileObject fo) {
        return isSpecifiedFile(fo, FILE_TYPE.MODEL);
    }

    @Override
    public boolean isBehavior(FileObject fo) {
        return isSpecifiedFile(fo, FILE_TYPE.BEHAVIOR);
    }

    @Override
    public boolean isComponent(FileObject fo) {
        return isSpecifiedFile(fo, FILE_TYPE.COMPONENT);
    }

    @Override
    public boolean isHelper(FileObject fo) {
        return isSpecifiedFile(fo, FILE_TYPE.HELPER);
    }

    @Override
    public boolean isTest(FileObject fo) {
        if (fo == null) {
            return false;
        }
        String path = fo.getPath();
        String fileName = fo.getName();
        if (path.contains("/Test/Case/") || path.contains("/Cake/Test/") || fileName.endsWith("Test")) { // NOI18N
            return true;
        }
        return false;
    }

    @Override
    public String getTestCaseClassName(FileObject fo) {
        String className = CakePhpUtils.getClassName(fo);
        if (StringUtils.isEmpty(className)) {
            return ""; // NOI18N
        }
        return className.concat("Test"); // NOI18N
    }

    @Override
    public String getTestedClassName(FileObject testCase) {
        String className = testCase.getName();
        int indexOfTest = className.lastIndexOf("Test"); // NOI18N
        if (indexOfTest != -1) {
            return className.substring(0, indexOfTest);
        }
        return ""; // NOI18N
    }

    @Override
    public String getViewFolderName(String controllerFileName) {
        return controllerFileName.replace(CONTROLLER_FILE_SUFIX, ""); // NOI18N
    }

    @Override
    public FileObject createView(FileObject controller, PhpBaseElement phpElement) throws IOException {
        FileObject fo = null;
        if (phpElement instanceof PhpClass.Method) {
            FileObject view = controller.getFileObject("../../" + DIR_VIEW);
            if (view != null) {
                fo = view.getFileObject(getViewFolderName(controller.getName()));
                if (fo == null) {
                    fo = view.createFolder(getViewFolderName(controller.getName()));
                }
            }
            if (fo != null) {
                fo = fo.createData(phpElement.getName() + "." + FILE_VIEW_EXT);
            }
        }
        return fo;
    }

    @Override
    public FILE_TYPE getFileType(FileObject currentFile) {
        if (currentFile == null) {
            return FILE_TYPE.NONE;
        }
        String path = currentFile.getPath();
        String fileName = currentFile.getName();

        if (path.contains("/Test/Fixture/")) {
            if (fileName.endsWith("Fixture")) {
                return FILE_TYPE.FIXTURE;
            }
        } else if (path.contains("/Test/Case/")) {
            if (fileName.endsWith("Test")) {
                return FILE_TYPE.TESTCASE;
            }
        } else if (path.contains("/Test/")) {
            return FILE_TYPE.TEST;
        } else if (path.contains("/Controller/Component/")) {
            if (fileName.endsWith("Component")) {
                return FILE_TYPE.COMPONENT;
            }
        } else if (path.contains("/Controller/")) {
            if (fileName.endsWith("Controller")) {
                return FILE_TYPE.CONTROLLER;
            }
        } else if (path.contains("/View/Helper/")) {
            if (fileName.endsWith("Helper")) {
                return FILE_TYPE.HELPER;
            }
        } else if (path.contains("/View/Element/")) {
            if (CakePhpUtils.isCtpFile(currentFile)) {
                return FILE_TYPE.ELEMENT;
            }
        } else if (path.contains("/View/Layout/")) {
            if (CakePhpUtils.isCtpFile(currentFile)) {
                return FILE_TYPE.LAYOUT;
            }
        } else if (path.contains("/View/")) {
            if (CakePhpUtils.isCtpFile(currentFile)) {
                return FILE_TYPE.VIEW;
            }
        } else if (path.contains("/Model/Behavior/")) {
            if (fileName.endsWith("Behavior")) {
                return FILE_TYPE.BEHAVIOR;
            }
        } else if (path.contains("/Model/")) {
            if (FileUtils.isPhpFile(currentFile)) {
                return FILE_TYPE.MODEL;
            }
        } else if (path.contains("/Config/")) {
            return FILE_TYPE.CONFIG;
        } else if (path.contains("/webroot/")) {
            return FILE_TYPE.WEBROOT;
        } else if (path.contains("/Console/")) {
            return FILE_TYPE.CONSOLE;
        }

        return FILE_TYPE.NONE;
    }

    @Override
    public List<String> getAllPluginNames() {
        ArrayList<String> allPlugins = new ArrayList<String>();
        for (DIR_TYPE dirType : Arrays.asList(DIR_TYPE.APP_PLUGIN, DIR_TYPE.PLUGIN)) {
            FileObject directory = getDirectory(dirType);
            if (directory == null) {
                continue;
            }

            for (FileObject child : directory.getChildren()) {
                if (!child.isFolder()) {
                    continue;
                }
                allPlugins.add(child.getName());
            }
        }

        return allPlugins;
    }
}
