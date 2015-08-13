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
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.editor.visitors.CakePhpFixtureVisitor;
import org.cakephp.netbeans.editor.visitors.CakePhpTestCaseVisitor;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToModelItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToTestCaseItem;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.Inflector;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Behavior for current fixture file of Go To Action.
 *
 * - model<br/>
 *
 * @author junichi11
 */
public class CakePhpFixtureGoToStatus extends CakePhpGoToStatus {

    private static final String FIXTURE = "Fixture"; // NOI18N
    private final List<GoToItem> models = new ArrayList<>();
    private final List<GoToItem> testCases = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(CakePhpFixtureGoToStatus.class.getName());
    private static final CakePhpFixtureGoToStatus INSTANCE = new CakePhpFixtureGoToStatus();

    private CakePhpFixtureGoToStatus() {
    }

    public static CakePhpFixtureGoToStatus getInstance() {
        return INSTANCE;
    }

    private void reset() {
        models.clear();
        testCases.clear();
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject currentFile, int offset) {
        reset();

        // scan
        final CakePhpFixtureVisitor visitor = new CakePhpFixtureVisitor();
        try {
            scanFixture(visitor, currentFile);
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        // If Fixture has $table, use it.
        String table = visitor.getTable();
        String modelName;
        if (!table.isEmpty()) {
            Inflector inflector = Inflector.getInstance();
            String singularize = inflector.singularize(table);
            modelName = inflector.camelize(singularize, false);
        } else {
            String className = CakePhpUtils.getClassName(currentFile);
            modelName = toModelName(className);

        }
        // get test case
        // XXX all test case?

        // set models
        if (phpModule != null) {
            Set<ClassElement> classElements = getClassElements(phpModule.getSourceDirectory(), modelName);

            // set
            setModels(classElements, modelName);
        }
    }

    @Override
    public List<GoToItem> getModels() {
        return models;
    }

    @Override
    public List<GoToItem> getTestCases() {
        testCases.clear();
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(getPhpModule());
        if (cakeModule == null) {
            return testCases;
        }

        FileObject testDirectory = cakeModule.getTestDirectory(CakePhpModule.DIR_TYPE.APP);
        if (testDirectory == null) {
            return testCases;
        }

        Enumeration<? extends FileObject> children = testDirectory.getChildren(true);
        while (children.hasMoreElements()) {
            FileObject child = children.nextElement();
            if (child == null || child.isFolder()) {
                continue;
            }

            try {
                CakePhpTestCaseVisitor visitor = new CakePhpTestCaseVisitor(child);
                scan(visitor, child);
                List<FileObject> fixtures = visitor.getFixtures();
                for (FileObject fixture : fixtures) {
                    if (fixture == getCurrentFile()) {
                        testCases.add(new GoToTestCaseItem(child, DEFAULT_OFFSET));
                        break;
                    }
                }
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return testCases;
    }

    @Override
    public List<GoToItem> getSmart() {
        return getModels();
    }

    private String toModelName(String className) {
        return className.replace(FIXTURE, ""); // NOI18N
    }

    private void setModels(Set<ClassElement> classElements, String modelName) {
        for (ClassElement classElement : classElements) {
            FileObject model = classElement.getFileObject();
            if (classElement.getName().equals(modelName) && CakePhpUtils.isModel(model)) {
                models.add(new GoToModelItem(model, DEFAULT_OFFSET));
            }
        }
    }

    private void scanFixture(CakePhpFixtureVisitor visitor, FileObject currentFile) throws ParseException {
        scan(visitor, currentFile);
    }
}
