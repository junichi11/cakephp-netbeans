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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
        setFieldsEnabled(enabledCheckBox.isSelected());
    }

    private void init() {
        enabledCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                fireChange();
                setFieldsEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        DocumentListener documentListener = new DefaultDocumentListener();
        appDirectoryTextField.getDocument().addDocumentListener(documentListener);
        cakePhpDirTextField.getDocument().addDocumentListener(documentListener);
        dotcakeTextField.getDocument().addDocumentListener(documentListener);
    }

    public boolean isEnabledCakePhp() {
        return enabledCheckBox.isSelected();
    }

    public void setEnabledCakePhp(boolean isEnabled) {
        enabledCheckBox.setSelected(isEnabled);
    }

    public boolean isAutoCreateView() {
        return autoCreateViewCheckBox.isSelected();
    }

    public void setAutoCreateView(boolean isAuto) {
        autoCreateViewCheckBox.setSelected(isAuto);
    }

    public String getCakePhpDirPath() {
        return cakePhpDirTextField.getText();
    }

    public void setCakePhpDirPath(String cakePhpDir) {
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

    public String getAppDirectoryPath() {
        return appDirectoryTextField.getText();
    }

    public void setAppDirectoryPath(String path) {
        appDirectoryTextField.setText(path);
    }

    public String getDotcakeFilePath() {
        return dotcakeTextField.getText().trim();
    }

    public void setDotcakeFilePath(String path) {
        dotcakeTextField.setText(path);
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

    final void setFieldsEnabled(boolean enabled) {
        ignoreTmpCheckBox.setEnabled(enabled);
        autoCreateViewCheckBox.setEnabled(enabled);
        showPopupForOneItemCheckBox.setEnabled(enabled);
        appDirectoryTextField.setEnabled(enabled);
        cakePhpDirTextField.setEnabled(enabled);
        dotcakeTextField.setEnabled(enabled);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        autoCreateViewCheckBox = new javax.swing.JCheckBox();
        cakePhpDirLabel = new javax.swing.JLabel();
        cakePhpDirTextField = new javax.swing.JTextField();
        ignoreTmpCheckBox = new javax.swing.JCheckBox();
        goToActionsLabel = new javax.swing.JLabel();
        goToActionsSeparator = new javax.swing.JSeparator();
        showPopupForOneItemCheckBox = new javax.swing.JCheckBox();
        customDirectoryPathLabel = new javax.swing.JLabel();
        customDirectoryPathSeparator = new javax.swing.JSeparator();
        appDirectoryLabel = new javax.swing.JLabel();
        appDirectoryTextField = new javax.swing.JTextField();
        generalLabel = new javax.swing.JLabel();
        generalSeparator = new javax.swing.JSeparator();
        enabledCheckBox = new javax.swing.JCheckBox();
        dotcakeLabel = new javax.swing.JLabel();
        dotcakeTextField = new javax.swing.JTextField();

        autoCreateViewCheckBox.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.autoCreateViewCheckBox.text")); // NOI18N

        cakePhpDirLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.cakePhpDirLabel.text")); // NOI18N

        cakePhpDirTextField.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.cakePhpDirTextField.text")); // NOI18N

        ignoreTmpCheckBox.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.ignoreTmpCheckBox.text")); // NOI18N

        goToActionsLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.goToActionsLabel.text")); // NOI18N

        showPopupForOneItemCheckBox.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.showPopupForOneItemCheckBox.text")); // NOI18N

        customDirectoryPathLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.customDirectoryPathLabel.text")); // NOI18N

        appDirectoryLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.appDirectoryLabel.text")); // NOI18N

        appDirectoryTextField.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.appDirectoryTextField.text")); // NOI18N

        generalLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.generalLabel.text")); // NOI18N

        enabledCheckBox.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.enabledCheckBox.text")); // NOI18N

        dotcakeLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.dotcakeLabel.text")); // NOI18N

        dotcakeTextField.setText(org.openide.util.NbBundle.getMessage(CakePhpCustomizerPanel.class, "CakePhpCustomizerPanel.dotcakeTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(autoCreateViewCheckBox)
                            .addComponent(showPopupForOneItemCheckBox))
                        .addContainerGap(160, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(goToActionsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(goToActionsSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(customDirectoryPathLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(customDirectoryPathSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(generalLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(generalSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(appDirectoryLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(appDirectoryTextField))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cakePhpDirLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cakePhpDirTextField))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(enabledCheckBox)
                                            .addComponent(ignoreTmpCheckBox))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(dotcakeLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dotcakeTextField)))))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(generalLabel)
                    .addComponent(generalSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enabledCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ignoreTmpCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(goToActionsLabel)
                    .addComponent(goToActionsSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPopupForOneItemCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoCreateViewCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(customDirectoryPathLabel)
                    .addComponent(customDirectoryPathSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appDirectoryLabel)
                    .addComponent(appDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cakePhpDirLabel)
                    .addComponent(cakePhpDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dotcakeLabel)
                    .addComponent(dotcakeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appDirectoryLabel;
    private javax.swing.JTextField appDirectoryTextField;
    private javax.swing.JCheckBox autoCreateViewCheckBox;
    private javax.swing.JLabel cakePhpDirLabel;
    private javax.swing.JTextField cakePhpDirTextField;
    private javax.swing.JLabel customDirectoryPathLabel;
    private javax.swing.JSeparator customDirectoryPathSeparator;
    private javax.swing.JLabel dotcakeLabel;
    private javax.swing.JTextField dotcakeTextField;
    private javax.swing.JCheckBox enabledCheckBox;
    private javax.swing.JLabel generalLabel;
    private javax.swing.JSeparator generalSeparator;
    private javax.swing.JLabel goToActionsLabel;
    private javax.swing.JSeparator goToActionsSeparator;
    private javax.swing.JCheckBox ignoreTmpCheckBox;
    private javax.swing.JCheckBox showPopupForOneItemCheckBox;
    // End of variables declaration//GEN-END:variables

    private class DefaultDocumentListener implements DocumentListener {

        public DefaultDocumentListener() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            processChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processChange();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processChange();
        }

        private void processChange() {
            fireChange();
        }
    }
}
