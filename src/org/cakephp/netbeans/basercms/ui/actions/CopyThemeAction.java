/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.basercms.ui.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.basercms.ui.CopyThemePanel;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Copy existing theme action.
 *
 * @author junichi11
 */
public class CopyThemeAction extends BaserCmsBaseAction implements ChangeListener {

    private static final RequestProcessor RP = new RequestProcessor(CopyThemeAction.class);
    private static final long serialVersionUID = 939505827965160398L;
    private CopyThemePanel panel = null;
    private List<String> themeNames = new ArrayList<>();

    @NbBundle.Messages("CopyThemeAction.name=Copy Existing Theme")
    @Override
    protected String getName() {
        return Bundle.CopyThemeAction_name();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        try {
            FileObject themeDirectory = getThemeDirectory(phpModule);
            if (themeDirectory == null) {
                showErrorDialog();
                return;
            }
            themeNames = getThemeNames(themeDirectory);
            Collections.sort(themeNames);
            if (themeNames.isEmpty()) {
                showErrorDialog();
                return;
            }

            // show dialog
            getPanel().setError(" "); // NOI18N
            getPanel().setExistingThemeNames(themeNames);
            validate();
            DialogDescriptor dialogDescriptor = getPanel().showDialog();
            if (dialogDescriptor.getValue() != DialogDescriptor.OK_OPTION) {
                return;
            }

            copy(themeDirectory);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            themeNames.clear();
        }
    }

    /**
     * Copy existing theme to new directory.
     *
     * @param themeDirectory webroot/theme
     * @throws IOException
     */
    private void copy(final FileObject themeDirectory) throws IOException {
        RP.post(new Runnable() {

            @Override
            public void run() {
                String themeName = getPanel().getThemeName();
                String existingThemeName = getPanel().getSelectedThemeName();
                FileObject existingThemeDirectory = themeDirectory.getFileObject(existingThemeName);
                try {
                    existingThemeDirectory.copy(themeDirectory, themeName, ""); // NOI18N
                    // get initial files
                    Set<FileObject> initialFiles = getInitialFiles(themeDirectory, themeName);
                    for (FileObject file : initialFiles) {
                        UiUtils.open(file, 0);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    /**
     * Get initial files to be opened when the copying is complete.
     *
     * @param themeDirectory
     * @param themeName
     * @return initial files to be opened
     */
    private Set<FileObject> getInitialFiles(FileObject themeDirectory, String themeName) {
        Set<FileObject> initialFiles = new HashSet<>();
        FileObject newThemeDirectory = themeDirectory.getFileObject(themeName);
        if (newThemeDirectory == null) {
            return initialFiles;
        }

        // Layouts default
        FileObject defaultFile = newThemeDirectory.getFileObject("Layouts/default.php");  // NOI18N
        if (defaultFile != null) {
            initialFiles.add(defaultFile);
        }

        // VERSION.txt
        FileObject versionFile = newThemeDirectory.getFileObject("VERSION.txt"); // NOI18N
        if (versionFile != null) {
            initialFiles.add(versionFile);
        }

        // config.php
        FileObject configFile = newThemeDirectory.getFileObject("config.php"); // NOI18N
        if (configFile != null) {
            initialFiles.add(configFile);
        }

        return initialFiles;
    }

    @NbBundle.Messages("CopyThemeAction.notFound.themes=Not found existing themes")
    private void showErrorDialog() {
        NotifyDescriptor.Message message = new NotifyDescriptor.Message(Bundle.CopyThemeAction_notFound_themes());
        DialogDisplayer.getDefault().notify(message);
    }

    /**
     * Get {@link CopyThemePanel}.
     *
     * @return panel
     */
    private CopyThemePanel getPanel() {
        if (panel == null) {
            panel = new CopyThemePanel();
            panel.addChangeListener(this);
        }
        return panel;
    }

    /**
     * Get webroot/theme directory.
     *
     * @param phpModule
     * @return theme directory if it exists, {@code null} otherwise
     */
    private FileObject getThemeDirectory(PhpModule phpModule) {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return null;
        }
        FileObject webrootDirectory = cakeModule.getWebrootDirectory(DIR_TYPE.APP);
        if (webrootDirectory == null) {
            return null;
        }
        return webrootDirectory.getFileObject("theme"); // NOI18N
    }

    /**
     * Get existing theme names.
     *
     * @param themeDirectory webroot/theme
     * @return theme directory names
     */
    private List<String> getThemeNames(@NonNull FileObject themeDirectory) {
        FileObject[] children = themeDirectory.getChildren();
        ArrayList<String> names = new ArrayList<>(children.length);
        for (FileObject child : children) {
            if (child.isFolder()) {
                names.add(child.getName());
            }
        }
        return names;
    }

    /**
     * Validation for new theme directory name.
     */
    private void validate() {
        String themeName = getPanel().getThemeName();

        BaserCmsCopyThemeValidator validator = new BaserCmsCopyThemeValidator();
        ValidationResult result = validator.validateThemeName(themeName, themeNames)
                .getResult();
        // error
        if (result.hasErrors()) {
            getPanel().setError(result.getErrors().get(0).getMessage());
            getPanel().setOkButtonValid(false);
            return;
        }

        // warnings
        if (result.hasWarnings()) {
            getPanel().setError(result.getWarnings().get(0).getMessage());
            getPanel().setOkButtonValid(false);
            return;
        }

        // everything ok
        getPanel().setError(" "); // NOI18N
        getPanel().setOkButtonValid(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        validate();
    }

}
