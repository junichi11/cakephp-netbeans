/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cakephp.netbeans.editor;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.openide.filesystems.FileObject;

/**
 *
 * @author igorf
 */
public class CakePhpEditorExtender extends EditorExtender {
    static final Logger LOGGER = Logger.getLogger(CakePhpEditorExtender.class.getName());
    private static final List<PhpBaseElement> ELEMENTS = Arrays.<PhpBaseElement>asList(
            new PhpVariable("$this", new PhpClass("View", "View"))); // NOI18N

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        if (CakePhpUtils.isView(fo)) {
            List<PhpBaseElement> elements = new LinkedList<PhpBaseElement>(ELEMENTS);
            return elements;
        }
        return Collections.emptyList();
    }
}
