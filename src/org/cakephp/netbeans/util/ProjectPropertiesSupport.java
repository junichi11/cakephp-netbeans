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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public static final String TESTING_PROVIDERS = "testing.providers"; // NOI18N
    private static final String PHPUNIT_BOOTSTRAP_SCRIPT_PATH = "auxiliary.org-netbeans-modules-php-phpunit.phpUnit_2e_path="; // NOI18N
    private static final String PHPUNIT_BOOTSTRAP_SCRIPT_ENABLED = "auxiliary.org-netbeans-modules-php-phpunit.phpUnit_2e_enabled="; // NOI18N
    private static final String PHPUNIT_BOOTSTRAP_PATH = "auxiliary.org-netbeans-modules-php-phpunit.bootstrap_2e_path="; // NOI18N
    private static final String PHPUNIT_BOOTSTRAP_ENABLED = "auxiliary.org-netbeans-modules-php-phpunit.bootstrap_2e_enabled="; // NOI18N
    private static final String PHPUNIT_BOOTSTRAP_CREATE_TESTS = "auxiliary.org-netbeans-modules-php-phpunit.bootstrap_2e_create_2e_tests="; // NOI18N
    private static final String UTF8 = "UTF-8"; // NOI18N

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
                        properties.setProperty(TESTING_PROVIDERS, "PhpUnit"); // NOI18N
                        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, properties);
                    }

                    ProjectManager.getDefault().saveProject(project);
                    return null;
                }
            });
        } catch (MutexException e) {
            Exceptions.printStackTrace((IOException) e.getException());
        }
        // PhpUnit module is changed since NB7.4
        // set project properties for PHPUnit
        setPhpUnitProperties(phpModule, bootstrapPath, scriptPath);
    }

    private static void setPhpUnitProperties(PhpModule phpModule, String bootstrapPath, String scriptPath) {
        HashMap<String, String> propertiesMap = new HashMap<>();
        if (!StringUtils.isEmpty(bootstrapPath)) {
            propertiesMap.put(PHPUNIT_BOOTSTRAP_CREATE_TESTS, "true"); // NOI18N
            propertiesMap.put(PHPUNIT_BOOTSTRAP_ENABLED, "true"); // NOI18N
            propertiesMap.put(PHPUNIT_BOOTSTRAP_PATH, bootstrapPath);
        }

        if (!StringUtils.isEmpty(scriptPath)) {
            propertiesMap.put(PHPUNIT_BOOTSTRAP_SCRIPT_ENABLED, "true"); // NOI18N
            propertiesMap.put(PHPUNIT_BOOTSTRAP_SCRIPT_PATH, scriptPath);
        }

        // get project.properties
        FileObject properties = CakePhpUtils.getProjectProperties(phpModule);
        if (properties == null) {
            return;
        }
        try {
            List<String> lines = properties.asLines(UTF8);
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(properties.getOutputStream(), UTF8))) {
                // write phpunit properties
                for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    pw.println(key + value);
                }

                // write other properties
                Set<String> keySet = propertiesMap.keySet();
                for (String line : lines) {
                    boolean isPhpUnitProperty = false;
                    for (String key : keySet) {
                        if (line.startsWith(key)) {
                            isPhpUnitProperty = true;
                            break;
                        }
                    }
                    if (isPhpUnitProperty) {
                        continue;
                    }
                    pw.println(line);
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
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
