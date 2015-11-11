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
package org.cakephp.netbeans.editor.codecompletion.methods;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cakephp.netbeans.editor.codecompletion.ImageCompletionItem;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class ImageMethod extends AssetMethod {

    private static final List<String> EXT_FILTER = Arrays.asList("jpeg", "jpg", "png", "gif", "bmp", "ico"); // NOI18N
    private static final String SPLIT_PLUGIN_REGEX_PATTERN = "^([A-Z].+?\\.|)(.+\\.[a-zA-Z]+)$";// NOI18N
    private final CakePhpModule cakeModule;

    ImageMethod(PhpModule phpModule) {
        super(phpModule);
        type = ASSET_TYPE.IMAGE;
        extFilter = EXT_FILTER;
        cakeModule = CakePhpModule.forPhpModule(phpModule);
    }

    @Override
    public CompletionItem createCompletionItem(String element, int startOffset, int removeLength) {
        FileObject target = getFileObject(element);
        return new ImageCompletionItem(element, startOffset, removeLength, target);
    }

    /**
     * Get FileObject for insert element.
     *
     * @param element string for inserting
     * @return FileObject
     */
    private FileObject getFileObject(String element) {
        Pattern pattern = Pattern.compile(SPLIT_PLUGIN_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(element);
        String pluginName = ""; // NOI18N
        String filePath = ""; // NOI18N
        if (matcher.find()) {
            pluginName = matcher.group(1).replace(DOT, ""); // NOI18N
            filePath = matcher.group(2);
        }
        FileObject webrootDirectory;
        if (!filePath.startsWith(SLASH)) {
            filePath = "img/" + filePath; // NOI18N
        }

        // get webroot directory
        if (pluginName.isEmpty()) {
            webrootDirectory = cakeModule.getDirectory(DIR_TYPE.APP, FILE_TYPE.WEBROOT, null);
        } else {
            webrootDirectory = cakeModule.getDirectory(DIR_TYPE.APP_PLUGIN, FILE_TYPE.WEBROOT, pluginName);
            if (webrootDirectory == null) {
                webrootDirectory = cakeModule.getDirectory(DIR_TYPE.PLUGIN, FILE_TYPE.WEBROOT, pluginName);
            }
        }
        if (webrootDirectory == null) {
            return null;
        }

        // get file
        return webrootDirectory.getFileObject(filePath);
    }
}
