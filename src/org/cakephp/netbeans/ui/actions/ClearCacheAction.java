/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.ui.actions;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.cakephp.netbeans.CakePhp;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.modules.CakePhpModule.DIR_TYPE;
import org.cakephp.netbeans.modules.CakePhpModule.FILE_TYPE;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * @author junichi11
 */
public final class ClearCacheAction extends BaseAction {

    private static final ClearCacheAction INSTANCE = new ClearCacheAction();
    private static final long serialVersionUID = -1978960583114966388L;
    private static final Logger LOGGER = Logger.getLogger(ClearCacheAction.class.getName());

    private ClearCacheAction() {
    }

    public static ClearCacheAction getInstance() {
        return INSTANCE;
    }

    @Override
    public void actionPerformed(PhpModule phpModule) {
        if (!CakePhpUtils.isCakePHP(phpModule)) {
            // called via shortcut
            return;
        }
        // delete files
        FileObject cache = getTempCacheDirectory(phpModule);
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
                        LOGGER.log(Level.WARNING, "can't delete: " + grandChild.getNameExt(), ex);
                        Icon icon = new ImageIcon(getClass().getResource("/" + CakePhp.CAKE_FAIL_ICON_16)); // NOI18N
                        NotificationDisplayer.getDefault().notify(getPureName(), icon, "Delete fail", null);
                    }
                }
            }
            Icon icon = new ImageIcon(getClass().getResource("/" + CakePhp.CAKE_SUCCESS_ICON_16)); // NOI18N
            NotificationDisplayer.getDefault().notify(getPureName(), icon, "Complete success", null);
        }
    }

    /**
     * Get tmp/cache directory.
     *
     * @param phpModule
     * @return tmp/cache directory
     */
    private FileObject getTempCacheDirectory(PhpModule phpModule) {
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module == null) {
            return null;
        }
        FileObject tmpDirectory = module.getDirectory(DIR_TYPE.APP, FILE_TYPE.TMP, null);
        if (tmpDirectory == null) {
            return null;
        }
        return tmpDirectory.getFileObject("cache"); // NOI18N
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
