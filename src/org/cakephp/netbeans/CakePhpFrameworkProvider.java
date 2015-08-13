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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.TimeUnit;
import org.cakephp.netbeans.commands.CakePhpCommandSupport;
import org.cakephp.netbeans.editor.codecompletion.CakePhpEditorExtenderFactory;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.options.CakePhpOptions;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.validator.CakePhpCustomizerValidator;
import org.cakephp.netbeans.versions.CakeVersion;
import org.cakephp.netbeans.versions.Versionable;
import org.cakephp.netbeans.versions.VersionsFactory;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.phpmodule.ImportantFilesImplementation;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

// TODO: in static block, consider registering *.ctp as a php mime-type (can be dangerous, do it only if it's not already set!)
public final class CakePhpFrameworkProvider extends PhpFrameworkProvider {

    private static final RequestProcessor RP = new RequestProcessor(CakePhpFrameworkProvider.class);
    private static final CakePhpFrameworkProvider INSTANCE = new CakePhpFrameworkProvider();
    private final BadgeIcon badgeIcon;

    @NbBundle.Messages({
        "LBL_CakePhpFramework=CakePHP PHP Web Framework",
        "LBL_CakePhpDescription=CakePHP PHP Web Framework"
    })
    private CakePhpFrameworkProvider() {
        super("cakephp", // NOI18N
                Bundle.LBL_CakePhpFramework(),
                Bundle.LBL_CakePhpDescription());
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(CakePhp.CAKE_BADGE_8),
                CakePhpFrameworkProvider.class.getResource("/" + CakePhp.CAKE_BADGE_8)); // NOI18N
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
        return CakePreferences.isEnabled(phpModule);
    }

    @Override
    public ImportantFilesImplementation getConfigurationFiles2(PhpModule phpModule) {
        return new ConfigurationFiles(phpModule);
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule phpModule) {
        return new CakePhpModuleExtender();
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule) {
        CakePhpModuleCustomizerExtender customizer = new CakePhpModuleCustomizerExtender(phpModule);
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule != null) {
            customizer.addChangeListener(cakeModule);
        }
        return customizer;
    }

    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        PhpModuleProperties properties = new PhpModuleProperties();
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module != null) {
            properties = module.getPhpModuleProperties(phpModule);
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

    @NbBundle.Messages({
        "# {0} - name",
        "CakePhpFrameworkProvider.autoditection=CakePHP autoditection : {0}",
        "CakePhpFrameworkProvider.autoditection.action=If you want to enable as CakePHP project, please click here."
    })
    @Override
    public void phpModuleOpened(PhpModule phpModule) {
        if (isInPhpModule(phpModule)) {
            // check available new version
            if (CakePhpOptions.getInstance().isNotifyNewVersion()) {
                notificationNewVersion(phpModule);
            }
            return;
        }

        // auto detection
        if (CakePhpOptions.getInstance().isNotifyAutoDetection()) {
            RP.schedule(new CakePhpAutoDetectionTask(phpModule), 1, TimeUnit.MINUTES);
        }
    }

    @Override
    public void phpModuleClosed(PhpModule phpModule) {
        // FIXME
        // CakePhpModule is created again
        // because StatusLineElementProvider is called after PhpModule is closed
        // CakePhpModuleFactory.getInstance().remove(phpModule);
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "# {1} - new version",
        "CakePhpFrameworkProvider.new.version.notification.title={0} : New version({1}) is available"
    })
    private void notificationNewVersion(PhpModule phpModule) {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return;
        }
        CakeVersion version = cakeModule.getCakeVersion();
        if (version != null && version.hasUpdate()) {
            // Notification
            NotificationDisplayer notification = NotificationDisplayer.getDefault();
            String latestStableVersion = version.getLatestStableVersion();
            notification.notify(
                    Bundle.CakePhpFrameworkProvider_new_version_notification_title(phpModule.getDisplayName(), latestStableVersion),
                    ImageUtilities.loadImageIcon(CakePhp.CAKE_ICON_16, false),
                    Bundle.CakePhpFrameworkProvider_new_version_notification_title(phpModule.getDisplayName(), latestStableVersion),
                    null);
        }
    }

    private static class CakePhpAutoDetectionTask implements Runnable {

        private final PhpModule phpModule;
        private Notification notification;

        public CakePhpAutoDetectionTask(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @Override
        public void run() {
            FileObject sourceDirectory = phpModule.getSourceDirectory();
            if (sourceDirectory == null) {
                // project is broken
                return;
            }
            CakeVersion version = (CakeVersion) VersionsFactory.getInstance().create(phpModule, Versionable.VERSION_TYPE.CAKEPHP);
            ValidationResult result = new CakePhpCustomizerValidator()
                    .validateCakePhpPath(sourceDirectory, CakePreferences.getCakePhpDirPath(phpModule))
                    .validateAppPath(sourceDirectory, CakePreferences.getAppDirectoryPath(phpModule, version))
                    .getResult();

            if (result.hasErrors()) {
                return;
            }
            if (result.hasWarnings()) {
                return;
            }

            // everything ok
            if (!CakePreferences.isEnabled(phpModule)) {
                NotificationDisplayer notificationDisplayer = NotificationDisplayer.getDefault();
                notification = notificationDisplayer.notify(
                        Bundle.CakePhpFrameworkProvider_autoditection(phpModule.getDisplayName()), // title
                        NotificationDisplayer.Priority.LOW.getIcon(), // icon
                        Bundle.CakePhpFrameworkProvider_autoditection_action(), // detail
                        new CakePhpAutoDetectionActionListener(), // action
                        NotificationDisplayer.Priority.LOW); // priority
            }
        }

        private class CakePhpAutoDetectionActionListener implements ActionListener {

            public CakePhpAutoDetectionActionListener() {
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                CakePreferences.setEnabled(phpModule, true);
                CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
                phpModule.notifyPropertyChanged(new PropertyChangeEvent(this, PhpModule.PROPERTY_FRAMEWORKS, null, null));
                if (cakeModule != null) {
                    cakeModule.notifyPropertyChanged(new PropertyChangeEvent(this, CakePhpModule.PROPERTY_CHANGE_CAKE, null, null));
                }
                notification.clear();
            }
        }

    }
}
