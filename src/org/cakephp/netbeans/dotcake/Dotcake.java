/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.dotcake;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Parse .cake file.
 * <pre>
 * format of .cake file
 * {
 *     "cake": "..\/lib\/",
 *     "build_path": {
 *         "models": [
 *             ".\/Model\/"
 *         ],
 *         ...,
 *         "plugins": [
 *             ".\/Plugin\/",
 *             "..\/plugins\/"
 *         ]
 *     }
 * }
 * </pre>
 * @link https://github.com/dotcake/dotcake
 * @author junichi11
 */
public final class Dotcake {

    private final String cake;
    @SerializedName("build_path") // NOI18N
    private final Map<String, List<String>> buildPath;
    private File dotcakeFile;
    private static final String DOTCAKE_NAME = ".cake"; // NOI18N

    public enum BuildPathCategory {

        MODELS,
        BEHAVIORS,
        DATASOURCES,
        DATABASES,
        SESSIONS,
        CONTROLLERS,
        COMPONENTS,
        AUTHS,
        ACLS,
        VIEWS,
        HELPERS,
        CONSOLES,
        COMMANDS,
        TASKS,
        LIBS,
        LOCALES,
        VENDORS,
        PLUGINS,
        NONE,;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

    public Dotcake(String cake, Map<String, List<String>> buildPath) {
        this.cake = cake;
        this.buildPath = buildPath;
    }

    private Dotcake setDotcakeFile(File dotcakeFile) {
        this.dotcakeFile = dotcakeFile;
        return this;
    }

    public File getDotcakeFile() {
        return dotcakeFile;
    }

    /**
     * Create an instance from json file.
     *
     * @param dotcakeFile .cake file
     * @return {@code null} if .cake file has some problem, {@code Dotcake}
     * instance otherwise.
     */
    @CheckForNull
    public static Dotcake fromJson(FileObject dotcakeFile) {
        if (!isDotcake(dotcakeFile)) {
            return null;
        }
        InputStream inputStream;
        try {
            inputStream = dotcakeFile.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            try {
                Gson gson = new Gson();
                Dotcake dotcake = gson.fromJson(reader, Dotcake.class)
                        .setDotcakeFile(FileUtil.toFile(dotcakeFile));
                return dotcake;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    reader.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    /**
     * Check whether file is .cake.
     *
     * @param file
     * @return {@code true} if file is .cake, {@code false} otherwise.
     */
    public static boolean isDotcake(FileObject file) {
        return file != null && !file.isFolder() && file.getNameExt().equals(DOTCAKE_NAME);
    }

    public String getCake() {
        return cake;
    }

    public Map<String, List<String>> getBuildPath() {
        return buildPath;
    }

    /**
     * Get build paths for specified name.
     *
     * @param name category name (e.g. models)
     * @return paths
     */
    public List<String> getBuildPaths(BuildPathCategory category) {
        if (category == null) {
            return Collections.emptyList();
        }
        String name = category.toString();
        if (StringUtils.isEmpty(name) || buildPath == null) {
            return Collections.emptyList();
        }
        List<String> paths = buildPath.get(name);
        if (paths == null) {
            return Collections.emptyList();
        }
        return paths;
    }
}
