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
package org.cakephp.netbeans.ui.wizards;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.github.BaserCmsGithubTags;
import org.cakephp.netbeans.versions.Versionable;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public class BaserCmsConfigurationInnerPanel extends ConfigurationInnerPanel {

    private int reloadCount = 0;
    private String errorMessage;
    private static final int RELOAD_LIMIT = 5;
    private static final Logger LOGGER = Logger.getLogger(BaserCmsConfigurationInnerPanel.class.getName());
    private static final long serialVersionUID = 7990017232014609165L;
    private boolean isNetworkError = true;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private static final RequestProcessor RP = new RequestProcessor(BaserCmsConfigurationInnerPanel.class);

    /**
     * Creates new form BaserCmsConfigurationPanel
     */
    public BaserCmsConfigurationInnerPanel() {
        initComponents();
        progressTextField.setVisible(false);
        progressTextField.setText(""); // NOI18N

        initVersion();
    }

    /**
     * Initialze versions.
     */
    @NbBundle.Messages("BaserCmsConfigurationInnerPanel.message.fetching=Fetching...")
    private void initVersion() {
        baserVersionComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setError();
                fireChange();
            }
        });
        baserVersionComboBox.setEnabled(false);
        baserVersionComboBox.addItem(Bundle.BaserCmsConfigurationInnerPanel_message_fetching());
        RP.post(new Runnable() {
            @Override
            public void run() {
                BaserCmsGithubTags githubTags = BaserCmsGithubTags.getInstance();
                final String[] names = githubTags.getNames();
                Arrays.sort(names, Versionable.VERSION_COMPARATOR);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        baserVersionComboBox.removeAllItems();
                        baserVersionComboBox.setModel(new DefaultComboBoxModel<>(names));
                        baserVersionComboBox.setEnabled(true);
                    }
                });
            }
        });
    }

    @Override
    public String getCategoryName() {
        return "baserCMS"; // NOI18N
    }

    @Override
    public String getErrorMessage() {
        setError();
        return errorMessage;
    }

    @Override
    public void setNetworkError(boolean isError) {
        isNetworkError = isError;
    }

    public String getSelectedUrl() {
        String selectedVersion = (String) baserVersionComboBox.getSelectedItem();
        BaserCmsGithubTags githubTags = BaserCmsGithubTags.getInstance();
        return githubTags.getZipUrl(selectedVersion);
    }

    public JTextField getProgressTextField() {
        return progressTextField;
    }

    /**
     * Set error message.
     */
    @NbBundle.Messages("BaserCmsConfigurationInnerPanel.error.message.fetching=Fetching versions...")
    private void setError() {
        errorMessage = null;
        if (isNetworkError) {
            errorMessage = Bundle.LBL_ConnectErrorMessage();
        }
        int itemCount = baserVersionComboBox.getItemCount();
        if (itemCount == 1 && baserVersionComboBox.getItemAt(0).equals(Bundle.BaserCmsConfigurationInnerPanel_message_fetching())) {
            errorMessage = Bundle.BaserCmsConfigurationInnerPanel_error_message_fetching();
        }
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
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

        baserVersionComboBox = new javax.swing.JComboBox<String>();
        reloadButton = new javax.swing.JButton();
        progressTextField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(reloadButton, org.openide.util.NbBundle.getMessage(BaserCmsConfigurationInnerPanel.class, "BaserCmsConfigurationInnerPanel.reloadButton.text")); // NOI18N
        reloadButton.setToolTipText(org.openide.util.NbBundle.getMessage(BaserCmsConfigurationInnerPanel.class, "BaserCmsConfigurationInnerPanel.reloadButton.toolTipText")); // NOI18N
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        progressTextField.setEditable(false);
        progressTextField.setText(org.openide.util.NbBundle.getMessage(BaserCmsConfigurationInnerPanel.class, "BaserCmsConfigurationInnerPanel.progressTextField.text")); // NOI18N
        progressTextField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(baserVersionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reloadButton)
                .addContainerGap(153, Short.MAX_VALUE))
            .addComponent(progressTextField)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(baserVersionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reloadButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(96, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("BaserCmsConfigurationInnerPanel.reload.limit.error=Reload limit is 5 for baserCMS version.")
    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        // reload tags : limit 5 times
        if (++reloadCount <= RELOAD_LIMIT) {
            BaserCmsGithubTags.getInstance().reload();
            initVersion();
        } else {
            reloadButton.setEnabled(false);
            LOGGER.log(Level.WARNING, Bundle.BaserCmsConfigurationInnerPanel_reload_limit_error());
        }
    }//GEN-LAST:event_reloadButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> baserVersionComboBox;
    private javax.swing.JTextField progressTextField;
    private javax.swing.JButton reloadButton;
    // End of variables declaration//GEN-END:variables

}
