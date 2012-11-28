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

import java.util.zip.ZipEntry;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author junichi11
 */
public class CakeZipEntryFilterTest extends NbTestCase {

    public CakeZipEntryFilterTest(String name) {
        super(name);
    }

    /**
     * Test of accept method, of class CakeZipEntryFilter.
     */
    @Test
    public void testAccept() {

        CakeZipEntryFilter filter = null;
        filter = new CakeZipEntryFilter(false);
        assertTrue(filter.accept(new ZipEntry("cakephp/app/empty")));
        assertTrue(filter.accept(new ZipEntry("cakephp/app/")));
        assertTrue(filter.accept(new ZipEntry("cakephp/app/correct.php")));
        assertTrue(filter.accept(new ZipEntry("app/empty")));
        assertTrue(filter.accept(new ZipEntry("app/empty/")));
        assertTrue(filter.accept(new ZipEntry("app/")));
        assertTrue(filter.accept(new ZipEntry("lib/")));
        assertTrue(filter.accept(new ZipEntry("plugins/")));
        assertTrue(filter.accept(new ZipEntry("vendors/")));

        assertFalse(filter.accept(new ZipEntry("empty/")));

        // delete empty file
        filter = new CakeZipEntryFilter(true);
        assertTrue(filter.accept(new ZipEntry("cakephp/app/")));
        assertTrue(filter.accept(new ZipEntry("cakephp/app/empty/")));
        assertTrue(filter.accept(new ZipEntry("cakephp/app/Empty")));
        assertTrue(filter.accept(new ZipEntry("app/")));
        assertTrue(filter.accept(new ZipEntry("lib/")));
        assertTrue(filter.accept(new ZipEntry("plugins/")));
        assertTrue(filter.accept(new ZipEntry("vendors/")));

        assertFalse(filter.accept(new ZipEntry("cakephp/app/empty")));
        assertFalse(filter.accept(new ZipEntry("empty/")));
    }

    /**
     * Test of getPath method, of class CakeZipEntryFilter.
     */
    @Test
    public void testGetPath() {
        CakeZipEntryFilter filter = new CakeZipEntryFilter(false);
        assertEquals("app/", filter.getPath(new ZipEntry("cakephp/app/")));
        assertEquals("app/", filter.getPath(new ZipEntry("app/")));
        assertEquals("lib/", filter.getPath(new ZipEntry("lib/")));
        assertEquals("plugins/", filter.getPath(new ZipEntry("plugins/")));
        assertEquals("vendors/", filter.getPath(new ZipEntry("vendors/")));
        assertEquals("cake/", filter.getPath(new ZipEntry("cake/")));
        assertEquals("vendors", filter.getPath(new ZipEntry("vendors")));
        assertEquals("app/correct.php", filter.getPath(new ZipEntry("cakephp/app/correct.php")));
        assertEquals("app/correct.php", filter.getPath(new ZipEntry("app/correct.php")));

        assertEquals("", filter.getPath(new ZipEntry("")));
    }
}
