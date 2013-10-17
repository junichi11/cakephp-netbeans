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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public class CakeVersion {

    private int major;
    private int minor;
    private int revision;
    private String notStable;
    private String versionNumber;
    private static CakeVersion INSTANCE = null;
    private static PhpModule pm;
    private static final Logger LOGGER = Logger.getLogger(CakeVersion.class.getName());

    private CakeVersion(PhpModule pm) {
        String[] split = getCakePhpVersionSplit(pm);
        if (split != null) {
            int length = split.length;
            if (length >= 4) {
                notStable = split[3];
            }
            if (length >= 3) {
                revision = Integer.parseInt(split[2]);
            }
            if (length >= 2) {
                minor = Integer.parseInt(split[1]);
            }
            if (length >= 1) {
                major = Integer.parseInt(split[0]);
            }

            if (length <= 0) {
                major = -1;
                minor = -1;
                revision = -1;
                notStable = ""; // NOI18N
            }
            CakeVersion.pm = pm;
        } else {
            FileObject cakephpDirectory = CakePhpModule.getCakePhpDirectory(pm);
            if (cakephpDirectory != null) {
                FileObject cake = CakePhpModule.getCakePhpDirectory(pm).getFileObject("cake"); // NOI18N
                if (cake != null) {
                    major = 1;
                    minor = -1;
                    revision = -1;
                    notStable = ""; // NOI18N
                } else {
                    FileObject cakePhpDirectory = CakePhpModule.getCakePhpDirectory(pm);
                    cake = cakePhpDirectory.getFileObject("lib/Cake"); // NOI18N

                    // installing with Composer
                    if (cake == null) {
                        cake = cakePhpDirectory.getFileObject("Vendor/pear-pear.cakephp.org/CakePHP/Cake"); // NOI18N
                    }

                    if (cake != null) {
                        FileObject app = CakePhpModule.getCakePhpDirectory(pm).getFileObject("App"); // NOI18N
                        if (app != null) {
                            major = 3;
                        } else {
                            major = 2;
                        }
                        minor = -1;
                        revision = -1;
                        notStable = ""; // NOI18N
                    }
                }
                if (cake == null) {
                    CakeVersion.pm = null;
                } else {
                    CakeVersion.pm = pm;
                }
            } else {
                CakeVersion.pm = null;
            }

        }
    }

    public static CakeVersion getInstance(PhpModule pm) {
        if (INSTANCE == null || CakeVersion.pm != pm || INSTANCE.versionNumber == null) {
            INSTANCE = new CakeVersion(pm);
        }
        return INSTANCE;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRevision() {
        return revision;
    }

    public String getNotStable() {
        return notStable;
    }

    public String getVersion() {
        return versionNumber;
    }

    /**
     * Check CakePHP major version
     *
     * @param majorVersion
     * @return
     */
    public boolean isCakePhp(int majorVersion) {
        return major == majorVersion;
    }

    /**
     * Get CakePHP version.
     *
     * @param PhpModule phpModule
     * @return String If can't get the version file, return null.
     */
    private String getCakePhpVersion(PhpModule phpModule) {
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
            version = root.getFileObject("cake/VERSION.txt"); // NOI18N
        } else {
            version = root.getFileObject("lib/Cake/VERSION.txt"); // NOI18N
        }
        // installing with Composer
        if (version == null) {
            version = root.getFileObject("Vendor/pear-pear.cakephp.org/CakePHP/Cake/VERSION.txt"); // NOI18N
        }
        if (version == null) {
            return null;
        }
        try {
            versionNumber = ""; // NOI18N
            for (String line : version.asLines("UTF-8")) { // NOI18N
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

    private String[] getCakePhpVersionSplit(PhpModule phpModule) {
        String version = getCakePhpVersion(phpModule);
        if (version == null) {
            return null;
        }
        return version.split("[., -]"); // NOI18N
    }
}
