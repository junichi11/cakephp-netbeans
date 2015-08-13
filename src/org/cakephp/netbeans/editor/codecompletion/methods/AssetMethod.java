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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import static org.cakephp.netbeans.editor.codecompletion.methods.Method.DOT;
import static org.cakephp.netbeans.editor.codecompletion.methods.Method.SLASH;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class AssetMethod extends Method {

    public enum ASSET_TYPE {

        IMAGE,
        CSS,
        SCRIPT;

        @Override
        public String toString() {
            String name;
            switch (this) {
                case IMAGE:
                    name = "img"; // NOI18N
                    break;
                case CSS:
                    name = "css"; // NOI18N
                    break;
                case SCRIPT:
                    name = "js"; // NOI18N
                    break;
                default:
                    throw new AssertionError();
            }
            return name;
        }
    }
    protected ASSET_TYPE type;
    protected List<String> extFilter;
    protected String subDirectoryPath = ""; // NOI18N
    protected final List<DIR_TYPE> dirTypes = Arrays.asList(DIR_TYPE.APP_PLUGIN, DIR_TYPE.PLUGIN, DIR_TYPE.APP);

    public AssetMethod(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public List<String> getElements(int argCount, String filter) {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return Collections.emptyList();
        }
        CakeVersion version = cakeModule.getCakeVersion();
        if (version == null) {
            return Collections.emptyList();
        }
        int cakeVersion = version.getMajor();
        List<String> elements = new LinkedList<>();
        if (type == null) {
            return elements;
        }

        if (argCount == 1) {
            String pluginName = null;
            boolean isPlugin = false;

            // CakePHP 2.x
            if (cakeVersion >= 2) {
                String[] split = filter.split("\\.", 2); // NOI18N
                int splitLength = split.length;

                // is plugin?
                if (splitLength > 0) {
                    isPlugin = isPlugin(split[0]);
                    if (isPlugin) {
                        if (splitLength > 1) {
                            filter = split[1];
                        } else {
                            filter = ""; // NOI18N
                        }
                        pluginName = split[0];
                    }
                }
            }

            // check subdirectory
            filter = setSubDirectoryPath(filter);

            // add elements
            for (DIR_TYPE dirType : dirTypes) {
                if (!isPlugin) {
                    if (PLUGINS.contains(dirType)) {
                        continue;
                    }
                }

                FileObject webrootDirectory = cakeModule.getDirectory(dirType, FILE_TYPE.WEBROOT, pluginName);
                if (webrootDirectory != null) {
                    FileObject targetDirectory;
                    if (filter.startsWith(SLASH)) {
                        targetDirectory = webrootDirectory;
                    } else {
                        targetDirectory = webrootDirectory.getFileObject(getRelativePath());
                    }
                    if (targetDirectory != null) {
                        for (FileObject element : targetDirectory.getChildren()) {
                            addElement(element, filter, elements, pluginName);
                        }
                        break;
                    }
                }
                if (isPlugin && !elements.isEmpty()) {
                    return elements;
                }
            }

            if (!subDirectoryPath.isEmpty() || isPlugin) {
                return elements;
            }

            // plugin names
            // CakePHP 2.x
            if (cakeVersion >= 2) {
                for (DIR_TYPE dirType : PLUGINS) {
                    FileObject pluginDirectory = cakeModule.getDirectory(dirType);
                    if (pluginDirectory != null) {
                        for (FileObject child : pluginDirectory.getChildren()) {
                            if (child.isFolder()) {
                                String name = child.getNameExt();
                                FileObject webrootDirectory = cakeModule.getDirectory(dirType, FILE_TYPE.WEBROOT, name);
                                if (webrootDirectory != null && webrootDirectory.getFileObject(type.toString()) != null) {
                                    if (name.startsWith(filter)) {
                                        name = name + DOT;
                                        elements.add(name);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return elements;
    }

    /**
     * Check whether plugin exists.
     *
     * @param pluginName plugin name
     * @return true if plugin directory exists, otherwise false.
     */
    private boolean isPlugin(String pluginName) {
        if (!pluginName.startsWith(SLASH)) {
            CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
            if (cakeModule == null) {
                return false;
            }
            for (DIR_TYPE dirType : PLUGINS) {
                FileObject pluginDirectory = cakeModule.getDirectory(dirType, FILE_TYPE.NONE, pluginName);
                if (pluginDirectory != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Set subdirectory path, and return string except for subdirectory path.
     *
     * @param filter string for filtering
     * @return string except for subdirectory path if subdirectory path exists,
     * otherwise original filter.
     */
    protected String setSubDirectoryPath(String filter) {
        subDirectoryPath = ""; // NOI18N
        int lastSlash = filter.lastIndexOf(SLASH);
        if (lastSlash > 0) {
            subDirectoryPath = filter.substring(0, lastSlash);
            filter = filter.substring(lastSlash + 1);
        }
        return filter;
    }

    /**
     * Add element starting with filter value to list.
     *
     * @param fo FileObject for adding
     * @param filter filtering with this value
     * @param elements List for adding (add to this)
     * @param pluginName plugin name if target is not plugin, set the null
     * @return true if add, otherwise false
     */
    protected boolean addElement(FileObject fo, String filter, List<String> elements, String pluginName) {
        String name = getFileName(fo);
        // set subdirectory path
        if (!subDirectoryPath.isEmpty()) {
            name = subDirectoryPath + SLASH + name; // NOI18N
        }

        // is root
        if (filter.startsWith(SLASH)) {
            name = SLASH + name;
            filter = filter.replaceFirst(SLASH, ""); // NOI18N
        }

        // filtering
        String fileName = getFileName(fo);
        if (fileName.startsWith(filter)) {
            if (!fo.isFolder()
                    && extFilter != null
                    && !extFilter.contains(fo.getExt())) {
                return false;
            }
            if (fo.isFolder() || !fo.getExt().isEmpty()) {
                if (fo.isFolder()) {
                    name = name + SLASH;
                }
                if (pluginName != null && !pluginName.isEmpty()) {
                    name = pluginName + DOT + name;
                }
                elements.add(name);
                return true;
            }
        }
        return false;
    }

    /**
     * Get relative path for subdirectory.
     *
     * @return relative path
     */
    private String getRelativePath() {
        if (subDirectoryPath.startsWith(SLASH)) {
            return subDirectoryPath.replaceFirst(SLASH, ""); // NOI18N
        }
        return type.toString() + SLASH + subDirectoryPath;
    }

    /**
     * Get file name. If ASSET_TYPE is IMAGE, get name with extention, otherwise
     * only name.
     *
     * @param fo target FileObject
     * @return file name if type is image, with extention.
     */
    private String getFileName(FileObject fo) {
        String name;
        if (type == ASSET_TYPE.IMAGE) {
            name = fo.getNameExt();
        } else {
            name = fo.getName();
        }
        return name;
    }
}
