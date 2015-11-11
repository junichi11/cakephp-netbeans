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
package org.cakephp.netbeans.editor.codecompletion.methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.cakephp.netbeans.editor.codecompletion.methods.Method.DOT;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;

/**
 * CakePHP 2.x
 *
 * @author junichi11
 */
public class ExtendMethod extends AssetMethod {

    private final FileObject currentFile;

    public ExtendMethod(PhpModule phpModule) {
        super(phpModule);
        this.currentFile = null;
    }

    public ExtendMethod(PhpModule phpModule, FileObject currentFile) {
        super(phpModule);
        this.currentFile = currentFile;

    }

    @Override
    public List<String> getElements(int argCount, String input) {
        // is ctp file?
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (currentFile == null || cakeModule == null || !CakePhpUtils.isCtpFile(currentFile)) {
            return Collections.emptyList();
        }

        // extend method was added from 2.x
        CakeVersion cakeVersion = cakeModule.getCakeVersion();
        if (cakeVersion == null) {
            return Collections.emptyList();
        }
        int major = cakeVersion.getMajor();
        if (major < 2) {
            return Collections.emptyList();
        }

        // plugin name for input value e.g. Debugkit.something
        String pluginName = null;
        String basePath;
        String[] pluginSplit = CakePhpUtils.pluginSplit(input);
        if (pluginSplit != null && pluginSplit.length == 2) {
            pluginName = pluginSplit[0];
            basePath = pluginSplit[1];
        } else {
            basePath = input;
        }

        // start with "/"?
        boolean isAbsolutePath = CakePhpUtils.isAbsolutePath(basePath);

        // set sub directory path
        String filter = setSubDirectoryPath(basePath);

        List<String> elements = new ArrayList<>();
        if (argCount == 1) {
            // get DIR_TYPE, FILE_TYPE
            DIR_TYPE dirType = cakeModule.getDirectoryType(currentFile);
            FILE_TYPE fileType = cakeModule.getFileType(currentFile);
            FileObject targetDirectory = getTargetDirectory(cakeModule, dirType, fileType, pluginName, isAbsolutePath);

            // add elememts
            if (targetDirectory != null) {
                for (FileObject child : targetDirectory.getChildren()) {
                    // can't use the same file
                    if (child != currentFile) {
                        addElement(child, filter, elements, pluginName);
                    }
                }
            }

            // add Plugins
            if (StringUtils.isEmpty(pluginName) && fileType == FILE_TYPE.ELEMENT) {
                if (!input.contains(SLASH) && !input.contains(DOT)) {
                    for (String name : cakeModule.getAllPluginNames()) {
                        if (name.startsWith(filter)) {
                            elements.add(name + DOT);
                        }
                    }
                }
            }
        }
        return elements;
    }

    private FileObject getTargetDirectory(CakePhpModule cakeModule, DIR_TYPE dirType, FILE_TYPE fileType, String pluginName, boolean isAbsolutePath) {
        boolean isPlugin = !StringUtils.isEmpty(pluginName);
        FileObject targetDirectory = null;
        // is plugin?
        if (isPlugin) {
            for (DIR_TYPE plugin : PLUGINS) {
                targetDirectory = cakeModule.getDirectory(plugin, fileType, pluginName);
                if (targetDirectory != null) {
                    break;
                }
            }
        } else {
            // get current plugin name
            String currentPluginName = cakeModule.getCurrentPluginName(currentFile);
            if (currentPluginName.isEmpty()) {
                currentPluginName = null;
            }
            targetDirectory = cakeModule.getDirectory(dirType, fileType, currentPluginName);
        }

        if (targetDirectory == null) {
            return null;
        }

        if (!isAbsolutePath && fileType == FILE_TYPE.VIEW && !isPlugin) {
            String viewFolderName = getViewFolderName(targetDirectory);
            targetDirectory = targetDirectory.getFileObject(viewFolderName);
        }

        if (targetDirectory == null) {
            return null;
        }

        return targetDirectory.getFileObject(subDirectoryPath);
    }

    private String getViewFolderName(FileObject viewBaseDirectory) {
        String path = currentFile.getPath();
        String viewBasePath = viewBaseDirectory.getPath();
        path = path.replace(viewBasePath, ""); // NOI18N
        int indexOfSlash = path.indexOf("/", 1); // NOI18N
        String viewFolderName = ""; // NOI18N
        if (indexOfSlash != -1) {
            viewFolderName = path.substring(1, indexOfSlash);
        }
        return viewFolderName;
    }
}
