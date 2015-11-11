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
package org.cakephp.netbeans.basercms.ui;

import java.awt.Dialog;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 * Panel for copying existing theme.
 *
 * @author junichi11
 */
public class CopyThemePanel extends JPanel {

    private static final long serialVersionUID = -1477757161264737208L;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final DialogDescriptor dialogDescriptor;

    /**
     * Creates new form CopyThemePanel
     */
    @NbBundle.Messages({
        "CopyThemePanel.title=Copy an existing theme"
    })
    public CopyThemePanel() {
        initComponents();
        init();
        dialogDescriptor = new DialogDescriptor(this, Bundle.CopyThemePanel_title(), true, null);
    }

    public String getThemeName() {
        return themeNameTextField.getText().trim();
    }

    public void setExistingThemeNames(List<String> names) {
        themesComboBox.removeAllItems();
        DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<>(names.toArray(new String[0]));
        themesComboBox.setModel(defaultComboBoxModel);
    }

    public String getSelectedThemeName() {
        return (String) themesComboBox.getModel().getSelectedItem();
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void init() {
        themeNameTextField.getDocument().addDocumentListener(new DeafultDocumentListener());
        DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<>();
        themesComboBox.setModel(defaultComboBoxModel);
    }

    public DialogDescriptor showDialog() {
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.pack();
        dialog.setVisible(true);
        return dialogDescriptor;
    }

    public void setError(String message) {
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void setOkButtonValid(boolean isValid) {
        if (dialogDescriptor == null) {
            return;
        }
        dialogDescriptor.setValid(isValid);
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

        titleLabel = new javax.swing.JLabel();
        themeNameLabel = new javax.swing.JLabel();
        themeNameTextField = new javax.swing.JTextField();
        themesLabel = new javax.swing.JLabel();
        themesComboBox = new javax.swing.JComboBox<String>();
        errorLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(titleLabel, org.openide.util.NbBundle.getMessage(CopyThemePanel.class, "CopyThemePanel.titleLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(themeNameLabel, org.openide.util.NbBundle.getMessage(CopyThemePanel.class, "CopyThemePanel.themeNameLabel.text")); // NOI18N

        themeNameTextField.setText(org.openide.util.NbBundle.getMessage(CopyThemePanel.class, "CopyThemePanel.themeNameTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(themesLabel, org.openide.util.NbBundle.getMessage(CopyThemePanel.class, "CopyThemePanel.themesLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(CopyThemePanel.class, "CopyThemePanel.errorLabel.text")); // NOI18N

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
                            .addComponent(themesLabel)
                            .addComponent(themeNameLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(themesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(themeNameTextField)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titleLabel)
                            .addComponent(errorLabel))
                        .addGap(0, 301, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(themeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(themeNameLabel))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(themesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(themesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel themeNameLabel;
    private javax.swing.JTextField themeNameTextField;
    private javax.swing.JComboBox<String> themesComboBox;
    private javax.swing.JLabel themesLabel;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    //~ Inner class
    private class DeafultDocumentListener implements DocumentListener {

        public DeafultDocumentListener() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }
    }
}
