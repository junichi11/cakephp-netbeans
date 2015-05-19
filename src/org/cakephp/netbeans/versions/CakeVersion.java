/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Versionable 2 only ("GPL") or the Common
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
 * by Oracle in the GPL Versionable 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Versionable 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Versionable 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Versionable 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Versionable 2 code and therefore, elected the GPL
 * Versionable 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.versions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.github.CakePhpGithubTags;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

public final class CakeVersion implements Versionable {

    private final int major;
    private final int minor;
    private final int revision;
    private final String notStable;
    private final String versionNumber;
    private String latestStableVersion;
    private static final Logger LOGGER = Logger.getLogger(CakeVersion.class.getName());

    CakeVersion(String versionNumber, int major, int minor, int revision, String notStable) {
        this.versionNumber = versionNumber;
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.notStable = notStable;
    }

    @Override
    public String getVersion() {
        return versionNumber;
    }

    @Override
    public int getMajor() {
        return major;
    }

    @Override
    public int getMinor() {
        return minor;
    }

    @Override
    public int getRevision() {
        return revision;
    }

    @Override
    public String getNotStable() {
        return notStable;
    }

    public boolean isCakePhp(int majorVersion) {
        return this.major == majorVersion;
    }

    @Override
    public boolean hasUpdate() {
        if (versionNumber == null) {
            return false;
        }
        latestStableVersion = getLatestStableVersion();
        if (latestStableVersion == null) {
            return false;
        }

        // TODO not stable
        if (!StringUtils.isEmpty(notStable)) {
            return false;
        }

        return !versionNumber.equals(latestStableVersion);
    }

    @Override
    public String getLatestStableVersion() {
        if (latestStableVersion == null) {
            CakePhpGithubTags githubTags = CakePhpGithubTags.getInstance();
            latestStableVersion = githubTags.getLatestStableVersion();
        }
        return latestStableVersion;
    }

    @Override
    public VERSION_TYPE getType() {
        return VERSION_TYPE.CAKEPHP;
    }

    /**
     * Get CakePHP VERSION.txt file.
     *
     * @param phpModule
     * @return version file if file exists, {@code null} otherwise.
     */
    public static FileObject getVersionFile(PhpModule phpModule) {
        // If install this plugin after PHP project was deleted,
        // PhpModule exists yet, but we can't get Project directory.
        // So, null might be returned to root variable
        FileObject root = CakePhpModule.getCakePhpDirectory(phpModule);
        if (root == null) {
            LOGGER.log(Level.INFO, "Not Found:{0}", CakePreferences.getCakePhpDirPath(phpModule));
            return null;
        }

        FileObject cake = root.getFileObject("cake"); // NOI18N
        FileObject version;
        if (cake != null) {
            // CakePHP 1.x
            version = root.getFileObject("cake/VERSION.txt"); // NOI18N
        } else {
            // CakePHP 2.x
            version = root.getFileObject("lib/Cake/VERSION.txt"); // NOI18N
        }
        // installing with Composer
        if (version == null) {
            // CakePHP 2.x
            version = root.getFileObject("Vendor/pear-pear.cakephp.org/CakePHP/Cake/VERSION.txt"); // NOI18N
        }

        // Composer
        if (version == null) {
            version = root.getFileObject("vendor/cakephp/cakephp/VERSION.txt"); // NOI18N
        }
        return version;

    }

    /**
     * Get CakePHP version number.
     *
     * @param versionFile
     * @return version number | {@code null}
     */
    public static String getVersionNumber(@NonNull FileObject versionFile) {
        try {
            String versionNumber = ""; // NOI18N
            for (String line : versionFile.asLines("UTF-8")) { // NOI18N
                if (!line.contains("//") && !line.equals("")) { // NOI18N
                    line = line.trim();
                    versionNumber = line;
                    break;
                }
            }
            return versionNumber;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

}
