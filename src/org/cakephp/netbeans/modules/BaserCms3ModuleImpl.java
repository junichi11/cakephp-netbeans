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
package org.cakephp.netbeans.modules;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.options.CakePhpOptions;
import org.cakephp.netbeans.versions.Versions;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * CakePhpModuleImpl for baserCMS 3.x.x
 *
 * @author junichi11
 */
public class BaserCms3ModuleImpl extends CakePhp2ModuleImpl {

    private static final String FILE_VIEW_RELATIVE = "../View/%s/%s.php"; // NOI18N
    private static final String FILE_THEME_VIEW_RELATIVE = "../View/Themed/%s/%s/%s.php"; // NOI18N
    // don't change order
    private static final List<DIR_TYPE> ALL_BASER_DIR_TYPES = Arrays.asList(DIR_TYPE.BASER_PLUGIN, DIR_TYPE.BASER);

    public BaserCms3ModuleImpl(PhpModule phpModule, Versions versions) {
        super(phpModule, versions);
    }

    @Override
    public boolean isView(FileObject fo) {
        if (fo == null) {
            return false;
        }
        String filePath = fo.getPath();
        if (filePath.matches("\\A.+(/webroot/theme/|/Baser/View/|/Baser/Plugin/.+/View/).+\\z")) { // NOI18N
            return true;
        }
        return super.isView(fo);
    }

    @Override
    public FileObject getView(FileObject controller, String viewName) {
        File parent = FileUtil.toFile(controller).getParentFile();
        File view = PropertyUtils.resolveFile(parent, String.format(FILE_VIEW_RELATIVE, getViewFolderName(controller.getName()), viewName));
        if (view.isFile()) {
            return FileUtil.toFileObject(view);
        }
        return null;
    }

    @Override
    public FileObject getView(FileObject controller, String viewName, FileObject theme) {
        File parent = FileUtil.toFile(controller).getParentFile();
        File view = PropertyUtils.resolveFile(parent, String.format(FILE_THEME_VIEW_RELATIVE, theme.getName(), getViewFolderName(controller.getName()), viewName));
        if (view.isFile()) {
            return FileUtil.toFileObject(view);
        }
        return null;
    }

    @Override
    public CakePhpModule.FILE_TYPE getFileType(FileObject currentFile) {
        if (currentFile == null) {
            return CakePhpModule.FILE_TYPE.NONE;
        }
        String path = currentFile.getPath();
        if (path.matches("\\A.+(/View/Elements/|/webroot/theme/.+/Elements/).+\\z")) { // NOI18N
            return CakePhpModule.FILE_TYPE.ELEMENT;
        } else if (path.matches("\\A.+(/View/Layouts/|/webroot/thehme/.+/Layouts/).+\\z")) { // NOI18N
            return CakePhpModule.FILE_TYPE.LAYOUT;
        } else if (path.matches("\\A.+(/View/|/webroot/theme/).+\\z")) { // NOI18N
            return CakePhpModule.FILE_TYPE.VIEW;
        }
        return super.getFileType(currentFile);
    }

    @Override
    public DIR_TYPE getDirectoryType(FileObject currentFile) {
        String path = currentFile.getPath();
        for (DIR_TYPE dirType : ALL_BASER_DIR_TYPES) {
            FileObject directory = getDirectory(dirType);
            if (directory == null) {
                continue;
            }
            if (path.startsWith(directory.getPath())) {
                return dirType;
            }
        }
        return super.getDirectoryType(currentFile);
    }

    @Override
    public String getFileNameWithExt(CakePhpModule.FILE_TYPE type, String name) {
        String fileNameWithExt = super.getFileNameWithExt(type, name);
        return fileNameWithExt.replace(".ctp", ".php"); // NOI18N
    }

    /**
     * Get {@link PhpModuleProperties}. If vagrant settings is enabled, set
     * index and url.
     *
     * @param phpModule {@link PhpModule}
     * @return
     */
    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        PhpModuleProperties properties = super.getPhpModuleProperties(phpModule);
        CakePhpOptions options = CakePhpOptions.getInstance();
        if (options.isBaserCmsEnabled() && options.isBaserCmsVagrantSettings()) {
            FileObject cakePhpDirectory = CakePhpModule.getCakePhpDirectory(phpModule);
            if (cakePhpDirectory != null) {
                // url
                // XXX url is not set
                // TODO WORKAROUND: find private.properties then set url to it manually
                properties = properties.setUrl("http://192.168.33.10/"); // NOI18N
                // index.php
                FileObject index = cakePhpDirectory.getFileObject("index.php"); // NOI18N
                if (index != null) {
                    properties = properties.setIndexFile(index);
                }
            }
        }
        return properties;
    }

    @Override
    public FileObject getDirectory(CakePhpModule.DIR_TYPE type) {
        FileObject cakePhpDirectory = getCakePhpDirectory();
        if (cakePhpDirectory == null || type == null) {
            return null;
        }
        switch (type) {
            case BASER:
                return cakePhpDirectory.getFileObject("lib/Baser"); // NOI18N
            case BASER_PLUGIN:
                return cakePhpDirectory.getFileObject("lib/Baser/Plugin"); // NOI18N
            default:
                return super.getDirectory(type);
        }
    }

    @Override
    public FileObject getDirectory(CakePhpModule.DIR_TYPE dirType, CakePhpModule.FILE_TYPE fileType, String pluginName) {
        if (dirType == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        switch (dirType) {
            case BASER_PLUGIN: // no break
                if (StringUtils.isEmpty(pluginName)) {
                    return null;
                }
                sb.append(pluginName).append("/"); // NOI18N
            case BASER:
                switch (fileType) {
                    case CONTROLLER:
                    case VIEW:
                    case MODEL:
                        sb.append(fileType.toString());
                        break;
                    case COMPONENT:
                        sb.append("Controller/Component"); // NOI18
                        break;
                    case HELPER:
                        sb.append("View/Helper"); // NOI18
                        break;
                    case ELEMENT:
                        sb.append("View/Elements"); // NOI18
                        break;
                    case LAYOUT:
                        sb.append("View/Layouts"); // NOI18
                        break;
                    case BEHAVIOR:
                        sb.append("Model/Behavior"); // NOI18
                        break;
                    case TEST:
                        sb.append("Test"); // NOI18
                        break;
                    case TESTCASE:
                        sb.append("Test/Case"); // NOI18
                        break;
                    case FIXTURE:
                        sb.append("Test/Fixture"); // NOI18
                        break;
                    case CONFIG:
                        sb.append("Config"); // NOI18
                        break;
                    case CONSOLE:
                        sb.append("Console"); // NOI18
                        break;
                    case WEBROOT:
                        if (dirType == DIR_TYPE.BASER) {
                            return null;
                        }
                        sb.append("webroot"); // NOI18
                        break;
                    case TMP:
                        return null;
                    case NONE:
                        if (dirType == DIR_TYPE.BASER_PLUGIN) {
                            return getDirectory(dirType).getFileObject(pluginName);
                        }
                        return getDirectory(dirType);
                    default:
                        return null;
                }
                break;
            default:
                return super.getDirectory(dirType, fileType, pluginName);
        }
        FileObject baseDirectory = getDirectory(dirType);
        if (baseDirectory == null) {
            return null;
        }
        return baseDirectory.getFileObject(sb.toString());
    }

}
