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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.editor.visitors.CakePhpTestCaseVisitor;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.ui.GoToBehaviorItem;
import org.cakephp.netbeans.ui.GoToComponentItem;
import org.cakephp.netbeans.ui.GoToControllerItem;
import org.cakephp.netbeans.ui.GoToDefaultItem;
import org.cakephp.netbeans.ui.GoToFixtureItem;
import org.cakephp.netbeans.ui.GoToHelperItem;
import org.cakephp.netbeans.ui.GoToItem;
import org.cakephp.netbeans.ui.GoToModelItem;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.openide.filesystems.FileObject;

/**
 * Behavior for current test file of Go To Action.
 *
 * - controller<br/>
 * - model<br/>
 * - helper<br/>
 * - component<br/>
 * - behavior<br/>
 * - fixture<br/>
 *
 * @author junichi11
 */
public class CakePhpTestCaseGoToStatus extends CakePhpGoToStatus {

    private boolean isContorller = false;
    private boolean isModel = false;
    private boolean isHelper = false;
    private boolean isComponent = false;
    private boolean isBehavior = false;
    private final List<GoToItem> fixtures = new ArrayList<GoToItem>();
    private static final Logger LOGGER = Logger.getLogger(CakePhpTestCaseGoToStatus.class.getName());
    private static CakePhpTestCaseGoToStatus INSTANCE = new CakePhpTestCaseGoToStatus();

    private CakePhpTestCaseGoToStatus() {
    }

    public static CakePhpTestCaseGoToStatus getInstance() {
        return INSTANCE;
    }

    private void reset(FileObject currentFile) {
        String className = CakePhpUtils.getClassName(currentFile);
        if (className.endsWith("ControllerTest")) { // NOI18N
            isContorller = true;
        } else if (className.endsWith("HelperTest")) { // NOI18N
            isHelper = true;
        } else if (className.endsWith("ComponentTest")) { // NOI18N
            isComponent = true;
        } else if (className.endsWith("BehaviorTest")) { // NOI18N
            isBehavior = true;
        } else {
            isModel = true;
        }
        fixtures.clear();
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject testCase, int offset) {
        reset(testCase);

        // scan
        final CakePhpTestCaseVisitor visitor = new CakePhpTestCaseVisitor(testCase);
        try {
            scanTestCase(visitor, testCase);
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        // set
        setFixtures(visitor.getFixtures());

        // sort
        sort(fixtures);
    }

    @Override
    public List<GoToItem> getControllers() {
        if (isContorller) {
            return getSmart();
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getModels() {
        if (isModel) {
            return getSmart();
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getComponents() {
        if (isComponent) {
            return getSmart();
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getHelpers() {
        if (isHelper) {
            return getSmart();
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getBehaviors() {
        if (isBehavior) {
            return getSmart();
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getFixtrues() {
        return fixtures;
    }

    @Override
    public List<GoToItem> getSmart() {
        PhpModule phpModule = getPhpModule();
        FileObject testCase = getCurrentFile();
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return Collections.emptyList();
        }

        // get class name
        String className = cakeModule.toFullyQualifiedNameForClassFile(testCase);
        List<GoToItem> items = new ArrayList<GoToItem>();
        Set<ClassElement> classElements = getClassElements(phpModule.getSourceDirectory(), className);
        for (ClassElement classElement : classElements) {
            FileObject fileObject = classElement.getFileObject();
            int currentOffset = getCurrentOffset(fileObject);
            items.add(createGoToItem(fileObject, currentOffset));
        }
        return items;
    }

    private GoToItem createGoToItem(FileObject fileObject, int offset) {
        if (isContorller) {
            return new GoToControllerItem(fileObject, offset);
        } else if (isModel) {
            return new GoToModelItem(fileObject, offset);
        } else if (isComponent) {
            return new GoToComponentItem(fileObject, offset);
        } else if (isBehavior) {
            return new GoToBehaviorItem(fileObject, offset);
        } else if (isHelper) {
            return new GoToHelperItem(fileObject, offset);
        } else {
            return new GoToDefaultItem(fileObject, offset);
        }
    }

    private void scanTestCase(CakePhpTestCaseVisitor visitor, FileObject testCase) throws ParseException {
        scan(visitor, testCase);
    }

    private void setFixtures(final List<FileObject> fixtureFiles) {
        for (FileObject fixture : fixtureFiles) {
            int defaultOffset = getCurrentOffset(fixture);
            fixtures.add(new GoToFixtureItem(fixture, defaultOffset));
        }
    }
}
