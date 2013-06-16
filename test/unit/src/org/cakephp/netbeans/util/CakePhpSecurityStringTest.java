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

import java.security.NoSuchAlgorithmException;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author junichi11
 */
public class CakePhpSecurityStringTest extends NbTestCase {

    public CakePhpSecurityStringTest(String name) {
        super(name);
    }

    /**
     * Test of generateSecurityKey method, of class CakePhpSecurityString.
     */
    @Test
    public void testGenerateSecurityKey() throws Exception {
        System.out.println("generateSecurityKey null");
        String result = CakePhpSecurityString.generateSecurityKey(null);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));
    }

    /**
     * Test of generateSecurityKey method, of class CakePhpSecurityString.
     */
    @Test
    public void testGenerateSecurityKeySHA1() throws Exception {
        System.out.println("generateSecurityKey SHA-1");
        String algorithm = "SHA-1";
        String result = CakePhpSecurityString.generateSecurityKey(algorithm);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));
    }

    /**
     * Test of generateSecurityKey method, of class CakePhpSecurityString.
     */
    @Test
    public void testGenerateSecurityKeySHA256() throws Exception {
        System.out.println("generateSecurityKey SHA-256");
        String algorithm = "SHA-256";
        String result = CakePhpSecurityString.generateSecurityKey(algorithm);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));
    }

    /**
     * Test of generateSecurityKey method, of class CakePhpSecurityString.
     */
    @Test
    public void testGenerateSecurityKeyMD5() throws Exception {
        System.out.println("generateSecurityKey MD5");
        String algorithm = "MD5";
        String result = CakePhpSecurityString.generateSecurityKey(algorithm);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));
    }

    /**
     * Test of generateSecurityKey method, of class CakePhpSecurityString.
     */
    @Test
    public void testGenerateSecurityKeyException() {
        System.out.println("generateSecurityKey Exception");
        String algorithm = "Error";
        String result;
        try {
            result = CakePhpSecurityString.generateSecurityKey(algorithm);
            fail("NoSuchAlgorithmException is excepted");
        } catch (NoSuchAlgorithmException ex) {
        }
    }

    /**
     * Test of hash method, of class CakePhpSecurityString.
     */
    @Test
    public void testHash() throws Exception {
        System.out.println("hash");
        String algorithm = "SHA-1";
        String string = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String result = CakePhpSecurityString.hash(string, algorithm);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));

        string = "日本語あいうえおアイウエオ";
        result = CakePhpSecurityString.hash(string, algorithm);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));

        string = "1234567890-@!#$%&'()=~|<>?_*+{}`\\";
        result = CakePhpSecurityString.hash(string, algorithm);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));

        algorithm = "SHA-256";
        result = CakePhpSecurityString.hash("", algorithm);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));

        algorithm = "MD5";
        result = CakePhpSecurityString.hash(null, algorithm);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));

        result = CakePhpSecurityString.hash(string, null);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));

        result = CakePhpSecurityString.hash(null, null);
        System.out.println(result);
        assertTrue(result.matches("^[0-9a-f]+$"));
    }

    /**
     * Test of hash method, of class CakePhpSecurityString.
     */
    @Test
    public void testHashException() {
        System.out.println("hash exception");
        String algorithm = "Test";
        String string = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String result;
        try {
            result = CakePhpSecurityString.hash(string, algorithm);
            fail("NoSuchAlgorithmException is excepted");
        } catch (NoSuchAlgorithmException ex) {
        }

        try {
            result = CakePhpSecurityString.hash(null, algorithm);
            fail("NoSuchAlgorithmException is excepted");
        } catch (NoSuchAlgorithmException ex) {
        }

    }

    /**
     * Test of generateCipherSeed method, of class CakePhpSecurityString.
     */
    @Test
    public void testGenerateCipherSeed() {
        System.out.println("generateCipherSeed");
        String result = CakePhpSecurityString.generateCipherSeed();
        System.out.println(result);
        assertTrue(result.matches("^[0-9]+$"));
        assertTrue(result.length() == 30);
    }
}
