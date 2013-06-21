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
import java.util.regex.Pattern;
import org.cakephp.netbeans.CakePhp;
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.cakephp.netbeans.module.CakePhpModule;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public final class CakePhpUtils {

    private static final String CONTROLLER_CLASS_SUFFIX = "Controller"; // NOI18N
    private static final String UNDERSCORE = "_"; // NOI18N

    private CakePhpUtils() {
    }

    public static boolean isCakePHP(PhpModule phpModule) {
        if (phpModule == null) {
            return false;
        }
        return CakePhpFrameworkProvider.getInstance().isInPhpModule(phpModule);
    }

    public static boolean isView(FileObject fo) {
        PhpModule phpModule = PhpModule.forFileObject(fo);
        return CakePhpModule.forPhpModule(phpModule).isView(fo);
    }

    public static FileObject getView(FileObject controller, PhpBaseElement phpElement) {
        FileObject view = null;
        if (phpElement instanceof PhpClass.Method) {
            view = getView(controller, getViewFileName(phpElement.getName()));
        }
        return view;
    }

    private static FileObject getView(FileObject controller, String viewName) {
        PhpModule phpModule = PhpModule.forFileObject(controller);
        return CakePhpModule.forPhpModule(phpModule).getView(controller, viewName);
    }

    public static FileObject getView(FileObject controller, PhpBaseElement phpElement, FileObject theme) {
        FileObject view = null;
        if (phpElement instanceof PhpClass.Method) {
            view = getView(controller, getViewFileName(phpElement.getName()), theme);
        }
        return view;
    }

    private static FileObject getView(FileObject controller, String viewName, FileObject theme) {
        PhpModule phpModule = PhpModule.forFileObject(controller);
        return CakePhpModule.forPhpModule(phpModule).getView(controller, viewName, theme);
    }

    public static boolean isControllerName(String name) {
        return name.endsWith(CakePhpUtils.CONTROLLER_CLASS_SUFFIX);
    }

    public static boolean isController(FileObject fo) {
        PhpModule phpModule = PhpModule.forFileObject(fo);
        return CakePhpModule.forPhpModule(phpModule).isController(fo);
    }

    public static FileObject getController(FileObject view) {
        PhpModule phpModule = PhpModule.forFileObject(view);
        return CakePhpModule.forPhpModule(phpModule).getController(view);
    }

    /**
     * Get class name
     *
     * @param fo FileObject
     * @return class name
     */
    public static String getClassName(FileObject fo) {
        EditorSupport support = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : support.getClasses(fo)) {
            return phpClass.getName();
        }
        return null;
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
        PhpModule phpModule = PhpModule.forFileObject(fo);
        return CakePhpModule.forPhpModule(phpModule).isComponent(fo);
    }

    /**
     * Check component file
     *
     * @param fo
     * @return component true, otherwise false
     */
    public static boolean isHelper(FileObject fo) {
        PhpModule phpModule = PhpModule.forFileObject(fo);
        return CakePhpModule.forPhpModule(phpModule).isHelper(fo);
    }

    /**
     * Check model file
     *
     * @param fo file
     * @return model true, otherwise false
     */
    public static boolean isModel(FileObject fo) {
        PhpModule phpModule = PhpModule.forFileObject(fo);
        return CakePhpModule.forPhpModule(phpModule).isModel(fo);
    }

    /**
     * Check model file
     *
     * @param fo file
     * @return model true, otherwise false
     */
    public static boolean isBehavior(FileObject fo) {
        PhpModule phpModule = PhpModule.forFileObject(fo);
        return CakePhpModule.forPhpModule(phpModule).isBehavior(fo);
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
        PhpModule phpModule = PhpModule.forFileObject(controller);
        return CakePhpModule.forPhpModule(phpModule).createView(controller, phpElement);
    }

    public static String getCamelCaseName(String name) {
        return toCamelCase(name, false);
    }

    public static String toUnderscoreCase(String string) {
        return toUnderscore(string);
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
        if (fileObject.getExt().equals(CakePhp.CTP)) {
            return true;
        }

        return false;
    }
}
