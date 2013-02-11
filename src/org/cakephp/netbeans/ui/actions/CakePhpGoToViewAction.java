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
package org.cakephp.netbeans.ui.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.CakeVersion;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.GoToViewAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public final class CakePhpGoToViewAction extends GoToViewAction {

    static final Logger LOGGER = Logger.getLogger(CakePhpGoToViewAction.class.getName());
    private static final long serialVersionUID = 9834759234756237L;
    private final FileObject controller;
    private final int offset;
    private FileObject theme;

    public CakePhpGoToViewAction(FileObject controller, int offset) {
        assert CakePhpUtils.isController(controller);
        this.controller = controller;
        this.offset = offset;
    }

    @Override
    public boolean goToView() {
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        PhpBaseElement phpElement = editorSupport.getElement(controller, offset);
        if (phpElement == null) {
            return false;
        }
        // Theme
        if (theme != null) {
            FileObject viewTheme = CakePhpUtils.getView(controller, phpElement, theme);
            if (viewTheme != null) {
                UiUtils.open(viewTheme, DEFAULT_OFFSET);
                return true;
            }
            return false;
        }
        // check theme directory
        if (getThemes() != null) {
            for (PhpClass phpClass : editorSupport.getClasses(controller)) {
                if (CakePhpUtils.isControllerName(phpClass.getName())) {
                    for (PhpClass.Field field : phpClass.getFields()) {
                        if (field.getName().equals("$theme")) { // NOI18N
                            CakePhpGoToViewActionPanel dialog = new CakePhpGoToViewActionPanel(this);
                            dialog.showDialog();
                            return true;
                        }
                    }
                }
            }
        }
        FileObject view = CakePhpUtils.getView(controller, phpElement);
        if (view != null) {
            UiUtils.open(view, DEFAULT_OFFSET);
            return true;
        }

        // auto create a view file
        PhpModule phpModule = PhpModule.forFileObject(controller);
        if (CakePreferences.getAutoCreateView(phpModule)) {
            try {
                view = CakePhpUtils.createView(controller, phpElement);
                if (view != null) {
                    UiUtils.open(view, DEFAULT_OFFSET);
                    return true;
                }
            } catch (IOException ex) {
                // do nothing
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return false;
    }

    public FileObject[] getThemes() {
        PhpModule phpModule = PhpModule.forFileObject(controller);
        FileObject[] themes = null;
        FileObject themeDirectory = null;
        if (CakeVersion.getInstance(phpModule).isCakePhp(2)) {
            themeDirectory = controller.getFileObject("../../View/Themed"); // NOI18N
        } else {
            themeDirectory = controller.getFileObject("../../views/themed"); // NOI18N
        }
        if (themeDirectory != null) {
            themes = themeDirectory.getChildren();
        }
        return themes;
    }

    public void setTheme(FileObject theme) {
        this.theme = theme;
    }
}
