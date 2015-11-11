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
package org.cakephp.netbeans.commands;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.base.input.InputProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessors;
import org.netbeans.api.extexecution.base.input.LineProcessor;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.executable.PhpExecutableValidator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommand;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.xml.sax.SAXException;

public final class CakeScript {

    static final Logger LOGGER = Logger.getLogger(CakeScript.class.getName());
    public static final String OPTIONS_SUB_PATH = "CakePhp"; // NOI18N
    public static final String SCRIPT_NAME = "cake"; // NOI18N
    public static final String SCRIPT_NAME_LONG = SCRIPT_NAME + ".php"; // NOI18N

    // commands
    private static final String BAKE_COMMAND = "bake"; // NOI18N
    private static final String LIST_COMMAND = "command_list"; // NOI18N
    private static final String PROJECT_COMMAND = "project"; // NOI18N
    private static final String TEST_COMMAND = "test"; // NOI18N

    // params
    private static final String HELP_PARAM = "--help"; // NOI18N
    private static final String EMPTY_PARAM = "--empty"; // NOI18N
    private static final String XML_PARAM = "--xml"; // NOI18N
    private static final String PLUGIN_PARAM = "--plugin"; // NOI18N
    private static final String APP_PARAM = "-app";

    private static final String UTF8 = "UTF-8"; // NOI18N
    private static final List<String> LIST_XML_COMMAND = Arrays.asList(LIST_COMMAND, XML_PARAM);
    // XXX any default params?
    private static final List<String> DEFAULT_PARAMS = Collections.emptyList();
    private static final String CORE_SHELLS_DIRECTORY = "cake/console/libs"; // NOI18N
    private static final String VENDORS_SHELLS_DIRECTORY = "vendors/shells"; // NOI18N
    private final String cakePath;
    private final List<String> appParams = new ArrayList<>();

    private CakeScript(String cakePath) {
        this.cakePath = cakePath;
    }

    /**
     * Get the project specific, <b>valid only</b> Cake script. If not found,
     * {@code null} is returned.
     *
     * @param phpModule PHP module for which Cake script is taken
     * @param warn <code>true</code> if user is warned when the Cake script is
     * not valid
     * @return Cake console script or {@code null} if the script is not valid
     */
    @NbBundle.Messages({
        "# {0} - error message",
        "CakeScript.script.invalid=<html>Project''s Cake script is not valid.<br>({0})"
    })
    public static CakeScript forPhpModule(PhpModule phpModule, boolean warn) throws InvalidPhpExecutableException {
        String console = null;
        FileObject script = getPath(phpModule);
        if (script != null) {
            console = FileUtil.toFile(script).getAbsolutePath();
        }
        String error = validate(console);
        if (error == null) {
            return new CakeScript(console);
        }
        if (warn) {
            NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                    Bundle.CakeScript_script_invalid(error),
                    NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(message);
        }
        throw new InvalidPhpExecutableException(error);
    }

    /**
     * Get cake script for installing with composer.
     *
     * @param phpModule
     * @return cake script
     * @throws InvalidPhpExecutableException
     */
    public static CakeScript forComposer(PhpModule phpModule) throws InvalidPhpExecutableException {
        String console;
        String scriptPath = "Vendor/bin/cake.php"; // NOI18N
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        String error = null;
        if (sourceDirectory != null) {
            FileObject cake = sourceDirectory.getFileObject(scriptPath);
            // #121
            if (cake == null) {
                cake = sourceDirectory.getFileObject("Vendor/cakephp/cakephp/lib/Cake/Console/cake.php"); // NOI18N
            }
            if (cake != null) {
                console = FileUtil.toFile(cake).getAbsolutePath();
                error = validate(console);
                if (error == null) {
                    return new CakeScript(console);
                }
            } else {
                error = "Not Found: cake.php script"; // NOI18N
            }
        }
        throw new InvalidPhpExecutableException(error);
    }

    private static FileObject getPath(PhpModule phpModule) {
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module == null) {
            return null;
        }
        FileObject consoleDirectory = module.getConsoleDirectory(CakePhpModule.DIR_TYPE.APP);
        if (consoleDirectory == null) {
            LOGGER.log(Level.WARNING, "Not found " + SCRIPT_NAME); // NOI18N
            return null;
        }
        return consoleDirectory.getFileObject(SCRIPT_NAME_LONG);
    }

    @NbBundle.Messages("CakeScript.script.label=Cake script")
    public static String validate(String command) {
        return PhpExecutableValidator.validateCommand(command, Bundle.CakeScript_script_label());
    }

    public static String getOptionsPath() {
        return UiUtils.OPTIONS_PATH + "/" + OPTIONS_SUB_PATH; // NOI18N
    }

    public void runCommand(PhpModule phpModule, List<String> parameters, Runnable postExecution) {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return;
        }
        appParams.addAll(getAppParam(phpModule));
        createPhpExecutable(phpModule)
                .displayName(getDisplayName(phpModule, parameters.get(0)))
                .additionalParameters(getAllParams(parameters))
                .run(getDescriptor(postExecution));
    }

    public String getHelp(PhpModule phpModule, String[] params) {
        assert phpModule != null;

        List<String> allParams = new ArrayList<>();
        // #116
        allParams.addAll(getAppParam(phpModule));
        allParams.addAll(Arrays.asList(params));
        allParams.add(HELP_PARAM);

        HelpLineProcessor lineProcessor = new HelpLineProcessor();
        Future<Integer> result = createPhpExecutable(phpModule)
                .displayName(getDisplayName(phpModule, allParams.get(0)))
                .additionalParameters(getAllParams(allParams))
                .run(getSilentDescriptor(), getOutProcessorFactory(lineProcessor));
        try {
            if (result != null) {
                result.get();
            }
        } catch (CancellationException ex) {
            // canceled
        } catch (ExecutionException ex) {
            UiUtils.processExecutionException(ex, OPTIONS_SUB_PATH);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return lineProcessor.getHelp();
    }

    public List<FrameworkCommand> getCommands(PhpModule phpModule) {
        List<FrameworkCommand> freshCommands = getFrameworkCommandsInternalXml(phpModule);
        if (freshCommands != null) {
            return freshCommands;
        }
        freshCommands = getFrameworkCommandsInternalConsole(phpModule);
        if (freshCommands != null) {
            return freshCommands;
        }
        // XXX some error => rerun command with console
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule != null && cakeModule.isCakePhp(2)) {
            runCommand(phpModule, Collections.singletonList(LIST_COMMAND), null);
        }
        return null;
    }

    public void bake(PhpModule phpModule) {
        runCommand(phpModule, Collections.singletonList(BAKE_COMMAND), null);
    }

    public void bakeTest(PhpModule phpModule, CakePhpModule.FILE_TYPE fileType, String className, String pluginName) {
        List<String> params = new ArrayList<>();
        params.add(BAKE_COMMAND);
        params.add(TEST_COMMAND);
        if (pluginName != null && !pluginName.isEmpty()) {
            params.add(PLUGIN_PARAM);
            params.add(pluginName);
        }
        params.add(fileType.toString().toLowerCase(Locale.ENGLISH));
        params.add(className);
        runCommand(phpModule, params, null);
    }

    /**
     * Bake project for installing with composer. If app name is empty, app
     * directory is source directory.
     *
     * @param phpModule
     * @param name app name
     * @param isEmpty --empty option
     * @param postExecution
     * @return
     */
    public Future<Integer> bakeProject(PhpModule phpModule, String name, boolean isEmpty, Runnable postExecution) {
        try {
            Reader reader = getReaderForBakeProject();
            FileObject sourceDirectory = phpModule.getSourceDirectory();
            File root = FileUtil.toFile(sourceDirectory);
            File app = new File(root, name);
            String appPath = (Utilities.isWindows() ? "/" : "") + app.getAbsolutePath(); // NOI18N
            ArrayList<String> parameters = new ArrayList<>();
            parameters.add(BAKE_COMMAND);
            parameters.add(PROJECT_COMMAND);
            if (isEmpty) {
                parameters.add(EMPTY_PARAM);
            }
            parameters.add(appPath);
            return new PhpExecutable(cakePath)
                    .workDir(root)
                    .displayName(getDisplayName(phpModule, "bake project")) // NOI18N
                    .additionalParameters(getAllParams(parameters))
                    .run(getDescriptor(postExecution).inputOutput(new CakePhpInputOutput(reader)));
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    /**
     * Get reader as standard input. This is used only for bake project.
     *
     * @return reader
     * @throws UnsupportedEncodingException
     */
    private Reader getReaderForBakeProject() throws UnsupportedEncodingException {
        String yes = "y\n"; // NOI18N
        InputStream in = new ByteArrayInputStream(yes.getBytes(UTF8));
        return new InputStreamReader(in, UTF8);
    }

    private PhpExecutable createPhpExecutable(PhpModule phpModule) {
        return new PhpExecutable(cakePath)
                .workDir(FileUtil.toFile(CakePhpModule.getCakePhpDirectory(phpModule)));
    }

    private List<String> getAllParams(List<String> params) {
        List<String> allParams = new ArrayList<>();
        allParams.addAll(DEFAULT_PARAMS);
        allParams.addAll(appParams);
        allParams.addAll(params);
        return allParams;
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "# {1} - command",
        "CakeScript.command.title={0} ({1})"
    })
    private String getDisplayName(PhpModule phpModule, String command) {
        return Bundle.CakeScript_command_title(phpModule.getDisplayName(), command);
    }

    private ExecutionDescriptor getDescriptor(Runnable postExecution) {
        ExecutionDescriptor executionDescriptor = PhpExecutable.DEFAULT_EXECUTION_DESCRIPTOR
                .optionsPath(OPTIONS_SUB_PATH);
        if (postExecution != null) {
            executionDescriptor = executionDescriptor.postExecution(postExecution);
        }
        return executionDescriptor;
    }

    private ExecutionDescriptor.InputProcessorFactory2 getOutProcessorFactory(final LineProcessor lineProcessor) {
        return new ExecutionDescriptor.InputProcessorFactory2() {
            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                return InputProcessors.ansiStripping(InputProcessors.bridge(lineProcessor));
            }
        };
    }

    private ExecutionDescriptor getSilentDescriptor() {
        return new ExecutionDescriptor()
                .inputOutput(InputOutput.NULL);
    }

    @NbBundle.Messages({
        "CakeScript.redirect.xml.error=error is occurred when xml file is created for command list."
    })
    private List<FrameworkCommand> getFrameworkCommandsInternalXml(PhpModule phpModule) {
        File tmpFile;
        try {
            tmpFile = File.createTempFile("nb-cake-commands-", ".xml"); // NOI18N
            tmpFile.deleteOnExit();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            return null;
        }

        // #116
        List<String> appParam = getAppParam(phpModule);

        ArrayList<String> listXmlParams = new ArrayList<>();
        listXmlParams.addAll(appParam);
        listXmlParams.addAll(LIST_XML_COMMAND);
        if (!redirectToFile(phpModule, tmpFile, listXmlParams)) {
            LOGGER.log(Level.WARNING, Bundle.CakeScript_redirect_xml_error());
            return null;
        }
        List<CakeCommandItem> commandsItem = new ArrayList<>();
        try {
            CakePhpCommandXmlParser.parse(tmpFile, commandsItem);
        } catch (SAXException ex) {
            // incorrect xml provided by cakephp?
            LOGGER.log(Level.INFO, null, ex);
        }
        if (commandsItem.isEmpty()) {
            // error
            tmpFile.delete();
            return null;
        }
        // parse each command
        List<FrameworkCommand> commands = new ArrayList<>();
        for (CakeCommandItem item : commandsItem) {
            ArrayList<String> commandParams = new ArrayList<>();
            commandParams.addAll(appParam);
            commandParams.addAll(Arrays.asList(item.getCommand(), HELP_PARAM, "xml")); // NOI18N
            if (!redirectToFile(phpModule, tmpFile, commandParams)) {
                commands.add(new CakePhpCommand(phpModule,
                        item.getCommand(), item.getDescription(), item.getDisplayName()));
                continue;
            }
            List<CakeCommandItem> mainCommandsItem = new ArrayList<>();
            try {
                CakePhpCommandXmlParser.parse(tmpFile, mainCommandsItem);
            } catch (SAXException ex) {
                LOGGER.log(Level.WARNING, "Xml file Error:{0}", ex.getMessage()); // NOI18N
                commands.add(new CakePhpCommand(phpModule,
                        item.getCommand(), item.getDescription(), item.getDisplayName()));
                continue;
            }
            if (mainCommandsItem.isEmpty()) {
                tmpFile.delete();
                return null;
            }
            // add main command
            CakeCommandItem main = mainCommandsItem.get(0);
            String mainCommand = main.getCommand();
            String provider = item.getDescription();
            commands.add(new CakePhpCommand(phpModule,
                    mainCommand,
                    "[" + provider + "] " + main.getDescription(), // NOI18N
                    main.getDisplayName()));

            // add subcommands
            List<CakeCommandItem> subcommands = main.getSubcommands();
            if (subcommands == null) {
                continue;
            }
            for (CakeCommandItem subcommand : subcommands) {
                String[] command = {mainCommand, subcommand.getCommand()};
                commands.add(new CakePhpCommand(phpModule,
                        command,
                        "[" + provider + "] " + subcommand.getDescription(), // NOI18N
                        main.getCommand() + " " + subcommand.getDisplayName())); // NOI18N
            }
        }
        tmpFile.delete();
        return commands;
    }

    private List<String> getAppParam(PhpModule phpModule) {
        ArrayList<String> appParam = new ArrayList<>();
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return Collections.emptyList();
        }
        FileObject app = cakeModule.getDirectory(CakePhpModule.DIR_TYPE.APP);
        if (app != null) {
            appParam.add(APP_PARAM);
            appParam.add(app.getPath());
        }
        return appParam;
    }

    @NbBundle.Messages({
        "# {0} - exitValue",
        "CakeScript.redirect.error=exitValue:{0} There may be some errors when redirect command result to file"
    })
    private boolean redirectToFile(PhpModule phpModule, File file, List<String> commands) {
        Future<Integer> result = createPhpExecutable(phpModule)
                .fileOutput(file, UTF8, true)
                .warnUser(false)
                .additionalParameters(commands)
                .run(getSilentDescriptor());
        try {
            if (result == null) {
                // error
                return false;
            }
            // CakePHP 3.x uses exit() in cake script, so, return value is not 0
            Integer exitValue = result.get();
            if (exitValue != 0) {
                if (exitValue != 1) {
                    LOGGER.log(Level.WARNING, Bundle.CakeScript_redirect_error(exitValue));
                    return false;
                }
            }
        } catch (CancellationException | ExecutionException ex) {
            // canceled
            return false;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }

        return true;
    }

    private List<FrameworkCommand> getFrameworkCommandsInternalConsole(PhpModule phpModule) {
        // cakephp1.3+
        List<FrameworkCommand> commands = new ArrayList<>();
        List<FileObject> shellDirs = new ArrayList<>();
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return commands;
        }
        String[] shells = {CORE_SHELLS_DIRECTORY, VENDORS_SHELLS_DIRECTORY, cakeModule.getAppName() + "/" + VENDORS_SHELLS_DIRECTORY};

        FileObject cakePhpDirectory = CakePhpModule.getCakePhpDirectory(phpModule);
        if (cakePhpDirectory != null) {
            for (String shell : shells) {
                FileObject shellFileObject = cakePhpDirectory.getFileObject(shell);
                if (shellFileObject != null) {
                    shellDirs.add(shellFileObject);
                }
            }
        }

        for (FileObject shellDir : shellDirs) {
            Enumeration<? extends FileObject> shellFiles;
            if (shellDir != null) {
                shellFiles = shellDir.getChildren(false);
            } else {
                return null;
            }
            if (shellFiles != null) {
                while (shellFiles.hasMoreElements()) {
                    FileObject shell = shellFiles.nextElement();
                    if (!shell.getName().equals("shell") && !shell.isFolder()) { // NOI18N
                        commands.add(new CakePhpCommand(
                                phpModule,
                                shell.getName(),
                                "[" + getShellsPlace(phpModule, shellDir) + "]", // NOI18N
                                shell.getName()));
                    }
                }
            }
        }
        return commands;
    }

    private String getShellsPlace(PhpModule phpModule, FileObject shellDir) {
        String place = ""; // NOI18N
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        FileObject source = CakePhpModule.getCakePhpDirectory(phpModule);
        if (cakeModule != null && source != null) {
            String app = cakeModule.getAppName();
            if (source.getFileObject(CORE_SHELLS_DIRECTORY) == shellDir) {
                place = "CORE"; // NOI18N
            } else if (source.getFileObject(app + "/" + VENDORS_SHELLS_DIRECTORY) == shellDir) {
                place = "APP VENDOR"; // NOI18N
            } else if (source.getFileObject(VENDORS_SHELLS_DIRECTORY) == shellDir) {
                place = "VENDOR"; // NOI18N
            }
        }
        return place;
    }

    //~ Inner classes
    private static class HelpLineProcessor implements LineProcessor {

        private final StringBuilder sb = new StringBuilder();

        @Override
        public void processLine(String line) {
            sb.append(line);
            sb.append("\n"); // NOI18N
        }

        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }

        public String getHelp() {
            return sb.toString();
        }
    }

    private static final class CakePhpInputOutput implements InputOutput {

        private final InputOutput io;
        private Reader in;

        public CakePhpInputOutput(Reader in) {
            io = IOProvider.getDefault().getIO("cakephp", false); // NOI18N
            this.in = in;
        }

        @Override
        public OutputWriter getOut() {
            return io.getOut();
        }

        @Override
        public Reader getIn() {
            if (in == null) {
                return io.getIn();
            }
            return in;
        }

        @Override
        public OutputWriter getErr() {
            return io.getErr();
        }

        @Override
        public void closeInputOutput() {
            io.closeInputOutput();
        }

        @Override
        public boolean isClosed() {
            return io.isClosed();
        }

        @Override
        public void setOutputVisible(boolean value) {
            io.setOutputVisible(value);
        }

        @Override
        public void setErrVisible(boolean value) {
            io.setErrVisible(value);
        }

        @Override
        public void setInputVisible(boolean value) {
            io.setInputVisible(value);
        }

        @Override
        public void select() {
            io.select();
        }

        @Override
        public boolean isErrSeparated() {
            return io.isErrSeparated();
        }

        @Override
        public void setErrSeparated(boolean value) {
            io.setErrSeparated(value);
        }

        @Override
        public boolean isFocusTaken() {
            return io.isFocusTaken();
        }

        @Override
        public void setFocusTaken(boolean value) {
            io.setFocusTaken(value);
        }

        @Override
        public Reader flushReader() {
            return getIn();
        }

        public void setIn(Reader in) {
            this.in = in;
        }
    }
}
