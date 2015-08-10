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

import java.util.HashMap;
import org.cakephp.netbeans.versions.Versionable.VERSION_TYPE;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class VersionsFactory {

    private static final VersionsFactory INSTANCE = new VersionsFactory();

    public static VersionsFactory getInstance() {
        return INSTANCE;
    }

    private VersionsFactory() {
    }

    /**
     * Create {@link Versions}.
     *
     * @param phpModule
     * @return {@link Versions}
     */
    public Versions create(@NonNull PhpModule phpModule) {
        HashMap<VERSION_TYPE, Versionable> versionsMap = new HashMap<>();
        for (VERSION_TYPE type : VERSION_TYPE.values()) {
            Versionable version = create(phpModule, type);
            if (version != null) {
                versionsMap.put(type, version);
            }
        }
        return new Versions(versionsMap);
    }

    /**
     * Create {@link Versionable}
     *
     * @param phpModule
     * @param versionType version type
     * @return {@link Versionable}
     */
    @CheckForNull
    public Versionable create(@NonNull PhpModule phpModule, @NonNull VERSION_TYPE versionType) {
        // get version file
        FileObject versionFile = getVersionFile(phpModule, versionType);
        if (versionFile == null) {
            return null;
        }

        // get version number
        String versionNumber = getVersionNumber(versionFile, versionType);
        if (versionNumber == null) {
            return null;
        }

        // split version number
        String[] versionSplit = versionSplit(versionNumber);

        // create
        return createVersion(versionNumber, versionSplit, versionType);
    }

    /**
     * Create {@code Versionable}.
     *
     * @param versionNumber
     * @param versionSplit
     * @param versionType
     * @return
     */
    @CheckForNull
    private Versionable createVersion(@NonNull String versionNumber, String[] versionSplit, @NonNull VERSION_TYPE versionType) {
        String notStable = null;
        if (versionSplit.length == 4) {
            notStable = versionSplit[3];
        }
        int revision = -1;
        if (versionSplit.length >= 3) {
            revision = Integer.parseInt(versionSplit[2]);
        }
        int minor = -1;
        if (versionSplit.length >= 2) {
            minor = Integer.parseInt(versionSplit[1]);
        }
        int major = -1;
        if (versionSplit.length >= 1) {
            major = Integer.parseInt(versionSplit[0]);
        }

        Versionable version = null;
        switch (versionType) {
            case CAKEPHP:
                version = new CakeVersion(versionNumber, major, minor, revision, notStable);
                break;
            case BASERCMS:
                version = new BaserCmsVersion(versionNumber, major, minor, revision, notStable);
                break;
            default:
                throw new AssertionError();
        }
        return version;
    }

    /**
     * Get version file for type.
     *
     * @param phpModule
     * @param versionType
     * @return version file if file is exists, {@code null} otherwise.
     */
    @CheckForNull
    private FileObject getVersionFile(PhpModule phpModule, VERSION_TYPE versionType) {
        switch (versionType) {
            case CAKEPHP:
                return CakeVersion.getVersionFile(phpModule);
            case BASERCMS:
                return BaserCmsVersion.getVersionFile(phpModule);
            default:
                return null;
        }
    }

    /**
     * Get version number from version file.
     *
     * @param versionFile version file
     * @param versionType {@link VERSION_TYPE}
     * @return version number if version number exists, {@code null} otherwise.
     */
    @CheckForNull
    private String getVersionNumber(@NonNull FileObject versionFile, VERSION_TYPE versionType) {
        switch (versionType) {
            case CAKEPHP:
                return CakeVersion.getVersionNumber(versionFile);
            case BASERCMS:
                return BaserCmsVersion.getVersionNumber(versionFile);
            default:
                return null;
        }
    }

    /**
     * Split with regex.
     *
     * @param version version number text
     * @return split version with regex
     */
    private String[] versionSplit(String version) {
        if (version == null) {
            return null;
        }
        return version.split("[., -]"); // NOI18N
    }
}
