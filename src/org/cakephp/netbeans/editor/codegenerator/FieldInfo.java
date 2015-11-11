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
package org.cakephp.netbeans.editor.codegenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import static org.cakephp.netbeans.editor.codegenerator.FieldInfo.Type.ACTS_AS;
import static org.cakephp.netbeans.editor.codegenerator.FieldInfo.Type.COMPONENTS;
import static org.cakephp.netbeans.editor.codegenerator.FieldInfo.Type.HELPERS;
import static org.cakephp.netbeans.editor.codegenerator.FieldInfo.Type.USES;
import org.cakephp.netbeans.util.CakePhpDocUtils;
import org.cakephp.netbeans.util.DocUtils;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public abstract class FieldInfo {

    public static final String COMPONENTS_NAME = "components"; // NOI18N
    public static final String HELPERS_NAME = "helpers"; // NOI18N
    public static final String USES_NAME = "uses"; // NOI18N
    public static final String ACTS_AS_NAME = "actsAs"; // NOI18N
    public static final String VALIDATE_NAME = "validate"; // NOI18N

    public enum Type {

        COMPONENTS,
        HELPERS,
        ACTS_AS,
        USES,
        VALIDATE;

        @Override
        public String toString() {
            String name = ""; // NOI18N
            switch (this) {
                case ACTS_AS:
                    name = ACTS_AS_NAME;
                    break;
                case USES:
                    name = USES_NAME;
                    break;
                case HELPERS:
                    name = HELPERS_NAME;
                    break;
                case COMPONENTS:
                    name = COMPONENTS_NAME;
                    break;
                case VALIDATE:
                    name = VALIDATE_NAME;
                    break;
                default:
                    break;
            }
            return name;
        }
    }
    protected final JTextComponent textComponent;
    protected final Type type;
    protected boolean isExist;
    protected int insertOffset;
    protected List<Property> possibleProperties;

    protected FieldInfo(Type type, JTextComponent textComponent) {
        this.textComponent = textComponent;
        this.type = type;
        isExist = false;
        possibleProperties = new ArrayList<>();
        this.insertOffset = 0;
    }

    /**
     * Get field name.
     *
     * @return field name.
     */
    public String getName() {
        return type.toString();
    }

    /**
     * Whether field exists
     *
     * @return true if field exists, otherwise false.
     */
    public boolean isExist() {
        return isExist;
    }

    /**
     * Get offset for insert.
     *
     * @return
     */
    public int getInsertOffset() {
        return insertOffset;
    }

    /**
     * Set offset for insert.
     *
     * @param insertOffset
     */
    public void setInsertOffset(int insertOffset) {
        this.insertOffset = insertOffset;
    }

    /**
     * Get possible values.
     *
     * @return properties
     */
    public List<Property> getPossibleProperties() {
        return possibleProperties;
    }

    /**
     * Get string for insert.
     *
     * @return
     */
    public abstract String getInsertString();

    /**
     * Get PHP class name.
     *
     * @return class name
     */
    public String getClassName() {
        Document document = textComponent.getDocument();
        return CakePhpDocUtils.getClassName(document);
    }

    /**
     * Set default offset. default is top position of class.
     */
    protected void setDefaultOffset() {
        int offset = 0;
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : editorSupport.getClasses(getFileObject())) {
            try {
                int rowEnd = DocUtils.getRowEnd(textComponent.getDocument(), phpClass.getOffset());
                offset = rowEnd + 1; // +1 is top of next line
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            break;
        }
        insertOffset = offset;
    }

    /**
     * Parse fields.
     */
    protected void parseFields() {
        try {
            ParserManager.parse(Collections.singleton(Source.create(textComponent.getDocument())), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    PHPParseResult info = (PHPParseResult) resultIterator.getParserResult();
                    if (info != null) {
                        parse(info);
                    }
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Parse fields. For example, scan document using visitor :
     * visitor.scan(Utils.getRoot(result)).
     *
     * @param result
     */
    protected abstract void parse(PHPParseResult result);

    /**
     * Update insert position. If insert position is changed when some template
     * code are inserted, then you have to update insert position.
     */
    public abstract void updateInsertPosition();

    /**
     * Get FileObject.
     *
     * @return FileObject
     */
    protected FileObject getFileObject() {
        Document document = textComponent.getDocument();
        return CakePhpDocUtils.getFileObject(document);
    }

    /**
     * Get PhpModule.
     *
     * @return PhpModule
     */
    protected PhpModule getPhpModule() {
        FileObject fileObject = getFileObject();
        return PhpModule.Factory.forFileObject(fileObject);
    }

    //~ inner classes
    public static class FieldInfoFactory {

        /**
         * Create FieldInfo.
         *
         * @param type
         * @param textComponent
         * @return
         */
        public static FieldInfo create(Type type, JTextComponent textComponent) {
            FieldInfo fieldInfo;
            switch (type) {
                case COMPONENTS: // no break
                case HELPERS: // no break
                case USES: // no break
                case ACTS_AS:
                    fieldInfo = new MBHCFieldInfo(type, textComponent);
                    break;
                case VALIDATE:
                    fieldInfo = new ValidateFiledInfo(type, textComponent);
                    break;
                default:
                    throw new AssertionError();
            }
            fieldInfo.setDefaultOffset();
            fieldInfo.parseFields();
            return fieldInfo;
        }
    }
}
