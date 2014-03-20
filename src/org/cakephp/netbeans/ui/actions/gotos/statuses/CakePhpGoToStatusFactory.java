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
package org.cakephp.netbeans.ui.actions.gotos.statuses;

import org.cakephp.netbeans.util.CakePhpUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class CakePhpGoToStatusFactory {

    private FileObject targetFile;
    private int offset;
    private CakePhpGoToStatus status;
    private static final CakePhpGoToStatusFactory INSTANCE = new CakePhpGoToStatusFactory();

    private CakePhpGoToStatusFactory() {
    }

    public static CakePhpGoToStatusFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Create object for each file type.
     *
     * @param targetFile
     * @param offset
     * @return
     */
    public CakePhpGoToStatus create(FileObject targetFile, int offset) {
        if (this.targetFile == targetFile && this.offset == offset) {
            if (status != null) {
                return status;
            }
        }
        this.targetFile = targetFile;
        this.offset = offset;
        if (CakePhpUtils.isController(targetFile)) {
            status = CakePhpControllerGoToStatus.getInstance();
        } else if (CakePhpUtils.isModel(targetFile)) {
            status = CakePhpModelGoToStatus.getInstance();
        } else if (CakePhpUtils.isView(targetFile)) {
            // TODO check ctp extension? or add settings for it?
            status = CakePhpViewGoToStatus.getInstance();
        } else if (CakePhpUtils.isComponent(targetFile)) {
            status = CakePhpComponentGoToStatus.getInstance();
        } else if (CakePhpUtils.isBehavior(targetFile)) {
            status = CakePhpBehaviorGoToStatus.getInstance();
        } else if (CakePhpUtils.isHelper(targetFile)) {
            status = CakePhpHelperGoToStatus.getInstance();
        } else if (CakePhpUtils.isTest(targetFile)) {
            status = CakePhpTestCaseGoToStatus.getInstance();
        } else if (CakePhpUtils.isFixture(targetFile)) {
            status = CakePhpFixtureGoToStatus.getInstance();
        } else {
            status = CakePhpDummyGoToStatus.getInstance();
        }
        status.setCurrentFile(targetFile);
        status.setOffset(offset);
        status.scan();
        return status;
    }
}
