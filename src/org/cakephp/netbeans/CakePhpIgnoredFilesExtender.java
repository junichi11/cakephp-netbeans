/*
 * TODO: add license
 */

package org.cakephp.netbeans;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleIgnoredFilesExtender;
import org.openide.filesystems.FileUtil;

public class CakePhpIgnoredFilesExtender extends PhpModuleIgnoredFilesExtender {
    // TODO: hide also another directories than "app/tmp"?
    private final File appTmp;

    public CakePhpIgnoredFilesExtender(PhpModule phpModule) {
        assert phpModule != null;

        appTmp = new File(FileUtil.toFile(CakePhpFrameworkProvider.getCakePhpDirectory(phpModule)), "app/tmp"); // NOI18N
    }

    @Override
    public Set<File> getIgnoredFiles() {
        return Collections.singleton(appTmp);
    }
}
