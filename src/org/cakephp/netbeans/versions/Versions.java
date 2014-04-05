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

import java.util.Collection;
import java.util.Map;
import org.cakephp.netbeans.versions.Versionable.VERSION_TYPE;
import org.netbeans.api.annotations.common.CheckForNull;

/**
 *
 * @author junichi11
 */
public class Versions {

    private final Map<VERSION_TYPE, Versionable> versionsMap;

    public Versions(Map<VERSION_TYPE, Versionable> versionsMap) {
        this.versionsMap = versionsMap;
    }

    /**
     * Get all versions.
     *
     * @return versions
     */
    public Collection<Versionable> getVersions() {
        return versionsMap.values();
    }

    /**
     * Get version for {@code VERSION_TYPE}.
     *
     * @param versionType {@code VERSION_TYPE}
     * @return version if has version for {@code VERSION_TYPE}, {@code null}
     * otherwise.
     */
    @CheckForNull
    public Versionable getVersion(VERSION_TYPE versionType) {
        return versionsMap.get(versionType);
    }

    /**
     * Check whether this class has version for {@code VERSION_TYPE}.
     *
     * @param versionType {@code VERSION_TYPE}
     * @return {@code true} if has version, {@code false} otherwise.
     */
    public boolean hasVersion(VERSION_TYPE versionType) {
        Versionable version = getVersion(versionType);
        return version != null;
    }

    public int size() {
        return versionsMap.size();
    }
}
