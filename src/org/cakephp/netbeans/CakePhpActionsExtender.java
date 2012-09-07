/*
 * TODO: add license
 */
package org.cakephp.netbeans;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.cakephp.netbeans.ui.actions.*;
import org.cakephp.netbeans.ui.wizards.InstallPluginsWizardAction;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.actions.GoToActionAction;
import org.netbeans.modules.php.spi.framework.actions.GoToViewAction;
import org.netbeans.modules.php.spi.framework.actions.RunCommandAction;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public class CakePhpActionsExtender extends PhpModuleActionsExtender {

    @Override
    public String getMenuName() {
        return NbBundle.getMessage(CakePhpActionsExtender.class, "LBL_MenuName");
    }

    @Override
    public List<? extends Action> getActions() {
        List<Action> list = new ArrayList<Action>();
        list.add(ClearCacheAction.getInstance());
        list.add(InstallPluginsWizardAction.getInstance());
        list.add(CreateAutoCompletionFileAction.getInstance());
        return list;
    }

    @Override
    public RunCommandAction getRunCommandAction() {
        return CakePhpRunCommandAction.getInstance();
    }

    @Override
    public boolean isViewWithAction(FileObject fo) {
        return CakePhpUtils.isView(fo);
    }

    @Override
    public boolean isActionWithView(FileObject fo) {
        return CakePhpUtils.isController(fo);
    }

    @Override
    public GoToActionAction getGoToActionAction(FileObject fo, int offset) {
        return new CakePhpGoToActionAction(fo);
    }

    @Override
    public GoToViewAction getGoToViewAction(FileObject fo, int offset) {
        return new CakePhpGoToViewAction(fo, offset);
    }
}
