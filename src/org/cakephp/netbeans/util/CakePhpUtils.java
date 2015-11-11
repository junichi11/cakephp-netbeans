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

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.cakephp.netbeans.CakePhp;
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public final class CakePhpUtils {

    private static final String CONTROLLER_CLASS_SUFFIX = "Controller"; // NOI18N
    private static final String UNDERSCORE = "_"; // NOI18N
    private static final Comparator<FileObject> FILE_COMPARATOR = new Comparator<FileObject>() {
        @Override
        public int compare(FileObject o1, FileObject o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };
    public static final String CTP_EXT = ".ctp"; // NOI18N

    private CakePhpUtils() {
    }

    public static boolean isCakePHP(PhpModule phpModule) {
        if (phpModule == null) {
            return false;
        }
        return CakePhpFrameworkProvider.getInstance().isInPhpModule(phpModule);
    }

    public static boolean isView(FileObject fo) {
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? false : cakeModule.isView(fo);
    }

    public static FileObject getView(FileObject controller, PhpBaseElement phpElement) {
        FileObject view = null;
        if (phpElement instanceof PhpClass.Method) {
            view = getView(controller, getViewFileName(phpElement.getName()));
        }
        return view;
    }

    public static FileObject getView(FileObject controller, String viewName) {
        PhpModule phpModule = PhpModule.Factory.forFileObject(controller);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? null : cakeModule.getView(controller, viewName);
    }

    public static FileObject getView(FileObject controller, PhpBaseElement phpElement, FileObject theme) {
        FileObject view = null;
        if (phpElement instanceof PhpClass.Method) {
            view = getView(controller, getViewFileName(phpElement.getName()), theme);
        }
        return view;
    }

    public static FileObject getView(FileObject controller, String viewName, FileObject theme) {
        PhpModule phpModule = PhpModule.Factory.forFileObject(controller);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? null : cakeModule.getView(controller, viewName, theme);
    }

    public static boolean isControllerName(String name) {
        return name.endsWith(CakePhpUtils.CONTROLLER_CLASS_SUFFIX);
    }

    public static boolean isController(FileObject fo) {
        if (isTest(fo)) {
            return false;
        }
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? false : cakeModule.isController(fo);
    }

    public static FileObject getController(FileObject view) {
        PhpModule phpModule = PhpModule.Factory.forFileObject(view);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? null : cakeModule.getController(view);
    }

    /**
     * Get class name
     *
     * @param fo FileObject
     * @return class name if php class name exists, otherwise empty string.
     */
    public static String getClassName(FileObject fo) {
        EditorSupport support = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : support.getClasses(fo)) {
            return phpClass.getName();
        }
        return ""; // NOI18N
    }

    /**
     * Get fully qualified class name
     *
     * @param fo FileObject
     * @return fully quqlified class name if class exists, otherwise null
     */
    public static String getFullyQualifiedClassName(FileObject fo) {
        EditorSupport support = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : support.getClasses(fo)) {
            return phpClass.getFullyQualifiedName();
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
        if (isTest(fo)) {
            return false;
        }
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? false : cakeModule.isComponent(fo);
    }

    /**
     * Check component file
     *
     * @param fo
     * @return component true, otherwise false
     */
    public static boolean isHelper(FileObject fo) {
        if (isTest(fo)) {
            return false;
        }
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? false : cakeModule.isHelper(fo);
    }

    /**
     * Check model file
     *
     * @param fo file
     * @return model true, otherwise false
     */
    public static boolean isModel(FileObject fo) {
        if (isTest(fo)) {
            return false;
        }
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? false : cakeModule.isModel(fo);
    }

    /**
     * Check model file
     *
     * @param fo file
     * @return model true, otherwise false
     */
    public static boolean isBehavior(FileObject fo) {
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? false : cakeModule.isBehavior(fo);
    }

    public static String getActionName(FileObject view) {
        return getActionName(view.getName());
    }

    // unit tests
    static String getActionName(String viewName) {
        return toCamelCase(viewName, true);
    }

    // unit tests
    static String getViewFileName(String actionName) {
        return toUnderscore(actionName);
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
        PhpModule phpModule = PhpModule.Factory.forFileObject(controller);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? null : cakeModule.createView(controller, phpElement);
    }

    public static String getCamelCaseName(String name) {
        return toCamelCase(name, false);
    }

    public static String toUnderscoreCase(String string) {
        return toUnderscore(string);
    }

    public static String detachQuotes(String text) {
        if (text == null) {
            return null;
        }
        if (text.matches("\".*\"") || text.matches("'.*'")) { // NOI18N
            return text.substring(1, text.length() - 1);
        }
        return text;
    }

    /**
     * Check whether file ext is "ctp".
     *
     * @param fileObject
     * @return true if file ext is ctp, othewise false.
     */
    public static boolean isCtpFile(FileObject fileObject) {
        if (fileObject == null) {
            return false;
        }
        return fileObject.getExt().equals(CakePhp.CTP);
    }

    public static int getActionMethodOffset(FileObject controller, FileObject view) {
        String actionMethodName = CakePhpUtils.getActionName(view);
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : editorSupport.getClasses(controller)) {
            if (CakePhpUtils.isControllerName(phpClass.getName())) {
                if (actionMethodName != null) {
                    for (PhpClass.Method method : phpClass.getMethods()) {
                        if (actionMethodName.equals(method.getName())) {
                            return method.getOffset();
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Check whether the file is test case.
     *
     * @param testFile
     * @return true if file is test case, otherwise false.
     */
    public static boolean isTest(FileObject fo) {
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule == null ? false : cakeModule.isTest(fo);
    }

    /**
     * Check whether the file is fixture.
     *
     * @param fileObject
     * @return true if file is fixture, otherwise false.
     */
    public static boolean isFixture(FileObject fileObject) {
        String className = getClassName(fileObject);
        return !StringUtils.isEmpty(className) && className.endsWith("Fixture"); // NOI18N
    }

    /**
     * Check whether the path starts with slash (/).
     *
     * @param path
     * @return true starts with slash, otherwise false
     */
    public static boolean isAbsolutePath(String path) {
        if (path == null || path.startsWith("//")) { // NOI18N
            return false;
        }
        return path.startsWith("/"); // NOI18N
    }

    /**
     * Append ctp extension (.ctp).
     *
     * @param path
     * @return path with ctp extension
     */
    public static String appendCtpExt(String path) {
        if (path == null || path.equals("")) { // NOI18N
            return ""; // NOI18N
        }
        return path.concat(CTP_EXT);

    }

    /**
     * Sort FileObject.
     *
     * @param files
     */
    public static void sort(List<FileObject> files) {
        Collections.sort(files, FILE_COMPARATOR);
    }

    /**
     * Split to plugin name and file name with dot.
     *
     * @param name
     * @return fist element is plugin name, second element is file name if
     * plugin name exists, null if name is null, otherwise file name.
     */
    public static String[] pluginSplit(String name) {
        if (name == null) {
            return null;
        }
        int indexOfDot = name.indexOf("."); // NOI18N
        if (indexOfDot != -1) {
            return new String[]{name.substring(0, indexOfDot), name.substring(indexOfDot + 1)};
        }
        return new String[]{name};
    }

    public static FileObject getCurrentFileObject() {
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor != null) {
            Document document = editor.getDocument();
            if (document != null) {
                return NbEditorUtilities.getFileObject(document);
            }
        }
        return null;
    }

    /**
     * Get nbproject directory
     *
     * @param phpModule
     * @return
     */
    public static FileObject getNbproject(PhpModule phpModule) {
        FileObject projectDirectory = phpModule.getProjectDirectory();
        return projectDirectory.getFileObject("nbproject"); // NOI18N
    }

    /**
     * Get project.properties.
     *
     * @param phpModule
     * @return project.properties if file exists, false otherwise
     */
    public static FileObject getProjectProperties(PhpModule phpModule) {
        FileObject nbproject = getNbproject(phpModule);
        return nbproject == null ? null : nbproject.getFileObject("project.properties"); // NOI18N
    }

}
