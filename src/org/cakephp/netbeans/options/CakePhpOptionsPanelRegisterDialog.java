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
package org.cakephp.netbeans.options;

import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CakePhpOptionsPanelRegisterDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 4509927348290152329L;
    private static final String HTTPS_GITHUB_COM = "https://github.com/"; // NOI18N
    private static final String HTTPS_GITHUB_COM_ZIPBALL_MASTER = "https://github.com///archive/master.zip"; // NOI18N
    private static final String GITHUB_ZIP_REGEX = "^https://github\\.com/.+/.+/archive/.+\\.zip$";
    private static final String ZIP_EXT = ".zip";

    /**
     * Creates new form CakePhpOptionsPanelRegisterDialog
     */
    public CakePhpOptionsPanelRegisterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initialize();
    }

    private void initialize() {
        nameTextField.setText(""); // NOI18N
        urlTextField.setText(HTTPS_GITHUB_COM_ZIPBALL_MASTER); // NOI18N
    }

    public String getPluginName() {
        return nameTextField.getText();
    }

    public String getUrl() {
        return urlTextField.getText();
    }

    public void setPluginName(String name) {
        nameTextField.setText(name);
    }

    public void setUrl(String url) {
        urlTextField.setText(url);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        urlLabel = new javax.swing.JLabel();
        urlTextField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        nameLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "CakePhpOptionsPanelRegisterDialog.nameLabel.text")); // NOI18N

        nameTextField.setText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "CakePhpOptionsPanelRegisterDialog.nameTextField.text")); // NOI18N

        urlLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "CakePhpOptionsPanelRegisterDialog.urlLabel.text")); // NOI18N

        urlTextField.setText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "CakePhpOptionsPanelRegisterDialog.urlTextField.text")); // NOI18N

        okButton.setText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "CakePhpOptionsPanelRegisterDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "CakePhpOptionsPanelRegisterDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText(org.openide.util.NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "CakePhpOptionsPanelRegisterDialog.errorLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(nameLabel)
                            .addComponent(urlLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField)
                            .addComponent(urlTextField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(errorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 271, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(urlLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(okButton)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(errorLabel)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

        private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (valid()) {
            setVisible(false);
            dispose();
        }
        }//GEN-LAST:event_okButtonActionPerformed

        private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        nameTextField.setText(""); // NOI18N
        urlTextField.setText(""); // NOI18N
        setVisible(false);
        dispose();
        }//GEN-LAST:event_cancelButtonActionPerformed

        private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        nameTextField.setText(""); // NOI18N
        urlTextField.setText(""); // NOI18N
        }//GEN-LAST:event_formWindowClosing

    /**
     * validation
     *
     * @return boolean
     */
    private boolean valid() {
        // empty
        String name = getPluginName();
        String url = getUrl();
        if (name.isEmpty() || url.isEmpty()) {
            errorLabel.setText(NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "ERR_EMPTY"));
            return false;
        }

        // invalid string
        String[] invalids = {";", ","}; // NOI18N
        for (String invalid : invalids) {
            if (name.contains(invalid) || url.contains(invalid)) {
                errorLabel.setText(NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "ERR_INVALID_STRING"));
                return false;
            }
        }

        // invalid github url
        if (url.startsWith(HTTPS_GITHUB_COM)) {
            if (!url.matches(GITHUB_ZIP_REGEX)) {
                errorLabel.setText(NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "ERR_INVALID_GITHUB_ZIPBALL_URL"));
                return false;
            }
        }
        if (!url.startsWith(HTTPS_GITHUB_COM) && !url.endsWith(ZIP_EXT)) {
            errorLabel.setText(NbBundle.getMessage(CakePhpOptionsPanelRegisterDialog.class, "ERR_INVALID_GITHUB_URL"));
            return false;
        }
        errorLabel.setText(""); // NOI18N
        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay
         * with the default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CakePhpOptionsPanelRegisterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                CakePhpOptionsPanelRegisterDialog dialog = new CakePhpOptionsPanelRegisterDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JTextField urlTextField;
    // End of variables declaration//GEN-END:variables
}
