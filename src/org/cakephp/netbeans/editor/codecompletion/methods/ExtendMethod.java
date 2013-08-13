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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.cakephp.netbeans.editor.codecompletion.methods.Method.DOT;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.CakeVersion;
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
        // extend method was added from 2.x
        int major = CakeVersion.getInstance(phpModule).getMajor();
        if (major < 2) {
            return Collections.emptyList();
        }

        // is ctp file?
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (currentFile == null || cakeModule == null || !CakePhpUtils.isCtpFile(currentFile)) {
            return Collections.emptyList();
        }

        // plugin name for input value e.g. Debugkit.something
        String pluginName = ""; // NOI18N
        String basePath;
        boolean isAbsolutePath = false;
        if (CakePhpUtils.isAbsolutePath(input)) {
            isAbsolutePath = true;
            basePath = input;
        } else {
            // split plugin
            String[] pluginSplit = CakePhpUtils.pluginSplit(input);
            if (pluginSplit != null && pluginSplit.length == 2) {
                pluginName = pluginSplit[0];
                basePath = pluginSplit[1];
            } else {
                basePath = input;
            }
            if (CakePhpUtils.isAbsolutePath(basePath)) {
                isAbsolutePath = true;
            }
        }

        // set sub directory path
        String filter = setSubDirectoryPath(basePath);

        List<String> elements = new ArrayList<String>();
        if (argCount == 1) {
            // set Plugins
            if (pluginName.isEmpty()) {
                Set<String> pluginNames = Collections.emptySet();
                if (!input.contains(SLASH) && !input.contains(DOT)) {
                    pluginNames = getPluginNames();
                }
                for (String name : pluginNames) {
                    if (name.startsWith(filter)) {
                        elements.add(name + DOT);
                    }
                }
            }

            // get DIR_TYPE
            CakePhpModule.DIR_TYPE dirType = cakeModule.getDirectoryType(currentFile);
            FileObject viewBaseDirectory;

            // is plugin?
            if (!pluginName.isEmpty()) {
                viewBaseDirectory = getPluginViewDirectory(pluginName);
            } else {
                pluginName = null;
                // get current plugin name
                String currentPluginName = cakeModule.getCurrentPluginName(currentFile);
                if (currentPluginName.isEmpty()) {
                    currentPluginName = null;
                }
                viewBaseDirectory = cakeModule.getViewDirectory(dirType, currentPluginName);
            }
            if (viewBaseDirectory == null) {
                return Collections.emptyList();
            }

            if (isAbsolutePath) {
                // do nothing
            } else if (cakeModule.isElement(currentFile)) {
                viewBaseDirectory = viewBaseDirectory.getFileObject("Elements"); // NOI18N
            } else if (cakeModule.isLayout(currentFile)) {
                viewBaseDirectory = viewBaseDirectory.getFileObject("Layouts"); // NOI18N
            } else {
                if (StringUtils.isEmpty(pluginName)) {
                    String viewFolderName = getViewFolderName(viewBaseDirectory);
                    viewBaseDirectory = viewBaseDirectory.getFileObject(viewFolderName);
                }
            }

            if (viewBaseDirectory == null) {
                return Collections.emptyList();
            }

            viewBaseDirectory = viewBaseDirectory.getFileObject(subDirectoryPath);

            // add elememts
            if (viewBaseDirectory != null) {
                for (FileObject child : viewBaseDirectory.getChildren()) {
                    // can't use the same file
                    if (child != currentFile) {
                        addElement(child, filter, elements, pluginName);
                    }
                }
            }
        }
        return elements;
    }

    private Set<String> getPluginNames() {
        Set<String> plugins = new HashSet<String>();
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return Collections.emptySet();
        }
        for (DIR_TYPE dirType : PLUGINS) {
            FileObject pluginDirectory = cakeModule.getDirectory(dirType);
            if (pluginDirectory == null || !pluginDirectory.isFolder()) {
                continue;
            }
            for (FileObject child : pluginDirectory.getChildren()) {
                if (child.isFolder()) {
                    plugins.add(child.getName());
                }
            }
        }
        return plugins;
    }

    private FileObject getPluginViewDirectory(String pluginName) {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return null;
        }
        for (DIR_TYPE dirType : PLUGINS) {
            FileObject pluginViewDirectory = cakeModule.getDirectory(dirType, FILE_TYPE.VIEW, pluginName);
            if (pluginViewDirectory == null || !pluginViewDirectory.isFolder()) {
                continue;
            }
            return pluginViewDirectory;
        }
        return null;
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
