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
/*
 * CakePhpCustumizerPanel.java
 *
 * Created on 2011/09/16, 23:34:44
 */
package org.cakephp.netbeans.ui.customizer;

import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author junichi11
 */
public class CakePhpCustomizerPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -7471518120349007940L;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * Creates new form CakePhpCustumizerPanel
     */
    public CakePhpCustomizerPanel() {
        initComponents();
        init();
    }

    private void init() {
        appNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fireChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireChange();
            }
        });
    }

    public JTextField getAppNameField() {
        return appNameField;
    }

    public void setAppNameField(String appName) {
        appNameField.setText(appName);
    }

    public boolean isAutoCreateView() {
        return autoCreateViewCheckBox.isSelected();
    }

    public void setAutoCreateView(boolean isAuto) {
        autoCreateViewCheckBox.setSelected(isAuto);
    }

    public boolean isUseProjectDirectory() {
        return useProjectDirectoryCheckBox.isSelected();
    }

    public void setUseProjectDirectory(boolean isCheck) {
        useProjectDirectoryCheckBox.setSelected(isCheck);
    }

    public String getCakePhpDirTextField() {
        return cakePhpDirTextField.getText();
    }

    public void setCakePhpDirTextField(String cakePhpDir) {
        cakePhpDirTextField.setText(cakePhpDir);
    }

    public boolean ignoreTmpDirectory() {
        return ignoreTmpCheckBox.isSelected();
    }

    public void setIgnoreTmpDirectory(boolean ignore) {
        ignoreTmpCheckBox.setSelected(ignore);
    }

    public boolean isShowPopupForOneItem() {
        return showPopupForOneItemCheckBox.isSelected();
    }

    public void setShowPopupForOneItem(boolean isEnabled) {
        showPopupForOneItemCheckBox.setSelected(isEnabled);
    }

    public void addChangeListener(ChangeListener changeListener) {
        changeSupport.addChangeListener(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeSupport.removeChangeListener(changeListener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        appNameLabel = new javax.swing.JLabel();
        appNameField = new javax.swing.JTextField();
        autoCreateViewCheckBox = new javax.swing.JCheckBox();
        useProjectDirectoryCheckBox = new javax.swing.JCheckBox();
        cakePhpDirLabel = new javax.swing.JLabel();
        cakePhpDirTextField = new javax.swing.JTextField();
        ignoreTmpCheckBox = new javax.swing.JCheckBox();
        goToActionsLabel = new javax.swing.JLabel();
        goToActionsSeparator = new javax.swing.JSeparator();
        showPopupForOneItemCheckBox = new javax.swing.JCheckBox();

        appNameLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.appNameLabel.text")); // NOI18N

        appNameField.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.appNameField.text")); // NOI18N
        appNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appNameFieldActionPerformed(evt);
            }
        });

        autoCreateViewCheckBox.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.autoCreateViewCheckBox.text")); // NOI18N

        useProjectDirectoryCheckBox.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.useProjectDirectoryCheckBox.text")); // NOI18N

        cakePhpDirLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.cakePhpDirLabel.text")); // NOI18N

        cakePhpDirTextField.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.cakePhpDirTextField.text")); // NOI18N

        ignoreTmpCheckBox.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.ignoreTmpCheckBox.text")); // NOI18N

        goToActionsLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.goToActionsLabel.text")); // NOI18N

        showPopupForOneItemCheckBox.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.showPopupForOneItemCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(appNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(appNameField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cakePhpDirLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cakePhpDirTextField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(goToActionsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(goToActionsSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(autoCreateViewCheckBox)
                            .addComponent(useProjectDirectoryCheckBox)
                            .addComponent(ignoreTmpCheckBox)
                            .addComponent(showPopupForOneItemCheckBox))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appNameLabel)
                    .addComponent(appNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoCreateViewCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useProjectDirectoryCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cakePhpDirLabel)
                    .addComponent(cakePhpDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ignoreTmpCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(goToActionsLabel)
                    .addComponent(goToActionsSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPopupForOneItemCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

	private void appNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appNameFieldActionPerformed
        // TODO add your handling code here:
	}//GEN-LAST:event_appNameFieldActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField appNameField;
    private javax.swing.JLabel appNameLabel;
    private javax.swing.JCheckBox autoCreateViewCheckBox;
    private javax.swing.JLabel cakePhpDirLabel;
    private javax.swing.JTextField cakePhpDirTextField;
    private javax.swing.JLabel goToActionsLabel;
    private javax.swing.JSeparator goToActionsSeparator;
    private javax.swing.JCheckBox ignoreTmpCheckBox;
    private javax.swing.JCheckBox showPopupForOneItemCheckBox;
    private javax.swing.JCheckBox useProjectDirectoryCheckBox;
    // End of variables declaration//GEN-END:variables
}
