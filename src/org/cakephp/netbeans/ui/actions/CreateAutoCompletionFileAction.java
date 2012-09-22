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
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.CakeVersion;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.actions.BaseAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Create a file for auto code complete Action
 *
 * @author junichi11
 */
public final class CreateAutoCompletionFileAction extends BaseAction {

    private static final CreateAutoCompletionFileAction INSTANCE = new CreateAutoCompletionFileAction();
    private static final long serialVersionUID = -6029721403470166137L;

    private CreateAutoCompletionFileAction() {
    }

    public static CreateAutoCompletionFileAction getInstance() {
        return INSTANCE;
    }

    /**
     * action performed (support only CakePHP 2.x)
     *
     * @param phpModule
     */
    @Override
    public void actionPerformed(PhpModule phpModule) {
        if (!CakePhpFrameworkProvider.getInstance().isInPhpModule(phpModule)
            || CakeVersion.getInstance(phpModule).isCakePhp(1)) {
            // called via shortcut
            return;
        }
        try {
            if (!CakePhpUtils.createAutoCompletionFile()) {
                NotifyDescriptor descriptor = new NotifyDescriptor.Message(NbBundle.getMessage(CreateAutoCompletionFileAction.class, "MSG_Failure"), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notifyLater(descriptor);
            }
        } catch (IOException ex) {
            NotifyDescriptor descriptor = new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notifyLater(descriptor);
        }
    }

    @Override
    protected String getPureName() {
        return NbBundle.getMessage(CreateAutoCompletionFileAction.class, "LBL_CreateAutoCompletionFile"); // NOI18N
    }

    @Override
    protected String getFullName() {
        return NbBundle.getMessage(CreateAutoCompletionFileAction.class, "LBL_CakePhpAction", getPureName()); // NOI18N
    }
}
