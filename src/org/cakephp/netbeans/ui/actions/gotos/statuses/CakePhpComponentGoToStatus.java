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
package org.cakephp.netbeans.ui.actions.gotos.statuses;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.editor.visitors.CakePhpComponentVisitor;
import org.cakephp.netbeans.ui.GoToComponentItem;
import org.cakephp.netbeans.ui.GoToItem;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;

/**
 * Behavior for current component file of Go To Action.
 *
 * @author junichi11
 */
public class CakePhpComponentGoToStatus extends CakePhpGoToStatus {

    private final List<GoToItem> components = new ArrayList<GoToItem>();
    private static final Logger LOGGER = Logger.getLogger(CakePhpComponentGoToStatus.class.getName());
    private static CakePhpComponentGoToStatus INSTANCE = new CakePhpComponentGoToStatus();

    private CakePhpComponentGoToStatus() {
    }

    public static CakePhpComponentGoToStatus getInstance() {
        return INSTANCE;
    }

    private void reset() {
        components.clear();
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject currentFile, int offset) {
        reset();

        // scan
        final CakePhpComponentVisitor visitor = new CakePhpComponentVisitor(currentFile);
        try {
            scanComponent(visitor, currentFile);
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        // set
        setComponents(visitor.getComponents());

        // sort
        sort(components);
    }

    @Override
    public List<GoToItem> getComponents() {
        return components;
    }

    @Override
    public List<GoToItem> getSmart() {
        List<GoToItem> items = getTestCases();
        if (!items.isEmpty()) {
            return items;
        } else {
            return getComponents();
        }
    }

    private void scanComponent(DefaultVisitor visitor, FileObject component) throws ParseException {
        scan(visitor, component);
    }

    private void setComponents(final List<FileObject> componentFiles) {
        for (FileObject component : componentFiles) {
            int defaultOffset = getCurrentOffset(component);
            components.add(new GoToComponentItem(component, defaultOffset));
        }
    }
}
