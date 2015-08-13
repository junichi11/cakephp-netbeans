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

import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import javax.swing.text.JTextComponent;

/**
 *
 * @author junichi11
 */
public class CakeZipEntryFilter implements ZipEntryFilter {

    private final boolean deleteEmpty;
    private JTextComponent component = null;
    private static final Set<String> TOP_DIRECTORIES = new HashSet<>();

    public CakeZipEntryFilter(boolean deletEmpty) {
        this.deleteEmpty = deletEmpty;
        initialize();
    }

    public CakeZipEntryFilter(boolean deletEmpty, JTextComponent component) {
        this.deleteEmpty = deletEmpty;
        this.component = component;
        initialize();
    }

    private void initialize() {
        TOP_DIRECTORIES.add("app");  // NOI18N
        TOP_DIRECTORIES.add("lib"); // NOI18N 2.x
        TOP_DIRECTORIES.add("plugins"); // NOI18N
        TOP_DIRECTORIES.add("vendors"); // NOI18N
        TOP_DIRECTORIES.add("cake"); // NOI18N 1.x
    }

    /**
     * Verify whether unzip for ZipEntry. If users check delete empty, unzip
     * method doesn't unzip the empty file.
     *
     * @param entry
     * @return true if unzip the file, otherwize false.
     */
    @Override
    public boolean accept(ZipEntry entry) {
        String name = entry.getName();
        String[] splits = splitPath(name);
        String topDirectory = splits[0];

        if (splits.length == 1
                && !TOP_DIRECTORIES.contains(topDirectory)
                && entry.isDirectory()) {
            return false;
        }
        if (!deleteEmpty) {
            return true;
        }
        return !(!entry.isDirectory() && name.endsWith("/empty")); // NOI18N
    }

    /**
     * Get path of ZipEntry.
     *
     * @param entry
     * @return
     */
    @Override
    public String getPath(ZipEntry entry) {
        String path = entry.getName();
        String[] splitPath = splitPath(path);
        String topDirectory = splitPath[0];
        if (splitPath.length == 1 || TOP_DIRECTORIES.contains(topDirectory)) {
            return path;
        }
        return path.replaceFirst(topDirectory + "/", ""); // NOI18N
    }

    /**
     * Set text to JTextComponent. e.g. display the file path on the
     * JTextComponent.
     *
     * @param text
     */
    @Override
    public void setText(String text) {
        component.setText(text);
    }

    /**
     * Split path by "/".
     *
     * @param path
     * @return
     */
    private String[] splitPath(String path) {
        return path.split("/"); // NOI18N
    }
}
