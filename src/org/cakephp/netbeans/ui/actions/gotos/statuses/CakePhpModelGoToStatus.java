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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.editor.visitors.CakePhpControllerVisitor;
import org.cakephp.netbeans.editor.visitors.CakePhpFixtureVisitor;
import org.cakephp.netbeans.editor.visitors.CakePhpModelVisitor;
import org.cakephp.netbeans.editor.visitors.CakePhpTestCaseVisitor;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToBehaviorItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToControllerItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToFixtureItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToModelItem;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.Inflector;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.openide.filesystems.FileObject;

/**
 * Behavior for current model file of Go To Action.
 *
 * - controller<br/>
 * - model<br/>
 * - behavior<br/>
 * - test case<br/>
 * - fixture<br/>
 *
 * @author junichi11
 */
public class CakePhpModelGoToStatus extends CakePhpGoToStatus {

    private final List<GoToItem> models = new ArrayList<>();
    private final List<GoToItem> fixtures = new ArrayList<>();
    private final List<GoToItem> behaviors = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(CakePhpModelGoToStatus.class.getName());
    private static final CakePhpModelGoToStatus INSTANCE = new CakePhpModelGoToStatus();

    private CakePhpModelGoToStatus() {
    }

    public static CakePhpModelGoToStatus getInstance() {
        return INSTANCE;
    }

    private void reset() {
        models.clear();
        fixtures.clear();
        behaviors.clear();
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject currentFile, int offset) {
        reset();
        final Set<FileObject> fixtureFiles = new HashSet<>();

        // scan
        final CakePhpModelVisitor modelVisitor = new CakePhpModelVisitor(currentFile);
        try {
            scanModel(modelVisitor, currentFile);
            scanTestCase(fixtureFiles);
            if (fixtureFiles.isEmpty()) {
                scanFixture(fixtureFiles);
            }
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        // set
        setModels(modelVisitor.getModels());
        setBehaviors(modelVisitor.getBehaviors());
        setFixtures(fixtureFiles);

        // sort
        sort(models);
        sort(behaviors);
        sort(fixtures);
    }

    private void scanModel(CakePhpModelVisitor modelVisitor, final FileObject currentFile) throws ParseException {
        scan(modelVisitor, currentFile);
    }

    private void scanTestCase(final Set<FileObject> fixtureFiles) throws ParseException {
        Set<ClassElement> testCaseClassElements = getTestCaseClassElements();
        for (ClassElement classElement : testCaseClassElements) {
            final FileObject testCase = classElement.getFileObject();
            ParserManager.parse(Collections.singleton(Source.create(testCase)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                    CakePhpTestCaseVisitor visitor = new CakePhpTestCaseVisitor(testCase);
                    visitor.scan(Utils.getRoot(parseResult));
                    fixtureFiles.addAll(visitor.getFixtures());
                }
            });

        }
    }

    @Override
    public List<GoToItem> getControllers() {
        // one to one
        List<GoToItem> items = getController();
        if (!items.isEmpty()) {
            return items;
        }

        // XXX more search?
        // only app directory
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(getPhpModule());
        if (cakeModule == null) {
            return Collections.emptyList();
        }
        FileObject controllerDirectory = cakeModule.getControllerDirectory(CakePhpModule.DIR_TYPE.APP);

        // scan controllers
        final Set<FileObject> controllers = new HashSet<>();
        Enumeration<? extends FileObject> children = controllerDirectory.getChildren(true);
        while (children.hasMoreElements()) {
            final FileObject fileObject = children.nextElement();
            if (fileObject == null || fileObject.isFolder() || !CakePhpUtils.isController(fileObject)) {
                continue;
            }
            try {
                scanController(fileObject, controllers);
            } catch (ParseException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }

        // add GoToItem
        List<GoToItem> controllerItems = new ArrayList<>(controllers.size());
        for (FileObject controller : controllers) {
            if (CakePhpUtils.isController(controller)) {
                controllerItems.add(new GoToControllerItem(controller, DEFAULT_OFFSET));
            }
        }
        return controllerItems;
    }

    private void scanController(final FileObject fileObject, final Set<FileObject> controllers) throws ParseException {
        ParserManager.parse(Collections.singleton(Source.create(fileObject)), new UserTask() {
            @Override
            public void run(ResultIterator resultIterator) throws Exception {
                ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                CakePhpControllerVisitor visitor = new CakePhpControllerVisitor(fileObject);
                visitor.scan(Utils.getRoot(parseResult));
                List<FileObject> modelFiles = visitor.getModels();
                if (modelFiles.contains(getCurrentFile())) {
                    controllers.add(fileObject);
                }
            }
        });
    }

    @Override
    public List<GoToItem> getModels() {
        return models;
    }

    @Override
    public List<GoToItem> getBehaviors() {
        return behaviors;
    }

    @Override
    public List<GoToItem> getFixtrues() {
        return fixtures;
    }

    @Override
    public List<GoToItem> getSmart() {
        List<GoToItem> controllers = getController();
        List<GoToItem> testCases = getTestCases();
        List<GoToItem> fixtrues = getFixtrues();
        int size = controllers.size() + testCases.size() + models.size() + fixtrues.size();
        List<GoToItem> items = new ArrayList<>(size);
        items.addAll(controllers);
        items.addAll(testCases);
        items.addAll(models);
        items.addAll(fixtrues);
        return items;
    }

    private void setFixtures(final Set<FileObject> fixtureFiles) {
        for (FileObject fixture : fixtureFiles) {
            fixtures.add(new GoToFixtureItem(fixture, DEFAULT_OFFSET));
        }
    }

    private void setBehaviors(final List<FileObject> behaviorFiles) {
        for (FileObject behavior : behaviorFiles) {
            behaviors.add(new GoToBehaviorItem(behavior, DEFAULT_OFFSET));
        }
    }

    private void setModels(final List<FileObject> modelFiles) {
        for (FileObject model : modelFiles) {
            models.add(new GoToModelItem(model, DEFAULT_OFFSET));
        }
    }

    private List<GoToItem> getController() {
        Inflector infrector = Inflector.getInstance();
        String className = CakePhpUtils.getClassName(getCurrentFile());

        // get controller name
        String controllerName = infrector.pluralize(className) + "Controller"; // NOI18N
        Set<ClassElement> classElements = getClassElements(getPhpModule().getSourceDirectory(), controllerName);
        List<GoToItem> items = new ArrayList<>(classElements.size());
        for (ClassElement classElement : classElements) {
            FileObject controller = classElement.getFileObject();
            if (CakePhpUtils.isController(controller)) {
                items.add(new GoToControllerItem(controller, DEFAULT_OFFSET));
            }
        }
        return items;
    }

    private void scanFixture(Set<FileObject> fixtureFiles) throws ParseException {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(getPhpModule());
        if (cakeModule == null) {
            return;
        }
        FileObject fixtureDirectory = cakeModule.getFixtureDirectory(CakePhpModule.DIR_TYPE.APP);
        if (fixtureDirectory == null) {
            return;
        }
        FileObject[] children = fixtureDirectory.getChildren();
        for (FileObject child : children) {
            final CakePhpFixtureVisitor visitor = new CakePhpFixtureVisitor();
            scan(visitor, child);
            String table = visitor.getTable();
            if (table.isEmpty()) {
                continue;
            }
            Inflector inflector = Inflector.getInstance();
            String singularize = inflector.singularize(table);
            String modelName = inflector.camelize(singularize, false);
            if (modelName.equals(getCurrentFile().getName())) {
                fixtureFiles.add(child);
            }
        }
    }
}
