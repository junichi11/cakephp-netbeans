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

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.CakePhp;
import org.cakephp.netbeans.basercms.BaserCms;
import org.cakephp.netbeans.options.CakePhpOptions;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author junichi11
 */
public class NewProjectConfigurationPanel extends JPanel {

    private static final long serialVersionUID = 7874450246517944114L;
    private String errorMessage;
    private boolean isNetworkError = false;
    private static final Logger LOGGER = Logger.getLogger(NewProjectConfigurationPanel.class.getName());
    private final Map<String, ConfigurationInnerPanel> panels = new HashMap<>();
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * Creates new form NewProjectConfigurationPanel
     */
    public NewProjectConfigurationPanel() {
        initComponents();
        cakephpRadioButton.setIcon(ImageUtilities.loadImageIcon(CakePhp.CAKE_ICON_16, false));
        basercmsRadioButton.setIcon(ImageUtilities.loadImageIcon(BaserCms.BASER_ICON_16, false));
        basercmsRadioButton.setVisible(CakePhpOptions.getInstance().isBaserCmsEnabled());
        // progress information
        if (isNetworkError()) {
            LOGGER.log(Level.WARNING, Bundle.LBL_ConnectErrorMessage());
        }
    }

    /**
     * Add ConfigurationInnerPanel. JRadioButtons named the same as category
     * name of panel must exist.
     *
     * @param panel ConfigurationInnerPanel
     */
    public void addPanel(@NonNull ConfigurationInnerPanel panel) {
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        boolean existButton = false;
        String categoryName = panel.getCategoryName();
        assert categoryName != null;
        while (buttons.hasMoreElements()) {
            AbstractButton button = buttons.nextElement();
            if (button.getText().equals(categoryName)) {
                existButton = true;
                break;
            }
        }
        if (existButton) {
            panel.setNetworkError(isNetworkError);
            panels.put(categoryName, panel);
        }
    }

    /**
     * Get CakePhpConfigurationInnerPanel.
     *
     * @return panel
     */
    public CakePhpConfigurationInnerPanel getCakePanel() {
        return (CakePhpConfigurationInnerPanel) panels.get(cakephpRadioButton.getText());
    }

    /**
     * Get error message.
     *
     * @return error message if there are some problems, {@code null} otherwise.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Check wheter local zip file path is set.
     *
     * @return {@code true} if the path is set, {@code false} otherwise.
     */
    private boolean isEnabledLocalPath() {
        String localZipFilePath = CakePhpOptions.getInstance().getLocalZipFilePath();
        return !StringUtils.isEmpty(localZipFilePath);
    }

    /**
     * Check whether PC is connected to internet.
     *
     * @return {@code ture} Is connected to internet, {@code false} otherwise.
     */
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

    /**
     * Set error message
     */
    public void setError() {
        if (isNetworkError) {
            if (!isEnabledLocalPath()) {
                errorMessage = Bundle.LBL_NewProjectWizardErrorMessage();
            } else {
                errorMessage = null;
            }
        }
    }

    /**
     * Set enabled for radio buttons.
     *
     * @param isEnabled
     */
    public void setRadioButtonsEnabled(boolean isEnabled) {
        cakephpRadioButton.setEnabled(isEnabled);
        basercmsRadioButton.setEnabled(isEnabled);
    }

    /**
     * Check whether CakePHP is selected.
     *
     * @return {@code true} if CakePHP is selected, {@code false} otherwise.
     */
    public boolean isCakePhp() {
        return cakephpRadioButton.isSelected();
    }

    /**
     * Check whether baserCMS is selected.
     *
     * @return {@code true} if baserCMS is selected, {@code false} otherwise.
     */
    public boolean isBaserCms() {
        return basercmsRadioButton.isSelected();
    }

    /**
     * Change to selected panel.
     */
    public void changePanel() {
        configurationPanel.removeAll();
        ConfigurationInnerPanel selectedPanel = getSelectedPanel();
        if (selectedPanel != null) {
            configurationPanel.add(selectedPanel, BorderLayout.CENTER);
        }
        configurationPanel.revalidate();
        configurationPanel.repaint();

        fireChange();
    }

    /**
     * Get selected panel.
     *
     * @return
     */
    @CheckForNull
    public ConfigurationInnerPanel getSelectedPanel() {
        Enumeration<AbstractButton> elements = buttonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton button = elements.nextElement();
            if (button.isSelected()) {
                return panels.get(button.getText());
            }
        }
        return null;
    }

    public void addChangeListener(ChangeListener changeListener) {
        changeSupport.addChangeListener(changeListener);
        for (ConfigurationInnerPanel panel : panels.values()) {
            panel.addChangeListener(changeListener);
        }
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeSupport.removeChangeListener(changeListener);
        for (ConfigurationInnerPanel panel : panels.values()) {
            panel.removeChangeListener(changeListener);
        }
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

        buttonGroup = new javax.swing.ButtonGroup();
        cakephpRadioButton = new javax.swing.JRadioButton();
        basercmsRadioButton = new javax.swing.JRadioButton();
        configurationPanel = new javax.swing.JPanel();

        setAutoscrolls(true);

        buttonGroup.add(cakephpRadioButton);
        cakephpRadioButton.setSelected(true);
        cakephpRadioButton.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.cakephpRadioButton.text")); // NOI18N
        cakephpRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cakephpRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup.add(basercmsRadioButton);
        basercmsRadioButton.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.basercmsRadioButton.text")); // NOI18N
        basercmsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                basercmsRadioButtonActionPerformed(evt);
            }
        });

        configurationPanel.setLayout(new javax.swing.BoxLayout(configurationPanel, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(configurationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(cakephpRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(basercmsRadioButton)
                .addGap(0, 42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cakephpRadioButton)
                    .addComponent(basercmsRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(configurationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void basercmsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_basercmsRadioButtonActionPerformed
        changePanel();
    }//GEN-LAST:event_basercmsRadioButtonActionPerformed

    private void cakephpRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cakephpRadioButtonActionPerformed
        changePanel();
    }//GEN-LAST:event_cakephpRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton basercmsRadioButton;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JRadioButton cakephpRadioButton;
    private javax.swing.JPanel configurationPanel;
    // End of variables declaration//GEN-END:variables

}
