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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.editor.visitors.CakePhpControllerVisitor;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToComponentItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToHelperItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToModelItem;
import org.cakephp.netbeans.ui.actions.gotos.items.GoToViewItem;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;

/**
 * Behavior for current controller file of Go To Action.
 *
 * - model<br/>
 * - view<br/>
 * - helper<br/>
 * - component<br/>
 * - test case
 *
 * @author junichi11
 */
public class CakePhpControllerGoToStatus extends CakePhpGoToStatus {

    private final List<GoToItem> models = new ArrayList<>();
    private final List<GoToItem> components = new ArrayList<>();
    private final List<GoToItem> helpers = new ArrayList<>();
    private final List<GoToItem> views = new ArrayList<>();
    private final List<GoToItem> allViews = new ArrayList<>();
    private Set<String> themeNames;
    private Set<String> allThemeNames;
    private boolean isTheme;
    private static final Logger LOGGER = Logger.getLogger(CakePhpControllerGoToStatus.class.getName());
    private static final CakePhpControllerGoToStatus INSTANCE = new CakePhpControllerGoToStatus();

    private CakePhpControllerGoToStatus() {
    }

    public static CakePhpControllerGoToStatus getInstance() {
        return INSTANCE;
    }

    private void reset() {
        models.clear();
        components.clear();
        helpers.clear();
        views.clear();
        allViews.clear();
        isTheme = false;
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject controller, int offset) {
        // reset
        reset();

        // scan
        final CakePhpControllerVisitor visitor = new CakePhpControllerVisitor(controller, offset);
        try {
            scanController(visitor, controller);
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        // set
        // don't change order
        setTheme(visitor.isTheme());
        setThemeNames(visitor.getThemeNames());
        setAllThemeNames(visitor.getAllThemeNames());
        setViews(visitor.getViewNames(), controller);
        setAllViews(visitor.getAllViewNames(), controller);
        setModels(visitor.getModels());
        setComponents(visitor.getComponents());
        setHelpers(visitor.getHelpers());

        // sort
        sort(views);
        sort(allViews);
        sort(models);
        sort(components);
        sort(helpers);

    }

    @Override
    public List<GoToItem> getModels() {
        return models;
    }

    @Override
    public List<GoToItem> getViews() {
        if (views.isEmpty()) {
            return allViews;
        }
        return views;
    }

    @Override
    public List<GoToItem> getComponents() {
        return components;
    }

    @Override
    public List<GoToItem> getHelpers() {
        return helpers;
    }

    @Override
    public List<GoToItem> getSmart() {
        if (!views.isEmpty()) {
            return views;
        }

        return allViews;
    }

    /**
     * Get theme directory.
     *
     * @return theme directories
     */
    private FileObject getThemeDirectory(String themeName) {
        FileObject controller = getCurrentFile();
        PhpModule phpModule = getPhpModule();
        FileObject themeDirectory = null;
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule != null) {
            CakeVersion version = cakeModule.getCakeVersion();
            if (version == null) {
                return null;
            }
            if (version.getMajor() >= 2) {
                themeDirectory = controller.getFileObject("../../View/Themed"); // NOI18N
            } else {
                themeDirectory = controller.getFileObject("../../views/themed"); // NOI18N
            }
            if (!StringUtils.isEmpty(themeName) && themeDirectory != null) {
                return themeDirectory.getFileObject(themeName);
            }
        }
        return themeDirectory;
    }

    /**
     * Get theme directories.
     *
     * @return theme directories
     */
    private FileObject[] getThemes() {
        FileObject themeDirectory = getThemeDirectory(null);
        if (themeDirectory != null) {
            return themeDirectory.getChildren();
        }
        return null;
//        return new FileObject[0];
    }

    private void addViews(List<GoToItem> views, final Set<String> viewNames, FileObject controller) {
        for (String viewName : viewNames) {
            // theme
            if (isTheme) {
                if (themeNames.isEmpty()) {
                    FileObject[] themes = getThemes();
                    if (themes != null) {
                        for (FileObject themeDirectory : themes) {
                            if (!themeDirectory.isFolder()) {
                                continue;
                            }
                            FileObject view = CakePhpUtils.getView(controller, viewName, themeDirectory);
                            if (view == null) {
                                continue;
                            }
                            views.add(new GoToViewItem(view, DEFAULT_OFFSET, themeDirectory.getName()));
                        }
                    }
                } else {
                    for (String themeName : themeNames) {
                        FileObject themeDirectory = getThemeDirectory(themeName);
                        if (themeDirectory != null) {
                            FileObject view = CakePhpUtils.getView(controller, viewName, themeDirectory);
                            if (view != null) {
                                views.add(new GoToViewItem(view, DEFAULT_OFFSET, themeDirectory.getName()));
                            }
                        }
                    }
                }
            }

            // app
            FileObject view = CakePhpUtils.getView(controller, viewName);
            if (view == null) {
                continue;
            }
            views.add(new GoToViewItem(view, DEFAULT_OFFSET));
        }
    }

    private void scanController(final DefaultVisitor visitor, FileObject controller) throws ParseException {
        scan(visitor, controller);
    }

    private void setModels(final List<FileObject> modelFiles) {
        for (FileObject model : modelFiles) {
            models.add(new GoToModelItem(model, DEFAULT_OFFSET));
        }
    }

    private void setComponents(final List<FileObject> componentFiles) {
        for (FileObject component : componentFiles) {
            components.add(new GoToComponentItem(component, DEFAULT_OFFSET));
        }
    }

    private void setHelpers(final List<FileObject> helperFiles) {
        for (FileObject helper : helperFiles) {
            helpers.add(new GoToHelperItem(helper, DEFAULT_OFFSET));
        }
    }

    private void setViews(final Set<String> viewNames, FileObject controller) {
        addViews(views, viewNames, controller);
    }

    private void setAllViews(final Set<String> allViewNames, FileObject controller) {
        addViews(allViews, allViewNames, controller);
    }

    private void setTheme(boolean isTheme) {
        this.isTheme = isTheme;
    }

    private void setThemeNames(Set<String> themeNames) {
        this.themeNames = themeNames;
    }

    private void setAllThemeNames(Set<String> allThemeNames) {
        this.allThemeNames = allThemeNames;
    }
}
