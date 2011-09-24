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
	private static final String CORE_SHELLS_DIRECTORY = "cake/console/libs"; // NOI18N
	private static final String VENDORS_SHELLS_DIRECTORY = "vendors/shells"; // NOI18N
	private static final String APP_VENDORS_SHELLS_DIRECTORY = "app/vendors/shells"; // NOI18N
	private static final String[] shells = {CORE_SHELLS_DIRECTORY, VENDORS_SHELLS_DIRECTORY, APP_VENDORS_SHELLS_DIRECTORY}; // NOI18N

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
		List<FileObject> shellDirs = new ArrayList<FileObject>();
		for(String shell : shells){
			FileObject shellFileObject = phpModule.getSourceDirectory().getFileObject(shell);
			if(shellFileObject != null){
				shellDirs.add(shellFileObject);
			}
		}
		List<FrameworkCommand> commands = new ArrayList<FrameworkCommand>();
		
		for (FileObject shellDir : shellDirs) {
			Enumeration<? extends FileObject> shellFiles = null;
			if (shellDir != null) {
				shellFiles = shellDir.getChildren(false);
			} else {
				return null;
			}
			if (shellFiles != null) {
				while (shellFiles.hasMoreElements()) {
					FileObject shell = shellFiles.nextElement();
					if (!shell.getName().equals("shell") && !shell.isFolder()) { // NOI18N
						commands.add(new CakePhpCommand(phpModule, shell.getName(), "[" + getShellsPlace(shellDir) + "]", shell.getName())); // NOI18N
					}
				}
			}
		}
		return commands;
	}
	
	private String getShellsPlace(FileObject shellDir){
		String place = "";
		FileObject source = phpModule.getSourceDirectory();
		if(source.getFileObject(CORE_SHELLS_DIRECTORY) == shellDir){
			place = "CORE";
		}else if (source.getFileObject(APP_VENDORS_SHELLS_DIRECTORY) == shellDir){
			place = "APP VENDOR";
		}else if (source.getFileObject(VENDORS_SHELLS_DIRECTORY) == shellDir){
			place = "VENDOR";
		}
		return place;
	}
	
}
