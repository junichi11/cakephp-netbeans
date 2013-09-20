/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.module;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE.APP;
import static org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE.APP_LIB;
import static org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE.APP_PLUGIN;
import static org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE.APP_VENDOR;
import static org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE.CORE;
import static org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE.PLUGIN;
import static org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE.VENDOR;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.BEHAVIOR;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.COMPONENT;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.CONFIG;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.CONSOLE;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.CONTROLLER;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.HELPER;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.MODEL;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.NONE;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.TEST;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.VIEW;
import static org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE.WEBROOT;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

public class CakePhp3ModuleImpl extends CakePhp2ModuleImpl {

    private static final Logger LOGGER = Logger.getLogger(CakePhp3ModuleImpl.class.getName());
    private static final String DIR_COMPONENT = "Component"; // NOI18N
    private static final String DIR_HELPER = "Helper"; // NOI18N
    private static final String DIR_BEHAVIOR = "Behavior"; // NOI18N

    public CakePhp3ModuleImpl(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public FileObject getConfigFile() {
        FileObject configDirectory = getConfigDirectory(CakePhpModule.DIR_TYPE.APP);
        if (configDirectory != null) {
            return configDirectory.getFileObject("app.php"); // NOI18N
        }
        return null;
    }

    @Override
    public FileObject getDirectory(CakePhpModule.DIR_TYPE type, CakePhpModule.FILE_TYPE fileType, String pluginName) {
        if (type == null) {
            return null;
        }
        if (fileType == null && pluginName == null) {
            return getDirectory(type);
        }
        if (pluginName != null) {
            switch (type) {
                case APP: // no break
                case APP_LIB: // no break
                case APP_VENDOR: // no break
                case CORE: // no break
                case VENDOR: // no break
                case PLUGIN:
                    return null;
                default:
                    break;
            }
        }

        StringBuilder sb = new StringBuilder();
        switch (type) {
            case APP_LIB: // no break
            case APP_VENDOR:
                if (fileType == null || fileType == CakePhpModule.FILE_TYPE.NONE) {
                    return getDirectory(type);
                } else {
                    return null;
                }
            case APP_PLUGIN: // no break
                if (pluginName == null || pluginName.isEmpty()) {
                    return null;
                }
                if (fileType == null) {
                    return getDirectory(type).getFileObject(pluginName);
                }
                sb.append(pluginName);
                sb.append("/"); // NOI18N
            case CORE: // no break
            case APP:
                switch (fileType) {
                    case CONTROLLER: // no break
                    case VIEW: // no break
                    case MODEL:
                        sb.append(fileType.toString());
                        break;
                    case COMPONENT:
                        sb.append("Controller/"); // NOI18N
                        sb.append(DIR_COMPONENT);
                        break;
                    case HELPER:
                        sb.append("View/"); // NOI18N
                        sb.append(DIR_HELPER);
                        break;
                    case BEHAVIOR:
                        sb.append("Model/"); // NOI18N
                        sb.append(DIR_BEHAVIOR);
                        break;
                    case TEST:
                        sb.append("Test"); // NOI18N
                        break;
                    case CONFIG:
                        sb.append("Config"); // NOI18N
                        break;
                    case CONSOLE:
                        sb.append("Console"); // NOI18N
                        break;
                    case WEBROOT:
                        if (type == CakePhpModule.DIR_TYPE.CORE) {
                            return null;
                        }
                        sb.append("webroot"); // NOI18N
                        break;
                    case NONE:
                        if (type == CakePhpModule.DIR_TYPE.APP_PLUGIN) {
                            return getDirectory(type).getFileObject(pluginName);
                        }
                        return getDirectory(type);
                    default:
                        return null;
                }
                break;
            default:
                return null;
        }
        FileObject directory = getDirectory(type);
        if (directory == null) {
            return null;
        }
        return directory.getFileObject(sb.toString());
    }

    @Override
    public FileObject getDirectory(CakePhpModule.DIR_TYPE type) {
        if (type == null) {
            return null;
        }
        FileObject sourceDirectory = getCakePhpDirectory();
        if (sourceDirectory == null) {
            LOGGER.log(Level.WARNING, "Not found source directory");
            return null;
        }
        String path = ""; // NOI18N
        switch (type) {
            case APP:
            case APP_LIB:
            case APP_PLUGIN:
            case APP_VENDOR:
                return getAppDirectory(type);
            case CORE:
                path = "lib/Cake"; // NOI18N
                break;
            case PLUGIN: // no break
            case VENDOR:
                return null;
            default:
                throw new AssertionError();
        }

        return sourceDirectory.getFileObject(path);
    }

    private FileObject getAppDirectory(CakePhpModule.DIR_TYPE dirType) {
        FileObject appDir = getAppDirectory();
        if (appDir == null) {
            return null;
        }

        switch (dirType) {
            case APP:
                return appDir;
            case APP_LIB:
                return appDir.getFileObject("Lib"); // NOI18N
            case APP_PLUGIN:
                return appDir.getFileObject("Plugin"); // NOI18N
            case APP_VENDOR:
                return appDir.getFileObject("vendor"); // NOI18N
            default:
                throw new AssertionError();
        }
    }

    /**
     * Get namespace.
     *
     * @param target
     * @return namespace
     */
    private String getNamespace(FileObject target) {
        String namespace = ""; // NOI18N
        if (target.isFolder()) {
            return namespace;
        }
        FileObject parent = target.getParent();
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        FileObject appDirectory = cakeModule.getDirectory(CakePhpModule.DIR_TYPE.APP);
        FileObject coreDirectory = cakeModule.getDirectory(CakePhpModule.DIR_TYPE.CORE);
        if (appDirectory == null || coreDirectory == null || parent == null) {
            return namespace;
        }
        String appPath = appDirectory.getPath();
        String corePath = coreDirectory.getPath();
        String parentPath = parent.getPath();
        String path = ""; // NOI18N

        // App
        if (parentPath.startsWith(appPath)) {
            path = parentPath.replace(appPath, ""); // NOI18N
            path = path.replaceAll("/", "\\\\"); // NOI18N
            path = "App" + path; // NOI18N
        }

        // Core
        if (parentPath.startsWith(corePath)) {
            path = parentPath.replace(corePath, ""); // NOI18N
            path = path.replaceAll("/", "\\\\"); // NOI18N
            path = "Cake" + path; // NOI18N
        }

        return path;
    }
}
