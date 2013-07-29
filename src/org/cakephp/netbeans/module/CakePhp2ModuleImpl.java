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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author junichi11
 */
public class CakePhp2ModuleImpl extends CakePhpModuleImpl {

    private static final Logger LOGGER = Logger.getLogger(CakePhp2ModuleImpl.class.getName());
    private static final String CONTROLLER_FILE_SUFIX = "Controller"; // NOI18N
    private static final String DIR_CONTROLLER = "Controller"; // NOI18N
    private static final String DIR_MODEL = "Model";
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

    public CakePhp2ModuleImpl(PhpModule phpModule) {
        super(phpModule);
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
                        sb.append("View/Element"); // NOI18
                        break;
                    case LAYOUT:
                        sb.append("View/Layout"); // NOI18
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
                        sb.append("Config"); // NOI18
                        break;
                    case CONSOLE:
                        sb.append("Console"); // NOI18
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
        String app = CakePreferences.getAppName(phpModule);
        switch (type) {
            case APP:
                path = app;
                break;
            case APP_LIB:
                path = app + "/Lib"; // NOI18N
                break;
            case APP_PLUGIN:
                path = app + "/Plugin"; // NOI18N
                break;
            case APP_VENDOR:
                path = app + "/Vendor"; // NOI18N
                break;
            case CORE:
                path = "lib/Cake"; // NOI18N
                break;
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
        if (fo == null || !fo.isData() || !fo.getExt().equals(FILE_VIEW_EXT)) {
            return false;
        }
        // Theme view file  View/Themed/ThemeName/Controller/View.ctp
        if (DIR_VIEW.equals(fo.getFileObject("../../../../").getName())) { // NOI18N
            return true;
        }
        File file = FileUtil.toFile(fo);
        File parent = file.getParentFile(); // controller
        if (parent == null) {
            return false;
        }
        parent = parent.getParentFile(); // scripts
        if (parent == null) {
            return false;
        }
        return DIR_VIEW.equals(parent.getName());
    }

    @Override
    public boolean isElement(FileObject fo) {
        if (fo == null) {
            return false;
        }
        String path = fo.getPath();
        if (path.contains("/View/Elements/")) { // NOI18N
            return true;
        }
        return false;
    }

    @Override
    public boolean isLayout(FileObject fo) {
        if (fo == null) {
            return false;
        }
        String path = fo.getPath();
        if (path.contains("/View/Layouts/")) { // NOI18N
            return true;
        }
        return false;
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

    private boolean isControllerFileName(String filename) {
        return filename.endsWith(CONTROLLER_FILE_SUFIX);
    }

    @Override
    public boolean isController(FileObject fo) {
        return fo != null
                && fo.isData()
                && isControllerFileName(fo.getName())
                && fo.getParent().getNameExt().equals(DIR_CONTROLLER);
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
        return isSpecifiedFile(fo, DIR_MODEL);
    }

    @Override
    public boolean isBehavior(FileObject fo) {
        return isSpecifiedFile(fo, DIR_BEHAVIOR);
    }

    @Override
    public boolean isComponent(FileObject fo) {
        return isSpecifiedFile(fo, DIR_COMPONENT);
    }

    @Override
    public boolean isHelper(FileObject fo) {
        return isSpecifiedFile(fo, DIR_HELPER);
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

        if (path.contains("/Controller/Component/")) {
            return FILE_TYPE.COMPONENT;
        } else if (path.contains("/Controller/")) {
            return FILE_TYPE.CONTROLLER;
        } else if (path.contains("/View/Helper/")) {
            return FILE_TYPE.HELPER;
        } else if (path.contains("/View/Element/")) {
            return FILE_TYPE.ELEMENT;
        } else if (path.contains("/View/Layout/")) {
            return FILE_TYPE.LAYOUT;
        } else if (path.contains("/View/")) {
            return FILE_TYPE.VIEW;
        } else if (path.contains("/Model/Behavior/")) {
            return FILE_TYPE.BEHAVIOR;
        } else if (path.contains("/Model/")) {
            return FILE_TYPE.MODEL;
        } else if (path.contains("/Config/")) {
            return FILE_TYPE.CONFIG;
        } else if (path.contains("/webroot/")) {
            return FILE_TYPE.WEBROOT;
        } else if (path.contains("/Test/Fixture/")) {
            return FILE_TYPE.FIXTURE;
        } else if (path.contains("/Test/Case/")) {
            return FILE_TYPE.TESTCASE;
        } else if (path.contains("/Test/")) {
            return FILE_TYPE.TEST;
        } else if (path.contains("/Console/")) {
            return FILE_TYPE.CONSOLE;
        }

        return FILE_TYPE.NONE;
    }
}
