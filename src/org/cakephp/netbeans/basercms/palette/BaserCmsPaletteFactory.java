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
package org.cakephp.netbeans.basercms.palette;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;

/**
 * Palette for baserCMS
 *
 * @author junichi11
 */
public class BaserCmsPaletteFactory {

    public static final String BASER_PALETTE_FOLDER = "Palettes/PHP"; // NOI18N
    public static final String BASERCMS3_CATEGORY = "baserCMS3"; // NOI18N
    private static PaletteController palette = null;

//    @MimeRegistration(mimeType = FileUtils.PHP_MIME_TYPE, service = PaletteController.class)
    public static PaletteController createPalette() {
        if (palette == null) {
            try {
                palette = PaletteFactory.createPalette(BASER_PALETTE_FOLDER, new BaserCmsPaletteActions(), null, new BaserCmsDragAndDropHandler());
                return palette;
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return palette;
    }

    private static class BaserCmsPaletteActions extends PaletteActions {

        @Override
        public Action[] getImportActions() {
            return null;
        }

        @Override
        public Action[] getCustomPaletteActions() {
            return null;
        }

        @Override
        public Action[] getCustomCategoryActions(Lookup arg0) {
            return null;
        }

        @Override
        public Action[] getCustomItemActions(Lookup arg0) {
            return null;
        }

        @Override
        public Action getPreferredAction(Lookup item) {
            return new BaserCmsPaletteInsertAction(item);
        }
    }

    private static class BaserCmsPaletteInsertAction extends AbstractAction {

        private static final long serialVersionUID = -543790648415736041L;
        private final Lookup item;

        public BaserCmsPaletteInsertAction(Lookup item) {
            this.item = item;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ActiveEditorDrop activeEditorDrop = item.lookup(ActiveEditorDrop.class);
            JTextComponent target = Utilities.getFocusedComponent();
            if (target == null) {
                return;
            }
            try {
                activeEditorDrop.handleTransfer(target);
            } finally {
                Utilities.requestFocus(target);
            }
            PaletteController palette = BaserCmsPaletteFactory.createPalette();
            palette.clearSelection();
        }

    }

    private static class BaserCmsDragAndDropHandler extends DragAndDropHandler {

        public BaserCmsDragAndDropHandler() {
            super(true);
        }

        @Override
        public void customize(ExTransferable t, Lookup item) {
        }

    }
}
