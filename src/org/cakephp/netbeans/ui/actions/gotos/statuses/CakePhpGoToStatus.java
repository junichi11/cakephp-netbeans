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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.cakephp.netbeans.module.CakePhpModule;
import org.cakephp.netbeans.module.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.module.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.ui.GoToBehaviorItem;
import org.cakephp.netbeans.ui.GoToComponentItem;
import org.cakephp.netbeans.ui.GoToControllerItem;
import org.cakephp.netbeans.ui.GoToFixtureItem;
import org.cakephp.netbeans.ui.GoToHelperItem;
import org.cakephp.netbeans.ui.GoToItem;
import org.cakephp.netbeans.ui.GoToModelItem;
import org.cakephp.netbeans.ui.GoToTestCaseItem;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.ElementQueryFactory;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.QuerySupportFactory;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 * Behavior for current file of Go To Action.
 *
 * @author junichi11
 */
public abstract class CakePhpGoToStatus {

    private FileObject currentFile;
    private int offset;
    private PhpModule phpModule;
    private static final Comparator<GoToItem> FILE_COMPARATOR = new Comparator<GoToItem>() {
        @Override
        public int compare(GoToItem o1, GoToItem o2) {
            return o1.getFileObject().getName().compareToIgnoreCase(o2.getFileObject().getName());
        }
    };

    CakePhpGoToStatus() {
    }

    public void setCurrentFile(FileObject currentFile) {
        this.currentFile = currentFile;
        if (currentFile != null) {
            this.phpModule = PhpModule.forFileObject(currentFile);
        } else {
            this.phpModule = null;
        }
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Get current file.
     *
     * @return current file
     */
    public FileObject getCurrentFile() {
        return currentFile;
    }

    /**
     * Get current caret position.
     *
     * @return offset for current caret position
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Get PhpModule.
     *
     * @return PhpModule
     */
    public PhpModule getPhpModule() {
        return phpModule;
    }

    /**
     * Get all items.
     *
     * @return all GoToItems
     */
    public List<GoToItem> getAll() {
        List<GoToItem> items = new ArrayList<GoToItem>(getAllSize());
        items.addAll(getControllers());
        items.addAll(getModels());
        items.addAll(getViews());
        items.addAll(getComponents());
        items.addAll(getHelpers());
        items.addAll(getBehaviors());
        items.addAll(getTestCases());
        items.addAll(getFixtrues());
        return items;
    }

    public void scan() {
        if (phpModule != null && currentFile != null) {
            scan(phpModule, currentFile, offset);
        }
    }

    protected abstract void scan(PhpModule phpModule, FileObject currentFile, int offset);

    protected void scan(final DefaultVisitor visitor, FileObject targetFile) throws ParseException {
        ParserManager.parse(Collections.singleton(Source.create(targetFile)), new UserTask() {
            @Override
            public void run(ResultIterator resultIterator) throws Exception {
                ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                visitor.scan(Utils.getRoot(parseResult));
            }
        });
    }

    /**
     * Get controller items.
     *
     * @return controller items, empty list if there is no item.
     */
    public List<GoToItem> getControllers() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAppControllers() {
        return createGoToItems(FILE_TYPE.CONTROLLER);
    }

    /**
     * Get model items.
     *
     * @return model items, empty list if there is no item.
     */
    public List<GoToItem> getModels() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAppModels() {
        return createGoToItems(FILE_TYPE.MODEL);
    }

    /**
     * Get view items.
     *
     * @return view items, empty list if there is no item.
     */
    public List<GoToItem> getViews() {
        return Collections.emptyList();
    }

    /**
     * Get component items.
     *
     * @return component items, empty list if there is no item.
     */
    public List<GoToItem> getComponents() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAppComponents() {
        return createGoToItems(FILE_TYPE.COMPONENT);
    }

    /**
     * Get helper items.
     *
     * @return helper items, empty list if there is no item.
     */
    public List<GoToItem> getHelpers() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAppHelpers() {
        return createGoToItems(FILE_TYPE.HELPER);
    }

    /**
     * Get behavior items.
     *
     * @return behavior item, empty list if there is no item.
     */
    public List<GoToItem> getBehaviors() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAppBehaviors() {
        return createGoToItems(FILE_TYPE.BEHAVIOR);
    }

    /**
     * Get test case items.
     *
     * @return test case items, empty list if there is no item.
     */
    public List<GoToItem> getTestCases() {
        if (currentFile == null || CakePhpUtils.isCtpFile(currentFile)) {
            return Collections.emptyList();
        }

        Set<ClassElement> classElements = getTestCaseClassElements();
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        Collection<PhpClass> classes = editorSupport.getClasses(currentFile);

        // XXX
        int startClassOffset = 0;
        for (PhpClass phpClass : classes) {
            startClassOffset = phpClass.getOffset();
            break;
        }
        // FIXME exception might be occurred
        // if user run action at outside php class.
        // e.g. document area.
        //
        String methodName = ""; // NOI18N
        if (offset > startClassOffset) {
            PhpBaseElement element = editorSupport.getElement(currentFile, offset);
            if (element != null) {
                if (element instanceof PhpClass.Method) {
                    PhpClass.Method method = (PhpClass.Method) element;
                    methodName = method.getName();
                }
            }
        }

        List<GoToItem> items = new ArrayList<GoToItem>();
        for (ClassElement classElement : classElements) {
            FileObject testClass = classElement.getFileObject();
            int defaultOffset = getCurrentOffset(testClass);
            items.add(new GoToTestCaseItem(testClass, defaultOffset));
            for (PhpClass phpClass : editorSupport.getClasses(testClass)) {
                for (PhpClass.Method method : phpClass.getMethods()) {
                    if (methodName.isEmpty()) {
                        // add all
                        items.add(new GoToTestCaseItem(testClass, method.getOffset(), method.getName()));
                        continue;
                    }

                    // add test method contains method name
                    // e.g. In case of getSomething: testGetSomething, testGetSomethingMore,...
                    String testMethodName = method.getName();
                    String lowerCaseTestMethodName = testMethodName.toLowerCase();
                    String lowerCaseMethodName = methodName.toLowerCase();
                    if (lowerCaseTestMethodName.contains(lowerCaseMethodName)) {
                        items.add(new GoToTestCaseItem(testClass, method.getOffset(), testMethodName));
                    }
                }
            }
        }
        return items;
    }

    /**
     * Get test case class elements.
     *
     * @return test case clas elements, empty list if there is no item.
     */
    public Set<ClassElement> getTestCaseClassElements() {
        if (CakePhpUtils.isCtpFile(currentFile)) {
            return Collections.emptySet();
        }

        String target = getTestCaseClassName(currentFile);
        if (!StringUtils.isEmpty(target) && !target.contains(" ")) { // NOI18N
            FileObject targetDirectory = phpModule.getTestDirectory();
            Set<ClassElement> classElements = new HashSet<ClassElement>();
            if (targetDirectory != null) {
                classElements.addAll(getClassElements(targetDirectory, target));
            }
            targetDirectory = phpModule.getSourceDirectory();
            if (targetDirectory != null) {
                classElements.addAll(getClassElements(targetDirectory, target));
            }
            return classElements;
        }
        return Collections.emptySet();
    }

    /**
     * Get class elements.
     *
     * @param targetDirectory
     * @param targetName class name
     * @return class elements
     */
    public Set<ClassElement> getClassElements(FileObject targetDirectory, String targetName) {
        if (targetDirectory == null || !targetDirectory.isFolder() || StringUtils.isEmpty(targetName)) {
            return Collections.emptySet();
        }

        ElementQuery.Index indexQuery = ElementQueryFactory.createIndexQuery(QuerySupportFactory.get(targetDirectory));
        return indexQuery.getClasses(NameKind.prefix(targetName));
    }

    /**
     * Get fixture items.
     *
     * @return fixture item, empty list if there is no item.
     */
    public List<GoToItem> getFixtrues() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAppFixtures() {
        return createGoToItems(FILE_TYPE.FIXTURE);
    }

    /**
     * Get smart items.
     *
     * @return smart items, empty list if there is no item.
     */
    public abstract List<GoToItem> getSmart();

    /**
     * Get current offset for opened file. If file ie opened, get current
     * offset. Otherwise return 0.
     *
     * @param target FileObject
     * @return current offset if the file is opened, otherwise 0.
     */
    public int getCurrentOffset(FileObject target) {
        List<? extends JTextComponent> componentList = EditorRegistry.componentList();
        int currentOffset = 0;
        for (JTextComponent editor : componentList) {
            Document document = editor.getDocument();
            if (document != null) {
                // TODO confirm whether there is isOpened method.
                FileObject fileObject = NbEditorUtilities.getFileObject(document);
                if (fileObject == target) {
                    currentOffset = editor.getCaretPosition();
                    break;
                }
            }
        }
        return currentOffset;
    }

    private int getAllSize() {
        return getAllSize(getControllers(),
                getModels(),
                getViews(),
                getComponents(),
                getHelpers(),
                getBehaviors(),
                getTestCases(),
                getFixtrues());
    }

    protected int getAllSize(List... lists) {
        int size = 0;
        for (List list : lists) {
            size = size + list.size();
        }
        return size;
    }

    public void sort(List<GoToItem> views) {
        Collections.sort(views, FILE_COMPARATOR);
    }

    private FileObject getAppDirectory(FILE_TYPE fileType) {
        if (phpModule == null) {
            return null;
        }
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        return cakeModule.getDirectory(DIR_TYPE.APP, fileType, null);
    }

    private List<GoToItem> createGoToItems(FILE_TYPE fileType) {
        return createGoToItems(getAppDirectory(fileType), fileType);
    }

    private List<GoToItem> createGoToItems(FileObject targetDirectory, FILE_TYPE fileType) {
        if (targetDirectory == null || !targetDirectory.isFolder()) {
            return Collections.emptyList();
        }
        Enumeration<? extends FileObject> children = targetDirectory.getChildren(true);
        ArrayList<GoToItem> items = new ArrayList<GoToItem>();
        while (children.hasMoreElements()) {
            FileObject next = children.nextElement();
            if (next.isFolder()) {
                continue;
            }
            switch (fileType) {
                case CONTROLLER:
                    if (CakePhpUtils.isController(next)) {
                        items.add(new GoToControllerItem(next, getCurrentOffset(next)));
                    }
                    break;
                case MODEL:
                    if (CakePhpUtils.isModel(next)) {
                        items.add(new GoToModelItem(next, getCurrentOffset(next)));
                    }
                    break;
                case COMPONENT:
                    if (CakePhpUtils.isComponent(next)) {
                        items.add(new GoToComponentItem(next, getCurrentOffset(next)));
                    }
                    break;
                case HELPER:
                    if (CakePhpUtils.isHelper(next)) {
                        items.add(new GoToHelperItem(next, getCurrentOffset(next)));
                    }
                    break;
                case BEHAVIOR:
                    if (CakePhpUtils.isBehavior(next)) {
                        items.add(new GoToBehaviorItem(next, getCurrentOffset(next)));
                    }
                    break;
                case FIXTURE:
                    if (CakePhpUtils.isFixture(next)) {
                        items.add(new GoToFixtureItem(next, getCurrentOffset(next)));
                    }
                    break;
                case TEST:
                    if (CakePhpUtils.isTest(next)) {
                        items.add(new GoToTestCaseItem(next, getCurrentOffset(next)));
                    }
                    break;
                default:
                    // do nothing
                    break;
            }
        }
        if (!items.isEmpty()) {
            sort(items);
            return items;
        }
        return Collections.emptyList();
    }

    private String getTestCaseClassName(FileObject fo) {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule != null) {
            return cakeModule.getTestCaseClassName(fo);
        }
        return ""; // NOI18N
    }
}
