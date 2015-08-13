/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.StyledDocument;
import org.cakephp.netbeans.CakePhp;
import org.cakephp.netbeans.ui.actions.gotos.CakePhpGoToBehaviorsAction;
import org.cakephp.netbeans.ui.actions.gotos.CakePhpGoToComponentsAction;
import org.cakephp.netbeans.ui.actions.gotos.CakePhpGoToControllersAction;
import org.cakephp.netbeans.ui.actions.gotos.CakePhpGoToFixturesAction;
import org.cakephp.netbeans.ui.actions.gotos.CakePhpGoToHelpersAction;
import org.cakephp.netbeans.ui.actions.gotos.CakePhpGoToModelsAction;
import org.cakephp.netbeans.ui.actions.gotos.CakePhpGoToSmartAction;
import org.cakephp.netbeans.ui.actions.gotos.CakePhpGoToTestCasesAction;
import org.cakephp.netbeans.ui.actions.gotos.CakePhpGoToViewsAction;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author junichi11
 */
@ActionID(
        category = "PHP",
        id = "org.cakephp.netbeans.ui.actions.CakePhpBaseMenuAction")
@ActionRegistration(
        menuText = "#LBL_CakePHP",
        lazy = false,
        displayName = "#LBL_CakePHP")
@ActionReferences({
    @ActionReference(path = "Editors/text/x-php5/Toolbars/Default", separatorBefore = 209000, position = 210000),
    @ActionReference(path = "Loaders/text/x-php5/Actions", position = 150),
    @ActionReference(path = "Editors/text/x-php5/Popup", position = 550)
})
@NbBundle.Messages("LBL_CakePHP=CakePHP")
public class CakePhpBaseMenuAction extends BaseAction implements Presenter.Popup, Presenter.Toolbar {

    private static final long serialVersionUID = 7615298169771540650L;

    private CakePhpBaseMenuAction() {
    }

    public static CakePhpBaseMenuAction getInstance() {
        return CakePhpBaseActionHolder.INSTANCE;
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @Override
    protected String getPureName() {
        return Bundle.LBL_CakePHP();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        // do nothing
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu menu = new JMenu(Bundle.LBL_CakePHP());
        PhpModule phpModule = PhpModule.Factory.inferPhpModule();
        if (CakePhpUtils.isCakePHP(phpModule)) {
            boolean isAvaibable = isAvailableWithEditor();
            // smart go to
            if (isAvaibable) {
                menu.add(CakePhpGoToSmartAction.getInstance());
                menu.add(CakePhpGoToTestCasesAction.getInstance());
                menu.add(CakePhpGoToControllersAction.getInstance());
                menu.add(CakePhpGoToViewsAction.getInstance());
                menu.add(CakePhpGoToModelsAction.getInstance());
                menu.add(CakePhpGoToComponentsAction.getInstance());
                menu.add(CakePhpGoToHelpersAction.getInstance());
                menu.add(CakePhpGoToBehaviorsAction.getInstance());
                menu.add(CakePhpGoToFixturesAction.getInstance());
            }

            // format
            if (isAvaibable) {
                JMenuItem format = new JMenuItem(FormatPlusAction.getInstance());
                menu.add(format);
            }
            // create test
            DataObject dataObject = getDataObject();
            if (dataObject != null) {
                JMenuItem createTest = new JMenuItem(new RunBakeTestAction(dataObject));
                menu.add(createTest);
            }

            // run action
            if (isAvaibable) {
                JMenuItem run = new JMenuItem(RunActionAction.getInstance());
                menu.add(run);
            }
        }
        if (menu.getItemCount() == 0) {
            menu.setVisible(false);
        }
        return menu;
    }

    private static DataObject getDataObject() {
        Lookup context = Utilities.actionsGlobalContext();
        return context.lookup(DataObject.class);
    }

    private static FileObject getFileObject() {
        DataObject dataObject = getDataObject();
        if (dataObject != null) {
            return dataObject.getPrimaryFile();
        }
        return null;
    }

    @Override
    public Component getToolbarPresenter() {
        if (CakePhpUtils.isCakePHP(PhpModule.Factory.inferPhpModule())) {
            CakeToolbarPresenter cakeToolbarPresenter = new CakeToolbarPresenter();
            cakeToolbarPresenter.setVisible(true);
            return cakeToolbarPresenter;
        } else {
            JButton button = new JButton();
            button.setVisible(false);
            return button;
        }
    }

    private boolean isAvailableWithEditor() {
        Lookup context = Utilities.actionsGlobalContext();
        EditorCookie ec = context.lookup(EditorCookie.class);
        StyledDocument document = ec.getDocument();
        return document != null;
    }

    //~ inner classes
    private static class CakePhpBaseActionHolder {

        private static final CakePhpBaseMenuAction INSTANCE = new CakePhpBaseMenuAction();
    }

    private static class CakeToolbarPresenter extends JButton {

        private static final long serialVersionUID = 2139565806752438122L;

        CakeToolbarPresenter() {
            this.setIcon(ImageUtilities.loadImageIcon(CakePhp.CAKE_ICON_16, true));
            // add listener
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PhpModule phpModule = PhpModule.Factory.inferPhpModule();
                    JPopupMenu popup = new JPopupMenu();
                    if (CakePhpUtils.isCakePHP(phpModule)) {
                        // add actions
                        // smart go to
                        popup.add(CakePhpGoToSmartAction.getInstance());
                        popup.add(CakePhpGoToTestCasesAction.getInstance());
                        popup.add(CakePhpGoToControllersAction.getInstance());
                        popup.add(CakePhpGoToViewsAction.getInstance());
                        popup.add(CakePhpGoToModelsAction.getInstance());
                        popup.add(CakePhpGoToComponentsAction.getInstance());
                        popup.add(CakePhpGoToHelpersAction.getInstance());
                        popup.add(CakePhpGoToBehaviorsAction.getInstance());
                        popup.add(CakePhpGoToFixturesAction.getInstance());

                        // format
                        popup.add(FormatPlusAction.getInstance());

                        // create test
                        DataObject dataObject = getDataObject();
                        if (dataObject != null) {
                            popup.add(new RunBakeTestAction(dataObject));
                        }

                        // run action
                        popup.add(RunActionAction.getInstance());

                        popup.show(CakeToolbarPresenter.this, 0, CakeToolbarPresenter.this.getHeight());
                    }
                }
            });
        }
    }
}
