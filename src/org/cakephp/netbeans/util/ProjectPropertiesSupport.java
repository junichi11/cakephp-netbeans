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
package org.cakephp.netbeans.util;

import java.io.IOException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;

/**
 *
 * @author junichi11
 */
public class ProjectPropertiesSupport {

    public static final String PHP_UNIT_BOOTSTRAP = "phpunit.bootstrap"; // NOI18N
    public static final String PHP_UNIT_BOOTSTRAP_FOR_CREATE_TESTS = "phpunit.bootstrap.create.tests"; // NOI18N
    public static final String PHP_UNIT_SCRIPT = "phpunit.script"; // NOI18N

    /**
     * Set PHPUnit bootstap.php, script.
     *
     * @param phpModule
     */
    public static void setPHPUnit(final PhpModule phpModule, final String bootstrapPath, final String scriptPath) {
        try {
            // store properties
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    Project project = getProject(phpModule);
                    AntProjectHelper helper = getAntProjectHelper(project);
                    if (helper == null) {
                        return null;
                    }
                    EditableProperties properties = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                    if (properties != null) {
                        boolean isChanged = false;;
                        // set bootstrap
                        if (!StringUtils.isEmpty(bootstrapPath)) {
                            properties.setProperty(PHP_UNIT_BOOTSTRAP, bootstrapPath);
                            properties.setProperty(PHP_UNIT_BOOTSTRAP_FOR_CREATE_TESTS, "true"); // NOI18N
                            isChanged = true;
                        }

                        // set script
                        if (!StringUtils.isEmpty(scriptPath)) {
                            properties.setProperty(PHP_UNIT_SCRIPT, scriptPath);
                            isChanged = true;
                        }

                        if (isChanged) {
                            helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, properties);
                        }
                    }

                    ProjectManager.getDefault().saveProject(project);
                    return null;
                }
            });
        } catch (MutexException e) {
            Exceptions.printStackTrace((IOException) e.getException());
        }
    }

    /**
     * Get Project for PhpModule.
     *
     * @param phpModule
     * @return
     */
    public static Project getProject(PhpModule phpModule) {
        FileObject projectDirectory = phpModule.getProjectDirectory();
        if (projectDirectory == null) {
            return null;
        }
        return getProject(projectDirectory);

    }

    /**
     * Get Project for FileObject.
     *
     * @param fo
     * @return
     */
    public static Project getProject(FileObject fo) {
        return FileOwnerQuery.getOwner(fo);
    }

    /**
     * Get AntProjectHelper.
     *
     * @param project
     * @return
     */
    private static AntProjectHelper getAntProjectHelper(Project project) {
        if (project == null) {
            return null;
        }
        return project.getLookup().lookup(AntProjectHelper.class);
    }
}
