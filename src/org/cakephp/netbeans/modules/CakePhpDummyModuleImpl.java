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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.cakephp.netbeans.dotcake.Dotcake;
import org.cakephp.netbeans.versions.Versions;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

public class CakePhpDummyModuleImpl extends CakePhpModuleImpl {

    public CakePhpDummyModuleImpl(PhpModule phpModule, Versions versions) {
        super(phpModule, versions, null);
    }

    public CakePhpDummyModuleImpl(PhpModule phpModule, Versions versions, Dotcake dotcake) {
        super(phpModule, versions, dotcake);
    }

    @Override
    public List<FileObject> getDirectories(CakePhpModule.DIR_TYPE dirType, CakePhpModule.FILE_TYPE fileType, String pluginName) {
        return Collections.emptyList();
    }

    @Override
    public FileObject getDirectory(CakePhpModule.DIR_TYPE type, CakePhpModule.FILE_TYPE fileType, String pluginName) {
        return null;
    }

    @Override
    public FileObject getDirectory(CakePhpModule.DIR_TYPE type) {
        return null;
    }

    @Override
    public boolean isInCakePhp() {
        return false;
    }

    @Override
    public CakePhpModule.FILE_TYPE getFileType(FileObject fileObject) {
        return CakePhpModule.FILE_TYPE.NONE;
    }

    @Override
    public boolean isView(FileObject fo) {
        return false;
    }

    @Override
    public boolean isElement(FileObject fo) {
        return false;
    }

    @Override
    public boolean isLayout(FileObject fo) {
        return false;
    }

    @Override
    public FileObject getView(FileObject controller, String viewName) {
        return null;
    }

    @Override
    public FileObject getView(FileObject controller, String viewName, FileObject theme) {
        return null;
    }

    @Override
    public boolean isController(FileObject fo) {
        return false;
    }

    @Override
    public FileObject getController(FileObject view) {
        return null;
    }

    @Override
    public boolean isModel(FileObject fo) {
        return false;
    }

    @Override
    public boolean isBehavior(FileObject fo) {
        return false;
    }

    @Override
    public boolean isComponent(FileObject fo) {
        return false;
    }

    @Override
    public boolean isHelper(FileObject fo) {
        return false;
    }

    @Override
    public boolean isTest(FileObject fo) {
        return false;
    }

    @Override
    public String getTestCaseClassName(FileObject fo) {
        return ""; // NOI18N
    }

    @Override
    public String getTestedClassName(FileObject testCase) {
        return ""; // NOI18N
    }

    @Override
    public String getViewFolderName(String controllerFileName) {
        return ""; // NOI18N
    }

    @Override
    public FileObject createView(FileObject controller, PhpBaseElement phpElement) throws IOException {
        return null;
    }

    @Override
    public String getFileNameWithExt(CakePhpModule.FILE_TYPE type, String name) {
        return ""; // NOI18N
    }

    @Override
    public String toViewDirectoryName(String name) {
        return ""; // NOI18N
    }

    @Override
    public void refresh() {
    }

    @Override
    public Set<String> getAllPluginNames() {
        return Collections.emptySet();
    }

}
