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
package org.cakephp.netbeans.ui.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author junichi11
 */
public final class CheckDefaultPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -9147653710118986641L;
    private static final String DEFAULT = "default";
    private static final String OK_PREFIX = "OK"; // NOI18N
    private final DialogDescriptor dialogDescriptor;
    private Dialog dialog;
    private final Icon successIcon = new ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_success_icon_16.png")); // NOI18N
    private final Icon failIcon = new ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png")); // NOI18N

    /**
     * Creates new form CheckDefaultPanel
     */
    public CheckDefaultPanel() {
        initComponents();
        dialogDescriptor = new DialogDescriptor(this, "Check Default", true, this);
    }

    /**
     * Open dialog
     *
     * @return void
     */
    public void showDialog() {
        dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Reset status label
     *
     * @return
     */
    public void reset() {
        cakeGenericCssStatusLabel.setText(DEFAULT);
        cakeIconStatusLabel.setText(DEFAULT);
        cakePowerStatusLabel.setText(DEFAULT);
        faviconStatusLabel.setText(DEFAULT);
        filesEmptyStatusLabel.setText(DEFAULT);
        testErrorIconStatusLabel.setText(DEFAULT);
        testPassIconStatusLabel.setText(DEFAULT);
        testPhpStatusLabel.setText(DEFAULT);
        testSkipIconStatusLabel.setText(DEFAULT);
        sessionCookieStatusLabel.setText(DEFAULT);
        cakeGenericCssLabel.setIcon(failIcon);
        cakeIconLabel.setIcon(failIcon);
        cakePowerLabel.setIcon(failIcon);
        faviconLabel.setIcon(failIcon);
        filesEmptyLabel.setIcon(failIcon);
        testErrorIconLabel.setIcon(failIcon);
        testPassIconLabel.setIcon(failIcon);
        testPhpLabel.setIcon(failIcon);
        testSkipIconLabel.setIcon(failIcon);
        sessionCookieLabel.setIcon(failIcon);
    }

    public void setCakeGenericCssStatusLabel(String status) {
        cakeGenericCssStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            cakeGenericCssLabel.setIcon(successIcon);
        }
    }

    public void setCakeIconStatusLabel(String status) {
        cakeIconStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            cakeIconLabel.setIcon(successIcon);
        }
    }

    public void setCakePowerStatusLabel(String status) {
        cakePowerStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            cakePowerLabel.setIcon(successIcon);
        }
    }

    public void setFaviconStatusLabel(String status) {
        faviconStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            faviconLabel.setIcon(successIcon);
        }
    }

    public void setFilesEmptyStatusLabel(String status) {
        filesEmptyStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            filesEmptyLabel.setIcon(successIcon);
        }
    }

    public void setTestErrorIconStatusLabel(String status) {
        testErrorIconStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            testErrorIconLabel.setIcon(successIcon);
        }
    }

    public void setTestFailIconStatusLabel(String status) {
        testFailIconStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            testFailIconLabel.setIcon(successIcon);
        }
    }

    public void setTestPassIconStatusLabel(String status) {
        testPassIconStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            testPassIconLabel.setIcon(successIcon);
        }
    }

    public void setTestPhpStatusLabel(String status) {
        testPhpStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            testPhpLabel.setIcon(successIcon);
        }
    }

    public void setTestSkipIconStatusLabel(String status) {
        testSkipIconStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            testSkipIconLabel.setIcon(successIcon);
        }
    }

    public void setSessionCookieStatusLabel(String status) {
        sessionCookieStatusLabel.setText(status);
        if (status.startsWith(OK_PREFIX)) {
            sessionCookieLabel.setIcon(successIcon);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        faviconLabel = new javax.swing.JLabel();
        faviconStatusLabel = new javax.swing.JLabel();
        cakeGenericCssLabel = new javax.swing.JLabel();
        filesEmptyLabel = new javax.swing.JLabel();
        testPhpLabel = new javax.swing.JLabel();
        cakeIconLabel = new javax.swing.JLabel();
        cakePowerLabel = new javax.swing.JLabel();
        testErrorIconLabel = new javax.swing.JLabel();
        testFailIconLabel = new javax.swing.JLabel();
        testPassIconLabel = new javax.swing.JLabel();
        testSkipIconLabel = new javax.swing.JLabel();
        cakeGenericCssStatusLabel = new javax.swing.JLabel();
        filesEmptyStatusLabel = new javax.swing.JLabel();
        testPhpStatusLabel = new javax.swing.JLabel();
        cakeIconStatusLabel = new javax.swing.JLabel();
        cakePowerStatusLabel = new javax.swing.JLabel();
        testErrorIconStatusLabel = new javax.swing.JLabel();
        testFailIconStatusLabel = new javax.swing.JLabel();
        testPassIconStatusLabel = new javax.swing.JLabel();
        testSkipIconStatusLabel = new javax.swing.JLabel();
        sessionCookieLabel = new javax.swing.JLabel();
        sessionCookieStatusLabel = new javax.swing.JLabel();

        faviconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(faviconLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.faviconLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(faviconStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.faviconStatusLabel.text")); // NOI18N

        cakeGenericCssLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(cakeGenericCssLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.cakeGenericCssLabel.text")); // NOI18N

        filesEmptyLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(filesEmptyLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.filesEmptyLabel.text")); // NOI18N

        testPhpLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(testPhpLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testPhpLabel.text")); // NOI18N

        cakeIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(cakeIconLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.cakeIconLabel.text")); // NOI18N

        cakePowerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(cakePowerLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.cakePowerLabel.text")); // NOI18N

        testErrorIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(testErrorIconLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testErrorIconLabel.text")); // NOI18N

        testFailIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(testFailIconLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testFailIconLabel.text")); // NOI18N

        testPassIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(testPassIconLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testPassIconLabel.text")); // NOI18N

        testSkipIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(testSkipIconLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testSkipIconLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cakeGenericCssStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.cakeGenericCssStatusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(filesEmptyStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.filesEmptyStatusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(testPhpStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testPhpStatusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cakeIconStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.cakeIconStatusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cakePowerStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.cakePowerStatusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(testErrorIconStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testErrorIconStatusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(testFailIconStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testFailIconStatusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(testPassIconStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testPassIconStatusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(testSkipIconStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.testSkipIconStatusLabel.text")); // NOI18N

        sessionCookieLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_fail_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(sessionCookieLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.sessionCookieLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sessionCookieStatusLabel, org.openide.util.NbBundle.getMessage(CheckDefaultPanel.class, "CheckDefaultPanel.sessionCookieStatusLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testPassIconLabel)
                    .addComponent(testSkipIconLabel)
                    .addComponent(faviconLabel)
                    .addComponent(testFailIconLabel)
                    .addComponent(cakeIconLabel)
                    .addComponent(testPhpLabel)
                    .addComponent(filesEmptyLabel)
                    .addComponent(cakeGenericCssLabel)
                    .addComponent(testErrorIconLabel)
                    .addComponent(cakePowerLabel)
                    .addComponent(sessionCookieLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testFailIconStatusLabel)
                    .addComponent(cakePowerStatusLabel)
                    .addComponent(testErrorIconStatusLabel)
                    .addComponent(testPhpStatusLabel)
                    .addComponent(cakeIconStatusLabel)
                    .addComponent(cakeGenericCssStatusLabel)
                    .addComponent(filesEmptyStatusLabel)
                    .addComponent(testSkipIconStatusLabel)
                    .addComponent(faviconStatusLabel)
                    .addComponent(testPassIconStatusLabel)
                    .addComponent(sessionCookieStatusLabel))
                .addGap(128, 128, 128))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(faviconLabel)
                    .addComponent(faviconStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cakeGenericCssLabel)
                    .addComponent(cakeGenericCssStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filesEmptyLabel)
                    .addComponent(filesEmptyStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testPhpLabel)
                    .addComponent(testPhpStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cakeIconLabel)
                    .addComponent(cakeIconStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cakePowerLabel)
                    .addComponent(cakePowerStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testErrorIconLabel)
                    .addComponent(testErrorIconStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testFailIconLabel)
                    .addComponent(testFailIconStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testPassIconLabel)
                    .addComponent(testPassIconStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testSkipIconLabel)
                    .addComponent(testSkipIconStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sessionCookieLabel)
                    .addComponent(sessionCookieStatusLabel))
                .addContainerGap(30, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cakeGenericCssLabel;
    private javax.swing.JLabel cakeGenericCssStatusLabel;
    private javax.swing.JLabel cakeIconLabel;
    private javax.swing.JLabel cakeIconStatusLabel;
    private javax.swing.JLabel cakePowerLabel;
    private javax.swing.JLabel cakePowerStatusLabel;
    private javax.swing.JLabel faviconLabel;
    private javax.swing.JLabel faviconStatusLabel;
    private javax.swing.JLabel filesEmptyLabel;
    private javax.swing.JLabel filesEmptyStatusLabel;
    private javax.swing.JLabel sessionCookieLabel;
    private javax.swing.JLabel sessionCookieStatusLabel;
    private javax.swing.JLabel testErrorIconLabel;
    private javax.swing.JLabel testErrorIconStatusLabel;
    private javax.swing.JLabel testFailIconLabel;
    private javax.swing.JLabel testFailIconStatusLabel;
    private javax.swing.JLabel testPassIconLabel;
    private javax.swing.JLabel testPassIconStatusLabel;
    private javax.swing.JLabel testPhpLabel;
    private javax.swing.JLabel testPhpStatusLabel;
    private javax.swing.JLabel testSkipIconLabel;
    private javax.swing.JLabel testSkipIconStatusLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        // noop
    }
}
