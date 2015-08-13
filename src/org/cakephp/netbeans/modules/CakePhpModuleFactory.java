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
package org.cakephp.netbeans.modules;

import java.util.HashMap;
import java.util.Map;
import org.cakephp.netbeans.dotcake.Dotcake;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.versions.CakeVersion;
import org.cakephp.netbeans.versions.Versionable;
import org.cakephp.netbeans.versions.Versionable.VERSION_TYPE;
import org.cakephp.netbeans.versions.Versions;
import org.cakephp.netbeans.versions.VersionsFactory;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class CakePhpModuleFactory {

    private final Map<PhpModule, CakePhpModule> modules = new HashMap<>();
    private static final CakePhpModuleFactory INSTANCE = new CakePhpModuleFactory();
    private boolean isCreating = false;

    /**
     * Get instance. singleton
     *
     * @return CakePhpMuduleFactory
     */
    public static CakePhpModuleFactory getInstance() {
        return INSTANCE;
    }

    private CakePhpModuleFactory() {
    }

    /**
     * Create CakePhpModule instance. Keep created instances by Factory.
     *
     * @param phpModule
     * @return CakePhpModule if it can create instance of implementation class,
     * otherwise null
     */
    public synchronized CakePhpModule create(PhpModule phpModule) {
        assert !isCreating;
        CakePhpModule module = modules.get(phpModule);
        if (module == null) {
            try {
                start();
                CakePhpModuleImpl impl = getCakePhpModuleImpl(phpModule);
                if (phpModule == null) {
                    return new CakePhpModule(phpModule, impl);
                }

                // create module class
                module = new CakePhpModule(phpModule, impl);
                modules.put(phpModule, module);
            } finally {
                finish();
            }
        }
        return module;
    }

    /**
     * Reset CakePhpModuleImpl for specific PhpModule.
     *
     * @param cakePhpModule
     */
    public void reset(@NonNull CakePhpModule cakePhpModule) {
        PhpModule phpModule = cakePhpModule.getPhpModule();
        if (phpModule != null) {
            CakePhpModuleImpl impl = getCakePhpModuleImpl(phpModule);
            cakePhpModule.setImpl(impl);
        }
    }

    /**
     * Remove CakePhpModule from moudles.
     *
     * @param phpModule
     */
    public void remove(@NonNull PhpModule phpModule) {
        modules.remove(phpModule);
    }

    private CakePhpModuleImpl getCakePhpModuleImpl(PhpModule phpModule) {
        VersionsFactory factory = VersionsFactory.getInstance();
        Versions versions = factory.create(phpModule);
        CakeVersion version = (CakeVersion) versions.getVersion(Versionable.VERSION_TYPE.CAKEPHP);
        if (version == null) {
            return new CakePhpDummyModuleImpl(phpModule, versions);
        }

        // .cake support (only CakePHP 2.x)
        Dotcake dotcake = getDotcake(phpModule);
        CakePhpModuleImpl impl;
        if (version.isCakePhp(1)) {
            impl = new CakePhp1ModuleImpl(phpModule, versions);
        } else if (version.isCakePhp(2)) {
            if (versions.hasVersion(VERSION_TYPE.BASERCMS)) {
                impl = new BaserCms3ModuleImpl(phpModule, versions);
            } else {
                impl = new CakePhp2ModuleImpl(phpModule, versions, dotcake);
            }
        } else {
            impl = new CakePhpDummyModuleImpl(phpModule, versions);
        }
        return impl;
    }

    /**
     * Get {@link Dotcake}.
     *
     * @param phpModule
     * @return Dotcake instance if .cake setting exists, {@code null} otherwise
     */
    private Dotcake getDotcake(PhpModule phpModule) {
        String dotcakeFilePath = CakePreferences.getDotcakeFilePath(phpModule);
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory != null && !StringUtils.isEmpty(dotcakeFilePath)) {
            return Dotcake.fromJson(sourceDirectory.getFileObject(dotcakeFilePath));
        }
        return null;
    }

    private void start() {
        isCreating = true;
    }

    private void finish() {
        isCreating = false;
    }
}
