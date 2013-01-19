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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.netbeans.modules.editor.lib2.search.EditorFindSupport;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Source",
id = "org.cakephp.netbeans.ui.actions.FormatPlusAction")
@ActionRegistration(
    displayName = "#CTL_FormatPlusAction")
@ActionReferences({
    @ActionReference(path = "Menu/Source", position = 350),
    @ActionReference(path = "Editors/text/x-php5/Popup", position = 765)
})
@Messages("CTL_FormatPlusAction=Format for CakePHP")
public final class FormatPlusAction implements ActionListener {

    private static final String REGEX = "^\\t(\\/\\*| \\*)"; // NOI18N
    private final EditorCookie context;
    private static final Logger LOGGER = Logger.getLogger(FormatPlusAction.class.getName());

    public FormatPlusAction(EditorCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // Check CakePHP project
        PhpModule phpModule = PhpModule.inferPhpModule();
        if (!CakePhpUtils.isCakePHP(phpModule)) {
            return;
        }

        Document doc = context.getDocument();

        reformat(doc);

        // Remove tab from Document Block
        removeIndentDocBlock();

        // Change line feed
        if (doc.getProperty(BaseDocument.READ_LINE_SEPARATOR_PROP) != BaseDocument.LS_LF) {
            doc.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, BaseDocument.LS_LF);
        }

    }

    /**
     * Reformat with NetBeans format settings
     *
     * @param doc
     */
    private void reformat(Document doc) {
        Reformat reformat = Reformat.get(doc);
        reformat.lock();
        try {
            reformat.reformat(0, doc.getLength());
        } catch (BadLocationException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } finally {
            reformat.unlock();
        }
    }

    /**
     * Remove indents of the Document Block
     */
    private void removeIndentDocBlock() {
        EditorFindSupport efs = EditorFindSupport.getInstance();
        Map<String, Object> property = new HashMap<String, Object>();
        property.put(EditorFindSupport.FIND_WHAT, REGEX);
        property.put(EditorFindSupport.FIND_REG_EXP, true);
        property.put(EditorFindSupport.FIND_WRAP_SEARCH, true);
        property.put(EditorFindSupport.FIND_REPLACE_WITH, "$1"); // NOI18N
        efs.putFindProperties(property);
        efs.replaceAll(property);
    }
}
