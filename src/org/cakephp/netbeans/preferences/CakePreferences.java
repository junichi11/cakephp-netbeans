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
package org.cakephp.netbeans.preferences;

import java.util.prefs.Preferences;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.modules.php.api.phpmodule.PhpModule;

/**
 *
 * @author junichi11
 */
public class CakePreferences {

    private static final String ENABLED = "enabled"; // NOI18N
    private static final String APP_NAME = "app-name"; // NOI18N
    private static final String AUTO_CREATE_VIEW = "auto-create-view"; // NOI18N
    private static final String CAKE_PHP_DIR_PATH = "cake-php-dir-path"; // NOI18N
    private static final String DEFAULT_APP_NAME = "app"; // NOI18N
    private static final String USE_PROJECT_DIRECTORY = "use-project-directory"; // NOI18N
    private static final String IGNORE_TMP_DIRECTORY = "ignore-tmp-directory"; // NOI18N
    private static final String SHOW_POPUP_FOR_ONE_ITEM = "show-popup-for-one-item"; // NOI18N
    private static final String APP_DIRECTORY_PATH = "app-directory-path"; // NOI18N
    private static final String DOTCAKE_FILE_PATH = "dotcake-file-path"; // NOI18N

    public static void setEnabled(PhpModule phpModule, Boolean isEnabled) {
        getPreferences(phpModule).putBoolean(ENABLED, isEnabled);
    }

    public static boolean isEnabled(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(ENABLED, false);
    }

    public static void setAppName(PhpModule phpModule, String appName) {
        getPreferences(phpModule).put(APP_NAME, appName);
    }

    public static String getAppName(PhpModule phpModule, CakeVersion version) {
        String appName = getPreferences(phpModule).get(APP_NAME, null); // NOI18N
        if (appName == null) {
            appName = DEFAULT_APP_NAME;
        }
        return appName;
    }

    public static void setAutoCreateView(PhpModule phpModule, boolean isAuto) {
        getPreferences(phpModule).putBoolean(AUTO_CREATE_VIEW, isAuto);
    }

    public static boolean getAutoCreateView(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(AUTO_CREATE_VIEW, false);
    }

    public static void setCakePhpDirPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(CAKE_PHP_DIR_PATH, path);
    }

    public static String getCakePhpDirPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(CAKE_PHP_DIR_PATH, ""); // NOI18N
    }

    public static void setUseProjectDirectory(PhpModule phpModule, boolean isProject) {
        getPreferences(phpModule).putBoolean(USE_PROJECT_DIRECTORY, isProject);
    }

    public static boolean useProjectDirectory(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(USE_PROJECT_DIRECTORY, false);
    }

    public static void setIgnoreTmpDirectory(PhpModule phpModule, boolean ignore) {
        getPreferences(phpModule).putBoolean(IGNORE_TMP_DIRECTORY, ignore);
    }

    public static boolean ignoreTmpDirectory(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(IGNORE_TMP_DIRECTORY, true);
    }

    public static void setShowPopupForOneItem(PhpModule phpModule, boolean isEnabled) {
        getPreferences(phpModule).putBoolean(SHOW_POPUP_FOR_ONE_ITEM, isEnabled);
    }

    public static boolean isShowPopupForOneItem(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(SHOW_POPUP_FOR_ONE_ITEM, true);
    }

    public static String getAppDirectoryPath(PhpModule phpModule, CakeVersion version) {
        String appName = getAppName(phpModule, version);
        return getPreferences(phpModule).get(APP_DIRECTORY_PATH, appName);
    }

    public static void setAppDirectoryPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(APP_DIRECTORY_PATH, path);
    }

    public static String getDotcakeFilePath(PhpModule phpModule) {
        return getPreferences(phpModule).get(DOTCAKE_FILE_PATH, ""); // NOI18N
    }

    public static void setDotcakeFilePath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(DOTCAKE_FILE_PATH, path);
    }

    private static Preferences getPreferences(PhpModule phpModule) {
        return phpModule.getPreferences(CakePreferences.class, true);
    }
}
