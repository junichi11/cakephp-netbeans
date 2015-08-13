/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cakephp.netbeans;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.spi.phpmodule.ImportantFilesImplementation;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;

/**
 *
 * @author junichi11
 */
public final class ConfigurationFiles extends FileChangeAdapter implements ImportantFilesImplementation {

    private final PhpModule phpModule;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    // @GuardedBy("this")
    private boolean isInitialized = false;

    public ConfigurationFiles(PhpModule phpModule) {
        assert phpModule != null;
        this.phpModule = phpModule;
    }

    @Override
    public Collection<FileInfo> getFiles() {
        List<FileObject> directories = getConfigDirectories();
        List<FileInfo> files = new ArrayList<>();
        for (FileObject directory : directories) {
            FileObject[] children = directory.getChildren();
            for (FileObject child : children) {
                if (child.isFolder() || !FileUtils.isPhpFile(child)) {
                    continue;
                }
                files.add(new FileInfo(child));
            }
        }
        return files;
    }

    private synchronized List<FileObject> getConfigDirectories() {
        CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
        if (cakeModule == null) {
            return Collections.emptyList();
        }
        FileObject config = cakeModule.getConfigDirectory(CakePhpModule.DIR_TYPE.APP);
        if (!isInitialized) {
            isInitialized = true;
            addListener(FileUtil.toFile(config));
        }
        return Collections.singletonList(config);
    }

    private void addListener(File path) {
        try {
            FileUtil.addRecursiveListener(this, path);
        } catch (IllegalArgumentException ex) {
            // noop, already listening
            assert false : path;
        }
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

    //~ FS
    @Override
    public void fileRenamed(FileRenameEvent fe) {
        fireChange();
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        fireChange();
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        fireChange();
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        fireChange();
    }

}
