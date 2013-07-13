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
public class CakePhp1ModuleImpl extends CakePhpModuleImpl {

    private static final Logger LOGGER = Logger.getLogger(CakePhp1ModuleImpl.class.getName());
    private static final String CONTROLLER_FILE_SUFIX = "_controller"; // NOI18N
    private static final String DIR_CONTROLLERS = "controllers"; // NOI18N
    private static final String DIR_MODELS = "models";
    private static final String DIR_VIEWS = "views"; // NOI18N
    private static final String FILE_VIEW_EXT = "ctp"; // NOI18N
    private static final String DIR_THEMED = "themed"; // NOI18N
    private static final String FILE_CONTROLLER_RELATIVE = "../../" + DIR_CONTROLLERS + "/%s.php"; // NOI18N
    private static final String FILE_THEME_CONTROLLER_RELATIVE = "../../../../" + DIR_CONTROLLERS + "/%s.php"; // NOI18N
    private static final String FILE_VIEW_RELATIVE = "../" + DIR_VIEWS + "/%s/%s." + FILE_VIEW_EXT; // NOI18N
    private static final String FILE_THEME_VIEW_RELATIVE = "../" + DIR_VIEWS + "/" + DIR_THEMED + "/%s/%s/%s." + FILE_VIEW_EXT; // NOI18N
    private static final String DIR_COMPONENTS = "components";
    private static final String DIR_HELPERS = "helpers";
    private static final String DIR_BEHAVIORS = "behaviors";
    private static final String DIR_FIXTURES = "fixtures";

    public CakePhp1ModuleImpl(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public FileObject getConsoleDirectory(DIR_TYPE type) {
        return getDirectory(DIR_TYPE.CORE, FILE_TYPE.CONSOLE);
    }

    @Override
    public FileObject getConsoleDirectory(DIR_TYPE type, String pluginName) {
        return getDirectory(DIR_TYPE.CORE, FILE_TYPE.CONSOLE, pluginName);
    }

    @Override
    public FileObject getDirectory(DIR_TYPE type, FILE_TYPE fileType, String pluginName) {
        if (type == null) {
            return null;
        }
        if (fileType == null && pluginName == null) {
            return getDirectory(type);
        }
        FileObject directory = null;
        String plugin = ""; // NOI18N
        switch (type) {
            case APP_LIB:    // no break
            case APP_VENDOR: // no break
            case VENDOR:     // no break
                if (pluginName != null) {
                    return null;
                }
                if (fileType == FILE_TYPE.NONE || fileType == null) {
                    return getDirectory(type);
                } else {
                    return null;
                }
            case APP_PLUGIN: // no break;
            case PLUGIN:
                if (pluginName != null && !pluginName.isEmpty()) {
                    plugin = CakePhpUtils.toUnderscoreCase(pluginName) + "/";
                    if (fileType == null) {
                        return getDirectory(type).getFileObject(plugin);
                    }
                } else {
                    return null;
                }
                break;
            default:
                if (pluginName != null && !pluginName.isEmpty()) {
                    return null;
                }
                break;
        }
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case CORE:
                String libs = "libs/"; // NOI18N
                switch (fileType) {
                    case CONTROLLER:
                    case VIEW:
                    case MODEL:
                        sb.append(libs); // NOI18N
                        sb.append(fileType.toString().toLowerCase());
                        break;
                    case COMPONENT:
                        sb.append(libs);
                        sb.append("controller/"); // NOI18N
                        sb.append(DIR_COMPONENTS);
                        break;
                    case HELPER:
                        sb.append(libs);
                        sb.append("view/"); // NOI18N
                        sb.append(DIR_HELPERS);
                        break;
                    case BEHAVIOR:
                        sb.append(libs);
                        sb.append("model/"); // NOI18N
                        sb.append(DIR_BEHAVIORS);
                        break;
                    case TEST:
                        sb.append("tests"); // NOI18N
                        break;
                    case FIXTURE:
                        sb.append("tests/"); // NOI18N
                        sb.append(DIR_FIXTURES);
                        break;
                    case CONFIG:
                        sb.append("config"); // NOI18N
                        break;
                    case CONSOLE:
                        sb.append("console"); // NOI18N
                        break;
                    case WEBROOT:
                        return null;
                    case NONE:
                        return getDirectory(type);
                    default:
                        throw new AssertionError();
                }
                break;
            case APP_PLUGIN: // no break
            case PLUGIN: // no break
                sb.append(plugin);
            case APP:
                switch (fileType) {
                    case CONTROLLER:
                    case VIEW:
                    case MODEL:
                        sb.append(fileType.toString().toLowerCase());
                        sb.append("s"); // NOI18N
                        break;
                    case COMPONENT:
                        sb.append("controllers/"); // NOI18
                        sb.append(DIR_COMPONENTS);
                        break;
                    case HELPER:
                        sb.append("views/"); // NOI18
                        sb.append(DIR_HELPERS);
                        break;
                    case BEHAVIOR:
                        sb.append("models/"); // NOI18
                        sb.append(DIR_BEHAVIORS);
                        break;
                    case TEST:
                        sb.append("tests"); // NOI18
                        break;
                    case FIXTURE:
                        sb.append("tests/"); // NOI18
                        sb.append(DIR_FIXTURES);
                        break;
                    case CONFIG:
                        sb.append("config"); // NOI18
                        break;
                    case CONSOLE:
                        return null;
                    case WEBROOT:
                        sb.append("webroot"); // NOI18
                        break;
                    case NONE:
                        if (type == DIR_TYPE.APP_PLUGIN || type == DIR_TYPE.PLUGIN) {
                            return getDirectory(type).getFileObject(plugin);
                        }
                        return getDirectory(type);
                    default:
                        throw new AssertionError();
                }
                break;
            default:
                throw new AssertionError();
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
        FileObject cakePhpDirectory = getCakePhpDirectory();
        if (cakePhpDirectory == null) {
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
                path = app + "/libs"; // NOI18N
                break;
            case APP_PLUGIN:
                path = app + "/plugins"; // NOI18N
                break;
            case APP_VENDOR:
                path = app + "/vendors"; // NOI18N
                break;
            case CORE:
                path = "cake"; // NOI18N
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

        return cakePhpDirectory.getFileObject(path);
    }

    @Override
    public String getFileNameWithExt(FILE_TYPE type, String name) {
        if (type == FILE_TYPE.CONTROLLER) {
            name += type.toString();
        }

        return CakePhpUtils.toUnderscoreCase(name) + "." + getExt(type); // NOI18N
    }

    @Override
    public String toViewDirectoryName(String name) {
        if (name == null) {
            return null;
        }
        return CakePhpUtils.toUnderscoreCase(name);
    }

    @Override
    public boolean isView(FileObject fo) {
        if (fo == null || !fo.isData() || !fo.getExt().equals(FILE_VIEW_EXT)) {
            return false;
        }
        // Theme view file  View/Themed/ThemeName/Controller/View.ctp
        if (DIR_VIEWS.equals(fo.getFileObject("../../../../").getName())) { // NOI18N
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
        return DIR_VIEWS.equals(parent.getName());
    }

    @Override
    public boolean isElement(FileObject fo) {
        if (fo == null) {
            return false;
        }
        String path = fo.getPath();
        if (path.contains("/views/elements/")) { // NOI18N
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
        if (path.contains("/views/layouts/")) { // NOI18N
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
                && fo.getParent().getNameExt().equals(DIR_CONTROLLERS);
    }

    @Override
    public FileObject getController(FileObject view) {
        File parent = FileUtil.toFile(view).getParentFile();
        File cakePhpDirectory = FileUtil.toFile(CakePhpModule.getCakePhpDirectory(phpModule));
        // for sub directory view file
        File grand = parent.getParentFile();
        File child;
        while (!grand.getName().equals("views")) { // NOI18N
            child = parent;
            parent = grand;
            grand = parent.getParentFile();
            if (grand == null || grand == cakePhpDirectory) {
                return null;
            }
            if (grand.getName().equals("themed")) { // NOI18N
                parent = child;
                break;
            }
            if (grand.getName().equals("views")) { // NOI18N
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
        return isSpecifiedFile(fo, DIR_MODELS);
    }

    @Override
    public boolean isBehavior(FileObject fo) {
        return isSpecifiedFile(fo, DIR_BEHAVIORS);
    }

    @Override
    public boolean isComponent(FileObject fo) {
        return isSpecifiedFile(fo, DIR_COMPONENTS);
    }

    @Override
    public boolean isHelper(FileObject fo) {
        return isSpecifiedFile(fo, DIR_HELPERS);
    }

    @Override
    public boolean isTest(FileObject fo) {
        if (fo == null) {
            return false;
        }
        String path = fo.getPath();
        String fileName = fo.getName();
        if (fileName.endsWith(".test")) { // NOI18N
            return true;
        }
        if (path.contains("/tests/cases/") || path.contains("/tests/test_app/")) { // NOI18N
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
        return className.concat("TestCase"); // NOI18N
    }

    @Override
    public String getTestedClassName(FileObject testCase) {
        String className = testCase.getName();
        int indexOfTest = className.lastIndexOf(".test"); // NOI18N
        if (indexOfTest != -1) {
            className = className.substring(0, indexOfTest);
            className = CakePhpUtils.getCamelCaseName(className);
            FileObject parent = testCase.getParent();

            if (className.endsWith("Controller")) { // NOI18N
                return className;
            }

            // get suffix
            String suffix = ""; // NOI18N
            if (parent != null && parent.isFolder()) {
                suffix = getClassNameSuffixForTestDirectory(parent);
            }

            // create class name
            if (!className.isEmpty()) {
                return className.concat(suffix);
            }
        }
        return ""; // NOI18N
    }

    private String getClassNameSuffixForTestDirectory(FileObject testDirectory) {
        String suffix = "";
        String parentFolderName = testDirectory.getName();
        if ("controllers".equals(parentFolderName)) { // NOI18N
            suffix = "Controller"; // NOI18N
        } else if ("models".equals(parentFolderName)) { // NOI18N
            suffix = ""; // NOI18N
        } else if ("components".equals(parentFolderName)) { // NOI18N
            suffix = "Component"; // NOI18N
        } else if ("helpers".equals(parentFolderName)) { // NOI18N
            suffix = "Helper"; // NOI18N
        } else if ("behaviors".equals(parentFolderName)) { // NOI18N
            suffix = "Behavior"; // NOI18N
        }
        return suffix;
    }

    @Override
    public String getViewFolderName(String controllerFileName) {
        return controllerFileName.replace(CONTROLLER_FILE_SUFIX, ""); // NOI18N
    }

    @Override
    public FileObject createView(FileObject controller, PhpBaseElement phpElement) throws IOException {
        FileObject fo = null;
        if (phpElement instanceof PhpClass.Method) {
            FileObject view = controller.getFileObject("../../" + DIR_VIEWS);
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
}
