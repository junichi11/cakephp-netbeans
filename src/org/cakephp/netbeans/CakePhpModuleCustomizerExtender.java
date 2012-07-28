/*
 * 
 */
package org.cakephp.netbeans;

import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.ui.customizer.CakePhpCustomizerPanel;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModule.Change;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleCustomizerExtender;
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

    CakePhpModuleCustomizerExtender(PhpModule phpModule) {
        appName = CakePreferences.getAppName(phpModule);
        originalAutoCreateState = CakePreferences.getAutoCreateView(phpModule);
        cakePhpDirPath = CakePreferences.getCakePhpDirPath(phpModule);
        isProjectDir = CakePreferences.useProjectDirectory(phpModule);
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
        String newAppName = getPanel().getAppNameField().getText();
        boolean newAutoCreateState = getPanel().isAutoCreateView();
        String newCakePhpDirPath = getPanel().getCakePhpDirTextField();
        if (newAutoCreateState != originalAutoCreateState) {
            CakePreferences.setAutoCreateView(phpModule, newAutoCreateState);
        }
        if (isProjectDir != getPanel().isUseProjectDirectory()) {
            CakePreferences.setUseProjectDirectory(phpModule, !isProjectDir);
        }
        if (!cakePhpDirPath.equals(newCakePhpDirPath)) {
            CakePreferences.setCakePhpDirPath(phpModule, newCakePhpDirPath);
        }
        if (!newAppName.equals(appName) && !newAppName.equals("")) { // NOI18N
            CakePreferences.setAppName(phpModule, newAppName);
            return EnumSet.of(Change.SOURCES_CHANGE);
        }
        return null;
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
        }
        return component;
    }
}
