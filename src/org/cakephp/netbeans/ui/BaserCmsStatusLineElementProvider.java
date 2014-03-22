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
package org.cakephp.netbeans.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.cakephp.netbeans.basercms.BaserCms;
import org.cakephp.netbeans.basercms.ui.actions.BaserCmsActionMenu;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.options.CakePhpOptions;
import org.cakephp.netbeans.versions.Versionable;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.awt.StatusLineElementProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 * {@link StatusLineElementProvider} for baserCMS version number.
 *
 * @author junichi11
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public class BaserCmsStatusLineElementProvider implements StatusLineElementProvider {

    private final JLabel versionLabel = new JLabel(ImageUtilities.loadImageIcon(BaserCms.BASER_ICON_16, false));
    private final Result<FileObject> result;

    public BaserCmsStatusLineElementProvider() {
        versionLabel.setVisible(false);
        versionLabel.addMouseListener(new PopupMenu());
        result = Utilities.actionsGlobalContext().lookupResult(FileObject.class);
        result.addLookupListener(new LookupListenerImpl());
    }

    @Override
    public Component getStatusLineElement() {
        return panelWithSeparator(versionLabel);
    }

    /**
     * Create Component(JPanel) and add separator and JLabel to it.
     *
     * @param label JLabel
     * @return panel
     */
    private Component panelWithSeparator(JLabel label) {
        // create separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL) {
            private static final long serialVersionUID = -6385848933295984637L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(3, 3);
            }
        };
        separator.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // create panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(separator, BorderLayout.WEST);
        panel.add(label, BorderLayout.EAST);
        return panel;
    }

    private class LookupListenerImpl implements LookupListener {

        public LookupListenerImpl() {
        }

        @Override
        public void resultChanged(LookupEvent event) {
            versionLabel.setVisible(false);
            if (!CakePhpOptions.getInstance().isBaserCmsEnabled()) {
                return;
            }
            Lookup.Result<?> lookupResult = (Lookup.Result<?>) event.getSource();
            Collection<?> fileObjects = (Collection<?>) lookupResult.allInstances();
            if (fileObjects.isEmpty()) {
                return;
            }

            FileObject fileObject = (FileObject) fileObjects.iterator().next();
            PhpModule phpModule = PhpModule.Factory.forFileObject(fileObject);
            if (phpModule == null) {
                return;
            }

            CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
            if (cakeModule == null) {
                return;
            }

            Versionable version = cakeModule.getVersion(Versionable.VERSION_TYPE.BASERCMS);
            if (version == null) {
                return;
            }
            versionLabel.setText(version.getVersion());
            versionLabel.setVisible(true);
        }
    }

    //~ Inner class
    private static class PopupMenu extends MouseAdapter {

        private final JPopupMenu popupMenu = new JPopupMenu();

        public PopupMenu() {
            for (AbstractAction action : BaserCmsActionMenu.ALL_ACTIONS) {
                popupMenu.add(action);
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (mouseEvent.isPopupTrigger()) {
                popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (mouseEvent.isPopupTrigger()) {
                popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        }
    }
}
