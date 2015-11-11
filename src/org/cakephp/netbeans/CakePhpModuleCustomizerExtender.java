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

import java.beans.PropertyChangeEvent;
import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.ui.customizer.CakePhpCustomizerPanel;
import org.cakephp.netbeans.validator.CakePhpCustomizerValidator;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CakePhpModuleCustomizerExtender extends PhpModuleCustomizerExtender {

    private CakePhpCustomizerPanel component;
    private final String appDirectoryPath;
    private final String cakePhpDirPath;
    private final String dotcakeFilePath;
    private final boolean isShowPopupForOneItem;
    private final boolean originalAutoCreateState;
    private final boolean originalIgnoreTmpDirectory;
    private final boolean isEnabled;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private String errorMessage;
    private boolean isValid;
    private final PhpModule phpModule;

    CakePhpModuleCustomizerExtender(PhpModule phpModule) {
        this.phpModule = phpModule;
        originalAutoCreateState = CakePreferences.getAutoCreateView(phpModule);
        cakePhpDirPath = CakePreferences.getCakePhpDirPath(phpModule);
        originalIgnoreTmpDirectory = CakePreferences.ignoreTmpDirectory(phpModule);
        isShowPopupForOneItem = CakePreferences.isShowPopupForOneItem(phpModule);
        isEnabled = CakePreferences.isEnabled(phpModule);
        CakeVersion cakeVersion = null;
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule != null) {
            cakeVersion = cakeModule.getCakeVersion();
        }
        appDirectoryPath = CakePreferences.getAppDirectoryPath(phpModule, cakeVersion);
        dotcakeFilePath = CakePreferences.getDotcakeFilePath(phpModule);
    }

    @Override
    @NbBundle.Messages("LBL_CakePHP=CakePHP")
    public String getDisplayName() {
        return Bundle.LBL_CakePHP();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        if (listener instanceof CakePhpModule) {
            changeSupport.addChangeListener(listener);
        }
        getPanel().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        if (listener instanceof CakePhpModule) {
            changeSupport.removeChangeListener(listener);
        }
        getPanel().removeChangeListener(listener);
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
        validate();
        return isValid;
    }

    @Override
    public String getErrorMessage() {
        validate();
        return errorMessage;
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    @Override
    public EnumSet<Change> save(PhpModule phpModule) {
        EnumSet<Change> enumset = EnumSet.of(Change.FRAMEWORK_CHANGE);
        boolean newAutoCreateState = getPanel().isAutoCreateView();
        String newCakePhpDirPath = getPanel().getCakePhpDirPath();
        boolean newIgnoreTmpDirectory = getPanel().ignoreTmpDirectory();
        String newAppDirectoryPath = getPanel().getAppDirectoryPath();
        String newDotcakeFilePath = getPanel().getDotcakeFilePath();

        if (newAutoCreateState != originalAutoCreateState) {
            CakePreferences.setAutoCreateView(phpModule, newAutoCreateState);
        }
        if (isEnabled != getPanel().isEnabledCakePhp()) {
            CakePreferences.setEnabled(phpModule, !isEnabled);
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
        if (!newAppDirectoryPath.equals(appDirectoryPath)) {
            CakePreferences.setAppDirectoryPath(phpModule, newAppDirectoryPath);
            fireChange();
        }
        if (!newDotcakeFilePath.equals(dotcakeFilePath)) {
            CakePreferences.setDotcakeFilePath(phpModule, newDotcakeFilePath);
        }
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule != null) {
            cakeModule.notifyPropertyChanged(new PropertyChangeEvent(this, CakePhpModule.PROPERTY_CHANGE_CAKE, null, null));
        }
        return enumset;
    }

    private CakePhpCustomizerPanel getPanel() {
        if (component == null) {
            component = new CakePhpCustomizerPanel();
            component.setAutoCreateView(originalAutoCreateState);
            component.setCakePhpDirPath(cakePhpDirPath);
            component.setIgnoreTmpDirectory(originalIgnoreTmpDirectory);
            component.setShowPopupForOneItem(isShowPopupForOneItem);
            component.setAppDirectoryPath(appDirectoryPath);
            component.setDotcakeFilePath(dotcakeFilePath);
            component.setEnabledCakePhp(isEnabled);
        }
        return component;
    }

    @NbBundle.Messages("CakePhpModuleCustomizerExtender.error.source.invalid=Can't find source directory. Project might be broken.")
    void validate() {
        CakePhpCustomizerPanel panel = getPanel();
        if (!panel.isEnabledCakePhp()) {
            isValid = true;
            errorMessage = null;
            return;
        }

        // get source directory
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            // broken project
            isValid = false;
            errorMessage = Bundle.CakePhpModuleCustomizerExtender_error_source_invalid();
            return;
        }

        // validate
        CakePhpCustomizerValidator validator = new CakePhpCustomizerValidator()
                .validateCakePhpPath(sourceDirectory, panel.getCakePhpDirPath())
                .validateAppPath(sourceDirectory, panel.getAppDirectoryPath())
                .validateDotcakeFilePath(sourceDirectory, panel.getDotcakeFilePath());
        ValidationResult result = validator.getResult();
        if (result.hasWarnings()) {
            isValid = false;
            errorMessage = result.getWarnings().get(0).getMessage();
            return;
        }
        // no problem
        isValid = true;
        errorMessage = null;
    }
}
