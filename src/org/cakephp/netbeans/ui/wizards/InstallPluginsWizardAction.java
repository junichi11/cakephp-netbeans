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
package org.cakephp.netbeans.ui.wizards;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.options.CakePhpPlugin;
import org.cakephp.netbeans.ui.actions.ClearCacheAction;
import org.cakephp.netbeans.util.CakeDefaultZipEntryFilter;
import org.cakephp.netbeans.util.CakePhpFileUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
@ActionID(category = "PHP", id = "org.cakephp.netbeans.ui.wizards.InstallPluginsWizardAction")
@ActionRegistration(displayName = "Install Plugins")
// @ActionReference(path="Menu/Tools", position=...)
public final class InstallPluginsWizardAction extends BaseAction implements ActionListener {

    private static final String LF = "\n"; // NOI18N
    private static final long serialVersionUID = 4405963698419409213L;
    private static final InstallPluginsWizardAction INSTANCE = new InstallPluginsWizardAction();

    private InstallPluginsWizardAction() {
    }

    public static InstallPluginsWizardAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return NbBundle.getMessage(ClearCacheAction.class, "LBL_CakePhpAction", getPureName());
    }

    @Override
    protected String getPureName() {
        return NbBundle.getMessage(InstallPluginsWizardAction.class, "LBL_InstallPlugin");
    }

    @NbBundle.Messages({
        "LBL_InstallPluginsTitle=CakePHP Install Plugins"
    })
    @Override
    protected void actionPerformed(PhpModule pm) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(new InstallPluginsWizardPanel(pm));
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(Bundle.LBL_InstallPluginsTitle());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            Panel<WizardDescriptor> panel = (InstallPluginsWizardPanel) panels.get(0);
            InstallPluginsVisualPanel component = (InstallPluginsVisualPanel) panel.getComponent();

            final String installPath = component.getInstallPathTextField();
            FileObject cakePhpDirectory = CakePhpModule.getCakePhpDirectory(pm);
            if (cakePhpDirectory == null) {
                return;
            }
            final FileObject installDirectory = cakePhpDirectory.getFileObject(installPath);
            if (installDirectory == null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        NotifyDescriptor descriptor = new NotifyDescriptor.Message(installPath + " dosen't exist.", NotifyDescriptor.ERROR_MESSAGE);
                        DialogDisplayer.getDefault().notifyLater(descriptor);
                    }
                });
                return;
            }
            final List<CakePhpPlugin> plugins = component.getCakePhpPluginList();
            // error list
            final StringBuilder errors = new StringBuilder();

            // create modeless dialog
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final InstallStatusDisplayPanel installStatusDisplayPanel = new InstallStatusDisplayPanel();
                    DialogDescriptor installStatusDescriptor = new DialogDescriptor(installStatusDisplayPanel, "Install status");
                    final Dialog installStatusDialog = DialogDisplayer.getDefault().createDialog(installStatusDescriptor);
                    installStatusDialog.setModal(false);
                    installStatusDialog.setVisible(true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // unzip
                            for (CakePhpPlugin plugin : plugins) {
                                if (plugin.isInstall()) {
                                    String pluginName = plugin.getName();
                                    // set status
                                    installStatusDisplayPanel.getDisplayStatusTextArea().append(pluginName + " : ");
                                    try {
                                        FileObject pluginDirectory = installDirectory.getFileObject(pluginName);
                                        if (pluginDirectory == null) {
                                            pluginDirectory = installDirectory.createFolder(pluginName);
                                        }

                                        // unzip
                                        if (pluginDirectory != null) {
                                            CakePhpFileUtils.unzip(plugin.getUrl(), FileUtil.toFile(pluginDirectory), new CakeDefaultZipEntryFilter());
                                            installStatusDisplayPanel.getDisplayStatusTextArea().append("Done" + LF);
                                        } else {
                                            installStatusDisplayPanel.getDisplayStatusTextArea().append("Can't find plugin directory" + LF);
                                        }
                                    } catch (IOException ex) {
                                        errors.append(pluginName).append(LF);
                                        installStatusDisplayPanel.getDisplayStatusTextArea().append("Error" + LF);
                                    }
                                }
                            }

                            String errorMessage = errors.toString();
                            NotifyDescriptor d;
                            if (!errorMessage.isEmpty()) {
                                //display error dialog
                                d = new NotifyDescriptor.Message("Please confirm the URL.\n" + errorMessage, NotifyDescriptor.ERROR_MESSAGE);
                            } else {
                                // display complete dialog
                                d = new NotifyDescriptor.Message("Install Complete!", NotifyDescriptor.INFORMATION_MESSAGE);
                            }
                            DialogDisplayer.getDefault().notifyLater(d);

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    installStatusDialog.setVisible(false);
                                    installStatusDialog.dispose();
                                }
                            });
                        }
                    }).start();
                }
            });
        }
    }
}
