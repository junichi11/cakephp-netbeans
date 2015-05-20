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
package org.cakephp.netbeans.validator;

import org.cakephp.netbeans.dotcake.Dotcake;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class CakePhpCustomizerValidator {

    private final ValidationResult result = new ValidationResult();

    public ValidationResult getResult() {
        return result;
    }

    @NbBundle.Messages({
        "CakePhpCustomizerValidator.error.cake.invalid=Existing CakePHP directory must be set.",
        "CakePhpCustomizerValidator.error.cake.script.invalid=Not found cake script file(cake.php)."
    })
    public CakePhpCustomizerValidator validateCakePhpPath(FileObject sourceDirectory, String path) {
        FileObject targetDirectory = sourceDirectory.getFileObject(path);
        if (targetDirectory == null) {
            result.addWarning(new ValidationResult.Message("cake.path", Bundle.CakePhpCustomizerValidator_error_cake_invalid())); // NOI18N
            return this;
        }
        FileObject cake = targetDirectory.getFileObject("cake"); // NOI18N
        FileObject script;
        if (cake != null) {
            script = targetDirectory.getFileObject("cake/console/cake.php"); // NOI18N
        } else {
            script = targetDirectory.getFileObject("lib/Cake/Console/cake.php"); // NOI18N
        }

        // installing with Composer
        // cake.php Vendor/bin/cake.php | Vendor/pear-pear.cakephp.org/CakePHP/bin/cake.php
        if (script == null) {
            script = targetDirectory.getFileObject("Vendor/pear-pear.cakephp.org/CakePHP/bin/cake.php"); // NOI18N
        }

        // composer
        if (script == null) {
            script = targetDirectory.getFileObject("vendor/cakephp/cakephp/Cake/Console/cake.php"); // NOI18N
        }

        if (script == null) {
            result.addWarning(new ValidationResult.Message("cake.script", Bundle.CakePhpCustomizerValidator_error_cake_script_invalid())); // NOI18N
        }
        return this;
    }

    @NbBundle.Messages({
        "CakePhpCustomizerValidator.error.app.invalid=Existing app directory must be set.",
        "CakePhpCustomizerValidator.error.app.config.invalid=App directory must have config/Config directory."
    })
    public CakePhpCustomizerValidator validateAppPath(FileObject sourceDirectory, String path) {
        FileObject targetDirectory = sourceDirectory.getFileObject(path);
        if (targetDirectory == null) {
            result.addWarning(new ValidationResult.Message("app.path", Bundle.CakePhpCustomizerValidator_error_app_invalid())); // NOI18N
            return this;
        }

        // Cake2.x
        FileObject config = targetDirectory.getFileObject("Config"); // NOI18N
        if (config == null) {
            // Cake1.x
            config = targetDirectory.getFileObject("config"); // NOI18N
        }
        if (config == null) {
            result.addWarning(new ValidationResult.Message("app.config", Bundle.CakePhpCustomizerValidator_error_app_config_invalid()));
        }
        return this;
    }

    @NbBundle.Messages({
        "CakePhpCustomizerValidator.error.dotcake.notFound=[.cake] Existing .cake file must be set.",
        "CakePhpCustomizerValidator.error.dotcake.notFile=[.cake] File path must be set.",
        "CakePhpCustomizerValidator.error.dotcake.invalid.file.format=[.cake] Invalid format. Can't get data from .cake.",
        "CakePhpCustomizerValidator.error.dotcake.invalid.file.name=[.cake] File name must be .cake."
    })
    public CakePhpCustomizerValidator validateDotcakeFilePath(FileObject sourceDirectory, String path) {
        // ignore if file path is empy
        if (StringUtils.isEmpty(path)) {
            return this;
        }

        FileObject targetFile = sourceDirectory.getFileObject(path);
        if (targetFile == null) {
            result.addWarning(new ValidationResult.Message("dotcake.path", Bundle.CakePhpCustomizerValidator_error_dotcake_notFound())); // NOI18N
            return this;
        }

        if (targetFile.isFolder()) {
            result.addWarning(new ValidationResult.Message("dotcake.path", Bundle.CakePhpCustomizerValidator_error_dotcake_notFile())); // NOI18N
            return this;
        }

        if (!targetFile.getNameExt().equals(".cake")) { // NOI18N
            result.addWarning(new ValidationResult.Message("dotcake.path", Bundle.CakePhpCustomizerValidator_error_dotcake_invalid_file_name())); // NOI18N
            return this;
        }

        // invalid format
        Dotcake dotcake = Dotcake.fromJson(targetFile);
        if (dotcake == null || dotcake.getCake() == null || dotcake.getBuildPath() == null) {
            result.addWarning(new ValidationResult.Message("dotcake.path", Bundle.CakePhpCustomizerValidator_error_dotcake_invalid_file_format())); // NOI18N
        }
        return this;
    }
}
