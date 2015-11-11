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
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToBehaviorItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToComponentItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToControllerItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToFixtureItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToHelperItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToModelItem;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

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

    private FILE_TYPE fileType = FILE_TYPE.NONE;
    private final List<GoToItem> fixtures = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(CakePhpTestCaseGoToStatus.class.getName());
    private static final CakePhpTestCaseGoToStatus INSTANCE = new CakePhpTestCaseGoToStatus();

    private CakePhpTestCaseGoToStatus() {
    }

    public static CakePhpTestCaseGoToStatus getInstance() {
        return INSTANCE;
    }

    private void reset(FileObject currentFile) {
        String className = getTestCaseClassName(currentFile);
        if (className.endsWith("ControllerTest")) { // NOI18N
            fileType = FILE_TYPE.CONTROLLER;
        } else if (className.endsWith("HelperTest")) { // NOI18N
            fileType = FILE_TYPE.HELPER;
        } else if (className.endsWith("ComponentTest")) { // NOI18N
            fileType = FILE_TYPE.COMPONENT;
        } else if (className.endsWith("BehaviorTest")) { // NOI18N
            fileType = FILE_TYPE.BEHAVIOR;
        } else if (className.endsWith("Test")) { // NOI18N
            fileType = FILE_TYPE.MODEL;
        } else {
            fileType = FILE_TYPE.NONE;
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
        if (fileType == FILE_TYPE.CONTROLLER) {
            return getSmart();
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getModels() {
        if (fileType == FILE_TYPE.MODEL) {
            return getSmart();
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getComponents() {
        if (fileType == FILE_TYPE.COMPONENT) {
            return getSmart();
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getHelpers() {
        if (fileType == FILE_TYPE.HELPER) {
            return getSmart();
        }
        return Collections.emptyList();
    }

    @Override
    public List<GoToItem> getBehaviors() {
        if (fileType == FILE_TYPE.BEHAVIOR) {
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
        String className = cakeModule.getTestedClassName(testCase);
        List<GoToItem> items = new ArrayList<>();
        Set<ClassElement> classElements = getClassElements(phpModule.getSourceDirectory(), className);
        for (ClassElement classElement : classElements) {
            FileObject fileObject = classElement.getFileObject();
            GoToItem goToItem = createGoToItem(fileObject, DEFAULT_OFFSET);
            if (goToItem != null && classElement.getName().equals(className)) {
                items.add(goToItem);
                break;
            }
        }
        return items;
    }

    private GoToItem createGoToItem(FileObject fileObject, int offset) {
        GoToItem item = null;
        switch (fileType) {
            case CONTROLLER:
                if (CakePhpUtils.isController(fileObject)) {
                    item = new GoToControllerItem(fileObject, offset);
                }
                break;
            case MODEL:
                if (CakePhpUtils.isModel(fileObject)) {
                    item = new GoToModelItem(fileObject, offset);
                }
                break;
            case COMPONENT:
                if (CakePhpUtils.isComponent(fileObject)) {
                    item = new GoToComponentItem(fileObject, offset);
                }
                break;
            case HELPER:
                if (CakePhpUtils.isHelper(fileObject)) {
                    item = new GoToHelperItem(fileObject, offset);
                }
                break;
            case BEHAVIOR:
                if (CakePhpUtils.isBehavior(fileObject)) {
                    item = new GoToBehaviorItem(fileObject, offset);
                }
                break;
            default:
                // do nothing
                break;
        }
        return item;
    }

    private void scanTestCase(CakePhpTestCaseVisitor visitor, FileObject testCase) throws ParseException {
        scan(visitor, testCase);
    }

    private void setFixtures(final List<FileObject> fixtureFiles) {
        for (FileObject fixture : fixtureFiles) {
            fixtures.add(new GoToFixtureItem(fixture, DEFAULT_OFFSET));
        }
    }

    /**
     * Get test case class name.
     *
     * @param fo FileObject
     * @return class name if php class name exists, otherwise empty string.
     */
    public static String getTestCaseClassName(FileObject fo) {
        String name = fo.getName();
        if (name.contains(".test")) { // NOI18N
            name = name.replace(".test", ""); // NOI18N
        } else if (name.endsWith("Test")) { // NOI18N
            return name;
        }

        // Cake 1.x
        name = CakePhpUtils.getCamelCaseName(name);
        EditorSupport support = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : support.getClasses(fo)) {
            String className = phpClass.getName();
            if (!className.endsWith("Test") && !className.endsWith("TestCase")) { // NOI18N
                continue;
            }

            if (className.startsWith(name)) {
                return className.replace("TestCase", "Test"); // NOI18N
            }
        }
        return ""; // NOI18N
    }
}
