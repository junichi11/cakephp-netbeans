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

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.options.CakePhpOptions;
import org.cakephp.netbeans.versions.Versionable;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

/**
 * Action Menu for baserCMS
 *
 * @author junichi11
 */
@ActionID(
        category = "PHP",
        id = "org.cakephp.netbeans.basercms.ui.actions.BaserCmsActionMenu")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_BaserCmsActionMenu")
@ActionReferences({
    @ActionReference(path = "Projects/org-netbeans-modules-php-project/Actions", position = 900)})
@NbBundle.Messages("CTL_BaserCmsActionMenu=baserCMS")
public final class BaserCmsActionMenu extends AbstractAction implements Presenter.Popup {

    public static final List<? extends AbstractAction> ALL_ACTIONS = Arrays.asList(
            new InitializeVagrantSettingsAction(),
            new RunPhpMyAdminAction(),
            new RunPhpPgAdminAction(),
            new CopyThemeAction()
    );
    private static final long serialVersionUID = -5524423067942402655L;
    private JMenu baserCmsActions = null;

    @NbBundle.Messages("BaserCmsActionMenu.name=baserCMS")
    public BaserCmsActionMenu() {
        super(Bundle.BaserCmsActionMenu_name());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // no-op
    }

    @Override
    public JMenuItem getPopupPresenter() {
        PhpModule phpModule = PhpModule.Factory.inferPhpModule();
        if (baserCmsActions == null) {
            baserCmsActions = new BaserCmsActions();
        }

        baserCmsActions.setVisible(true);
        if (!CakePhpOptions.getInstance().isBaserCmsEnabled()) {
            baserCmsActions.setVisible(false);
            return baserCmsActions;
        }

        // check whether module is baserCMS
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule != null) {
            Versionable version = cakeModule.getVersion(Versionable.VERSION_TYPE.BASERCMS);
            if (version == null) {
                baserCmsActions.setVisible(false);
            }
        } else {
            baserCmsActions.setVisible(false);
        }
        return baserCmsActions;
    }

    //~ Inner class
    private static final class BaserCmsActions extends JMenu {

        private static final long serialVersionUID = 3119993986472746166L;

        public BaserCmsActions() {
            super(Bundle.CTL_BaserCmsActionMenu());
            // add actions
            for (AbstractAction action : ALL_ACTIONS) {
                add(action);
            }
        }
    }

}
