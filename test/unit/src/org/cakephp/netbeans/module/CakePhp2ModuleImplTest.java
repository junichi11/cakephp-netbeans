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
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.cakephp.netbeans.modules.CakePhp2ModuleImpl;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.versions.VersionsFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public class CakePhp2ModuleImplTest extends NbTestCase {

    PhpModule phpModule;
    CakePhp2ModuleImpl module;
    String app;
    String cakeDirectoryPath;
    boolean useProjectPath;
    boolean useAutoCreate;
    String cakeVersion;
    private static final String CAKES_DIRECTORY_NAME = "cakes";

    public CakePhp2ModuleImplTest(String name) {
        super(name);
    }

    @Before
    @Override
    public void setUp() {
        app = "app";
        cakeDirectoryPath = "";
        useProjectPath = false;
        useAutoCreate = true;
        cakeVersion = "cake2.2";
        init();
    }

    @After
    @Override
    public void tearDown() {
    }

    /**
     * Test of getDirectory method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testGetDirectory_3args() {
        FileObject result;
        String path;
        String fullPath;

        // APP
        //=================================================
        // don't exist plugin name
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.BEHAVIOR, null);
        path = app + "/Model/Behavior";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.COMPONENT, null);
        path = app + "/Controller/Component";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.CONFIG, null);
        path = app + "/Config";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.CONTROLLER, null);
        path = app + "/Controller";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.HELPER, null);
        path = app + "/View/Helper";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.MODEL, null);
        path = app + "/Model";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.TEST, null);
        path = app + "/Test";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.VIEW, null);
        path = app + "/View";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.CONSOLE, null);
        path = app + "/Console";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.WEBROOT, null);
        path = app + "/webroot";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.NONE, null);
        path = app;
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP, null, null);
        path = app;
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));

        // exist plugin name
        result = module.getDirectory(DIR_TYPE.APP, null, "Nbtest");
        assertNull(result);
        result = module.getDirectory(DIR_TYPE.APP, null, "NbtestAppPlugin");
        assertNull(result);
        result = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.CONTROLLER, "NbtestAppPlugin");
        assertNull(result);
        result = module.getDirectory(DIR_TYPE.APP, null, "");
        assertNull(result);

        // APP_PLUGIN
        //=================================================
        String appPlugin = app + "/Plugin/NbtestAppPlugin";
        String pluginName = "NbtestAppPlugin";

        // exist plguin name
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.BEHAVIOR, pluginName);
        path = appPlugin + "/Model/Behavior";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.COMPONENT, pluginName);
        path = appPlugin + "/Controller/Component";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.CONFIG, pluginName);
        path = appPlugin + "/Config";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.CONTROLLER, pluginName);
        path = appPlugin + "/Controller";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.HELPER, pluginName);
        path = appPlugin + "/View/Helper";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.MODEL, pluginName);
        path = appPlugin + "/Model";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.TEST, pluginName);
        path = appPlugin + "/Test";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.VIEW, pluginName);
        path = appPlugin + "/View";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.CONSOLE, pluginName);
        path = appPlugin + "/Console";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.WEBROOT, pluginName);
        path = appPlugin + "/webroot";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.NONE, pluginName);
        path = appPlugin;
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, null, pluginName);
        path = appPlugin;
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, null, null);
        path = app + "/Plugin";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));

        // don't extist plugin name
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.CONTROLLER, "NbtestPlugin");
        assertNull(result);
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.CONTROLLER, "");
        assertNull(result);
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.CONSOLE, null);
        assertNull(result);

        // PLUGIN
        //=================================================
        String plugins = "plugins/NbtestPlugin";
        pluginName = "NbtestPlugin";

        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.BEHAVIOR, pluginName);
        path = plugins + "/Model/Behavior";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.COMPONENT, pluginName);
        path = plugins + "/Controller/Component";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.CONFIG, pluginName);
        path = plugins + "/Config";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.CONTROLLER, pluginName);
        path = plugins + "/Controller";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.HELPER, pluginName);
        path = plugins + "/View/Helper";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.MODEL, pluginName);
        path = plugins + "/Model";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.TEST, pluginName);
        path = plugins + "/Test";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.VIEW, pluginName);
        path = plugins + "/View";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.CONSOLE, pluginName);
        path = plugins + "/Console";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.WEBROOT, pluginName);
        path = plugins + "/webroot";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.NONE, pluginName);
        path = plugins;
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, null, pluginName);
        path = plugins;
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.PLUGIN, null, null);
        path = "/plugins";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));

        // don't exisit plugin name
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.CONTROLLER, "Nbtest");
        assertNull(result);
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.HELPER, "");
        assertNull(result);
        result = module.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.BEHAVIOR, null);
        assertNull(result);

        // CORE
        //=================================================
        String cake = "lib/Cake";
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.BEHAVIOR, null);
        path = cake + "/Model/Behavior";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.COMPONENT, null);
        path = cake + "/Controller/Component";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.CONFIG, null);
        path = cake + "/Config";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.CONTROLLER, null);
        path = cake + "/Controller";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.HELPER, null);
        path = cake + "/View/Helper";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.MODEL, null);
        path = cake + "/Model";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.TEST, null);
        path = cake + "/Test";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.VIEW, null);
        path = cake + "/View";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.CONSOLE, null);
        path = cake + "/Console";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.NONE, null);
        path = cake;
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.CORE, null, null);
        path = cake;
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));

        // webroot
        result = module.getDirectory(DIR_TYPE.CORE, FILE_TYPE.WEBROOT, null);
        assertNull(result);

        // exisit plugin name
        for (FILE_TYPE type : FILE_TYPE.values()) {
            result = module.getDirectory(DIR_TYPE.CORE, type, "NbtestAppPlugin");
            assertNull(result);
            result = module.getDirectory(DIR_TYPE.CORE, type, "");
            assertNull(result);
        }

        // APP_LIB
        //=================================================
        result = module.getDirectory(DIR_TYPE.APP_LIB, null, null);
        path = app + "/Lib";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_LIB, FILE_TYPE.NONE, null);
        path = app + "/Lib";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));

        // exist plugin name
        FILE_TYPE[] types = FILE_TYPE.values();
        for (FILE_TYPE type : types) {
            result = module.getDirectory(DIR_TYPE.APP_LIB, type, "NbtestPlugin");
            assertNull(result);
        }

        // exist file type
        for (FILE_TYPE type : types) {
            if (type == FILE_TYPE.NONE) {
                continue;
            }
            result = module.getDirectory(DIR_TYPE.APP_LIB, type, null);
            assertNull(result);
        }

        // APP_VENDOR
        //=================================================
        result = module.getDirectory(DIR_TYPE.APP_VENDOR, null, null);
        path = app + "/Vendor";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.APP_VENDOR, FILE_TYPE.NONE, null);
        path = app + "/Vendor";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));

        // exist plugin name
        for (FILE_TYPE type : types) {
            result = module.getDirectory(DIR_TYPE.APP_VENDOR, type, "NbtestPlugin");
            assertNull(result);
        }

        // exist file type
        for (FILE_TYPE type : types) {
            if (type == FILE_TYPE.NONE) {
                continue;
            }
            result = module.getDirectory(DIR_TYPE.APP_VENDOR, type, null);
            assertNull(result);
        }

        // VENDOR
        //=================================================
        result = module.getDirectory(DIR_TYPE.VENDOR, null, null);
        path = "vendors";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));
        result = module.getDirectory(DIR_TYPE.VENDOR, FILE_TYPE.NONE, null);
        path = "vendors";
        fullPath = result.getPath();
        assertTrue(fullPath.endsWith(path));

        // exist plugin name
        for (FILE_TYPE type : types) {
            result = module.getDirectory(DIR_TYPE.VENDOR, type, "NbtestPlugin");
            assertNull(result);
        }

        // exist file type
        for (FILE_TYPE type : types) {
            if (type == FILE_TYPE.NONE) {
                continue;
            }
            result = module.getDirectory(DIR_TYPE.VENDOR, type, null);
            assertNull(result);
        }

        // NULL
        //=================================================
        result = module.getDirectory(null, FILE_TYPE.MODEL, "NbtestPlugin");
        assertNull(result);
    }

    /**
     * Test of getDirectory method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testGetDirectory_CakePhpModuleDIR_TYPE() {
        FileObject result;
        result = module.getDirectory(DIR_TYPE.APP);
        assertTrue(result.getPath().endsWith(app));
        result = module.getDirectory(DIR_TYPE.APP_LIB);
        assertTrue(result.getPath().endsWith(app + "/Lib"));
        result = module.getDirectory(DIR_TYPE.APP_PLUGIN);
        assertTrue(result.getPath().endsWith(app + "/Plugin"));
        result = module.getDirectory(DIR_TYPE.APP_VENDOR);
        assertTrue(result.getPath().endsWith(app + "/Vendor"));
        result = module.getDirectory(DIR_TYPE.CORE);
        assertTrue(result.getPath().endsWith("/lib/Cake"));
        result = module.getDirectory(DIR_TYPE.PLUGIN);
        assertTrue(result.getPath().endsWith("plugins"));
        result = module.getDirectory(DIR_TYPE.VENDOR);
        assertTrue(result.getPath().endsWith("vendors"));

        result = module.getDirectory(null);
        assertNull(result);
    }

    /**
     * Test of getFileNameWithExt method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testGetFileNameWithExt() {
        String name = "TestFile";
        String result = "";
        result = module.getFileNameWithExt(FILE_TYPE.CONTROLLER, name);
        assertEquals(name + "Controller.php", result);
        result = module.getFileNameWithExt(FILE_TYPE.BEHAVIOR, name);
        assertEquals(name + "Behavior.php", result);
        result = module.getFileNameWithExt(FILE_TYPE.COMPONENT, name);
        assertEquals(name + "Component.php", result);
        result = module.getFileNameWithExt(FILE_TYPE.CONFIG, name);
        assertEquals("TestFile.php", result);
        result = module.getFileNameWithExt(FILE_TYPE.CONSOLE, name);
        assertEquals("TestFile.php", result);
        result = module.getFileNameWithExt(FILE_TYPE.HELPER, name);
        assertEquals(name + "Helper.php", result);
        result = module.getFileNameWithExt(FILE_TYPE.MODEL, name);
        assertEquals(name + ".php", result);
        result = module.getFileNameWithExt(FILE_TYPE.NONE, name);
        assertEquals("TestFile.php", result);
        result = module.getFileNameWithExt(FILE_TYPE.TEST, name);
        assertEquals("TestFile.php", result);
        result = module.getFileNameWithExt(FILE_TYPE.WEBROOT, name);
        assertEquals("TestFile.php", result);
        result = module.getFileNameWithExt(null, name);
        assertEquals("TestFile.php", result);

        result = module.getFileNameWithExt(FILE_TYPE.VIEW, name);
        assertEquals("test_file.ctp", result);
    }

    /**
     * Test of toViewDirectoryName method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testToViewDirectoryName() {
        String result;
        result = module.toViewDirectoryName("Sample");
        assertEquals("Sample", result);
        result = module.toViewDirectoryName("TestDirectory");
        assertEquals("TestDirectory", result);

        result = module.toViewDirectoryName(null);
        assertEquals(null, result);
    }

    /**
     * Test of isView method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testIsView() {
        FileObject fo = null;
        fo = getCakePhpDir().getFileObject(app + "/View/Nbtests/index.ctp");
        boolean result;
        result = module.isView(fo);
        assertTrue(result);

        fo = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        result = module.isView(fo);
        assertFalse(result);
        result = module.isView(null);
        assertFalse(result);
    }

    /**
     * Test of getView method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testGetView_FileObject_String() {
        FileObject controller;
        FileObject expResult;
        controller = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        String viewName = "index";
        expResult = getCakePhpDir().getFileObject(app + "/View/Nbtests/index.ctp");
        FileObject result;
        result = module.getView(controller, viewName);
        assertEquals(expResult, result);

        controller = getCakePhpDir().getFileObject(app + "/View/Nbtests/index.ctp");
        result = module.getView(controller, viewName);
        assertNull(result);
        assertNull(module.getView(controller, null));

        // app/Plugin
        controller = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/Controller/NbtestsController.php");
        viewName = "index";
        expResult = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/View/Nbtests/index.ctp");
        result = module.getView(controller, viewName);
        assertEquals(expResult, result);

        controller = getCakePhpDir().getFileObject(app + "/Plugin/NbtestAppPlugin/View/Nbtests/index.ctp");
        result = module.getView(controller, viewName);
        assertNull(result);
        assertNull(module.getView(controller, null));

        // plugins
        controller = getCakePhpDir().getFileObject("plugins/NbtestPlugin/Controller/NbtestsController.php");
        viewName = "index";
        expResult = getCakePhpDir().getFileObject("plugins/NbtestPlugin/View/Nbtests/index.ctp");
        result = module.getView(controller, viewName);
        assertEquals(expResult, result);

        controller = getCakePhpDir().getFileObject("plugins/NbtestPlugin/View/Nbtests/index.ctp");
        result = module.getView(controller, viewName);
        assertNull(result);
        assertNull(module.getView(controller, null));
    }

    /**
     * Test of getView method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testGetView_3args() {
        FileObject controller = null;
        String viewName = "index";
        FileObject theme = null;
        FileObject expResult = null;
        FileObject result;
        theme = getCakePhpDir().getFileObject(app + "/View/Themed/NbtestTheme");
        controller = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        expResult = getCakePhpDir().getFileObject(app + "/View/Themed/NbtestTheme/Nbtests/index.ctp");
        result = module.getView(controller, viewName, theme);
        assertEquals(expResult, result);

        controller = getCakePhpDir().getFileObject(app + "/View/Nbtests/index.ctp");
        result = module.getView(controller, viewName, theme);
        assertNull(result);
    }

    /**
     * Test of isController method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testIsController() {
        FileObject fo = null;
        boolean result;
        fo = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        result = module.isController(fo);
        assertTrue(result);

        fo = getCakePhpDir().getFileObject(app + "/View/Nbtests/index.ctp");
        result = module.isController(fo);
        assertFalse(result);

        result = module.isController(null);
        assertFalse(result);
    }

    /**
     * Test of getController method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testGetController() {
        FileObject view;
        FileObject expResult;
        view = getCakePhpDir().getFileObject(app + "/View/Nbtests/index.ctp");
        expResult = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        FileObject result;
        result = module.getController(view);
        assertEquals(expResult, result);

        view = getCakePhpDir().getFileObject(app + "/View/Themed/NbtestTheme/Nbtests/index.ctp");
        result = module.getController(view);
        assertEquals(expResult, result);

        view = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        assertNull(module.getController(view));
    }

    /**
     * Test of isModel method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testIsModel() {
        FileObject fo = null;
        boolean result;
        fo = getCakePhpDir().getFileObject(app + "/Model/Nbtest.php");
        result = module.isModel(fo);
        assertTrue(result);

        fo = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        result = module.isModel(fo);
        assertFalse(result);
    }

    /**
     * Test of isBehavior method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testIsBehavior() {
        FileObject fo = null;
        boolean result;
        fo = getCakePhpDir().getFileObject(app + "/Model/Behavior/NbtestBehavior.php");
        result = module.isBehavior(fo);
        assertTrue(result);

        fo = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        result = module.isBehavior(fo);
        assertFalse(result);
    }

    /**
     * Test of isComponent method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testIsComponent() {
        FileObject fo = null;
        boolean result;
        fo = getCakePhpDir().getFileObject(app + "/Controller/Component/NbtestComponent.php");
        result = module.isComponent(fo);
        assertTrue(result);

        fo = getCakePhpDir().getFileObject(app + "/Model/Nbtest.php");
        result = module.isComponent(fo);
        assertFalse(result);
    }

    /**
     * Test of isHelper method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testIsHelper() {
        FileObject fo = null;
        boolean result;
        fo = getCakePhpDir().getFileObject(app + "/View/Helper/NbtestHelper.php");
        result = module.isHelper(fo);
        assertTrue(result);

        fo = getCakePhpDir().getFileObject(app + "/Controller/Component/NbtestComponent.php");
        result = module.isHelper(fo);
        assertFalse(result);
    }

    /**
     * Test of getViewFolderName method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testGetViewFolderName() {
        String controllerFileName = "";
        String expResult = "";
        String result;
        controllerFileName = "NbtestsController";
        expResult = "Nbtests";
        result = module.getViewFolderName(controllerFileName);
        assertEquals(expResult, result);
        controllerFileName = "Nbtests";
        expResult = "Nbtests";
        result = module.getViewFolderName(controllerFileName);
        assertEquals(expResult, result);
        controllerFileName = "nbtests_controller";
        expResult = "nbtests_controller";
        result = module.getViewFolderName(controllerFileName);
        assertEquals(expResult, result);
        controllerFileName = "";
        expResult = "";
        result = module.getViewFolderName(controllerFileName);
        assertEquals(expResult, result);
    }

    /**
     * Test of createView method, of class CakePhp2ModuleImpl.
     */
    @Test
    public void testCreateView() throws Exception {
        FileObject controller = null;
        PhpClass phpElement = null;
        FileObject result;
        controller = getCakePhpDir().getFileObject(app + "/Controller/NbtestsController.php");
        phpElement = new PhpClass("NbtestsController", "NbtestsController").addMethod("testaction", "testaction");
        PhpClass.Method method = null;
        for (PhpClass.Method m : phpElement.getMethods()) {
            method = m;
            break;
        }
        result = module.createView(controller, method);
        assertNotNull(result);
        if (result != null) {
            result.delete();
        }
    }

    @Test
    public void testUseProjectDirectoryPath() {
        app = "app";
        useProjectPath = true;
        cakeDirectoryPath = "../";
        try {
            testCreateView();
        } catch (Exception ex) {
            fail();
        }

        testGetController();
        testGetDirectory_3args();
        testGetDirectory_CakePhpModuleDIR_TYPE();
        testGetFileNameWithExt();
        testGetViewFolderName();
        testGetView_3args();
        testGetView_FileObject_String();
        testIsComponent();
        testIsController();
        testIsHelper();
        testIsModel();
        testIsView();
        testToViewDirectoryName();
    }

    @Test
    public void testUseCustomAppName() {
        app = "myapp";
        useProjectPath = true;
        cakeDirectoryPath = "../";
        try {
            testCreateView();
        } catch (Exception ex) {
            fail();
        }

        testGetController();
        testGetDirectory_3args();
        testGetDirectory_CakePhpModuleDIR_TYPE();
        testGetFileNameWithExt();
        testGetViewFolderName();
        testGetView_3args();
        testGetView_FileObject_String();
        testIsComponent();
        testIsController();
        testIsHelper();
        testIsModel();
        testIsView();
        testToViewDirectoryName();
    }

    private FileObject getCakePhpDir() {
        return FileUtil.toFileObject(getDataDir()).getFileObject(CAKES_DIRECTORY_NAME + "/" + cakeVersion);
    }

    private void init() {
        phpModule = new PhpModule() {
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
        };
        module = new CakePhp2ModuleImpl(phpModule, VersionsFactory.getInstance().create(phpModule));
    }
}
