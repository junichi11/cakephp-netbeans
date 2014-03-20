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
package org.cakephp.netbeans.github;

/**
 *
 * @author junichi11
 */
public final class GithubTag {

    private final String name;
    private final String zipball_url;
    private final String tarball_url;
    private final GithubTagCommit commit;

    // Format
    //  {
    //    "name": "2.4.3",
    //    "zipball_url": "https://api.github.com/repos/cakephp/cakephp/zipball/2.4.3",
    //    "tarball_url": "https://api.github.com/repos/cakephp/cakephp/tarball/2.4.3",
    //    "commit": {
    //      "sha": "eb34e011e2384041997da167821c36125e8f836e",
    //      "url": "https://api.github.com/repos/cakephp/cakephp/commits/eb34e011e2384041997da167821c36125e8f836e"
    //    }
    //  }
    public GithubTag(String name, String zipball_url, String tarball_url, GithubTagCommit commit) {
        this.name = name;
        this.zipball_url = zipball_url;
        this.tarball_url = tarball_url;
        this.commit = commit;
    }

    public String getName() {
        return name;
    }

    public String getZipballUrl() {
        return zipball_url;
    }

    public String getTarballUrl() {
        return tarball_url;
    }

    public GithubTagCommit getCommit() {
        return commit;
    }

    @Override
    public String toString() {
        return name;
    }

}
