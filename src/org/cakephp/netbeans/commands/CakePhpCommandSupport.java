package org.cakephp.netbeans.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import org.cakephp.netbeans.CakeScript;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram.InvalidPhpProgramException;
import org.netbeans.modules.php.spi.commands.FrameworkCommand;
import org.netbeans.modules.php.spi.commands.FrameworkCommandSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class CakePhpCommandSupport extends FrameworkCommandSupport{
	static final Logger LOGGER = Logger.getLogger(CakePhpCommandSupport.class.getName());

	public CakePhpCommandSupport(PhpModule phpModule){
		super(phpModule);
	}
	
	@Override
	public String getFrameworkName() {
		return NbBundle.getMessage(CakePhpCommandSupport.class, "MSG_CakePHP");
	}

	@Override
	public void runCommand(CommandDescriptor cd) {
		Callable<Process> callable = createCommand(cd.getFrameworkCommand().getCommands(), cd.getCommandParams());
		ExecutionDescriptor descriptor = getDescriptor();
		String displayName = getOutputTitle(cd);
		ExecutionService service = ExecutionService.newService(callable, descriptor, displayName);
		service.run();
	}

	@Override
	protected ExternalProcessBuilder getProcessBuilder(boolean warnUser) {
		ExternalProcessBuilder externalProcessBuilder = super.getProcessBuilder(warnUser);
		if (externalProcessBuilder == null) {
			return null;
		}
		CakeScript cakeScript = null;
		try {
			cakeScript = CakeScript.forPhpModule(phpModule);
		} catch (InvalidPhpProgramException ex) {
			NotifyDescriptor descriptor = new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
			DialogDisplayer.getDefault().notifyLater(descriptor);
			return null;
		}
		assert cakeScript.isValid();

		externalProcessBuilder = externalProcessBuilder.workingDirectory(FileUtil.toFile(phpModule.getSourceDirectory())).addArgument(cakeScript.getProgram());
		for (String param : cakeScript.getParameters()) {
			externalProcessBuilder = externalProcessBuilder.addArgument(param);
		}
		return externalProcessBuilder;
	}

	@Override
	protected String getOptionsPath() {
		return null;
	}

	@Override
	protected File getPluginsDirectory() {
		return null;
	}

	@Override
	protected List<FrameworkCommand> getFrameworkCommandsInternal() {
		// TODO Find more better way.
		FileObject fo = phpModule.getSourceDirectory().getFileObject("cake/console/libs");
		Enumeration<? extends FileObject> shells = null;
		if(fo != null){
			shells = fo.getChildren(false);
		}else{
			return null;
		}
		List<FrameworkCommand> commands = new ArrayList<FrameworkCommand>();
		if(shells != null){
			while(shells.hasMoreElements()){
				FileObject shell = shells.nextElement();
				if(!shell.getName().equals("shell") && !shell.isFolder()){
					commands.add(new CakePhpCommand(phpModule, shell.getName(), shell.getName(), shell.getName()));
				}
			}
		}
		return commands;
	}
	
}
