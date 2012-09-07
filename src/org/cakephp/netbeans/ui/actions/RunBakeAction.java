/*
 * TODO: add license
 */

package org.cakephp.netbeans.ui.actions;

import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.cakephp.netbeans.commands.CakeScript;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

public final class RunBakeAction extends BaseAction {
    private static final long serialVersionUID = 534878564698977L;
    private static final RunBakeAction INSTANCE = new RunBakeAction();

    private RunBakeAction() {
    }

    public static RunBakeAction getInstance() {
        return INSTANCE;
    }

    @Override
    public void actionPerformed(PhpModule phpModule) {
        if (!CakePhpFrameworkProvider.getInstance().isInPhpModule(phpModule)) {
            // called via shortcut
            return;
        }
        try {
            CakeScript.forPhpModule(phpModule, true).bake(phpModule);
        } catch (InvalidPhpExecutableException ex) {
            NotifyDescriptor descriptor = new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notifyLater(descriptor);
        }
    }

    @Override
    protected String getPureName() {
        return NbBundle.getMessage(RunBakeAction.class, "LBL_RunBake");
    }

    @Override
    protected String getFullName() {
        return NbBundle.getMessage(RunBakeAction.class, "LBL_CakePhpAction", getPureName());
    }
}
