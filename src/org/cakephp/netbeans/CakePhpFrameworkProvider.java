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
package org.cakephp.netbeans;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import org.cakephp.netbeans.commands.CakePhpCommandSupport;
import org.cakephp.netbeans.editor.CakePhpEditorExtenderFactory;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

// TODO: in static block, consider registering *.ctp as a php mime-type (can be dangerous, do it only if it's not already set!)
public final class CakePhpFrameworkProvider extends PhpFrameworkProvider {

    // TODO: provide better badge icon
    private static final String ICON_PATH = "org/cakephp/netbeans/ui/resources/cakephp_badge_8.png"; // NOI18N
    private static final CakePhpFrameworkProvider INSTANCE = new CakePhpFrameworkProvider();
    private static final Comparator<File> FILE_COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };
    private final BadgeIcon badgeIcon;

    private CakePhpFrameworkProvider() {
        super("cakephp", // NOI18N
                NbBundle.getMessage(CakePhpFrameworkProvider.class, "LBL_CakePhpFramework"),
                NbBundle.getMessage(CakePhpFrameworkProvider.class, "LBL_CakePhpDescription"));
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                CakePhpFrameworkProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }

    @PhpFrameworkProvider.Registration(position = 500)
    public static CakePhpFrameworkProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    @Override
    public boolean isInPhpModule(PhpModule phpModule) {
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module == null) {
            return false;
        }
        FileObject app = module.getDirectory(DIR_TYPE.APP);
        if (app == null) {
            return false;
        }
        FileObject cake = module.getDirectory(DIR_TYPE.CORE);
        return cake != null && cake.isFolder();
    }

    @Override
    public File[] getConfigurationFiles(PhpModule phpModule) {
        // return all php files from app/config
        List<File> configFiles = new LinkedList<File>();
        FileObject config = CakePhpModule.forPhpModule(phpModule).getConfigDirectory(DIR_TYPE.APP);
        assert config != null : "app/config or app/Config not found for CakePHP project " + phpModule.getDisplayName();
        if (config != null && config.isFolder()) {
            Enumeration<? extends FileObject> children = config.getChildren(true);
            while (children.hasMoreElements()) {
                FileObject child = children.nextElement();
                if (child.isData() && FileUtils.isPhpFile(child)) {
                    configFiles.add(FileUtil.toFile(child));
                }
            }
        }
        if (!configFiles.isEmpty()) {
            Collections.sort(configFiles, FILE_COMPARATOR);
        }
        return configFiles.toArray(new File[configFiles.size()]);
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule phpModule) {
        return new CakePhpModuleExtender();
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule) {
        return new CakePhpModuleCustomizerExtender(phpModule);
    }

    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        PhpModuleProperties properties = new PhpModuleProperties();
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        FileObject webroot = module.getWebrootDirectory(DIR_TYPE.APP);
        if (webroot != null) {
            properties = properties.setWebRoot(webroot);
        }
        FileObject test = module.getTestDirectory(DIR_TYPE.APP);
        if (test != null) {
            properties = properties.setTests(test);
        }
        return properties;
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule phpModule) {
        return new CakePhpActionsExtender();
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule phpModule) {
        return new CakePhpIgnoredFilesExtender(phpModule);
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule phpModule) {
        return new CakePhpCommandSupport(phpModule);
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule phpModule) {
        return CakePhpEditorExtenderFactory.create(phpModule);
    }
}
