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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public final class CakePhpSecurityString {

    // CakePHP 1.x, 2.x
    private static final String CONFIGURE_WRITE_SECURITY_CIPHER_SEED_FORMAT = "\tConfigure::write('Security.cipherSeed', '%s');"; // NOI18N
    private static final String CONFIGURE_WRITE_SECURITY_CIPHER_SEED_PATTERN = "Configure::write('Security.cipherSeed"; // NOI18N
    private static final String CONFIGURE_WRITE_SECURITY_SALTS_FORMAT = "\tConfigure::write('Security.salt', '%s');"; // NOI18N
    private static final String CONFIGURE_WRITE_SECURITY_SALT_PATTERN = "Configure::write('Security.salt'"; // NOI18N

    CakePhpSecurityString() {
    }

    /**
     * Change Security.salt and Security.cipherSeed
     *
     * @param config core.php file
     * @throws IOException
     */
    public static void changeSecurityString(FileObject config) throws IOException, NoSuchAlgorithmException {
        List<String> lines = config.asLines();
        String salt = CakePhpSecurityString.generateSecurityKey("SHA-1"); // NOI18N
        String cipherSeed = CakePhpSecurityString.generateCipherSeed();
        OutputStream outputStream = config.getOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true); // NOI18N
        try {
            for (String line : lines) {
                if (line.contains(CONFIGURE_WRITE_SECURITY_SALT_PATTERN)) {
                    line = String.format(CONFIGURE_WRITE_SECURITY_SALTS_FORMAT, salt);
                } else if (line.contains(CONFIGURE_WRITE_SECURITY_CIPHER_SEED_PATTERN)) {
                    line = String.format(CONFIGURE_WRITE_SECURITY_CIPHER_SEED_FORMAT, cipherSeed);
                } else {
                    // nothing
                }
                pw.println(line);
            }
        } finally {
            outputStream.close();
            pw.close();
        }
    }

    /**
     * Generate security random string
     *
     * @param algorithm e.g. SHA-1, SHA-256, MD5, ...
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String generateSecurityKey(String algorithm) throws NoSuchAlgorithmException {
        String uuid = UUID.randomUUID().toString();

        return hash(uuid, algorithm);
    }

    public static String generateCipherSeed() {
        StringBuilder sb = new StringBuilder(30);
        for (int i = 0; i < 30; i++) {
            int random = (int) (Math.random() * 10);
            sb.append(Integer.toString(random));
        }

        return sb.toString();
    }

    /**
     * Generate digest from string
     *
     * @param string if string is null, generate uuid (UUID.randamUUID())
     * @param algorithm e.g. SHA-1, SHA-256, MD5, ... if null, use SHA-1
     * @return digest (Hexadecimal hash value)
     * @throws NoSuchAlgorithmException
     */
    public static String hash(String string, String algorithm) throws NoSuchAlgorithmException {
        if (string == null) {
            string = UUID.randomUUID().toString();
        }
        if (algorithm == null) {
            algorithm = "SHA-1"; // NOI18N
        }

        MessageDigest md = MessageDigest.getInstance(algorithm);
        try {
            md.update(string.getBytes("UTF-8")); // NOI18N
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }

        return toHex(md.digest());
    }

    /**
     * Convert byte array to a hexadecimal numbers string
     *
     * @param bytes byte array
     * @return hexadeximal numbers string
     */
    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02x", b); // NOI18N
            sb.append(hex);
        }

        return sb.toString();
    }

}
