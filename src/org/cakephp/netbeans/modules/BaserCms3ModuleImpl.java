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

import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.versions.Versions;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;

/**
 * CakePhpModuleImpl for baserCMS 3.x.x
 *
 * @author junichi11
 */
public class BaserCms3ModuleImpl extends CakePhp2ModuleImpl {

    public BaserCms3ModuleImpl(PhpModule phpModule, Versions versions) {
        super(phpModule, versions);
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
                sb.append(pluginName);
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
