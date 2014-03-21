/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.editor.codegenerator.ui;

import java.awt.Image;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import org.cakephp.netbeans.editor.codegenerator.Property;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Petr Pisl
 */
public abstract class CheckNode extends DefaultMutableTreeNode {

    public static final int SINGLE_SELECTION = 0;
    public static final int DIG_IN_SELECTION = 4;
    private static final long serialVersionUID = 1162165389878878902L;
    protected int selectionMode;
    protected boolean isSelected;

    public CheckNode() {
        this(null);
    }

    public CheckNode(Object userObject) {
        this(userObject, true, false);
    }

    public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected) {
        super(userObject, allowsChildren);
        this.isSelected = isSelected;
        setSelectionMode(DIG_IN_SELECTION);
    }

    private void setSelectionMode(int mode) {
        selectionMode = mode;
    }

    public int getSelectionMode() {
        return selectionMode;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;

        if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
            Enumeration<?> en = children.elements();
            while (en.hasMoreElements()) {
                CheckNode node = (CheckNode) en.nextElement();
                node.setSelected(isSelected);
            }
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public abstract Image getIcon();

    public static class MBHCNode extends CheckNode {

        private static final long serialVersionUID = 708502066152240792L;

        public MBHCNode(String name) {
            super(name, true, false);
        }

        @Override
        public Image getIcon() {
            return ImageUtilities.loadImage("org/cakephp/netbeans/resources/fieldPublic.png"); // NOI18N
        }
    }

    public static class MBHCClassNode extends CheckNode {

        private static final long serialVersionUID = 5828076119228208952L;
        protected final Property property;

        public MBHCClassNode(Property property) {
            super(property.getName(), false, property.isSelected());
            this.property = property;
        }

        @Override
        public void setSelected(boolean isSelected) {
            super.setSelected(isSelected);
            property.setSelected(isSelected);
        }

        @Override
        public Image getIcon() {
            return ImageUtilities.loadImage("org/cakephp/netbeans/resources/class.png"); // NOI18N
        }
    }
}
