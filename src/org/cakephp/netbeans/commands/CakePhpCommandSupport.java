package org.cakephp.netbeans.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommand;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport.CommandDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class CakePhpCommandSupport extends FrameworkCommandSupport {

    public CakePhpCommandSupport(PhpModule phpModule) {
        super(phpModule);
    }

    @NbBundle.Messages("CakePhpCommandSupport.name=CakePHP")
    @Override
    public String getFrameworkName() {
        return Bundle.CakePhpCommandSupport_name();
    }

    @Override
    public void runCommand(CommandDescriptor commandDescriptor, Runnable postExecution) {
        String[] commands = commandDescriptor.getFrameworkCommand().getCommands();
        String[] commandParams = commandDescriptor.getCommandParams();
        List<String> params = new ArrayList<String>(commands.length + commandParams.length);
        params.addAll(Arrays.asList(commands));
        params.addAll(Arrays.asList(commandParams));
        try {
            CakeScript.forPhpModule(phpModule, false).runCommand(phpModule, params, postExecution);
        } catch (InvalidPhpExecutableException ex) {
            UiUtils.invalidScriptProvided(ex.getLocalizedMessage(), CakeScript.OPTIONS_SUB_PATH);
        }
    }

    @Override
    protected String getOptionsPath() {
        return CakeScript.getOptionsPath();
    }

    @Override
    protected File getPluginsDirectory() {
        return null;
    }

    @Override
    protected List<FrameworkCommand> getFrameworkCommandsInternal() {
        try {
            return CakeScript.forPhpModule(phpModule, true).getCommands(phpModule);
        } catch (InvalidPhpExecutableException ex) {
            UiUtils.invalidScriptProvided(ex.getLocalizedMessage(), CakeScript.OPTIONS_SUB_PATH);
        }
        return null;
    }

}
