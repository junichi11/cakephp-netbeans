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
package org.cakephp.netbeans.versions;

import java.io.IOException;
import org.cakephp.netbeans.github.BaserCmsGithubTags;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Version for baserCMS.
 *
 * @author junichi11
 */
public final class BaserCmsVersion implements Versionable {

    private final int major;
    private final int minor;
    private final int revision;
    private final String notStable;
    private final String versionNumber;
    private String latestStableVersion;

    BaserCmsVersion(String versionNumber, int major, int minor, int revision, String notStable) {
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
            BaserCmsGithubTags githubTags = BaserCmsGithubTags.getInstance();
            latestStableVersion = githubTags.getLatestStableVersion();
            if (latestStableVersion.startsWith("basercms-")) { // NOI18N
                latestStableVersion = latestStableVersion.replace("basercms-", ""); // NOI18N
            }
        }
        return latestStableVersion;
    }

    @Override
    public VERSION_TYPE getType() {
        return VERSION_TYPE.BASERCMS;
    }

    /**
     * Get baserCMS version file.
     *
     * @param phpModule
     * @return version file.
     */
    public static FileObject getVersionFile(PhpModule phpModule) {
        FileObject baserRootDirectory = CakePhpModule.getCakePhpDirectory(phpModule);
        if (baserRootDirectory == null) {
            return null;
        }

        return baserRootDirectory.getFileObject("lib/Baser/VERSION.txt"); // NOI18N
    }

    /**
     * Get version number from version file.
     *
     * @param versionFile version file
     * @return version number
     */
    public static String getVersionNumber(@NonNull FileObject versionFile) {
        try {
            String versionNumber = ""; // NOI18N
            for (String line : versionFile.asLines("UTF-8")) { // NOI18N
                // version number exists at first line
                line = line.trim();
                versionNumber = line;
                break;
            }
            return versionNumber;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
