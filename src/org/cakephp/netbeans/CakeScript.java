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
package org.cakephp.netbeans;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.input.InputProcessors;
import org.netbeans.api.extexecution.input.LineProcessor;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.spi.commands.FrameworkCommand;
import org.netbeans.modules.php.spi.commands.FrameworkCommandSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.InputOutput;

public class CakeScript extends PhpProgram {

    public static final String SCRIPT_NAME = "cake"; // NOI18N
    public static final String SCRIPT_NAME_LONG = SCRIPT_NAME + ".php"; // NOI18N
    private static final String CMD_BAKE = "bake"; // NOI18N
    private static final String TEST = "test";
    private final PhpModule phpModule;

    private CakeScript(String command) {
        this(command, null);
    }

    private CakeScript(String command, PhpModule phpModule) {
        super(command);
        this.phpModule = phpModule;
    }

    /**
     * Get the project specific, <b>valid only</b> Cake script. If not found,
     * the {@link InvalidPhpProgramException} is thrown.
     *
     * @param phpModule PHP module for which Cake script is taken
     * @return the project specific, <b>valid only</b> Cake script
     * @throws InvalidPhpProgramException if Zend script is not valid or missing
     * completely
     */
    public static CakeScript forPhpModule(PhpModule phpModule) throws InvalidPhpProgramException {
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        FileObject consoleDirectory = module.getConsoleDirectory(DIR_TYPE.APP);
        if (consoleDirectory == null) {
            LOGGER.log(Level.WARNING, "Not found " + SCRIPT_NAME);
            throw new InvalidPhpProgramException(NbBundle.getMessage(CakeScript.class, "MSG_CakeNotFound"));
        }
        FileObject cake = consoleDirectory.getFileObject(SCRIPT_NAME_LONG);

        if (cake == null) {
            throw new InvalidPhpProgramException(NbBundle.getMessage(CakeScript.class, "MSG_CakeNotFound"));
        }

        // validate
        String cakePath = FileUtil.toFile(cake).getAbsolutePath();
        String error = validate(cakePath);
        if (error != null) {
            throw new InvalidPhpProgramException(error);
        }
        return new CakeScript(cakePath, phpModule);
    }

    @Override
    public String validate() {
        // TODO: WARNING: perhaps no reliable way to find out whether the cake command is executable (INFO message to IDE log is written)
        return FileUtils.validateScript(getProgram(), NbBundle.getMessage(CakeScript.class, "LBL_CakeTool"));
    }

    public static String validate(String command) {
        return new CakeScript(command).validate();
    }

    // TODO: later, run it via FrameworkCommandSupport
    public void runBake() {
        ExecutionDescriptor executionDescriptor = getExecutionDescriptor()
                .outProcessorFactory(ANSI_STRIPPING_FACTORY)
                .errProcessorFactory(ANSI_STRIPPING_FACTORY);

        ExternalProcessBuilder processBuilder = getProcessBuilder()
                .addArgument(CMD_BAKE);
        assert phpModule != null;
        if (phpModule != null) {
            processBuilder = processBuilder
                    .workingDirectory(FileUtil.toFile(CakePhpModule.getCakePhpDirectory(phpModule)));
        }
        executeLater(processBuilder, executionDescriptor, CMD_BAKE);
    }

    public void runBakeTest(FILE_TYPE fileType, String className, String pluginName) {
        ExecutionDescriptor executionDescriptor = getExecutionDescriptor()
                .outProcessorFactory(ANSI_STRIPPING_FACTORY)
                .errProcessorFactory(ANSI_STRIPPING_FACTORY);
        FrameworkCommandSupport commandSupport = CakePhpFrameworkProvider.getInstance().getFrameworkCommandSupport(phpModule);
        ExternalProcessBuilder processBuilder;
        if (pluginName != null && !pluginName.isEmpty()) {
            processBuilder = commandSupport.createSilentCommand(CMD_BAKE, TEST, "--plugin", pluginName, fileType.toString().toLowerCase(), className);
        } else {
            processBuilder = commandSupport.createSilentCommand(CMD_BAKE, TEST, fileType.toString().toLowerCase(), className);
        }
        assert phpModule != null;
        if (phpModule != null) {
            processBuilder = processBuilder
                    .workingDirectory(FileUtil.toFile(CakePhpModule.getCakePhpDirectory(phpModule)));
        }
        executeLater(processBuilder, executionDescriptor, CMD_BAKE + "" + TEST);
    }

    public static String getHelp(PhpModule phpModule, FrameworkCommand command) {
        FrameworkCommandSupport commandSupport = CakePhpFrameworkProvider.getInstance().getFrameworkCommandSupport(phpModule);
        ExternalProcessBuilder processBuilder = commandSupport.createCommand(command.getCommands(), "--help"); // NOI18N
        assert processBuilder != null;
        final HelpLineProcessor lineProcessor = new HelpLineProcessor();
        ExecutionDescriptor descriptor = new ExecutionDescriptor().inputOutput(InputOutput.NULL)
                .outProcessorFactory(new ExecutionDescriptor.InputProcessorFactory() {
            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                return InputProcessors.ansiStripping(InputProcessors.bridge(lineProcessor));
            }
        });
        try {
            executeAndWait(processBuilder, descriptor, "Help");
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return lineProcessor.getHelp();
    }

    static class HelpLineProcessor implements LineProcessor {

        private StringBuilder sb = new StringBuilder();

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
}
