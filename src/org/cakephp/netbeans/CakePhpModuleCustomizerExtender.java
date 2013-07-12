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
package org.cakephp.netbeans;

import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.ui.customizer.CakePhpCustomizerPanel;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CakePhpModuleCustomizerExtender extends PhpModuleCustomizerExtender {

    private final String appName;
    private final boolean originalAutoCreateState;
    private CakePhpCustomizerPanel component;
    private final String cakePhpDirPath;
    private final boolean isProjectDir;
    private final boolean originalIgnoreTmpDirectory;
    private final boolean isShowPopupForOneItem;

    CakePhpModuleCustomizerExtender(PhpModule phpModule) {
        appName = CakePreferences.getAppName(phpModule);
        originalAutoCreateState = CakePreferences.getAutoCreateView(phpModule);
        cakePhpDirPath = CakePreferences.getCakePhpDirPath(phpModule);
        isProjectDir = CakePreferences.useProjectDirectory(phpModule);
        originalIgnoreTmpDirectory = CakePreferences.ignoreTmpDirectory(phpModule);
        isShowPopupForOneItem = CakePreferences.isShowPopupForOneItem(phpModule);
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(CakePhpModuleCustomizerExtender.class, "LBL_CakePHP"); // NOI18N
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public EnumSet<Change> save(PhpModule phpModule) {
        EnumSet<Change> enumset = EnumSet.of(Change.FRAMEWORK_CHANGE);
        String newAppName = getPanel().getAppNameField().getText();
        boolean newAutoCreateState = getPanel().isAutoCreateView();
        String newCakePhpDirPath = getPanel().getCakePhpDirTextField();
        boolean newIgnoreTmpDirectory = getPanel().ignoreTmpDirectory();
        if (newAutoCreateState != originalAutoCreateState) {
            CakePreferences.setAutoCreateView(phpModule, newAutoCreateState);
        }
        if (isProjectDir != getPanel().isUseProjectDirectory()) {
            CakePreferences.setUseProjectDirectory(phpModule, !isProjectDir);
        }
        if (isShowPopupForOneItem != getPanel().isShowPopupForOneItem()) {
            CakePreferences.setShowPopupForOneItem(phpModule, !isShowPopupForOneItem);
        }
        if (!cakePhpDirPath.equals(newCakePhpDirPath)) {
            CakePreferences.setCakePhpDirPath(phpModule, newCakePhpDirPath);
        }
        if (newIgnoreTmpDirectory != originalIgnoreTmpDirectory) {
            CakePreferences.setIgnoreTmpDirectory(phpModule, newIgnoreTmpDirectory);
            enumset.add(Change.IGNORED_FILES_CHANGE);
        }
        if (!newAppName.equals(appName) && !newAppName.equals("")) { // NOI18N
            CakePreferences.setAppName(phpModule, newAppName);
        }
        return enumset;
    }

    private CakePhpCustomizerPanel getPanel() {
        if (component == null) {
            component = new CakePhpCustomizerPanel();
            component.setAutoCreateView(originalAutoCreateState);
            if (!appName.equals("")) {
                component.setAppNameField(appName);
            }
            component.setCakePhpDirTextField(cakePhpDirPath);
            component.setUseProjectDirectory(isProjectDir);
            component.setIgnoreTmpDirectory(originalIgnoreTmpDirectory);
            component.setShowPopupForOneItem(isShowPopupForOneItem);
        }
        return component;
    }
}
