package org.cakephp.netbeans.commands;

import java.lang.ref.WeakReference;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommand;

/**
 *
 * @author junichi11
 */
public class CakePhpCommand extends FrameworkCommand {

    private final WeakReference<PhpModule> phpModule;

    public CakePhpCommand(PhpModule phpModule, String command, String description, String displayName) {
        super(command, description, displayName);
        assert phpModule != null;
        this.phpModule = new WeakReference<PhpModule>(phpModule);
    }

    public CakePhpCommand(PhpModule phpModule, String[] command, String description, String displayName) {
        super(command, description, displayName);
        assert phpModule != null;
        this.phpModule = new WeakReference<PhpModule>(phpModule);
    }

    @Override
    protected String getHelpInternal() {
        PhpModule module = phpModule.get();
        if (module == null) {
            return ""; // NOI18N
        }
        try {
            return CakeScript.forPhpModule(module, false).getHelp(module, getCommands());
        } catch (InvalidPhpExecutableException ex) {
            UiUtils.invalidScriptProvided(ex.getLocalizedMessage(), CakeScript.OPTIONS_SUB_PATH);
        }
        return ""; // NOI18N
    }

}
