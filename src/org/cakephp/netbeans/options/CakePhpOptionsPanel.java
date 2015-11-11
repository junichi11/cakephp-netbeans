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
 *//*
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
package org.cakephp.netbeans.options;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.cakephp.netbeans.basercms.BaserCms;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

final class CakePhpOptionsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1542234585504356049L;
    private final CakePhpOptionsPanelController controller;
    private CakePhpOptionsPanelRegisterDialog dialog;
    private final CakePhpPluginTableModel model = new CakePhpPluginTableModel();

    CakePhpOptionsPanel(CakePhpOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        initialize();
        // basercms
        baserCmsLabel.setIcon(ImageUtilities.loadImageIcon(BaserCms.BASER_ICON_16, false));
        // TODO listen to changes in form fields and call controller.changed()
    }

    public void initialize() {
        pluginTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        pluginTable.setSize(new Dimension(600, 600));
        TableColumnModel columnModel = pluginTable.getColumnModel();
        TableColumn column = columnModel.getColumn(CakePhpPluginTableModel.NAME);
        column.setMinWidth(150);
        column.setPreferredWidth(150);
        column = columnModel.getColumn(CakePhpPluginTableModel.URL);
        column.setMinWidth(450);
        column.setPreferredWidth(450);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        optionsTabbedPane = new javax.swing.JTabbedPane();
        generalPanel = new javax.swing.JPanel();
        defaultSeparator = new javax.swing.JSeparator();
        defaultLabel = new javax.swing.JLabel();
        ignoreTempDirectoryCheckBox = new javax.swing.JCheckBox();
        autoCreateViewCheckBox = new javax.swing.JCheckBox();
        notificationLabel = new javax.swing.JLabel();
        notifyNewVersionCheckBox = new javax.swing.JCheckBox();
        notifyAutoDetectionCheckBox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        testLabel = new javax.swing.JLabel();
        testStderrCheckBox = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        newProjectPanel = new javax.swing.JPanel();
        localFilePathLabel = new javax.swing.JLabel();
        localFilePathTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        installingComposerLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        composerJsonEditorPane = new javax.swing.JEditorPane();
        pluginsPanel = new javax.swing.JPanel();
        pluginListLabel = new javax.swing.JLabel();
        messageLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pluginTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        nodePanel = new javax.swing.JPanel();
        customNodesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        customNodesList = new javax.swing.JList<String>();
        cmsPanel = new javax.swing.JPanel();
        baserCmsEnabledCheckBox = new javax.swing.JCheckBox();
        baserCmsLabel = new javax.swing.JLabel();
        baserCmsLearnMoreLabel = new javax.swing.JLabel();
        baserCmsVagrantSettingsCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(defaultLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.defaultLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(ignoreTempDirectoryCheckBox, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.ignoreTempDirectoryCheckBox.text")); // NOI18N
        ignoreTempDirectoryCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ignoreTempDirectoryCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(autoCreateViewCheckBox, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.autoCreateViewCheckBox.text")); // NOI18N
        autoCreateViewCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoCreateViewCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(notificationLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.notificationLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(notifyNewVersionCheckBox, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.notifyNewVersionCheckBox.text")); // NOI18N
        notifyNewVersionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notifyNewVersionCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(notifyAutoDetectionCheckBox, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.notifyAutoDetectionCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(testLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.testLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(testStderrCheckBox, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.testStderrCheckBox.text")); // NOI18N
        testStderrCheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.testStderrCheckBox.toolTipText")); // NOI18N

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addComponent(defaultLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(defaultSeparator)
                        .addContainerGap())
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(autoCreateViewCheckBox)
                            .addComponent(ignoreTempDirectoryCheckBox))
                        .addContainerGap(295, Short.MAX_VALUE))
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(generalPanelLayout.createSequentialGroup()
                                .addComponent(notificationLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1))
                            .addGroup(generalPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(notifyAutoDetectionCheckBox)
                                    .addComponent(notifyNewVersionCheckBox))
                                .addGap(0, 324, Short.MAX_VALUE)))
                        .addGap(12, 12, 12))
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(generalPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(testStderrCheckBox)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(generalPanelLayout.createSequentialGroup()
                                .addComponent(testLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator2)))
                        .addGap(12, 12, 12))))
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(defaultLabel)
                    .addComponent(defaultSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ignoreTempDirectoryCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoCreateViewCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(notificationLabel)
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notifyNewVersionCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notifyAutoDetectionCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(testLabel)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testStderrCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        notificationLabel.getAccessibleContext().setAccessibleParent(this);

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.generalPanel.TabConstraints.tabTitle"), generalPanel); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(localFilePathLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.localFilePathLabel.text")); // NOI18N

        localFilePathTextField.setText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.localFilePathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.resetButton.text")); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(installingComposerLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.installingComposerLabel.text")); // NOI18N

        composerJsonEditorPane.setContentType("text/x-json"); // NOI18N
        jScrollPane3.setViewportView(composerJsonEditorPane);

        javax.swing.GroupLayout newProjectPanelLayout = new javax.swing.GroupLayout(newProjectPanel);
        newProjectPanel.setLayout(newProjectPanelLayout);
        newProjectPanelLayout.setHorizontalGroup(
            newProjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newProjectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newProjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newProjectPanelLayout.createSequentialGroup()
                        .addComponent(localFilePathLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(localFilePathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3)
                    .addGroup(newProjectPanelLayout.createSequentialGroup()
                        .addComponent(installingComposerLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        newProjectPanelLayout.setVerticalGroup(
            newProjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newProjectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newProjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(localFilePathLabel)
                    .addComponent(localFilePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton)
                    .addComponent(resetButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(installingComposerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.newProjectPanel.TabConstraints.tabTitle"), newProjectPanel); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(pluginListLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.pluginListLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(messageLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.messageLabel.text")); // NOI18N
        messageLabel.setToolTipText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.messageLabel.toolTipText")); // NOI18N

        pluginTable.setModel(model);
        pluginTable.setColumnSelectionAllowed(true);
        pluginTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(pluginTable);
        pluginTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(editButton, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.editButton.text")); // NOI18N
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteButton, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.deleteButton.text")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pluginsPanelLayout = new javax.swing.GroupLayout(pluginsPanel);
        pluginsPanel.setLayout(pluginsPanelLayout);
        pluginsPanelLayout.setHorizontalGroup(
            pluginsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pluginsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pluginsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pluginsPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pluginsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(editButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)))
                    .addGroup(pluginsPanelLayout.createSequentialGroup()
                        .addComponent(pluginListLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(messageLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pluginsPanelLayout.setVerticalGroup(
            pluginsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pluginsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pluginsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pluginListLabel)
                    .addComponent(messageLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pluginsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                    .addGroup(pluginsPanelLayout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.pluginsPanel.TabConstraints.tabTitle"), pluginsPanel); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(customNodesLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.customNodesLabel.text")); // NOI18N

        jScrollPane1.setViewportView(customNodesList);

        javax.swing.GroupLayout nodePanelLayout = new javax.swing.GroupLayout(nodePanel);
        nodePanel.setLayout(nodePanelLayout);
        nodePanelLayout.setHorizontalGroup(
            nodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customNodesLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(435, Short.MAX_VALUE))
        );
        nodePanelLayout.setVerticalGroup(
            nodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(customNodesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.nodePanel.TabConstraints.tabTitle"), nodePanel); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(baserCmsEnabledCheckBox, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.baserCmsEnabledCheckBox.text")); // NOI18N
        baserCmsEnabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baserCmsEnabledCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(baserCmsLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.baserCmsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(baserCmsLearnMoreLabel, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.baserCmsLearnMoreLabel.text")); // NOI18N
        baserCmsLearnMoreLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                baserCmsLearnMoreLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                baserCmsLearnMoreLabelMouseEntered(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(baserCmsVagrantSettingsCheckBox, org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.baserCmsVagrantSettingsCheckBox.text")); // NOI18N

        javax.swing.GroupLayout cmsPanelLayout = new javax.swing.GroupLayout(cmsPanel);
        cmsPanel.setLayout(cmsPanelLayout);
        cmsPanelLayout.setHorizontalGroup(
            cmsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cmsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cmsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(baserCmsLabel)
                    .addGroup(cmsPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(cmsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(cmsPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(baserCmsVagrantSettingsCheckBox))
                            .addGroup(cmsPanelLayout.createSequentialGroup()
                                .addComponent(baserCmsEnabledCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(baserCmsLearnMoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(263, Short.MAX_VALUE))
        );
        cmsPanelLayout.setVerticalGroup(
            cmsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cmsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(baserCmsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cmsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(baserCmsEnabledCheckBox)
                    .addComponent(baserCmsLearnMoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(baserCmsVagrantSettingsCheckBox)
                .addContainerGap(160, Short.MAX_VALUE))
        );

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.cmsPanel.TabConstraints.tabTitle"), cmsPanel); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(optionsTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(optionsTabbedPane)
        );
    }// </editor-fold>//GEN-END:initComponents

        private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Open Dialog
        initDialog();
        dialog.setTitle("Add");
        dialog.setVisible(true);

        // validate
        if (!isRegisterOK()) {
            return;
        }

        // Resiter plugins
        model.addPlugin(new CakePhpPlugin(dialog.getPluginName().trim(), dialog.getUrl().trim()));

        controller.changed();
        }//GEN-LAST:event_addButtonActionPerformed

        private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        int index = pluginTable.getSelectedRow();
        // is not selected
        if (index == -1) {
            return;
        }
        // Open Dialog
        initDialog();
        CakePhpPlugin selected = model.getPlugins().get(index);
        dialog.setPluginName(selected.getName());
        dialog.setUrl(selected.getUrl());
        dialog.setTitle("Edit");
        dialog.setVisible(true);

        // validate
        if (!isRegisterOK()) {
            return;
        }

        // Resiter plugins
        CakePhpPlugin plugin = new CakePhpPlugin(dialog.getPluginName().trim(), dialog.getUrl().trim());
        model.editPlugin(index, plugin);
        controller.changed();
        }//GEN-LAST:event_editButtonActionPerformed

        private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int index = pluginTable.getSelectedRow();
        if (index == -1) {
            return;
        }
        model.removePlugin(index);
        controller.changed();
        }//GEN-LAST:event_deleteButtonActionPerformed

    @NbBundle.Messages("LBL_LocalFilePath=Local File Path (zip file)")
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File localFile = new FileChooserBuilder(CakePhpOptionsPanel.class.getName())
                .setTitle(Bundle.LBL_LocalFilePath())
                .setFilesOnly(true)
                .setFileFilter(new FileNameExtensionFilter("ZIP File", "zip")) // NOI18N
                .showOpenDialog();
        if (localFile != null) {
            setLocalPath(localFile.getAbsolutePath());
            controller.changed();
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        localFilePathTextField.setText("");
        controller.changed();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void ignoreTempDirectoryCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ignoreTempDirectoryCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_ignoreTempDirectoryCheckBoxActionPerformed

    private void autoCreateViewCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoCreateViewCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_autoCreateViewCheckBoxActionPerformed

    private void notifyNewVersionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notifyNewVersionCheckBoxActionPerformed
        controller.changed();
    }//GEN-LAST:event_notifyNewVersionCheckBoxActionPerformed

    private void baserCmsLearnMoreLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_baserCmsLearnMoreLabelMouseClicked
        try {
            URL url = new URL("http://basercms.net/"); // NOI18N
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_baserCmsLearnMoreLabelMouseClicked

    private void baserCmsLearnMoreLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_baserCmsLearnMoreLabelMouseEntered
        baserCmsLearnMoreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_baserCmsLearnMoreLabelMouseEntered

    private void baserCmsEnabledCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baserCmsEnabledCheckBoxActionPerformed
        baserCmsVagrantSettingsCheckBox.setEnabled(baserCmsEnabledCheckBox.isSelected());
    }//GEN-LAST:event_baserCmsEnabledCheckBoxActionPerformed

    private void setLocalPath(String path) {
        localFilePathTextField.setText(path);
    }

    private void initDialog() {
        Frame f = WindowManager.getDefault().getMainWindow();
        dialog = new CakePhpOptionsPanelRegisterDialog(f, true);
        int x = f.getX() + (f.getWidth() - dialog.getWidth()) / 2;
        int y = f.getY() + (f.getHeight() - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
    }

    private boolean isRegisterOK() {
        if (dialog != null) {
            if (dialog.getPluginName().isEmpty() || dialog.getUrl().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    void load() {
        CakePhpOptions options = CakePhpOptions.getInstance();
        model.setPlugins(options.getPlugins());
        localFilePathTextField.setText(options.getLocalZipFilePath());
        ignoreTempDirectoryCheckBox.setSelected(options.isIgnoreTmpDirectory());
        autoCreateViewCheckBox.setSelected(options.isAutoCreateView());
        notifyNewVersionCheckBox.setSelected(options.isNotifyNewVersion());
        notifyAutoDetectionCheckBox.setSelected(options.isNotifyAutoDetection());
        testStderrCheckBox.setSelected(options.isTestStderr());
        composerJsonEditorPane.setText(options.getComposerJson());
        setAvailableCustomNodes();
        // cms
        baserCmsEnabledCheckBox.setSelected(options.isBaserCmsEnabled());
        baserCmsVagrantSettingsCheckBox.setSelected(options.isBaserCmsVagrantSettings());
        baserCmsEnabledCheckBoxActionPerformed(null);
    }

    void store() {
        CakePhpOptions options = CakePhpOptions.getInstance();
        if (controller.isChanged()) {
            options.setPlugins(model.getPlugins());
            options.setLocalZipFilePath(localFilePathTextField.getText());
            options.setIgnoreTmpDirectory(ignoreTempDirectoryCheckBox.isSelected());
            options.setAutoCreateView(autoCreateViewCheckBox.isSelected());
            options.setNotifyNewVersion(notifyNewVersionCheckBox.isSelected());
            options.setNotifyAutoDetection(notifyAutoDetectionCheckBox.isSelected());
            options.setTestStderr(testStderrCheckBox.isSelected());
        }
        List<String> nodes = customNodesList.getSelectedValuesList();
        options.setAvailableCustomNodes(nodes);
        options.setComposerJson(composerJsonEditorPane.getText());
        // cms
        options.setBaserCmsEnabled(baserCmsEnabledCheckBox.isSelected());
        options.setBaserCmsVagrantSettings(baserCmsVagrantSettingsCheckBox.isSelected());
        // notify
        notifyPropertyChanged();
    }

    private void notifyPropertyChanged() {
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
        for (Project project : openProjects) {
            PhpModule phpModule = PhpModule.Factory.lookupPhpModule(project);
            if (phpModule != null) {
                if (CakePhpUtils.isCakePHP(phpModule)) {
                    CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
                    if (cakeModule == null) {
                        continue;
                    }
                    cakeModule.notifyPropertyChanged(new PropertyChangeEvent(this, CakePhpModule.PROPERTY_CHANGE_CAKE, null, null));
                }
            }
        }
    }

    private void setAvailableCustomNodes() {
        CakePhpOptions options = CakePhpOptions.getInstance();
        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        for (String node : CakePhpOptions.ALL_AVAILABLE_NODES) {
            defaultListModel.addElement(node);
        }
        customNodesList.setModel(defaultListModel);
        List<String> availableCustomNodes = options.getAvailableCustomNodes();
        for (String node : availableCustomNodes) {
            int indexOf = defaultListModel.indexOf(node);
            customNodesList.addSelectionInterval(indexOf, indexOf);
        }
    }

    boolean valid() {
        return true;
    }

    private static class CakePhpPluginTableModel extends AbstractTableModel {

        private static final int NAME = 0;
        private static final int URL = 1;
        private static final long serialVersionUID = 6148058724466511289L;
        private List<CakePhpPlugin> plugins;
        private final String[] column;

        public CakePhpPluginTableModel() {
            column = new String[]{
                NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.pluginTable.columnModel.title0"),
                NbBundle.getMessage(CakePhpOptionsPanel.class, "CakePhpOptionsPanel.pluginTable.columnModel.title1")
            };
            plugins = new ArrayList<>();
        }

        @Override
        public int getRowCount() {
            return plugins.size();
        }

        @Override
        public int getColumnCount() {
            return column.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CakePhpPlugin plugin = plugins.get(rowIndex);
            if (columnIndex == NAME) {
                return plugin.getName();
            } else if (columnIndex == URL) {
                return plugin.getUrl();
            }
            return null;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return column[columnIndex];
        }

        public void setPlugins(List<CakePhpPlugin> plugins) {
            this.plugins = plugins;
        }

        public List<CakePhpPlugin> getPlugins() {
            return plugins;
        }

        public void addPlugin(CakePhpPlugin plugin) {
            plugins.add(plugin);
            int index = plugins.indexOf(plugin);
            fireTableRowsInserted(index, index);
        }

        public void editPlugin(int index, CakePhpPlugin plugin) {
            plugins.set(index, plugin);
            fireTableRowsUpdated(index, index);
        }

        public void removePlugin(int index) {
            plugins.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JCheckBox autoCreateViewCheckBox;
    private javax.swing.JCheckBox baserCmsEnabledCheckBox;
    private javax.swing.JLabel baserCmsLabel;
    private javax.swing.JLabel baserCmsLearnMoreLabel;
    private javax.swing.JCheckBox baserCmsVagrantSettingsCheckBox;
    private javax.swing.JButton browseButton;
    private javax.swing.JPanel cmsPanel;
    private javax.swing.JEditorPane composerJsonEditorPane;
    private javax.swing.JLabel customNodesLabel;
    private javax.swing.JList<String> customNodesList;
    private javax.swing.JLabel defaultLabel;
    private javax.swing.JSeparator defaultSeparator;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel generalPanel;
    private javax.swing.JCheckBox ignoreTempDirectoryCheckBox;
    private javax.swing.JLabel installingComposerLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel localFilePathLabel;
    private javax.swing.JTextField localFilePathTextField;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JPanel newProjectPanel;
    private javax.swing.JPanel nodePanel;
    private javax.swing.JLabel notificationLabel;
    private javax.swing.JCheckBox notifyAutoDetectionCheckBox;
    private javax.swing.JCheckBox notifyNewVersionCheckBox;
    private javax.swing.JTabbedPane optionsTabbedPane;
    private javax.swing.JLabel pluginListLabel;
    private javax.swing.JTable pluginTable;
    private javax.swing.JPanel pluginsPanel;
    private javax.swing.JButton resetButton;
    private javax.swing.JLabel testLabel;
    private javax.swing.JCheckBox testStderrCheckBox;
    // End of variables declaration//GEN-END:variables
}
