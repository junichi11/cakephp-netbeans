/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Versionable 2 only ("GPL") or the Common
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
 * by Oracle in the GPL Versionable 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Versionable 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Versionable 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Versionable 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Versionable 2 code and therefore, elected the GPL
 * Versionable 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.versions;

import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author junichi11
 */
public interface Versionable {

    public enum VERSION_TYPE {

        CAKEPHP,
        BASERCMS,
    }

    public String getVersion();

    public int getMajor();

    public int getMinor();

    public int getRevision();

    public String getNotStable();

    public boolean hasUpdate();

    public String getLatestStableVersion();

    public VERSION_TYPE getType();

    public static final Comparator<String> VERSION_COMPARATOR = new Comparator<String>() {

        private static final String NUMBER_REGEX = "[0-9]+"; // NOI18N
        private static final String SPLIT_REGEX = "[., -]"; // NOI18N

        @Override
        public int compare(String a, String b) {
            String[] aArray = a.split(SPLIT_REGEX);
            String[] bArray = b.split(SPLIT_REGEX);
            int aLength = aArray.length;
            int bLength = bArray.length;
            for (int i = 0; i < aLength; i++) {
                if (i == aLength - 1) {
                    if ((bLength - aLength) < 0) {
                        return -1;
                    }
                }
                String aString = aArray[i];
                String bString = bArray[i];
                if (aString.matches(NUMBER_REGEX) && bString.matches(NUMBER_REGEX)) {
                    try {
                        Integer aInt = Integer.parseInt(aString);
                        Integer bInt = Integer.parseInt(bString);
                        if (!Objects.equals(aInt, bInt)) {
                            return bInt - aInt;
                        }
                    } catch (NumberFormatException ex) {
                        return 1;
                    }
                } else {
                    return b.compareTo(a);
                }
            }
            return 1;
        }
    };

}
