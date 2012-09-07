/*
 * TODO: add license
 */
package org.cakephp.netbeans.ui.actions;

import java.io.IOException;
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.CakeVersion;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Create a file for auto code complete Action
 *
 * @author junichi11
 */
public final class CreateAutoCompletionFileAction extends BaseAction {

    private static final CreateAutoCompletionFileAction INSTANCE = new CreateAutoCompletionFileAction();
    private static final long serialVersionUID = -6029721403470166137L;

    private CreateAutoCompletionFileAction() {
    }

    public static CreateAutoCompletionFileAction getInstance() {
        return INSTANCE;
    }

    /**
     * action performed (support only CakePHP 2.x)
     *
     * @param phpModule
     */
    @Override
    public void actionPerformed(PhpModule phpModule) {
        if (!CakePhpFrameworkProvider.getInstance().isInPhpModule(phpModule)
            || CakeVersion.getInstance(phpModule).isCakePhp(1)) {
            // called via shortcut
            return;
        }
        try {
            if (!CakePhpUtils.createAutoCompletionFile()) {
                NotifyDescriptor descriptor = new NotifyDescriptor.Message(NbBundle.getMessage(CreateAutoCompletionFileAction.class, "MSG_Failure"), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notifyLater(descriptor);
            }
        } catch (IOException ex) {
            NotifyDescriptor descriptor = new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notifyLater(descriptor);
        }
    }

    @Override
    protected String getPureName() {
        return NbBundle.getMessage(CreateAutoCompletionFileAction.class, "LBL_CreateAutoCompletionFile"); // NOI18N
    }

    @Override
    protected String getFullName() {
        return NbBundle.getMessage(CreateAutoCompletionFileAction.class, "LBL_CakePhpAction", getPureName()); // NOI18N
    }
}
