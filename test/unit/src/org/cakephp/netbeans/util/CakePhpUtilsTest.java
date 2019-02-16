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
package org.cakephp.netbeans.util;

import java.io.IOException;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

public class CakePhpUtilsTest extends NbTestCase {

    public CakePhpUtilsTest(String name) {
        super(name);
    }

    @Test
    public void testActionName() {
        assertEquals("index", CakePhpUtils.getActionName("index"));
        assertEquals("myIndex", CakePhpUtils.getActionName("my_index"));
    }

    @Test
    public void testViewName() {
        assertEquals("index", CakePhpUtils.getViewFileName("index"));
        assertEquals("my_index", CakePhpUtils.getViewFileName("myIndex"));
    }

    @Test
    public void testIsControllerName() {
        assertTrue(CakePhpUtils.isControllerName("PostsController"));

        assertFalse(CakePhpUtils.isControllerName("Postscontroller"));
        assertFalse(CakePhpUtils.isControllerName("PostsHelper"));
    }

    @Test
    public void testIsCtpFile() {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject root = fs.getRoot();
        try {
            FileObject indexCtp = root.createData("index", "ctp");
            FileObject indexPhp = root.createData("index", "php");
            assertTrue(CakePhpUtils.isCtpFile(indexCtp));
            assertFalse(CakePhpUtils.isCtpFile(indexPhp));
            assertFalse(CakePhpUtils.isCtpFile(null));
            indexCtp.delete();
            indexPhp.delete();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testIsAbsolutePath() {
        assertTrue(CakePhpUtils.isAbsolutePath("/common"));
        assertTrue(CakePhpUtils.isAbsolutePath("/Common/test"));

        assertFalse(CakePhpUtils.isAbsolutePath("test"));
        assertFalse(CakePhpUtils.isAbsolutePath("Common/test"));
        assertFalse(CakePhpUtils.isAbsolutePath("//Common/test"));
        assertFalse(CakePhpUtils.isAbsolutePath("///Common/test"));
        assertFalse(CakePhpUtils.isAbsolutePath(null));
        assertFalse(CakePhpUtils.isAbsolutePath(""));
    }

    @Test
    public void testAppendCtpExt() {
        assertEquals("/common.ctp", CakePhpUtils.appendCtpExt("/common"));
        assertEquals("common.ctp", CakePhpUtils.appendCtpExt("common"));

        assertEquals("", CakePhpUtils.appendCtpExt(""));
        assertEquals("", CakePhpUtils.appendCtpExt(null));
    }

    @Test
    public void testPluginSplit() {
        String[] pluginSplit = CakePhpUtils.pluginSplit("Some.common");
        assertTrue(pluginSplit.length == 2);
        assertEquals("Some", pluginSplit[0]);
        assertEquals("common", pluginSplit[1]);

        pluginSplit = CakePhpUtils.pluginSplit("MyPlgin.");
        assertTrue(pluginSplit.length == 2);
        assertEquals("MyPlgin", pluginSplit[0]);
        assertEquals("", pluginSplit[1]);

        pluginSplit = CakePhpUtils.pluginSplit("index");
        assertTrue(pluginSplit.length == 1);
        assertEquals("index", pluginSplit[0]);

        pluginSplit = CakePhpUtils.pluginSplit("");
        assertTrue(pluginSplit.length == 1);
        assertEquals("", pluginSplit[0]);

        pluginSplit = CakePhpUtils.pluginSplit(null);
        assertEquals(null, pluginSplit);
    }

    @Test
    public void testDetachQuotes() {
        assertEquals("", CakePhpUtils.detachQuotes("")); // NOI18N
        assertEquals("", CakePhpUtils.detachQuotes("\"\"")); // NOI18N
        assertEquals("", CakePhpUtils.detachQuotes("''")); // NOI18N
        assertEquals("test", CakePhpUtils.detachQuotes("\"test\"")); // NOI18N
        assertEquals("test", CakePhpUtils.detachQuotes("test")); // NOI18N
        assertEquals("message", CakePhpUtils.detachQuotes("'message'")); // NOI18N
        assertEquals("\"test", CakePhpUtils.detachQuotes("\"test")); // NOI18N
        assertEquals("test\"", CakePhpUtils.detachQuotes("test\"")); // NOI18N
        assertEquals("'test", CakePhpUtils.detachQuotes("'test")); // NOI18N
        assertEquals("test'", CakePhpUtils.detachQuotes("test'")); // NOI18N
        assertEquals("'test'", CakePhpUtils.detachQuotes("\"'test'\"")); // NOI18N
        assertEquals("\"test\"test", CakePhpUtils.detachQuotes("\"test\"test")); // NOI18N

        assertEquals(null, CakePhpUtils.detachQuotes(null)); // NOI18N
    }
}
