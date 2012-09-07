/*
 * TODO: add license
 */

package org.cakephp.netbeans.ui.actions;

import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.spi.framework.actions.GoToActionAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public final class CakePhpGoToActionAction extends GoToActionAction {
    private static final long serialVersionUID = 309456937957136545L;

    private final FileObject view;

    public CakePhpGoToActionAction(FileObject view) {
        assert CakePhpUtils.isView(view);
        this.view = view;
    }

    @Override
    public boolean goToAction() {
        FileObject controller = CakePhpUtils.getController(view);
        if (controller != null) {
            UiUtils.open(controller, getActionMethodOffset(controller));
            return true;
        }
        return false;
    }

    private int getActionMethodOffset(FileObject controller) {
        String actionMethodName = CakePhpUtils.getActionName(view);
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : editorSupport.getClasses(controller)) {
            if (CakePhpUtils.isControllerName(phpClass.getName())) {
                if (actionMethodName != null) {
                    for (PhpClass.Method method : phpClass.getMethods()) {
                        if (actionMethodName.equals(method.getName())) {
                            return method.getOffset();
                        }
                    }
                }
                return phpClass.getOffset();
            }
        }
        return DEFAULT_OFFSET;
    }
}
