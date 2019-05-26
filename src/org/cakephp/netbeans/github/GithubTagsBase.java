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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public abstract class GithubTagsBase {

    private final List<GithubTag> tags = new ArrayList<>();
    private final List<String> names = new ArrayList<>();
    private boolean isNetworkError = true;
    private static final Pattern LINK_URL_PATTERN = Pattern.compile("<(?<url>https://.+)>; rel=\"(?<rel>.+)\""); // NOI18N
    private static final Logger LOGGER = Logger.getLogger(GithubTagsBase.class.getName());

    @NonNull
    public abstract String getUrl();

    public abstract Filter getFilter();

    private synchronized void init() {
        isNetworkError = false;
        // fetch tags
        tags.clear();
        try {
            addGitHubTags(getUrl(), false);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
            isNetworkError = true;
        }

        // add names
        names.clear();
        if (isNetworkError) {
            return;
        }

        Filter filter = getFilter();
        if (filter == null) {
            filter = new DefaultFilter();
        }
        for (GithubTag tag : tags) {
            String name = tag.getName();
            if (filter.accept(name)) {
                names.add(name);
            }
        }
    }

    private void addGitHubTags(String url, boolean isLast) throws MalformedURLException, UnsupportedEncodingException, IOException {
        if (StringUtils.isEmpty(url)) {
            return;
        }
        URL tagsJson = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) tagsJson.openConnection();
        urlConnection.setRequestMethod("GET"); // NOI18N

        BufferedReader reader = new BufferedReader(new InputStreamReader(tagsJson.openStream(), "UTF-8")); // NOI18N
        addGitHubTags(reader);

        // paging
        if (!isLast) {
            String link = urlConnection.getHeaderField("Link"); // NOI18N
            if (!StringUtils.isEmpty(link)) {
                String next = getLinkUrl(link, "next"); // NOI18N
                String last = getLinkUrl(link, "last"); // NOI18N
                if (next != null) {
                    // get tags recursively
                    addGitHubTags(next, last != null && next.equals(last));
                }
            }
        }
    }

    private void addGitHubTags(Reader reader) {
        // JSON -> Object
        Gson gson = new Gson();
        try {
            JsonReader jsonReader = new JsonReader(reader);
            Type type = new TypeToken<ArrayList<GithubTag>>() {
            }.getType();
            tags.addAll((Collection<? extends GithubTag>) gson.fromJson(jsonReader, type));
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
                isNetworkError = true;
            }
        }
    }

    @CheckForNull
    private String getLinkUrl(String link, String rel) {
        if (StringUtils.isEmpty(link) || StringUtils.isEmpty(rel)) {
            return null;
        }
        String[] urls = link.split(","); // NOI18N
        for (String url : urls) {
            String trimedUrl = url.trim();
            Matcher matcher = LINK_URL_PATTERN.matcher(trimedUrl);
            if (matcher.find()) {
                if (rel.equals(matcher.group("rel"))) { // NOI18N
                    return matcher.group("url"); // NOI18N
                }
            }
        }
        return null;
    }

    public void reload() {
        init();
    }

    public List<GithubTag> getTags() {
        if (isNetworkError) {
            reload();
        }
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags;
    }

    public String getZipUrl(String name) {
        for (GithubTag tag : tags) {
            if (tag.getName().equals(name)) {
                return tag.getZipballUrl();
            }
        }
        return null;
    }

    public String[] getNames() {
        if (isNetworkError) {
            reload();
        }
        return names.toArray(new String[0]);
    }

    public String getLatestStableVersion() {
        if (isNetworkError) {
            reload();
        }
        for (String name : names) {
            // not stable version
            if (name.contains("-")) { // NOI18N
                continue;
            }
            return name;
        }
        return null;
    }

    public boolean isNetworkError() {
        return isNetworkError;
    }

    //~ Inner classes
    public interface Filter {

        public boolean accept(String name);
    }

    public class DefaultFilter implements Filter {

        @Override
        public boolean accept(String name) {
            return true;
        }

    }

}
