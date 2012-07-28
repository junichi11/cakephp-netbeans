/*
 * TODO: add license
 */
package org.cakephp.netbeans.ui.actions;

import java.io.IOException;
import java.util.Enumeration;
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.actions.BaseAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * @author junichi11
 */
public final class ClearCacheAction extends BaseAction {

    private static final ClearCacheAction INSTANCE = new ClearCacheAction();
    private static final long serialVersionUID = -1978960583114966388L;

    private ClearCacheAction() {
    }

    public static ClearCacheAction getInstance() {
        return INSTANCE;
    }

    @Override
    public void actionPerformed(PhpModule phpModule) {
        if (!CakePhpFrameworkProvider.getInstance().isInPhpModule(phpModule)) {
            // called via shortcut
            return;
        }
        FileObject cache = CakePhpFrameworkProvider.getCakePhpDirectory(phpModule).getFileObject("app/tmp/cache");
        if (cache != null && cache.isFolder()) {
            Enumeration<? extends FileObject> children = cache.getChildren(true);
            while (children.hasMoreElements()) {
                FileObject child = children.nextElement();
                Enumeration<? extends FileObject> grandChildren = child.getChildren(true);
                while (grandChildren.hasMoreElements()) {
                    FileObject grandChild = grandChildren.nextElement();
                    try {
                        grandChild.delete();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    @Override
    protected String getPureName() {
        return NbBundle.getMessage(ClearCacheAction.class, "LBL_ClearCache");
    }

    @Override
    protected String getFullName() {
        return NbBundle.getMessage(ClearCacheAction.class, "LBL_CakePhpAction", getPureName());
    }
}
