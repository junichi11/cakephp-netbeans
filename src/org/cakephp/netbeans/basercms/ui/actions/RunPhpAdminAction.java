/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.basercms.ui.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "# {0} - name",
    "RunPhpAdminAction.fullname=Run {0}",
    "RunPhpAdminAction.url.invalid=URL is invalid"
})
public abstract class RunPhpAdminAction extends BaserCmsBaseAction {

    private static final long serialVersionUID = -7430154772521872357L;
    private static final Logger LOGGER = Logger.getLogger(RunPhpAdminAction.class.getName());

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        String urlSubpath = getUrlSubpath();
        if (StringUtils.isEmpty(urlSubpath)) {
            return;
        }
        PhpModuleProperties.Factory factory = phpModule.getLookup().lookup(PhpModuleProperties.Factory.class);
        if (factory == null) {
            return;
        }
        PhpModuleProperties properties = factory.getProperties();
        if (properties == null) {
            return;
        }
        String urlFullpath = properties.getUrl();
        if (StringUtils.isEmpty(urlFullpath)) {
            return;
        }

        if (!urlFullpath.endsWith("/")) { // NOI18N
            urlFullpath = urlFullpath.concat("/");
        }
        URL url;
        try {
            url = new URL(urlFullpath.concat(urlSubpath));
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, Bundle.RunPhpAdminAction_url_invalid());
        }
    }

    /**
     * get subpath for URL. e.g. phpMyAdmin
     *
     * @return subpath for URL
     */
    protected abstract String getUrlSubpath();

}
