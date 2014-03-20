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
import org.cakephp.netbeans.editor.codecompletion.CakePhpCompletionItem;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class Method {

    protected static final String SLASH = "/"; // NOI18N
    protected static final String DOT = "."; // NOI18N
    private static final String ELEMENT = "element"; // NOI18N
    private static final String FETCH = "fetch"; // NOI18N
    private static final String CSS = "css"; // NOI18N
    private static final String SCRIPT = "script"; // NOI18N
    private static final String IMAGE = "image"; // NOI18N
    private static final String RENDER = "render"; // NOI18N
    private static final String EXTEND = "extend"; // NOI18N
    protected PhpModule phpModule;
    // fetch : CakePHP 2.1+
    public static final List<String> METHODS = Arrays.asList(ELEMENT, FETCH, CSS, SCRIPT, IMAGE, RENDER, EXTEND);
    protected static final List<DIR_TYPE> PLUGINS = Arrays.asList(DIR_TYPE.APP_PLUGIN, DIR_TYPE.PLUGIN);

    Method(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    public abstract List<String> getElements(int argCount, String filter);

    /**
     * Create CompletionItem.
     *
     * @param element
     * @param startOffset
     * @param removeLength
     * @return CompletionItem
     */
    public CompletionItem createCompletionItem(String element, int startOffset, int removeLength) {
        return new CakePhpCompletionItem(element, startOffset, removeLength);
    }

    public static class Factory {

        public static Method create(String method, PhpModule phpModule, FileObject fo) {
            if (method != null && !method.isEmpty()) {
                if (method.equals(EXTEND)) { //NOI18N
                    return new ExtendMethod(phpModule, fo);
                }
                if (method.equals(RENDER)) { //NOI18N
                    return new RenderMethod(phpModule, fo);
                }
                if (method.equals(ELEMENT)) { //NOI18N
                    return new ElementMethod(phpModule, fo);
                }
                if (method.equals(FETCH)) { // NOI18N
                    return new FetchMethod(phpModule);
                }
                if (method.equals(CSS)) {
                    return new CssMethod(phpModule);
                }
                if (method.equals(SCRIPT)) {
                    return new ScriptMethod(phpModule);
                }
                if (method.equals(IMAGE)) {
                    return new ImageMethod(phpModule);
                }
            }
            return null;
        }
    }
}
