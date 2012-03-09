package org.cakephp.netbeans.commands;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.cakephp.netbeans.CakeScript;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram.InvalidPhpProgramException;
import org.netbeans.modules.php.spi.commands.FrameworkCommand;
import org.netbeans.modules.php.spi.commands.FrameworkCommandSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.InputOutput;
import org.xml.sax.SAXException;

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

		externalProcessBuilder = externalProcessBuilder.workingDirectory(FileUtil.toFile(CakePhpFrameworkProvider.getCakePhpDirectory(phpModule))).addArgument(cakeScript.getProgram());
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
		// TODO Find a better way.
		// Create commands from xml
		List<FrameworkCommand> commands;
		// cakephp2
		commands = getFrameworkCommandsInternalXml();
		if (commands != null) {
			return commands;
		}

		// cakephp1.3+
		commands = new ArrayList<FrameworkCommand>();
		List<FileObject> shellDirs = new ArrayList<FileObject>();
		for(String shell : shells){
			FileObject shellFileObject = CakePhpFrameworkProvider.getCakePhpDirectory(phpModule).getFileObject(shell);
			if(shellFileObject != null){
				shellDirs.add(shellFileObject);
			}
		}
		
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
	
	private List<FrameworkCommand> getFrameworkCommandsInternalXml(){
		File output = getRedirectOutput("command_list", "--xml");// NOI18N
		if(output == null){
			return null;
		}
		List<CakeCommandItem> commandsItem = new ArrayList<CakeCommandItem>();
		try {
			CakePhpCommandXmlParser.parse(output, commandsItem);
		} catch (SAXException ex) {
			LOGGER.log(Level.WARNING, "Xml file Error:{0}", ex.getMessage());
		}
		if(commandsItem.isEmpty()){
			return null;
		}
		List<FrameworkCommand> commands = new ArrayList<FrameworkCommand>();
		for(CakeCommandItem item : commandsItem){
			File commandXml = getRedirectOutput(item.getCommand(), "--help", "xml"); // NOI18N
			if(commandXml == null){
				commands.add(new CakePhpCommand(phpModule,
					item.getCommand(), item.getDescription(), item.getDisplayName()));
				continue;
			}
			List<CakeCommandItem> mainCommandsItem = new ArrayList<CakeCommandItem>();
			try {
				CakePhpCommandXmlParser.parse(commandXml, mainCommandsItem);
			} catch (SAXException ex) {
				LOGGER.log(Level.WARNING, "Xml file Error:{0}", ex.getMessage());
				commands.add(new CakePhpCommand(phpModule,
					item.getCommand(), item.getDescription(), item.getDisplayName()));
				continue;
			}
			if (mainCommandsItem.isEmpty()) {
				return null;
			}
			// add main command
			CakeCommandItem main = mainCommandsItem.get(0);
			String mainCommand = main.getCommand();
			String provider = item.getDescription();
			if(!provider.equals("CORE") && !provider.matches("^[a-z0-9-_]+")){
				mainCommand = provider + "." + mainCommand;
			}
			commands.add(new CakePhpCommand(phpModule,
				mainCommand, "[" + provider + "] " + main.getDescription(), main.getDisplayName())); // NOI18N

			// add subcommands
			List<CakeCommandItem> subcommands = main.getSubcommands();
			if(subcommands == null){
				continue;
			}
			for(CakeCommandItem subcommand : subcommands){
				String[] command = {mainCommand, subcommand.getCommand()};
				commands.add(new CakePhpCommand(phpModule,
					command, "[" + provider + "] " + subcommand.getDescription(), main.getCommand() + " " + subcommand.getDisplayName()));// NOI18N
			}
		}
		return commands;
	}
	
	public File getRedirectOutput(String command, String... param) {
		// No error dialog is displayed
		ExternalProcessBuilder processBuilder = createSilentCommand(command, param);
		if (processBuilder == null) {
			return null;
		}
		File output = null;
		try {
			final RedirectInputProcessor inputProcessor = new RedirectInputProcessor();
			ExecutionDescriptor descriptor = new ExecutionDescriptor().inputOutput(InputOutput.NULL).outProcessorFactory(new ExecutionDescriptor.InputProcessorFactory() {
				@Override
				public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
					return inputProcessor;
				}
			});
			ExecutionService service = ExecutionService.newService(processBuilder, descriptor, "redirect output"); // NOI18N
			// result of the Future is exit code of the process
			Future<Integer> task = service.run();
			try {
				if(task.get().intValue() == 0){
					output = inputProcessor.getOutput();
				}
			} catch (InterruptedException ex) {
				Exceptions.printStackTrace(ex);
			} catch (ExecutionException ex) {
				Exceptions.printStackTrace(ex);
			}
		} catch (IOException ex) {
			Exceptions.printStackTrace(ex);
		}
		return output;
	}
	
	private String getShellsPlace(FileObject shellDir){
		String place = ""; // NOI18N
		FileObject source = CakePhpFrameworkProvider.getCakePhpDirectory(phpModule);
		if(source.getFileObject(CORE_SHELLS_DIRECTORY) == shellDir){
			place = "CORE"; // NOI18N
		}else if (source.getFileObject(APP_VENDORS_SHELLS_DIRECTORY) == shellDir){
			place = "APP VENDOR"; // NOI18N
		}else if (source.getFileObject(VENDORS_SHELLS_DIRECTORY) == shellDir){
			place = "VENDOR"; // NOI18N
		}
		return place;
	}
	
	private class RedirectInputProcessor implements InputProcessor{
		private File output;
		private FileOutputStream outputStream;
		private BufferedOutputStream buffer;
		
		public RedirectInputProcessor() throws IOException{
			output = File.createTempFile("xml_output", ".tmp"); // NOI18N
			outputStream = new FileOutputStream(output);
			buffer = new BufferedOutputStream(outputStream);
			
			output.deleteOnExit();
		}
		
		@Override
		public void processInput(char[] chars) throws IOException {
			for(char c : chars){
				buffer.write((byte)c);
			}
		}

		@Override
		public void reset() throws IOException {
//			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void close() throws IOException {
			buffer.close();
			outputStream.close();
		}
		
		public File getOutput(){
			return output;
		}
	}
}
