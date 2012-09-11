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
package org.cakephp.netbeans.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

public final class CakePhpUtils {

    public enum DIR {

        APP,
        APP_PLUGIN,
        CORE,
        PLUGIN,;
    }

    public enum FILE {

        NONE,
        MODEL,
        VIEW,
        CONTROLLER,
        BEHAVIOR,
        HELPER,
        COMPONENT,
        CONFIG,;

        @Override
        public String toString() {
            String name = name().toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            return name;
        }
    }
    public static final int CAKE_VERSION_MAJOR = 0;
    public static final int CAKE_VERSION_MINOR = 1;
    public static final int CAKE_VERSION_REVISION = 2;
    public static final int CAKE_VERSION_NOT_STABLE = 3;
    private static final String APP = "App"; // NOI18N
    private static final String CAKE_AUTO_COMPLETION_PHP = "_cake_auto_completion_.php"; // NOI18N
    private static final String COMPONENT = "Component"; // NOI18N
    private static final String CONTROLLER_CLASS_SUFFIX = "Controller"; // NOI18N
    private static final String CONTROLLER_FILE_SUFIX = "_controller"; // NOI18N
    private static final String CONTROLLER_FILE_SUFIX_2 = "Controller"; // NOI18N cake2.x.x
    private static final String DIR_CONTROLLERS = "controllers"; // NOI18N
    private static final String DIR_CONTROLLER_2 = "Controller"; // NOI18N cake2.x.x
    private static final String DIR_MODELS = "models";
    private static final String DIR_MODEL_2 = "Model";
    private static final String DIR_NBPROJECT = "nbproject"; // NOI18N
    private static final String DIR_VIEWS = "views"; // NOI18N
    private static final String DIR_VIEW_2 = "View"; // NOI18N cake2.x.x
    private static final String FILE_VIEW_EXT = "ctp"; // NOI18N
    private static final String HELPER = "Helper"; // NOI18N
    private static final String PROPERTY_ANNOTATION_PATTERN = " * @property %s $%s"; // NOI18N
    private static final String UNDERSCORE = "_"; // NOI18N
    private static final String DIR_THEMED = "themed"; // NOI18N
    private static final String DIR_THEMED_2 = "Themed"; // NOI18N
    private static final String FILE_CONTROLLER_RELATIVE = "../../" + DIR_CONTROLLERS + "/%s.php"; // NOI18N
    private static final String FILE_CONTROLLER_RELATIVE_2 = "../../" + DIR_CONTROLLER_2 + "/%s.php"; // NOI18N cake2.0
    private static final String FILE_THEME_CONTROLLER_RELATIVE = "../../../../" + DIR_CONTROLLERS + "/%s.php"; // NOI18N
    private static final String FILE_THEME_CONTROLLER_RELATIVE_2 = "../../../../" + DIR_CONTROLLER_2 + "/%s.php"; // NOI18N cake2.0
    private static final String FILE_VIEW_RELATIVE = "../" + DIR_VIEWS + "/%s/%s." + FILE_VIEW_EXT; // NOI18N
    private static final String FILE_VIEW_RELATIVE_2 = "../" + DIR_VIEW_2 + "/%s/%s." + FILE_VIEW_EXT; // NOI18N cake2.0
    private static final String FILE_THEME_VIEW_RELATIVE = "../" + DIR_VIEWS + "/" + DIR_THEMED + "/%s/%s/%s." + FILE_VIEW_EXT; // NOI18N
    private static final String FILE_THEME_VIEW_RELATIVE_2 = "../" + DIR_VIEW_2 + "/" + DIR_THEMED_2 + "/%s/%s/%s." + FILE_VIEW_EXT; // NOI18N cake2.0

    private CakePhpUtils() {
    }

    public static boolean isView(FileObject fo) {
        if (!fo.isData() || !fo.getExt().equals(FILE_VIEW_EXT)) {
            return false;
        }
        // Theme view file  View/Themed/ThemeName/Controller/View.ctp
        if (DIR_VIEW_2.equals(fo.getFileObject("../../../../").getName())
            || DIR_VIEWS.equals(fo.getFileObject("../../../../").getName())) { // NOI18N
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
        // cake 2.x.x
        if (DIR_VIEW_2.equals(parent.getName())) {
            return true;
        }
        return DIR_VIEWS.equals(parent.getName());
    }

    public static FileObject getView(FileObject controller, PhpBaseElement phpElement) {
        FileObject view = null;
        if (phpElement instanceof PhpClass.Method) {
            view = getView(controller, getViewFileName(phpElement.getName()));
        }
        return view;
    }

    private static FileObject getView(FileObject controller, String viewName) {
        File parent = FileUtil.toFile(controller).getParentFile();
        File view = PropertyUtils.resolveFile(parent, String.format(FILE_VIEW_RELATIVE, getViewFolderName(controller.getName()), viewName));
        if (!view.isFile()) {
            view = PropertyUtils.resolveFile(parent, String.format(FILE_VIEW_RELATIVE_2, getViewFolderName(controller.getName()), viewName));
        }
        if (view.isFile()) {
            return FileUtil.toFileObject(view);
        }
        return null;
    }

    public static FileObject getView(FileObject controller, PhpBaseElement phpElement, FileObject theme) {
        FileObject view = null;
        if (phpElement instanceof PhpClass.Method) {
            view = getView(controller, getViewFileName(phpElement.getName()), theme);
        }
        return view;
    }

    private static FileObject getView(FileObject controller, String viewName, FileObject theme) {
        File parent = FileUtil.toFile(controller).getParentFile();
        File view = PropertyUtils.resolveFile(parent, String.format(FILE_THEME_VIEW_RELATIVE, theme.getName(), getViewFolderName(controller.getName()), viewName));
        if (!view.isFile()) {
            view = PropertyUtils.resolveFile(parent, String.format(FILE_THEME_VIEW_RELATIVE_2, theme.getName(), getViewFolderName(controller.getName()), viewName));
        }
        if (view.isFile()) {
            return FileUtil.toFileObject(view);
        }
        return null;
    }

    public static boolean isControllerName(String name) {
        return name.endsWith(CakePhpUtils.CONTROLLER_CLASS_SUFFIX);
    }

    public static boolean isControllerFileName(String filename) {
        if (filename.endsWith(CakePhpUtils.CONTROLLER_FILE_SUFIX_2)) {
            return true;
        }
        return filename.endsWith(CakePhpUtils.CONTROLLER_FILE_SUFIX);
    }

    public static boolean isController(FileObject fo) {
        if (fo.isData()
            && isControllerFileName(fo.getName())
            && fo.getParent().getNameExt().equals(DIR_CONTROLLER_2)) {
            return true;
        }
        return fo.isData()
            && isControllerFileName(fo.getName())
            && fo.getParent().getNameExt().equals(DIR_CONTROLLERS);
    }

    public static FileObject getController(FileObject view) {
        File parent = FileUtil.toFile(view).getParentFile();
        File action = PropertyUtils.resolveFile(parent, String.format(FILE_CONTROLLER_RELATIVE, getControllerFileName(parent.getName())));
        if (!action.isFile()) {
            action = PropertyUtils.resolveFile(parent, String.format(FILE_CONTROLLER_RELATIVE_2, getControllerFileName(parent.getName())));
        }
        // Theme view file
        if (!action.isFile()) {
            action = PropertyUtils.resolveFile(parent, String.format(FILE_THEME_CONTROLLER_RELATIVE, getControllerFileName(parent.getName())));
        }
        if (!action.isFile()) {
            action = PropertyUtils.resolveFile(parent, String.format(FILE_THEME_CONTROLLER_RELATIVE_2, getControllerFileName(parent.getName())));
        }
        if (action.isFile()) {
            return FileUtil.toFileObject(action);
        }
        return null;
    }

    /**
     * Get class name
     * @param fo FileObject
     * @return class name
     */
    public static String getClassName(FileObject fo){
        EditorSupport support = Lookup.getDefault().lookup(EditorSupport.class);
        for(PhpClass phpClass : support.getClasses(fo)){
            return phpClass.getName();
        }
        return null;
    }

    /**
     * Check component file
     *
     * @param fo
     * @return component true, otherwise false
     */
    public static boolean isComponent(FileObject fo) {
        if (fo.isData()
            && fo.getParent().getNameExt().equals("Component") // NOI18N
            && FileUtils.isPhpFile(fo)) {
            return true;
        }

        if (fo.isData()
            && fo.getParent().getNameExt().equals("components") // NOI18N
            && FileUtils.isPhpFile(fo)) {
            return true;
        }
        return false;
    }

    /**
     * Check component file
     *
     * @param fo
     * @return component true, otherwise false
     */
    public static boolean isHelper(FileObject fo) {
        if (fo.isData()
            && fo.getParent().getNameExt().equals("Helper") // NOI18N
            && FileUtils.isPhpFile(fo)) {
            return true;
        }

        if (fo.isData()
            && fo.getParent().getNameExt().equals("helpers") // NOI18N
            && FileUtils.isPhpFile(fo)) {
            return true;
        }
        return false;
    }

    /**
     * Check model file
     *
     * @param fo file
     * @return model true, otherwise false
     */
    public static boolean isModel(FileObject fo) {
        // CakePHP 2.x
        if (fo.isData()
            && fo.getParent().getNameExt().equals(DIR_MODEL_2)
            && FileUtils.isPhpFile(fo)) {
            return true;
        }

        // CakePHP 1.x
        if (fo.isData()
            && fo.getParent().getNameExt().equals(DIR_MODELS)
            && FileUtils.isPhpFile(fo)) {
            return true;
        }

        return false;
    }

    /**
     * Get directory
     *
     * @param dirType APP, CORE, APP_PLUGIN, PLUGIN
     * @param fileType
     * @param pluginName APP_PLUGIN, PLUGIN: plugin name , otherwise null
     * @return directory FileObject
     */
    public static FileObject getDirectory(PhpModule pm, DIR dirType, FILE fileType, String pluginName) {
        FileObject sourceDirectory = CakePhpFrameworkProvider.getCakePhpDirectory(pm);
        if (pluginName != null && pluginName.isEmpty()) {
            pluginName = null;
        }

        StringBuilder directoryPath = new StringBuilder();
        CakeVersion version = CakeVersion.getInstance(pm);
        if (version.isCakePhp(1)) {
            switch (dirType) {
                case APP:
                    directoryPath.append("app/"); // NOI18N
                    break;
                case CORE:
                    directoryPath.append("cake/libs/"); // NOI18N
                    break;
                case APP_PLUGIN:
                    directoryPath.append("app/plugins/"); // NOI18N
                    break;
                case PLUGIN:
                    directoryPath.append("plugins/"); // NOI18N
                    break;
                default:
                    return null;
            }

            if (dirType == DIR.APP_PLUGIN || dirType == DIR.PLUGIN) {
                if (pluginName == null) {
                    return sourceDirectory.getFileObject(directoryPath.toString());
                } else {
                    pluginName = toUnderscore(pluginName);
                    directoryPath.append(pluginName).append("/"); // NOI18N
                }
            }

            switch (dirType) {
                case APP:
                case APP_PLUGIN:
                case PLUGIN:
                    switch (fileType) {
                        case MODEL:
                            directoryPath.append("models/"); // NOI18N
                            break;
                        case BEHAVIOR:
                            directoryPath.append("models/behaviors/"); // NOI18N
                            break;
                        case VIEW:
                            directoryPath.append("views/"); // NOI18N
                            break;
                        case HELPER:
                            directoryPath.append("views/helpers/"); // NOI18N
                            break;
                        case CONTROLLER:
                            directoryPath.append("controllers/"); // NOI18N
                            break;
                        case COMPONENT:
                            directoryPath.append("controllers/components/"); // NOI18N
                            break;
                        case CONFIG:
                            directoryPath.append("config/"); // NOI18N
                        default:
                            break;
                    }
                    break;
                case CORE:
                    switch (fileType) {
                        case MODEL:
                            directoryPath.append("model/"); // NOI18N
                            break;
                        case BEHAVIOR:
                            directoryPath.append("model/behaviors/"); // NOI18N
                            break;
                        case VIEW:
                            directoryPath.append("view/"); // NOI18N
                            break;
                        case HELPER:
                            directoryPath.append("view/helpers/"); // NOI18N
                            break;
                        case CONTROLLER:
                            directoryPath.append("controller/"); // NOI18N
                            break;
                        case COMPONENT:
                            directoryPath.append("controller/components/"); // NOI18N
                            break;
                        case CONFIG:
                            directoryPath.append("../config/"); // NOI18N
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        } else if (version.isCakePhp(2)) {
            switch (dirType) {
                case APP:
                    directoryPath.append("app/"); // NOI18N
                    break;
                case CORE:
                    directoryPath.append("lib/Cake/"); // NOI18N
                    break;
                case APP_PLUGIN:
                    directoryPath.append("app/Plugin/"); // NOI18N
                    break;
                case PLUGIN:
                    directoryPath.append("plugins/"); // NOI18N
                    break;
                default:
                    return null;
            }

            if (dirType == DIR.APP_PLUGIN || dirType == DIR.PLUGIN) {
                if (pluginName == null) {
                    return sourceDirectory.getFileObject(directoryPath.toString());
                } else {
                    directoryPath.append(pluginName).append("/"); // NOI18N
                }
            }

            switch (dirType) {
                case APP:
                case APP_PLUGIN:
                case PLUGIN:
                    switch (fileType) {
                        case MODEL:
                            directoryPath.append("Model/"); // NOI18N
                            break;
                        case BEHAVIOR:
                            directoryPath.append("Model/Behavior/"); // NOI18N
                            break;
                        case VIEW:
                            directoryPath.append("View/"); // NOI18N
                            break;
                        case HELPER:
                            directoryPath.append("View/Helper/"); // NOI18N
                            break;
                        case CONTROLLER:
                            directoryPath.append("Controller/"); // NOI18N
                            break;
                        case COMPONENT:
                            directoryPath.append("Controller/Component/"); // NOI18N
                            break;
                        case CONFIG:
                            directoryPath.append("Config/"); // NOI18N
                        default:
                            break;
                    }
                    break;
                case CORE:
                    switch (fileType) {
                        case MODEL:
                            directoryPath.append("Model/"); // NOI18N
                            break;
                        case BEHAVIOR:
                            directoryPath.append("Model/Behavior/"); // NOI18N
                            break;
                        case VIEW:
                            directoryPath.append("View/"); // NOI18N
                            break;
                        case HELPER:
                            directoryPath.append("View/Helper/"); // NOI18N
                            break;
                        case CONTROLLER:
                            directoryPath.append("Controller/"); // NOI18N
                            break;
                        case COMPONENT:
                            directoryPath.append("Controller/Component/"); // NOI18N
                            break;
                        case CONFIG:
                            directoryPath.append("Config/"); // NOI18N
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
        return sourceDirectory.getFileObject(directoryPath.toString());
    }

    /**
     * Get specified file object
     * @param pm
     * @param dirType
     * @param fileType
     * @param name Call name (e.g. Plugin -> DebugKit.Toolbar, HtmlHelper -> Html, ...
     * @return
     */
    public static FileObject getFile(PhpModule pm, DIR dirType, FILE fileType, String name){
        String[] split = name.split("[.]"); // NOI18N
        FileObject fo = null;
        FileObject directory = null;
        switch (split.length){
            case 2: // Plugin
                String pluginName = split[0];
                name = split[1];
                directory = getDirectory(pm, dirType, fileType, pluginName);
                break;
            case 1:
                directory = getDirectory(pm, dirType, fileType, null);
                break;
            default :
                break;
        }
        if(directory != null){
            fo = directory.getFileObject(getFileNameWithExt(fileType, name));
        }
        return fo;
    }

    /**
     * Get specified files
     * @param pm
     * @param dirType
     * @param fileType
     * @return files
     */
    public static List<FileObject> getFiles(PhpModule pm, DIR dirType, FILE fileType){
        FileObject directory = getDirectory(pm, dirType, fileType, null);

        return fileFilter(directory, fileType);
    }

    /**
     * Filter specified files
     * @param targetDirectory
     * @param fileType
     * @return
     */
    private static List<FileObject> fileFilter(FileObject targetDirectory, FILE fileType) {
        if (targetDirectory == null) {
            return null;
        }
        List<FileObject> list = new ArrayList<FileObject>();
        PhpModule pm = PhpModule.forFileObject(targetDirectory);
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);

        Enumeration<? extends FileObject> children = targetDirectory.getChildren(true);
        while (children.hasMoreElements()) {
            FileObject child = children.nextElement();
            if(!FileUtils.isPhpFile(child)){
                continue;
            }
            String name = ""; // NOI18N

            // get class name
            for(PhpClass php : editorSupport.getClasses(child)){
                name = php.getName();
                break;
            }

            String test = child.getName().toLowerCase();
            if(name == null || name.isEmpty() || test.endsWith("test")){ // NOI18N
                continue;
            }

            switch (fileType){
                case MODEL:
                    if(!name.endsWith("Behavior") // NOI18N
                        && !name.endsWith(fileType.toString())
                        && !name.startsWith(APP)){
                        list.add(child);
                    }
                    break;
                default:
                    if(name.endsWith(fileType.toString()) && !name.startsWith(APP)){
                        list.add(child);
                    }
                    break;
            }
        }
        return list;
    }

    /**
     * Get file name
     *
     * @param fileType
     * @param name e.g. TreeBehavior->Tree, HtmlHelper->Html,...
     * @return file name with extension | null
     */
    public static String getFileNameWithExt(FILE fileType, String name) {
        String fileName = null;
        PhpModule pm = PhpModule.inferPhpModule();
        CakeVersion version = CakeVersion.getInstance(pm);
        if (version.isCakePhp(1)) {
            fileName = toUnderscore(name);
        } else if (version.isCakePhp(2)) {
            switch (fileType) {
                case MODEL:
                    fileName = name;
                    break;
                case BEHAVIOR:
                    fileName = name + "Behavior"; // NOI18N
                    break;
                case HELPER:
                    fileName = name + "Helper"; // NOI18N
                    break;
                case CONTROLLER:
                    fileName = name + "Controller"; // NOI18N
                    break;
                case COMPONENT:
                    fileName = name + "Component"; // NOI18N
                    break;
                case VIEW:
                default:
                    break;
            }
        }
        return fileName + ".php"; // NOI18N
    }

    public static String getActionName(FileObject view) {
        return getActionName(view.getName());
    }

    // unit tests
    static String getActionName(String viewName) {
        return toCamelCase(viewName, true);
    }

    private static String getControllerFileName(String viewName) {
        if (viewName.toLowerCase().equals(viewName)) {
            return viewName + CONTROLLER_FILE_SUFIX;
        }
        return viewName + CONTROLLER_FILE_SUFIX_2; // cake 2.x.x
    }

    // unit tests
    static String getViewFileName(String actionName) {
        return toUnderscore(actionName);
    }

    private static String getViewFolderName(String controllerFileName) {
        if (controllerFileName.toLowerCase().equals(controllerFileName)) {
            return controllerFileName.replace(CONTROLLER_FILE_SUFIX, ""); // NOI18N
        }
        return controllerFileName.replace(CONTROLLER_FILE_SUFIX_2, ""); // NOI18N cake 2.x.x
    }

    // my_posts -> MyPosts or myPosts
    private static String toCamelCase(String underscored, boolean firstLowerCase) {
        StringBuilder sb = new StringBuilder(underscored.length());
        boolean first = firstLowerCase;
        for (String part : underscored.split(Pattern.quote(UNDERSCORE))) { // NOI18N
            if (first) {
                first = false;
                sb.append(part);
            } else {
                sb.append(part.substring(0, 1).toUpperCase());
                sb.append(part.substring(1));
            }
        }
        return sb.toString();
    }

    // MyPosts -> my_posts
    private static String toUnderscore(String input) {
        StringBuilder sb = new StringBuilder(2 * input.length());
        for (int i = 0; i < input.length(); ++i) {
            char ch = input.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i != 0) {
                    sb.append(UNDERSCORE);
                }
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static FileObject createView(FileObject controller, PhpBaseElement phpElement) throws IOException {
        FileObject fo = null;
        if (phpElement instanceof PhpClass.Method) {
            FileObject view = controller.getFileObject("../../" + DIR_VIEWS);
            if (view == null) {
                view = controller.getFileObject("../../" + DIR_VIEW_2);
            }
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

    public static String getCamelCaseName(String name) {
        return toCamelCase(name, false);
    }

    /**
     * Create auto completion file - add to the nbproject directory (only
     * CakePHP 2.x)
     *
     * @return boolean - if create success, return true.
     *
     * @throws IOException
     */
    public static boolean createAutoCompletionFile() throws IOException {
        PhpModule pm = PhpModule.inferPhpModule();
        if (!CakePhpFrameworkProvider.getInstance().isInPhpModule(pm)) {
            // noting
            return false;
        }

        FileObject projectDirectory = pm.getProjectDirectory();
        FileObject nbprojectDirectory = projectDirectory.getFileObject(DIR_NBPROJECT); // NOI18N
        if (nbprojectDirectory == null) {
            return false;
        }
        // create a new file for auto completion
        FileObject output = nbprojectDirectory.getFileObject(CAKE_AUTO_COMPLETION_PHP);
        if (output == null) {
            output = nbprojectDirectory.createData(CAKE_AUTO_COMPLETION_PHP);
        }

        PrintWriter pw = new PrintWriter(output.getOutputStream());
        pw.println("<?php"); // NOI18N
        pw.println("/**"); // NOI18N
        pw.println(" * For CakePHP Component & Model Code Completion"); // NOI18N

        // get models, componets
        List<FileObject> propertyList = getFiles(pm, DIR.APP, FILE.MODEL);
        propertyList.addAll(getFiles(pm, DIR.APP, FILE.COMPONENT));
        propertyList.addAll(getFiles(pm, DIR.APP_PLUGIN, FILE.COMPONENT));
        propertyList.addAll(getFiles(pm, DIR.PLUGIN, FILE.COMPONENT));

        // loop add property annotation
        if (propertyList != null) {
            boolean modelFlg = true;
            boolean componentFlg = true;
            List<FileObject> behaviorList = getFiles(pm, DIR.CORE, FILE.BEHAVIOR);
            behaviorList.addAll(getFiles(pm, DIR.APP, FILE.BEHAVIOR));
            behaviorList.addAll(getFiles(pm, DIR.APP_PLUGIN, FILE.BEHAVIOR));
            behaviorList.addAll(getFiles(pm, DIR.PLUGIN, FILE.BEHAVIOR));

            for (FileObject property : propertyList) {
                String name = property.getName();
                if (name.endsWith(COMPONENT)) {
                    if (componentFlg) {
                        pw.println(" * =================================================="); // NOI18N
                        pw.println(" * Component"); // NOI18N
                        pw.println(" * =================================================="); // NOI18N
                        componentFlg = false;
                    }
                    pw.format(PROPERTY_ANNOTATION_PATTERN, name, name.replace(COMPONENT, "")); // NOI18N
                    pw.println();
                } else {
                    if (modelFlg) {
                        pw.println(" * =================================================="); // NOI18N
                        pw.println(" * Model"); // NOI18N
                        pw.println(" * =================================================="); // NOI18N
                        modelFlg = false;
                    }
                    pw.format(PROPERTY_ANNOTATION_PATTERN, name, name); // NOI18N
                    pw.println();
                    for (FileObject behavior : behaviorList) {
                        pw.format(PROPERTY_ANNOTATION_PATTERN, behavior.getName(), name); // NOI18N
                        pw.println();
                    }
                }
            }
        }

        pw.println(" */"); // NOI18N
        pw.println("class Controller{}"); // NOI18N


        pw.println("/**"); // NOI18N
        pw.println(" * For CakePHP Hepler Code Completion"); // NOI18N

        // get helper
        propertyList = getFiles(pm, DIR.APP, FILE.HELPER);
        propertyList.addAll(getFiles(pm, DIR.APP_PLUGIN, FILE.HELPER));
        propertyList.addAll(getFiles(pm, DIR.PLUGIN, FILE.HELPER));

        // loop add property annotation
        if (propertyList != null) {
            boolean helperFlg = true;
            for (FileObject property : propertyList) {
                String name = property.getName();
                if (name.endsWith(HELPER)) {
                    if (helperFlg) {
                        pw.println(" * =================================================="); // NOI18N
                        pw.println(" * Helper"); // NOI18N
                        pw.println(" * =================================================="); // NOI18N
                        helperFlg = false;
                    }
                    pw.format(PROPERTY_ANNOTATION_PATTERN, name, name.replace(HELPER, "")); // NOI18N
                    pw.println();
                }
            }
        }
        pw.println(" */"); // NOI18N
        pw.println("class View{}"); // NOI18N
        pw.close();
        UiUtils.open(output, 0);
        return true;
    }

    /**
     * Change app/tmp directory permission (777)
     *
     * @param tmpDirectory app/tmp Directory
     */
    public static void chmodTmpDirectory(FileObject tmpDirectory) {
        if (tmpDirectory == null) {
            return;
        }
        File tmp = FileUtil.toFile(tmpDirectory);
        tmp.setExecutable(true, false);
        tmp.setReadable(true, false);
        tmp.setWritable(true, false);
        Enumeration<? extends FileObject> children = tmpDirectory.getChildren(true);
        while (children.hasMoreElements()) {
            File child = FileUtil.toFile(children.nextElement());
            child.setExecutable(true, false);
            child.setReadable(true, false);
            child.setWritable(true, false);
        }
    }
}
