/*
 * TODO: add license
 */
package org.cakephp.netbeans.ui.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cakephp.netbeans.preferences.CakePreferences;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.CakeVersion;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.actions.GoToViewAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public final class CakePhpGoToViewAction extends GoToViewAction {

    static final Logger LOGGER = Logger.getLogger(CakePhpGoToViewAction.class.getName());
    private static final long serialVersionUID = 9834759234756237L;
    private final FileObject controller;
    private final int offset;
    private FileObject theme;

    public CakePhpGoToViewAction(FileObject controller, int offset) {
        assert CakePhpUtils.isController(controller);
        this.controller = controller;
        this.offset = offset;
    }

    @Override
    public boolean goToView() {
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        PhpBaseElement phpElement = editorSupport.getElement(controller, offset);
        if (phpElement == null) {
            return false;
        }
        // Theme
        if (theme != null) {
            FileObject viewTheme = CakePhpUtils.getView(controller, phpElement, theme);
            if (viewTheme != null) {
                UiUtils.open(viewTheme, DEFAULT_OFFSET);
                return true;
            }
            return false;
        }
        for (PhpClass phpClass : editorSupport.getClasses(controller)) {
            if (CakePhpUtils.isControllerName(phpClass.getName())) {
                for (PhpClass.Field field : phpClass.getFields()) {
                    if (field.getName().equals("$theme")) { // NOI18N
                        CakePhpGoToViewActionPanel dialog = new CakePhpGoToViewActionPanel(this);
                        dialog.showDialog();
                        return true;
                    }
                }
            }
        }
        FileObject view = CakePhpUtils.getView(controller, phpElement);
        if (view != null) {
            UiUtils.open(view, DEFAULT_OFFSET);
            return true;
        }

        // auto create a view file
        PhpModule phpModule = PhpModule.forFileObject(controller);
        if (CakePreferences.getAutoCreateView(phpModule)) {
            try {
                view = CakePhpUtils.createView(controller, phpElement);
                if (view != null) {
                    UiUtils.open(view, DEFAULT_OFFSET);
                    return true;
                }
            } catch (IOException ex) {
                // do nothing
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return false;
    }

    public FileObject[] getThemes() {
        PhpModule phpModule = PhpModule.forFileObject(controller);
        FileObject[] themes = null;
        if (CakeVersion.getInstance(phpModule).isCakePhp(2)) {
            themes = controller.getFileObject("../../View/Themed").getChildren(); // NOI18N
        } else {
            themes = controller.getFileObject("../../views/themed").getChildren(); // NOI18N
        }
        return themes;
    }

    public void setTheme(FileObject theme) {
        this.theme = theme;
    }
}
