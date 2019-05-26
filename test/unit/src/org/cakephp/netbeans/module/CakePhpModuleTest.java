/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.module;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.DefaultFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public class CakePhpModuleTest extends NbTestCase {

    PhpModule phpModule1;
    PhpModule phpModule2;
    CakePhpModule module;
    String app;
    String cakeDirectoryPath;
    boolean useProjectPath;
    boolean useAutoCreate;
    String cakeVersion;
    private static final String CAKES_DIRECTORY_NAME = "cakes";

    public CakePhpModuleTest(String name) {
        super(name);
    }

    @Before
    @Override
    public void setUp() {
        app = "app";
        cakeDirectoryPath = "";
        useProjectPath = false;
        useAutoCreate = true;
        init();
    }

    @After
    @Override
    public void tearDown() {
    }

    /**
     * Test of getViewDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetViewDirectory_CakePhpModuleDIR_TYPE() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/views");
        result = module.getViewDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("cake/libs/view");
        result = module.getViewDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getViewDirectory(type);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/View");
        result = module.getViewDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("lib/Cake/View");
        result = module.getViewDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getViewDirectory(type);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getViewDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetViewDirectory_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        String pluginDir = "NbtestAppplugins";
        // retrun view dir
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/views");
        result = module.getViewDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/views");
        result = module.getViewDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getViewDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        pluginDir = "NbtestAppPlugin";
        // return view dir
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/View");
        result = module.getViewDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/View");
        result = module.getViewDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getViewDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getViewFile method, of class CakePhpModule.
     */
    @Test
    public void testGetViewFile_3args() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/views/nbtests/index.ctp");
        result = module.getViewFile(DIR_TYPE.APP, "Nbtests", "index");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("cake/libs/view/elements/sql_dump.ctp");
        result = module.getViewFile(DIR_TYPE.CORE, "Elements", "sql_dump");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getViewFile(type, "Nbtests", "index");
            assertEquals(expResult, result);
        }
        result = module.getViewFile(DIR_TYPE.APP, "Nbtests", null);
        assertEquals(expResult, result);
        result = module.getViewFile(DIR_TYPE.APP, null, "index");
        assertEquals(expResult, result);
        result = module.getViewFile(DIR_TYPE.CORE, "", null);
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/View/Nbtests/index.ctp");
        result = module.getViewFile(DIR_TYPE.APP, "Nbtests", "index");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("lib/Cake/View/Elements/sql_dump.ctp");
        result = module.getViewFile(DIR_TYPE.CORE, "Elements", "sql_dump");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getViewFile(type, "Nbtests", "index");
            assertEquals(expResult, result);
        }
        result = module.getViewFile(DIR_TYPE.APP, "Nbtests", null);
        assertEquals(expResult, result);
        result = module.getViewFile(DIR_TYPE.APP, null, "index");
        assertEquals(expResult, result);
        result = module.getViewFile(DIR_TYPE.CORE, "", null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getViewFile method, of class CakePhpModule.
     */
    @Test
    public void testGetViewFile_4args() {
        FileObject expResult;
        FileObject result;
        String pluginName;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        pluginName = "NbtestAppplugins";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/views/nbtests/index.ctp");
        result = module.getViewFile(DIR_TYPE.APP_PLUGIN, "Nbtests", "index", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/views/nbtests/index.ctp");
        result = module.getViewFile(DIR_TYPE.PLUGIN, "Nbtests", "index", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getViewFile(type, "Nbtests", "index", "NbtestPlugins");
            assertEquals(expResult, result);
        }
        result = module.getViewFile(DIR_TYPE.APP, "Nbtests", null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getViewFile(DIR_TYPE.CORE, "", null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getViewFile(DIR_TYPE.CORE, null, null, "NbtestPlugins");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        // retrun view file
        pluginName = "NbtestAppPlugin";
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/View/Nbtests/index.ctp");
        result = module.getViewFile(DIR_TYPE.APP_PLUGIN, "Nbtests", "index", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/View/Nbtests/index.ctp");
        result = module.getViewFile(DIR_TYPE.PLUGIN, "Nbtests", "index", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getViewFile(type, "Nbtests", "index", "NbtestPlugin");
            assertEquals(expResult, result);
        }
        result = module.getViewFile(DIR_TYPE.APP, "Nbtests", null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getViewFile(DIR_TYPE.CORE, "", null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getViewFile(DIR_TYPE.CORE, null, null, "NbtestPlugins");
        assertEquals(expResult, result);
    }

    /**
     * Test of getControllerDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetControllerDirectory_CakePhpModuleDIR_TYPE() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/controllers");
        result = module.getControllerDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("cake/libs/controller");
        result = module.getControllerDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getControllerDirectory(type);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Controller");
        result = module.getControllerDirectory(DIR_TYPE.APP);
        assertEquals(expResult, result);
        assertNotNull(result);

        expResult = getCakePhpDir().getFileObject("lib/Cake/Controller");
        result = module.getControllerDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getControllerDirectory(type);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getControllerDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetControllerDirectory_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        String pluginDir = "NbtestAppplugins";
        // retrun view dir
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/controllers");
        result = module.getControllerDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/controllers");
        result = module.getControllerDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getControllerDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        pluginDir = "NbtestAppPlugin";
        // return view dir
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Controller");
        result = module.getControllerDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Controller");
        result = module.getControllerDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getControllerDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

    }

    /**
     * Test of getControllerFile method, of class CakePhpModule.
     */
    @Test
    public void testGetControllerFile_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/controllers/nbtests_controller.php");
        result = module.getControllerFile(DIR_TYPE.APP, "Nbtests");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("cake/libs/controller/app_controller.php");
        result = module.getControllerFile(DIR_TYPE.CORE, "App");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getControllerFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getControllerFile(DIR_TYPE.APP, null);
        assertEquals(expResult, result);
        result = module.getControllerFile(DIR_TYPE.APP, "");
        assertEquals(expResult, result);
        result = module.getControllerFile(DIR_TYPE.APP, "NotExist");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        result = module.getControllerFile(DIR_TYPE.APP, "Nbtests");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("lib/Cake/Controller/CakeErrorController.php");
        result = module.getControllerFile(DIR_TYPE.CORE, "CakeError");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getControllerFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getControllerFile(DIR_TYPE.APP, null);
        assertEquals(expResult, result);
        result = module.getControllerFile(DIR_TYPE.APP, "");
        assertEquals(expResult, result);
        result = module.getControllerFile(DIR_TYPE.APP, "NotExist");
        assertEquals(expResult, result);
    }

    /**
     * Test of getControllerFile method, of class CakePhpModule.
     */
    @Test
    public void testGetControllerFile_3args() {
        FileObject expResult;
        FileObject result;
        String pluginName;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        pluginName = "NbtestAppplugins";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/controllers/nbtests_controller.php");
        result = module.getControllerFile(DIR_TYPE.APP_PLUGIN, "Nbtests", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/controllers/nbtests_controller.php");
        result = module.getControllerFile(DIR_TYPE.PLUGIN, "Nbtests", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getControllerFile(type, "Nbtests", "NbtestPlugins");
            assertEquals(expResult, result);
        }
        result = module.getControllerFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getControllerFile(DIR_TYPE.PLUGIN, "", "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getControllerFile(DIR_TYPE.PLUGIN, "NotExist", "NbtestPlugins");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        // retrun view file
        pluginName = "NbtestAppPlugin";
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Controller/NbtestsController.php");
        result = module.getControllerFile(DIR_TYPE.APP_PLUGIN, "Nbtests", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Controller/NbtestsController.php");
        result = module.getControllerFile(DIR_TYPE.PLUGIN, "Nbtests", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getControllerFile(type, "Nbtests", "NbtestPlugin");
            assertEquals(expResult, result);
        }
        result = module.getControllerFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getControllerFile(DIR_TYPE.PLUGIN, "", "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getControllerFile(DIR_TYPE.PLUGIN, "NotExist", "NbtestPlugins");
        assertEquals(expResult, result);
    }

    /**
     * Test of getModelDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetModelDirectory_CakePhpModuleDIR_TYPE() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/models");
        result = module.getModelDirectory(DIR_TYPE.APP);
        assertEquals(expResult, result);
        assertNotNull(result);

        expResult = getCakePhpDir().getFileObject("cake/libs/model");
        result = module.getModelDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getModelDirectory(type);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Model");
        result = module.getModelDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("lib/Cake/Model");
        result = module.getModelDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getModelDirectory(type);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getModelDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetModelDirectory_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        String pluginDir = "NbtestAppplugins";
        // retrun view dir
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/models");
        result = module.getModelDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/models");
        result = module.getModelDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getModelDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        pluginDir = "NbtestAppPlugin";
        // return view dir
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Model");
        result = module.getModelDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Model");
        result = module.getModelDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getModelDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getModelFile method, of class CakePhpModule.
     */
    @Test
    public void testGetModelFile_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/models/nbtest.php");
        result = module.getModelFile(DIR_TYPE.APP, "Nbtest");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("cake/libs/model/model.php");
        result = module.getModelFile(DIR_TYPE.CORE, "Model");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getModelFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getModelFile(DIR_TYPE.APP, null);
        assertEquals(expResult, result);
        result = module.getModelFile(DIR_TYPE.APP, "");
        assertEquals(expResult, result);
        result = module.getModelFile(DIR_TYPE.CORE, "NotExist");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Model/Nbtest.php");
        result = module.getModelFile(DIR_TYPE.APP, "Nbtest");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("lib/Cake/Model/Model.php");
        result = module.getModelFile(DIR_TYPE.CORE, "Model");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getModelFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getModelFile(DIR_TYPE.APP, null);
        assertEquals(expResult, result);
        result = module.getModelFile(DIR_TYPE.CORE, "");
        assertEquals(expResult, result);
        result = module.getModelFile(DIR_TYPE.APP, "NotExist");
        assertEquals(expResult, result);
    }

    /**
     * Test of getModelFile method, of class CakePhpModule.
     */
    @Test
    public void testGetModelFile_3args() {
        FileObject expResult;
        FileObject result;
        String pluginName;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        pluginName = "NbtestAppplugins";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/models/nbtest.php");
        result = module.getModelFile(DIR_TYPE.APP_PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/models/nbtest.php");
        result = module.getModelFile(DIR_TYPE.PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getModelFile(type, "Nbtests", "NbtestPlugins");
            assertEquals(expResult, result);
        }
        result = module.getModelFile(DIR_TYPE.PLUGIN, "", "NbtestAppplugins");
        assertEquals(expResult, result);
        result = module.getModelFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getModelFile(DIR_TYPE.APP_PLUGIN, "NotExist", "NbtestPlugins");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        // retrun view file
        pluginName = "NbtestAppPlugin";
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Model/Nbtest.php");
        result = module.getModelFile(DIR_TYPE.APP_PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Model/Nbtest.php");
        result = module.getModelFile(DIR_TYPE.PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getModelFile(type, "Nbtests", "NbtestPlugin");
            assertEquals(expResult, result);
        }
        result = module.getModelFile(DIR_TYPE.APP_PLUGIN, "", "NbtestAppPlugin");
        assertEquals(expResult, result);
        result = module.getModelFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugin");
        assertEquals(expResult, result);
        result = module.getModelFile(DIR_TYPE.PLUGIN, "NotExist", "NbtestPlugin");
        assertEquals(expResult, result);
    }

    /**
     * Test of getComponentDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetComponentDirectory_CakePhpModuleDIR_TYPE() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/controllers/components");
        result = module.getComponentDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("cake/libs/controller/components");
        result = module.getComponentDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getComponentDirectory(type);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Controller/Component");
        result = module.getComponentDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("lib/Cake/Controller/Component");
        result = module.getComponentDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getComponentDirectory(type);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getComponentDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetComponentDirectory_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        String pluginDir = "NbtestAppplugins";
        // retrun view dir
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/controllers/components");
        result = module.getComponentDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/controllers/components");
        result = module.getComponentDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getComponentDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        pluginDir = "NbtestAppPlugin";
        // return view dir
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Controller/Component");
        result = module.getComponentDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Controller/Component");
        result = module.getComponentDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getComponentDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getComponentFile method, of class CakePhpModule.
     */
    @Test
    public void testGetComponentFile_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/controllers/components/nbtest.php");
        result = module.getComponentFile(DIR_TYPE.APP, "Nbtest");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("cake/libs/controller/components/session.php");
        result = module.getComponentFile(DIR_TYPE.CORE, "Session");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getComponentFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getComponentFile(DIR_TYPE.CORE, null);
        assertEquals(expResult, result);
        result = module.getComponentFile(DIR_TYPE.APP, "");
        assertEquals(expResult, result);
        result = module.getComponentFile(DIR_TYPE.APP, "NotExist");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Controller/Component/NbtestComponent.php");
        result = module.getComponentFile(DIR_TYPE.APP, "Nbtest");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("lib/Cake/Controller/Component/SecurityComponent.php");
        result = module.getComponentFile(DIR_TYPE.CORE, "Security");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getComponentFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getComponentFile(DIR_TYPE.CORE, null);
        assertEquals(expResult, result);
        result = module.getComponentFile(DIR_TYPE.APP, "");
        assertEquals(expResult, result);
        result = module.getComponentFile(DIR_TYPE.APP, "NotExist");
        assertEquals(expResult, result);
    }

    /**
     * Test of getComponentFile method, of class CakePhpModule.
     */
    @Test
    public void testGetComponentFile_3args() {
        FileObject expResult;
        FileObject result;
        String pluginName;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        pluginName = "NbtestAppplugins";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/controllers/components/nbtest_plugin.php");
        result = module.getComponentFile(DIR_TYPE.APP_PLUGIN, "NbtestPlugin", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/controllers/components/nbtest_plugin.php");
        result = module.getComponentFile(DIR_TYPE.PLUGIN, "NbtestPlugin", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getComponentFile(type, "Nbtests", "NbtestPlugins");
            assertEquals(expResult, result);
        }
        result = module.getComponentFile(DIR_TYPE.PLUGIN, "", "NbtestAppplugins");
        assertEquals(expResult, result);
        result = module.getComponentFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getComponentFile(DIR_TYPE.APP_PLUGIN, "NotExist", "NbtestPlugins");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        // retrun view file
        pluginName = "NbtestAppPlugin";
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Controller/Component/NbtestComponent.php");
        result = module.getComponentFile(DIR_TYPE.APP_PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Controller/Component/NbtestComponent.php");
        result = module.getComponentFile(DIR_TYPE.PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getComponentFile(type, "Nbtests", "NbtestPlugin");
            assertEquals(expResult, result);
        }
        result = module.getComponentFile(DIR_TYPE.APP_PLUGIN, "", "NbtestAppPlugin");
        assertEquals(expResult, result);
        result = module.getComponentFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugin");
        assertEquals(expResult, result);
        result = module.getComponentFile(DIR_TYPE.PLUGIN, "NotExist", "NbtestPlugin");
        assertEquals(expResult, result);
    }

    /**
     * Test of getHelperDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetHelperDirectory_CakePhpModuleDIR_TYPE() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/views/helpers");
        result = module.getHelperDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("cake/libs/view/helpers");
        result = module.getHelperDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getHelperDirectory(type);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/View/Helper");
        result = module.getHelperDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("lib/Cake/View/Helper");
        result = module.getHelperDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getHelperDirectory(type);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getHelperDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetHelperDirectory_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        String pluginDir = "NbtestAppplugins";
        // retrun view dir
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/views/helpers");
        result = module.getHelperDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/views/helpers");
        result = module.getHelperDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getHelperDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        pluginDir = "NbtestAppPlugin";
        // return view dir
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/View/Helper");
        result = module.getHelperDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/View/Helper");
        result = module.getHelperDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getHelperDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getHelperFile method, of class CakePhpModule.
     */
    @Test
    public void testGetHelperFile_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/views/helpers/nbtest.php");
        result = module.getHelperFile(DIR_TYPE.APP, "Nbtest");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("cake/libs/view/helpers/html.php");
        result = module.getHelperFile(DIR_TYPE.CORE, "Html");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getHelperFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getHelperFile(DIR_TYPE.APP, null);
        assertEquals(expResult, result);
        result = module.getHelperFile(DIR_TYPE.APP, "");
        assertEquals(expResult, result);
        result = module.getHelperFile(DIR_TYPE.CORE, "NotExist");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/View/Helper/NbtestHelper.php");
        result = module.getHelperFile(DIR_TYPE.APP, "Nbtest");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("lib/Cake/View/Helper/FormHelper.php");
        result = module.getHelperFile(DIR_TYPE.CORE, "Form");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getHelperFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getHelperFile(DIR_TYPE.CORE, null);
        assertEquals(expResult, result);
        result = module.getHelperFile(DIR_TYPE.APP, "");
        assertEquals(expResult, result);
        result = module.getHelperFile(DIR_TYPE.APP, "NotExist");
        assertEquals(expResult, result);
    }

    /**
     * Test of getHelperFile method, of class CakePhpModule.
     */
    @Test
    public void testGetHelperFile_3args() {
        FileObject expResult;
        FileObject result;
        String pluginName;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        pluginName = "NbtestAppplugins";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/views/helpers/nbtest_plugin.php");
        result = module.getHelperFile(DIR_TYPE.APP_PLUGIN, "NbtestPlugin", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/views/helpers/nbtest_plugin.php");
        result = module.getHelperFile(DIR_TYPE.PLUGIN, "NbtestPlugin", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getHelperFile(type, "Nbtests", "NbtestPlugins");
            assertEquals(expResult, result);
        }
        result = module.getHelperFile(DIR_TYPE.PLUGIN, "", "NbtestAppplugins");
        assertEquals(expResult, result);
        result = module.getHelperFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getHelperFile(DIR_TYPE.APP_PLUGIN, "NotExist", "NbtestPlugins");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        // retrun view file
        pluginName = "NbtestAppPlugin";
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/View/Helper/NbtestHelper.php");
        result = module.getHelperFile(DIR_TYPE.APP_PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/View/Helper/NbtestHelper.php");
        result = module.getHelperFile(DIR_TYPE.PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getHelperFile(type, "Nbtests", "NbtestPlugin");
            assertEquals(expResult, result);
        }
        result = module.getHelperFile(DIR_TYPE.APP_PLUGIN, "", "NbtestAppPlugin");
        assertEquals(expResult, result);
        result = module.getHelperFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugin");
        assertEquals(expResult, result);
        result = module.getHelperFile(DIR_TYPE.PLUGIN, "NotExist", "NbtestPlugin");
        assertEquals(expResult, result);
    }

    /**
     * Test of getBehaviorDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetBehaviorDirectory_CakePhpModuleDIR_TYPE() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/models/behaviors");
        result = module.getBehaviorDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("cake/libs/model/behaviors");
        result = module.getBehaviorDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getBehaviorDirectory(type);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Model/Behavior");
        result = module.getBehaviorDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("lib/Cake/Model/Behavior");
        result = module.getBehaviorDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getBehaviorDirectory(type);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getBehaviorDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetBehaviorDirectory_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        String pluginDir = "NbtestAppplugins";
        // retrun view dir
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/models/behaviors");
        result = module.getBehaviorDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/models/behaviors");
        result = module.getBehaviorDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getBehaviorDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        pluginDir = "NbtestAppPlugin";
        // return view dir
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Model/Behavior");
        result = module.getBehaviorDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Model/Behavior");
        result = module.getBehaviorDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getBehaviorDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getBehaviorFile method, of class CakePhpModule.
     */
    @Test
    public void testGetBehaviorFile_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/models/behaviors/nbtest.php");
        result = module.getBehaviorFile(DIR_TYPE.APP, "Nbtest");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("cake/libs/model/behaviors/tree.php");
        result = module.getBehaviorFile(DIR_TYPE.CORE, "Tree");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getBehaviorFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getBehaviorFile(DIR_TYPE.APP, null);
        assertEquals(expResult, result);
        result = module.getBehaviorFile(DIR_TYPE.APP, "");
        assertEquals(expResult, result);
        result = module.getBehaviorFile(DIR_TYPE.APP, "NotExist");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Model/Behavior/NbtestBehavior.php");
        result = module.getBehaviorFile(DIR_TYPE.APP, "Nbtest");
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("lib/Cake/Model/Behavior/AclBehavior.php");
        result = module.getBehaviorFile(DIR_TYPE.CORE, "Acl");
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getBehaviorFile(type, "Nbtests");
            assertEquals(expResult, result);
        }
        result = module.getBehaviorFile(DIR_TYPE.APP, null);
        assertEquals(expResult, result);
        result = module.getBehaviorFile(DIR_TYPE.APP, "");
        assertEquals(expResult, result);
        result = module.getBehaviorFile(DIR_TYPE.APP, "NotExist");
        assertEquals(expResult, result);
    }

    /**
     * Test of getBehaviorFile method, of class CakePhpModule.
     */
    @Test
    public void testGetBehaviorFile_3args() {
        FileObject expResult;
        FileObject result;
        String pluginName;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        pluginName = "NbtestAppplugins";
        // retrun view file
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/models/behaviors/nbtest.php");
        result = module.getBehaviorFile(DIR_TYPE.APP_PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/models/behaviors/nbtest.php");
        result = module.getBehaviorFile(DIR_TYPE.PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getBehaviorFile(type, "Nbtests", "NbtestPlugins");
            assertEquals(expResult, result);
        }
        result = module.getBehaviorFile(DIR_TYPE.PLUGIN, "", "NbtestAppplugins");
        assertEquals(expResult, result);
        result = module.getBehaviorFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugins");
        assertEquals(expResult, result);
        result = module.getBehaviorFile(DIR_TYPE.APP_PLUGIN, "NotExist", "NbtestPlugins");
        assertEquals(expResult, result);

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        // retrun view file
        pluginName = "NbtestAppPlugin";
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Model/Behavior/NbtestBehavior.php");
        result = module.getBehaviorFile(DIR_TYPE.APP_PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginName = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Model/Behavior/NbtestBehavior.php");
        result = module.getBehaviorFile(DIR_TYPE.PLUGIN, "Nbtest", pluginName);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getBehaviorFile(type, "Nbtests", "NbtestPlugin");
            assertEquals(expResult, result);
        }
        result = module.getBehaviorFile(DIR_TYPE.APP_PLUGIN, "", "NbtestAppPlugin");
        assertEquals(expResult, result);
        result = module.getBehaviorFile(DIR_TYPE.APP_PLUGIN, null, "NbtestPlugin");
        assertEquals(expResult, result);
        result = module.getBehaviorFile(DIR_TYPE.PLUGIN, "NotExist", "NbtestPlugin");
        assertEquals(expResult, result);
    }

    /**
     * Test of getConfigDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetConfigDirectory_CakePhpModuleDIR_TYPE() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/config");
        result = module.getConfigDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("cake/config");
        result = module.getConfigDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getConfigDirectory(type);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Config");
        result = module.getConfigDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("lib/Cake/Config");
        result = module.getConfigDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getConfigDirectory(type);
            assertEquals(expResult, result);
        }
        result = module.getConfigDirectory(null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getConfigDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetConfigDirectory_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        String pluginDir = "NbtestAppplugins";
        // retrun view dir
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/config");
        result = module.getConfigDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/config");
        result = module.getConfigDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getConfigDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        pluginDir = "NbtestAppPlugin";
        // return view dir
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Config");
        result = module.getConfigDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Config");
        result = module.getConfigDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getTestDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getTestDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetTestDirectory_CakePhpModuleDIR_TYPE() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/tests");
        result = module.getTestDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("cake/tests");
        result = module.getTestDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getTestDirectory(type);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Test");
        result = module.getTestDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("lib/Cake/Test");
        result = module.getTestDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getTestDirectory(type);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getTestDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetTestDirectory_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        String pluginDir = "NbtestAppplugins";
        // retrun view dir
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/plugins/nbtest_appplugins/tests");
        result = module.getTestDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugins";
        expResult = getCakePhpDir().getFileObject("plugins/nbtest_plugins/tests");
        result = module.getTestDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getTestDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        pluginDir = "NbtestAppPlugin";
        // return view dir
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Test");
        result = module.getTestDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Test");
        result = module.getTestDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getTestDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getWebrootDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetWebrootDirectory() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app + "/webroot");
        result = module.getWebrootDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        DIR_TYPE[] types = {null, DIR_TYPE.CORE, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getWebrootDirectory(type);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/webroot");
        result = module.getWebrootDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        for (DIR_TYPE type : types) {
            result = module.getWebrootDirectory(type);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getConsoleDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetConsoleDirectory_CakePhpModuleDIR_TYPE() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject("cake/console");
        for (DIR_TYPE type : DIR_TYPE.values()) {
            result = module.getConsoleDirectory(type);
            assertNotNull(result);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Console");
        result = module.getConsoleDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = getCakePhpDir().getFileObject("lib/Cake/Console");
        result = module.getConsoleDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        DIR_TYPE[] types = {null, DIR_TYPE.APP_PLUGIN, DIR_TYPE.APP_VENDOR, DIR_TYPE.APP_LIB, DIR_TYPE.PLUGIN, DIR_TYPE.VENDOR};
        for (DIR_TYPE type : types) {
            result = module.getConsoleDirectory(type);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getConsoleDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetConsoleDirectory_CakePhpModuleDIR_TYPE_String() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        cakeVersion = "cake1.3";
        app = "custom";
        String pluginDir = "NbtestAppplugins";
        // retrun view dir
        module = CakePhpModule.forPhpModule(phpModule1);
        // return null
        expResult = null;
        for (DIR_TYPE type : DIR_TYPE.values()) {
            result = module.getConsoleDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }

        // cake2
        //=================================================
        cakeVersion = "cake2.2";
        app = "myapp";
        pluginDir = "NbtestAppPlugin";
        // return view dir
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Console");
        result = module.getConsoleDirectory(DIR_TYPE.APP_PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        pluginDir = "NbtestPlugin";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Console");
        result = module.getConsoleDirectory(DIR_TYPE.PLUGIN, pluginDir);
        assertNotNull(result);
        assertEquals(expResult, result);

        // return null
        expResult = null;
        DIR_TYPE[] types = {null, DIR_TYPE.APP, DIR_TYPE.APP_LIB, DIR_TYPE.APP_VENDOR, DIR_TYPE.CORE, DIR_TYPE.VENDOR};
        for (DIR_TYPE type : types) {
            result = module.getConsoleDirectory(type, pluginDir);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getCakePhpDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetCakePhpDirectory() {
        FileObject expResult;
        FileObject result;
        cakeDirectoryPath = "../";
        // cake1
        //=================================================
        useProjectPath = false;
        cakeVersion = "cake1.3";
        expResult = getCakePhpDir();
        app = "app";
        result = CakePhpModule.getCakePhpDirectory(phpModule1);
        assertEquals(expResult, result);

        // use relative path
        useProjectPath = true;
        app = "custom";
        result = CakePhpModule.getCakePhpDirectory(phpModule1);
        assertEquals(expResult, result);

        // cake2
        //=================================================
        useProjectPath = false;
        cakeVersion = "cake2.2";
        expResult = getCakePhpDir();
        app = "myapp";
        result = CakePhpModule.getCakePhpDirectory(phpModule2);
        assertEquals(expResult, result);

        // use relative path
        useProjectPath = true;
        app = "app";
        result = CakePhpModule.getCakePhpDirectory(phpModule2);
        assertEquals(expResult, result);
        useProjectPath = false;

    }

    /**
     * Test of getDirectory method, of class CakePhpModule.
     */
    @Test
    public void testGetDirectory() {
        FileObject expResult;
        FileObject result;
        // cake1
        //=================================================
        useProjectPath = false;
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        expResult = getCakePhpDir().getFileObject(app);
        result = module.getDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject(app + "/libs");
        result = module.getDirectory(DIR_TYPE.APP_LIB);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject(app + "/plugins");
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject(app + "/vendors");
        result = module.getDirectory(DIR_TYPE.APP_VENDOR);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("cake");
        result = module.getDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("plugins");
        result = module.getDirectory(DIR_TYPE.PLUGIN);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("vendors");
        result = module.getDirectory(DIR_TYPE.VENDOR);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        result = module.getDirectory(null);
        assertEquals(expResult, result);

        // cake2
        //=================================================
        useProjectPath = false;
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        expResult = getCakePhpDir().getFileObject(app);
        result = module.getDirectory(DIR_TYPE.APP);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject(app + "/Lib");
        result = module.getDirectory(DIR_TYPE.APP_LIB);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject(app + "/Plugin");
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject(app + "/Vendor");
        result = module.getDirectory(DIR_TYPE.APP_VENDOR);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("lib/Cake");
        result = module.getDirectory(DIR_TYPE.CORE);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("plugins");
        result = module.getDirectory(DIR_TYPE.PLUGIN);
        assertNotNull(result);
        assertEquals(expResult, result);
        expResult = getCakePhpDir().getFileObject("vendors");
        result = module.getDirectory(DIR_TYPE.VENDOR);
        assertNotNull(result);
        assertEquals(expResult, result);

        expResult = null;
        result = module.getDirectory(null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getFiles method, of class CakePhpModule.
     */
    @Test
    public void testGetFiles() {
        List<FileObject> expResult;
        List<FileObject> result;
        // cake1
        //=================================================
        useProjectPath = false;
        cakeVersion = "cake1.3";
        app = "custom";
        module = CakePhpModule.forPhpModule(phpModule1);
        FileObject controllers = getCakePhpDir().getFileObject(app + "/controllers");
        expResult = Arrays.asList(controllers.getFileObject("nbtests_controller.php"));
        result = module.getFiles(controllers, new DefaultFileFilter());
        assertNotNull(result);
        assertEquals(expResult, result);
        result = module.getFiles(controllers, null);
        assertNotNull(result);
        assertEquals(expResult, result);

        result = module.getFiles(getCakePhpDir().getFileObject(app + "/config/core.php"), new DefaultFileFilter());
        assertEquals(Collections.EMPTY_LIST, result);
        result = module.getFiles(null, new DefaultFileFilter());
        assertEquals(Collections.EMPTY_LIST, result);

        // cake2
        //=================================================
        useProjectPath = false;
        cakeVersion = "cake2.2";
        app = "myapp";
        module = CakePhpModule.forPhpModule(phpModule2);
        controllers = getCakePhpDir().getFileObject(app + "/Controller");
        expResult = Arrays.asList(controllers.getFileObject("NbtestsController.php"), controllers.getFileObject("PagesController.php"));
        result = module.getFiles(controllers, new DefaultFileFilter());
        assertNotNull(result);
        assertEquals(expResult.size(), result.size());
        assertEquals(true, result.containsAll(expResult));
        result = module.getFiles(controllers, null);
        assertNotNull(result);
        assertEquals(expResult.size(), result.size());
        assertEquals(true, result.containsAll(expResult));

        result = module.getFiles(getCakePhpDir().getFileObject(app + "/Config/core.php"), new DefaultFileFilter());
        assertEquals(Collections.EMPTY_LIST, result);
        result = module.getFiles(null, new DefaultFileFilter());
        assertEquals(Collections.EMPTY_LIST, result);
    }

    /**
     * Test of getFileNameWithExt method, of class CakePhpModule.
     */
    @Test
    public void testGetFileNameWithExt() {
    }

    /**
     * Test of isController method, of class CakePhpModule.
     */
    @Test
    public void testIsController() {
    }

    /**
     * Test of getController method, of class CakePhpModule.
     */
    @Test
    public void testGetController() {
    }

    /**
     * Test of isModel method, of class CakePhpModule.
     */
    @Test
    public void testIsModel() {
    }

    /**
     * Test of isView method, of class CakePhpModule.
     */
    @Test
    public void testIsView() {
    }

    /**
     * Test of getView method, of class CakePhpModule.
     */
    @Test
    public void testGetView_FileObject_String() {
    }

    /**
     * Test of getView method, of class CakePhpModule.
     */
    @Test
    public void testGetView_3args() {
    }

    /**
     * Test of isHelper method, of class CakePhpModule.
     */
    @Test
    public void testIsHelper() {
    }

    /**
     * Test of isComponent method, of class CakePhpModule.
     */
    @Test
    public void testIsComponent() {
    }

    /**
     * Test of getViewFolderName method, of class CakePhpModule.
     */
    @Test
    public void testGetViewFolderName() {
    }

    /**
     * Test of createView method, of class CakePhpModule.
     */
    @Test
    public void testCreateView() throws Exception {
    }

    /**
     * Test of forPhpModule method, of class CakePhpModule.
     */
    @Test
    public void testForPhpModule() {
    }

    private FileObject getCakePhpDir() {
        return FileUtil.toFileObject(getDataDir()).getFileObject(CAKES_DIRECTORY_NAME + "/" + cakeVersion);
    }

    private void init() {
        cakeVersion = "cake1.3";
        phpModule1 = new TestPhpModule();
        module = CakePhpModule.forPhpModule(phpModule1);
        cakeVersion = "cake2.2";
        phpModule2 = new TestPhpModule();
        module = CakePhpModule.forPhpModule(phpModule2);
    }

    private class TestPhpModule implements PhpModule {

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public String getDisplayName() {
            return "testPhpModule";
        }

        @Override
        public FileObject getProjectDirectory() {
            if (useProjectPath) {
                return FileUtil.toFileObject(getDataDir()).getFileObject(CAKES_DIRECTORY_NAME + "/" + cakeVersion + "/" + app);
            }
            return FileUtil.toFileObject(getDataDir()).getFileObject(CAKES_DIRECTORY_NAME + "/" + cakeVersion);
        }

        @Override
        public FileObject getSourceDirectory() {
            if (useProjectPath) {
                return FileUtil.toFileObject(getDataDir()).getFileObject(CAKES_DIRECTORY_NAME + "/" + cakeVersion + "/" + app);
            }
            return FileUtil.toFileObject(getDataDir()).getFileObject(CAKES_DIRECTORY_NAME + "/" + cakeVersion);
        }

        @Override
        public Preferences getPreferences(Class<?> type, boolean bln) {
            return new Preferences() {
                @Override
                public void put(String key, String value) {
                }

                @Override
                public String get(String key, String def) {
                    String ret = "";
                    if (key.equals("app-name")) {
                        ret = app;
                    } else if (key.equals("cake-php-dir-path")) {
                        ret = cakeDirectoryPath;
                    }
                    return ret;
                }

                @Override
                public void remove(String key) {
                }

                @Override
                public void clear() throws BackingStoreException {
                }

                @Override
                public void putInt(String key, int value) {
                }

                @Override
                public int getInt(String key, int def) {
                    return 0;
                }

                @Override
                public void putLong(String key, long value) {
                }

                @Override
                public long getLong(String key, long def) {
                    return 0;
                }

                @Override
                public void putBoolean(String key, boolean value) {
                }

                @Override
                public boolean getBoolean(String key, boolean def) {
                    if (key.equals("use-project-directory")) {
                        return useProjectPath;
                    } else if (key.equals("auto-create-view")) {
                        return useAutoCreate;
                    }
                    return true;
                }

                @Override
                public void putFloat(String key, float value) {
                }

                @Override
                public float getFloat(String key, float def) {
                    return 0;
                }

                @Override
                public void putDouble(String key, double value) {
                }

                @Override
                public double getDouble(String key, double def) {
                    return 0;
                }

                @Override
                public void putByteArray(String key, byte[] value) {
                }

                @Override
                public byte[] getByteArray(String key, byte[] def) {
                    return null;
                }

                @Override
                public String[] keys() throws BackingStoreException {
                    return null;
                }

                @Override
                public String[] childrenNames() throws BackingStoreException {
                    return null;
                }

                @Override
                public Preferences parent() {
                    return null;
                }

                @Override
                public Preferences node(String pathName) {
                    return null;
                }

                @Override
                public boolean nodeExists(String pathName) throws BackingStoreException {
                    return false;
                }

                @Override
                public void removeNode() throws BackingStoreException {
                }

                @Override
                public String name() {
                    return "test";
                }

                @Override
                public String absolutePath() {
                    return null;
                }

                @Override
                public boolean isUserNode() {
                    return true;
                }

                @Override
                public String toString() {
                    return null;
                }

                @Override
                public void flush() throws BackingStoreException {
                }

                @Override
                public void sync() throws BackingStoreException {
                }

                @Override
                public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
                }

                @Override
                public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
                }

                @Override
                public void addNodeChangeListener(NodeChangeListener ncl) {
                }

                @Override
                public void removeNodeChangeListener(NodeChangeListener ncl) {
                }

                @Override
                public void exportNode(OutputStream os) throws IOException, BackingStoreException {
                }

                @Override
                public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
                }
            };
        }

        @Override
        public boolean isBroken() {
            return false;
        }

        @Override
        public void notifyPropertyChanged(PropertyChangeEvent pce) {
        }

        @Override
        public FileObject getTestDirectory(FileObject fo) {
            return null;
        }

        @Override
        public List<FileObject> getTestDirectories() {
            return Collections.emptyList();
        }

        @Override
        public Lookup getLookup() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
