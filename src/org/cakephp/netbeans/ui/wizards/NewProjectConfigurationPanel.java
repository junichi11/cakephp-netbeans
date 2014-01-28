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
 * NewProjectConfigurationPanel.java
 *
 * Created on 2011/09/27
 */
package org.cakephp.netbeans.ui.wizards;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.cakephp.netbeans.github.CakePhpGithubTags;
import org.cakephp.netbeans.options.CakePhpOptions;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.composer.api.Composer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class NewProjectConfigurationPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 7874450246517944114L;
    private String errorMessage;
    private boolean isNetworkError = false;
    private static final Logger LOGGER = Logger.getLogger(NewProjectConfigurationPanel.class.getName());

    /**
     * Creates new form NewProjectConfigurationPanel
     */
    @NbBundle.Messages({
        "LBL_ConnectErrorMessage=Is not connected to the network.",
        "LBL_NewProjectWizardErrorMessage=Please, connect to the network or set CakePHP local file option"
    })
    public NewProjectConfigurationPanel() {
        initComponents();

        // progress information
        progressTextField.setText(""); // NOI18N
        progressTextField.setEnabled(false);
        progressTextField.setVisible(false);
        if (isNetworkError()) {
            LOGGER.log(Level.WARNING, Bundle.LBL_ConnectErrorMessage());
            return;
        }

        // github
        initUnzipping();

        // local
        initLocalUnzipping();

        // composer
        initComposer();
    }

    private void initUnzipping() {
        unzipRadioButton.setSelected(true);

        CakePhpGithubTags githubTags = CakePhpGithubTags.getInstance();
        String[] names = githubTags.getNames();
        Arrays.sort(names, new ComparatorImpl());
        versionComboBox.setEnabled(true);
        versionComboBox.setModel(new DefaultComboBoxModel(names));
    }

    private void initLocalUnzipping() {
        // local file
        CakePhpOptions options = CakePhpOptions.getInstance();
        String localFilePath = options.getLocalZipFilePath();
        if (StringUtils.isEmpty(localFilePath)) {
            unzipLocalFileRadioButton.setEnabled(false);
            unzipLocalFileRadioButton.setVisible(false);
        } else {
            unzipLocalFileRadioButton.setToolTipText(localFilePath);
        }
    }

    @NbBundle.Messages({
        "NewProjectConfigurationPanel.composer.app.name.tooltip=app name: If it's empty, source directory is app directory"
    })
    private void initComposer() {
        boolean isAvailableComposer = true;
        try {
            // check whether composer is set
            Composer.getDefault();
        } catch (InvalidPhpExecutableException ex) {
            isAvailableComposer = false;
        }

        final boolean isAvailable = isAvailableComposer;
        // use invokeLator because enabled doesn't work
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                composerRadioButton.setEnabled(isAvailable);
                composerRadioButton.setVisible(isAvailable);
                emptyCheckBox.setSelected(true);
                emptyCheckBox.setEnabled(false);
                emptyCheckBox.setVisible(isAvailable);
                appNameTextField.setText(""); // NOI18N
                appNameTextField.setEnabled(false);
                appNameTextField.setVisible(isAvailable);
                appNameTextField.setToolTipText(Bundle.NewProjectConfigurationPanel_composer_app_name_tooltip());
                appNameLabel.setEnabled(false);
                appNameLabel.setVisible(isAvailable);
            }
        });
    }

    public boolean isDatabasePhp() {
        return databaseCheckBox.isSelected();
    }

    public String getSelectedUrl() {
        String selectedVersion = (String) versionComboBox.getSelectedItem();
        CakePhpGithubTags githubTags = CakePhpGithubTags.getInstance();
        return githubTags.getZipUrl(selectedVersion);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isUnzip() {
        return unzipRadioButton.isSelected();
    }

    public boolean isLocalFile() {
        return unzipLocalFileRadioButton.isSelected();
    }

    public boolean isComposer() {
        return composerRadioButton.isSelected();
    }

    public boolean isGit() {
        return gitCloneRadioButton.isSelected();
    }

    public boolean useEmptyOption() {
        return emptyCheckBox.isSelected();
    }

    public String getAppName() {
        return appNameTextField.getText().trim();
    }

    public JTextField getProgressTextField() {
        return progressTextField;
    }

    private boolean isEnabledLocalPath() {
        String localZipFilePath = CakePhpOptions.getInstance().getLocalZipFilePath();
        return !StringUtils.isEmpty(localZipFilePath);
    }

    private boolean isNetworkError() {
        isNetworkError = false;
        try {
            URL url = new URL("http://google.com"); // NOI18N
            URLConnection openConnection = url.openConnection();
            openConnection.getInputStream();
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            isNetworkError = true;
        }
        return isNetworkError;
    }

    public void setError() {
        if (isNetworkError) {
            if (!isEnabledLocalPath()) {
                errorMessage = Bundle.LBL_NewProjectWizardErrorMessage();
            } else {
                errorMessage = null;
                unzipRadioButton.setEnabled(false);
                composerRadioButton.setEnabled(false);
                emptyCheckBox.setEnabled(false);
                versionComboBox.setEnabled(false);
                gitCloneRadioButton.setEnabled(false);
                unzipLocalFileRadioButton.setEnabled(true);
            }
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

        buttonGroup = new javax.swing.ButtonGroup();
        unzipRadioButton = new javax.swing.JRadioButton();
        unzipLocalFileRadioButton = new javax.swing.JRadioButton();
        composerRadioButton = new javax.swing.JRadioButton();
        gitCloneRadioButton = new javax.swing.JRadioButton();
        progressTextField = new javax.swing.JTextField();
        versionComboBox = new javax.swing.JComboBox();
        databaseCheckBox = new javax.swing.JCheckBox();
        dbDetailButton = new javax.swing.JButton();
        emptyCheckBox = new javax.swing.JCheckBox();
        appNameTextField = new javax.swing.JTextField();
        appNameLabel = new javax.swing.JLabel();

        setAutoscrolls(true);

        buttonGroup.add(unzipRadioButton);
        unzipRadioButton.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.unzipRadioButton.text")); // NOI18N
        unzipRadioButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                unzipRadioButtonStateChanged(evt);
            }
        });

        buttonGroup.add(unzipLocalFileRadioButton);
        unzipLocalFileRadioButton.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.unzipLocalFileRadioButton.text")); // NOI18N

        buttonGroup.add(composerRadioButton);
        composerRadioButton.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.composerRadioButton.text")); // NOI18N
        composerRadioButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                composerRadioButtonStateChanged(evt);
            }
        });

        buttonGroup.add(gitCloneRadioButton);
        gitCloneRadioButton.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.gitCloneRadioButton.text")); // NOI18N
        gitCloneRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gitCloneRadioButtonActionPerformed(evt);
            }
        });

        progressTextField.setEditable(false);
        progressTextField.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.progressTextField.text")); // NOI18N
        progressTextField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        databaseCheckBox.setSelected(true);
        databaseCheckBox.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.databaseCheckBox.text")); // NOI18N

        dbDetailButton.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbDetailButton.text")); // NOI18N
        dbDetailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbDetailButtonActionPerformed(evt);
            }
        });

        emptyCheckBox.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.emptyCheckBox.text")); // NOI18N
        emptyCheckBox.setEnabled(false);

        appNameTextField.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.appNameTextField.text")); // NOI18N

        appNameLabel.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.appNameLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressTextField)
            .addGroup(layout.createSequentialGroup()
                .addComponent(composerRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emptyCheckBox)
                .addGap(18, 18, 18)
                .addComponent(appNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(appNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(databaseCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dbDetailButton))
                    .addComponent(gitCloneRadioButton)
                    .addComponent(unzipLocalFileRadioButton)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(unzipRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(versionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseCheckBox)
                    .addComponent(dbDetailButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(unzipRadioButton)
                    .addComponent(versionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(unzipLocalFileRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(composerRadioButton)
                    .addComponent(emptyCheckBox)
                    .addComponent(appNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gitCloneRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void gitCloneRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gitCloneRadioButtonActionPerformed
        try {
            Process process = Runtime.getRuntime().exec("git"); // NOI18N
            process.waitFor();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            gitCloneRadioButton.setSelected(false);
            unzipRadioButton.setSelected(true);
            gitCloneRadioButton.setEnabled(false);
        }
    }//GEN-LAST:event_gitCloneRadioButtonActionPerformed

    private void dbDetailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbDetailButtonActionPerformed
        NewProjectConfigurationDetailPanel detailPanel = NewProjectConfigurationDetailPanel.getDefault();
        detailPanel.showDialog();
    }//GEN-LAST:event_dbDetailButtonActionPerformed

    private void composerRadioButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_composerRadioButtonStateChanged
        emptyCheckBox.setEnabled(isComposer());
        appNameTextField.setEnabled(isComposer());
        appNameLabel.setEnabled(isComposer());
    }//GEN-LAST:event_composerRadioButtonStateChanged

    private void unzipRadioButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_unzipRadioButtonStateChanged
        versionComboBox.setEnabled(isUnzip());
    }//GEN-LAST:event_unzipRadioButtonStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appNameLabel;
    private javax.swing.JTextField appNameTextField;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JRadioButton composerRadioButton;
    private javax.swing.JCheckBox databaseCheckBox;
    private javax.swing.JButton dbDetailButton;
    private javax.swing.JCheckBox emptyCheckBox;
    private javax.swing.JRadioButton gitCloneRadioButton;
    private javax.swing.JTextField progressTextField;
    private javax.swing.JRadioButton unzipLocalFileRadioButton;
    private javax.swing.JRadioButton unzipRadioButton;
    private javax.swing.JComboBox versionComboBox;
    // End of variables declaration//GEN-END:variables

    //~ inner class
    private static class ComparatorImpl implements Comparator<String> {

        public ComparatorImpl() {
        }
        private static final String NUMBER_REGEX = "[0-9]+"; // NOI18N
        private static final String SPLIT_REGEX = "[., -]"; // NOI18N

        @Override
        public int compare(String a, String b) {
            String[] aArray = a.split(SPLIT_REGEX);
            String[] bArray = b.split(SPLIT_REGEX);
            int aLength = aArray.length;
            int bLength = bArray.length;
            for (int i = 0; i < aLength; i++) {
                if (i == aLength - 1) {
                    if ((bLength - aLength) < 0) {
                        return -1;
                    }
                }
                String aString = aArray[i];
                String bString = bArray[i];
                if (aString.matches(NUMBER_REGEX) && bString.matches(NUMBER_REGEX)) {
                    try {
                        Integer aInt = Integer.parseInt(aString);
                        Integer bInt = Integer.parseInt(bString);
                        if (aInt == bInt) {
                            continue;
                        } else {
                            return bInt - aInt;
                        }
                    } catch (NumberFormatException ex) {
                        return 1;
                    }
                } else {
                    return b.compareTo(a);
                }
            }
            return 1;
        }
    }
}
