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
package org.cakephp.netbeans.editor.codecompletion.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.text.Document;
import org.cakephp.netbeans.editor.codecompletion.CakePhpCompletionItem;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.util.CakePhpDocUtils;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public class MBHCField extends FieldImpl {

    private final FILE_TYPE fileType;

    public MBHCField(String name, Document doc, int offset, PhpModule phpModule) {
        super(name, doc, offset, phpModule);
        fileType = getFileType(name);
    }

    private FILE_TYPE getFileType(String fieldName) {
        if (fieldName != null) {
            switch (fieldName) {
                case Field.COMPONENTS:
                    return FILE_TYPE.COMPONENT;
                case Field.HELPERS:
                    return FILE_TYPE.HELPER;
                case Field.ACTS_AS:
                    return FILE_TYPE.BEHAVIOR;
                case Field.USES:
                    return FILE_TYPE.MODEL;
                default:
            }
        }
        return FILE_TYPE.NONE;
    }

    @Override
    public List<CompletionItem> getCompletionItems() {
        TokenSequence<PHPTokenId> ts = CakePhpDocUtils.getTokenSequence(getDocument(), getOffset());
        if (ts == null) {
            return Collections.emptyList();
        }
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null || getCaretPositionToken().id() != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
            return Collections.emptyList();
        }

        // caret position information
        Token<PHPTokenId> caretToken = ts.token();
        String caretInputString = CakePhpUtils.detachQuotes(caretToken.text().toString());
        int insertStart = ts.offset() + 1;
        int removeLength = caretInputString.length();

        ArrayList<CompletionItem> items = new ArrayList<>();
        String pluginName = null;
        CakePhpModule.DIR_TYPE dirType = CakePhpModule.DIR_TYPE.APP;

        // plugin
        String[] pluginSplit = CakePhpUtils.pluginSplit(caretInputString);
        if (pluginSplit.length == 2) {
            pluginName = pluginSplit[0];
            dirType = CakePhpModule.DIR_TYPE.APP_PLUGIN;
        }

        if (pluginSplit.length == 1) {
            // add plugin names
            for (String name : cakeModule.getAllPluginNames()) {
                items.add(new CakePhpCompletionItem(name + ".", insertStart, removeLength)); // NOI18N
            }
        }

        // add items
        for (FileObject targetDirectory : getTargetDirectories(cakeModule, dirType, pluginName)) {
            addItems(targetDirectory, pluginName, caretInputString, items, insertStart, removeLength);
        }

        return items;
    }

    private List<FileObject> getTargetDirectories(CakePhpModule cakeModule, CakePhpModule.DIR_TYPE dirType, String pluginName) {
        ArrayList<FileObject> targetDirectories = new ArrayList<>();
        List<DIR_TYPE> types = Collections.emptyList();
        if (dirType == DIR_TYPE.APP) {
            if (fileType == FILE_TYPE.MODEL) {
                types = Arrays.asList(DIR_TYPE.APP);
            } else {
                types = Arrays.asList(DIR_TYPE.APP, DIR_TYPE.CORE);
            }
        }

        if (dirType == DIR_TYPE.APP_PLUGIN) {
            types = Arrays.asList(DIR_TYPE.APP_PLUGIN);
        }

        for (DIR_TYPE type : types) {
            FileObject directory = getTargetDirectory(cakeModule, type, pluginName);
            if (directory != null) {
                targetDirectories.add(directory);
            }
        }
        return targetDirectories;
    }

    private FileObject getTargetDirectory(CakePhpModule cakeModule, CakePhpModule.DIR_TYPE dirType, String pluginName) {
        FileObject targetDirectory = cakeModule.getDirectory(dirType, fileType, pluginName);
        if (targetDirectory == null && dirType == CakePhpModule.DIR_TYPE.APP_PLUGIN) {
            targetDirectory = cakeModule.getDirectory(CakePhpModule.DIR_TYPE.PLUGIN, fileType, pluginName);
        }
        return targetDirectory;
    }

    private void addItems(FileObject targetDirectory, String pluginName, String caretInputString, ArrayList<CompletionItem> items, int insertStart, int removeLength) {
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (FileObject child : targetDirectory.getChildren()) {
            if (child.isFolder()) {
                continue;
            }
            String commonDisplayName = getCommonDisplayName(child, fileType, editorSupport);
            if (commonDisplayName != null && !commonDisplayName.equals("App") && !commonDisplayName.endsWith("AppModel")) { // NOI18N
                if (pluginName != null) {
                    commonDisplayName = pluginName + "." + commonDisplayName; // NOI18N
                }
                if (commonDisplayName.toLowerCase().startsWith(caretInputString.toLowerCase())) {
                    items.add(new CakePhpCompletionItem(commonDisplayName, insertStart, removeLength));
                }
            }
        }
    }

    private String getCommonDisplayName(FileObject fileObject, FILE_TYPE fileType, EditorSupport editorSupport) {
        Collection<PhpClass> phpClasses = editorSupport.getClasses(fileObject);
        String className = null;
        for (PhpClass phpClass : phpClasses) {
            className = phpClass.getName();
            break;
        }
        if (className == null) {
            return null;
        }
        if (fileType == FILE_TYPE.MODEL) {
            return className;
        }

        String suffix = fileType.toString();
        if (!className.endsWith(suffix)) {
            return null;
        }
        return className.replace(suffix, ""); // NOI18N
    }

}
