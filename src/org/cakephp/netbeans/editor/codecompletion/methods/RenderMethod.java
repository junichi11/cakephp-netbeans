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
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class RenderMethod extends AssetMethod {

    private final FileObject currentFile;

    public RenderMethod(PhpModule phpModule) {
        super(phpModule);
        this.currentFile = null;
    }

    public RenderMethod(PhpModule phpModule, FileObject currentFile) {
        super(phpModule);
        this.currentFile = currentFile;
    }

    @Override
    public List<String> getElements(int argCount, String filter) {
        if (currentFile == null || !CakePhpUtils.isController(currentFile)) {
            return Collections.emptyList();
        }

        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return Collections.emptyList();
        }

        // get view folder name
        String viewFolderName = cakeModule.getViewFolderName(currentFile.getName());
        if (StringUtils.isEmpty(viewFolderName)) {
            return Collections.emptyList();
        }

        // set sub directory path
        filter = setSubDirectoryPath(filter);
        List<String> elements = new ArrayList<>();

        if (argCount == 1) {
            CakePhpModule.DIR_TYPE dirType = cakeModule.getDirectoryType(currentFile);

            // add sub directory path
            if (!StringUtils.isEmpty(subDirectoryPath)) {
                viewFolderName = viewFolderName.concat(SLASH + subDirectoryPath);
            }

            // get current plugin name
            String currentPluginName = cakeModule.getCurrentPluginName(currentFile);
            if (currentPluginName.isEmpty()) {
                currentPluginName = null;
            }
            FileObject viewDirectory = cakeModule.getViewDirectory(dirType, currentPluginName);

            // add elements
            if (viewDirectory != null) {
                FileObject specificViewDirectory = viewDirectory.getFileObject(viewFolderName);
                if (specificViewDirectory != null) {
                    for (FileObject child : specificViewDirectory.getChildren()) {
                        addElement(child, filter, elements, null);
                    }
                }
            }
        }
        return elements;
    }
}
