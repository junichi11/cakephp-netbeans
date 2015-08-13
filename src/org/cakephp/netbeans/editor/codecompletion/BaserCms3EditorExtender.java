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
package org.cakephp.netbeans.editor.codecompletion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.openide.filesystems.FileObject;

/**
 * EditorExtender for baserCMS3
 *
 * @author junichi11
 */
public class BaserCms3EditorExtender extends CakePhp2EditorExtender {

    public BaserCms3EditorExtender(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public void addDefaultComponents(PhpClass phpClass, FileObject fo) {
        // add all components
        PhpModule phpModule = getPhpModule();
        CakePhpModule baserModule = CakePhpModule.forPhpModule(phpModule);
        if (baserModule != null) {
            addComponents(phpClass, baserModule.getDirectory(DIR_TYPE.BASER, FILE_TYPE.COMPONENT, null));
        }
        super.addDefaultComponents(phpClass, fo);
    }

    @Override
    public void addDefaultHelpers(PhpClass phpClass, FileObject fo) {
        if (!isView()) {
            return;
        }
        // add all helpers
        PhpModule phpModule = getPhpModule();
        CakePhpModule baserModule = CakePhpModule.forPhpModule(phpModule);
        if (baserModule != null) {
            addHelpers(phpClass, baserModule.getDirectory(DIR_TYPE.BASER, FILE_TYPE.HELPER, null));
            List<String> pluginNames = getBaserPluginNames(baserModule);
            for (String pluginName : pluginNames) {
                addHelpers(phpClass, baserModule.getDirectory(DIR_TYPE.BASER_PLUGIN, FILE_TYPE.HELPER, pluginName));
            }
        }
        super.addDefaultHelpers(phpClass, fo);
    }

    /**
     * Add components to field for {@link PhpClass}.
     *
     * @param phpClass
     * @param componentDirectory
     */
    private void addComponents(@NonNull PhpClass phpClass, FileObject componentDirectory) {
        addElements(phpClass, componentDirectory, FILE_TYPE.COMPONENT);
    }

    /**
     * Add helpers to field for {@link PhpClass}.
     *
     * @param phpClass
     * @param helperDirectory
     */
    private void addHelpers(@NonNull PhpClass phpClass, FileObject helperDirectory) {
        addElements(phpClass, helperDirectory, FILE_TYPE.HELPER);
    }

    /**
     * Add elements to field for {@link PhpClass}.
     *
     * @param phpClass
     * @param helperDirectory
     */
    private void addElements(@NonNull PhpClass phpClass, FileObject targetDirectory, FILE_TYPE fileType) {
        if (targetDirectory == null) {
            return;
        }
        for (FileObject child : targetDirectory.getChildren()) {
            if (!FileUtils.isPhpFile(child)) {
                continue;
            }
            String fullName = child.getName();
            if (fileType == FILE_TYPE.HELPER && "AppHelper".equals(fullName)) { // NOI18N
                continue;
            }
            String name = fullName.replace(fileType.toString(), ""); // NOI18N
            if (fileType == FILE_TYPE.MODEL) {
                phpClass.addField(name, new PhpClass(name, name), child, 0);
            } else {
                phpClass.addField(name, new PhpClass(name, name + fileType.toString()), child, 0);
            }
        }
    }

    /**
     * Get plugin names of baserCMS.
     *
     * @param baserModule {@link CakePhpModule}
     * @return plugin names
     */
    private List<String> getBaserPluginNames(CakePhpModule baserModule) {
        FileObject baserPluginDirectory = baserModule.getDirectory(DIR_TYPE.BASER_PLUGIN);
        if (baserPluginDirectory == null) {
            return Collections.emptyList();
        }
        FileObject[] children = baserPluginDirectory.getChildren();
        ArrayList<String> pluginNames = new ArrayList<>(children.length);
        for (FileObject child : children) {
            if (!child.isFolder()) {
                continue;
            }
            pluginNames.add(child.getName());
        }
        return pluginNames;
    }

}
