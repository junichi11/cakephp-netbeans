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
package org.cakephp.netbeans.options;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public class CakePhpOptions {

    private static final int NAME = 0;
    private static final int URL = 1;
    private static final String PLUGINS = "plugins"; // NOI18N
    private static final String NEW_PROJECT = "new-project"; // NOI18N
    private static final String LOCAL_ZIP_FILE_PATH = "local-zip-file-path"; // NOI18N
    private static final String IGNORE_TMP = "ignore-tmp"; // NOI18N
    private static final String AUTO_CREATE_VIEW = "auto-create-view"; // NOI18N
    private static final String NOTIFY_NEW_VERSION = "notify-new-version"; // NOI18N
    private static final String NOTIFY_AUTO_DETECTION = "notify-auto-detection"; // NOI18N
    private static final String TEST_STDERR = "test-stderr"; // NOI18N
    private static final String COMPOSER_JSON = "composer-json"; // NOI18N
    private static final String AVAILABLE_CUSTOM_NODES = "available-custom-nodes"; // NOI18N
    private static final String BASERCMS_ENABLED = "basercms-enabled"; // NOI18N
    private static final String BASERCMS_VAGARANT_SETTINGS = "basercms-vagrant-settings"; // NOI18N
    private static final CakePhpOptions INSTANCE = new CakePhpOptions();
    public static final List<String> DEFAULT_AVAILABLE_NODES = Arrays.asList(
            "Console", // NOI18N
            "Controller", // NOI18N
            "Model", // NOI18N
            "View", // NOI18N
            "Helper", // NOI18N
            "webroot" // NOI18N
    );
    public static final List<String> ALL_AVAILABLE_NODES = new ArrayList<>(DEFAULT_AVAILABLE_NODES);

    static {
        ALL_AVAILABLE_NODES.add("app/Plugin"); // NOI18N
    }

    private CakePhpOptions() {
    }

    public static CakePhpOptions getInstance() {
        return INSTANCE;
    }

    public List<CakePhpPlugin> getPlugins() {
        ArrayList<CakePhpPlugin> plugins = new ArrayList<>();
        Preferences p = getPreferences().node(PLUGINS).node(PLUGINS);
        String s = "";
        if (p != null) {
            s = p.get(PLUGINS, ""); // NOI18N
        }
        if (s.isEmpty()) {
            s = NbBundle.getMessage(CakePhpOptions.class, "CakePhpOptions.defaultPlugins"); // NOI18N
        }

        String[] rows = s.split(";"); // NOI18N
        Arrays.sort(rows);
        for (String row : rows) {
            String[] cells = row.split(","); // NOI18N
            if (cells.length == 2) {
                plugins.add(new CakePhpPlugin(cells[NAME], cells[URL]));
            }
        }
        return plugins;
    }

    public void setPlugins(List<CakePhpPlugin> plugins) {
        Preferences p = getPreferences().node(PLUGINS).node(PLUGINS);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (CakePhpPlugin plugin : plugins) {
            if (first) {
                first = false;
            } else {
                sb.append(";"); // NOI18N
            }
            sb.append(plugin.getName())
                    .append(",") // NOI18N
                    .append(plugin.getUrl());
        }
        p.put(PLUGINS, sb.toString());
    }

    public String getLocalZipFilePath() {
        return getPreferences().node(NEW_PROJECT).get(LOCAL_ZIP_FILE_PATH, ""); // NOI18N
    }

    public void setLocalZipFilePath(String path) {
        getPreferences().node(NEW_PROJECT).put(LOCAL_ZIP_FILE_PATH, path);
    }

    public boolean isIgnoreTmpDirectory() {
        return getPreferences().getBoolean(IGNORE_TMP, true);
    }

    public void setIgnoreTmpDirectory(boolean isIgnore) {
        getPreferences().putBoolean(IGNORE_TMP, isIgnore);
    }

    public boolean isAutoCreateView() {
        return getPreferences().getBoolean(AUTO_CREATE_VIEW, false);
    }

    public void setAutoCreateView(boolean isAuto) {
        getPreferences().putBoolean(AUTO_CREATE_VIEW, isAuto);
    }

    public boolean isNotifyNewVersion() {
        return getPreferences().getBoolean(NOTIFY_NEW_VERSION, false);
    }

    public void setNotifyNewVersion(boolean isNotify) {
        getPreferences().putBoolean(NOTIFY_NEW_VERSION, isNotify);

    }

    public boolean isNotifyAutoDetection() {
        return getPreferences().getBoolean(NOTIFY_AUTO_DETECTION, true);
    }

    public void setNotifyAutoDetection(boolean isNotify) {
        getPreferences().putBoolean(NOTIFY_AUTO_DETECTION, isNotify);

    }

    public boolean isTestStderr() {
        return getPreferences().getBoolean(TEST_STDERR, false);
    }

    public void setTestStderr(boolean isStderr) {
        getPreferences().putBoolean(TEST_STDERR, isStderr);

    }

    public String getComposerJson() {
        String composerJson = getPreferences().get(COMPOSER_JSON, null);
        if (composerJson == null) {
            composerJson = getDefaultComposerJson();
        }
        return composerJson;
    }

    private String getDefaultComposerJson() {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = CakePhpOptions.class.getResourceAsStream("/org/cakephp/netbeans/resources/composer.json"); // NOI18N
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) { // NOI18N
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n"); // NOI18N
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return sb.toString();
    }

    public void setComposerJson(String text) {
        getPreferences().put(COMPOSER_JSON, text);
    }

    public List<String> getAvailableCustomNodes() {
        String nodes = getPreferences().get(AVAILABLE_CUSTOM_NODES, null);
        if (nodes == null) {
            return DEFAULT_AVAILABLE_NODES;
        }
        return StringUtils.explode(nodes, "|"); // NOI18N
    }

    public void setAvailableCustomNodes(List<String> nodes) {
        getPreferences().put(AVAILABLE_CUSTOM_NODES, StringUtils.implode(nodes, "|")); // NOI18N
    }

    public boolean isBaserCmsEnabled() {
        return getPreferences().getBoolean(BASERCMS_ENABLED, false);
    }

    public void setBaserCmsEnabled(boolean isEnabled) {
        getPreferences().putBoolean(BASERCMS_ENABLED, isEnabled);
    }

    public boolean isBaserCmsVagrantSettings() {
        return getPreferences().getBoolean(BASERCMS_VAGARANT_SETTINGS, false);
    }

    public void setBaserCmsVagrantSettings(boolean isEnabled) {
        getPreferences().putBoolean(BASERCMS_VAGARANT_SETTINGS, isEnabled);
    }

    public Preferences getPreferences() {
        return NbPreferences.forModule(CakePhpOptions.class);
    }

}
