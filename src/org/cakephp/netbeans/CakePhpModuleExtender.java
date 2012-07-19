package org.cakephp.netbeans;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.cakephp.netbeans.ui.wizards.NewProjectConfigurationPanel;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.eclipse.jgit.api.Git;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 * @author juncihi11
 */
public class CakePhpModuleExtender extends PhpModuleExtender {

    private static final String GIT_GITHUB_COM_CAKEPHP_CAKEPHP_GIT = "git://github.com/cakephp/cakephp.git";
    private NewProjectConfigurationPanel panel = null;

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        return getErrorMessage() == null;
    }

    @Override
    public String getErrorMessage() {
        return getPanel().getErrorMessage();
    }

    @Override
    public String getWarningMessage() {
        return null;
    }

    @Override
    public Set<FileObject> extend(PhpModule phpModule) throws ExtendingException {
        FileObject localPath = phpModule.getSourceDirectory();

        if (getPanel().getUnzipRadioButton().isSelected()) {
            Map<String, String> tagsMap = getPanel().getTagsMap();
            String url = tagsMap.get(getPanel().getVersionList().getSelectedValue().toString());
            // create cakephp app from zip file.
            try {
                URL zipUrl = new URL(url);
                ZipInputStream zipInputStream = new ZipInputStream(zipUrl.openStream());
                ZipEntry zipEntry = null;
                boolean firstFlg = true;
                String rootDir = ""; // NOI18N

                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    if (firstFlg == true) {
                        rootDir = zipEntry.getName();
                        firstFlg = false;
                        zipInputStream.closeEntry();
                        continue;
                    }
                    String zipName = zipEntry.getName().replace(rootDir, ""); // NOI18N
                    File baseDir = FileUtil.toFile(localPath);
                    File outFile = new File(baseDir, zipName);
                    if (localPath != null && zipEntry.isDirectory()) {
                        outFile.mkdir();
                        zipInputStream.closeEntry();
                        continue;
                    }
                    if (localPath != null) {
                        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile));
                        int data = 0;
                        while ((data = zipInputStream.read()) != -1) {
                            outputStream.write(data);
                        }
                        zipInputStream.closeEntry();
                        outputStream.close();
                    }

                }
                zipInputStream.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            // clone to new project from github repo
            String remotePath = GIT_GITHUB_COM_CAKEPHP_CAKEPHP_GIT;
            Git.cloneRepository().setURI(remotePath).setDirectory(FileUtil.toFile(localPath)).call();
        }

        // set opened file
        Set<FileObject> files = new HashSet<FileObject>();
        FileObject config;
        if (phpModule.getSourceDirectory().getFileObject("lib/Cake") != null) { // NOI18N
            config = phpModule.getSourceDirectory().getFileObject("app/Config/core.php"); // NOI18N
        } else {
            config = phpModule.getSourceDirectory().getFileObject("app/config/core.php"); // NOI18N
        }
        if (config != null) {
            files.add(config);
        }
        if (files.isEmpty()) {
            FileObject index = phpModule.getSourceDirectory().getFileObject("app/webroot/index.php"); // NOI18N
            if (index != null) {
                files.add(index);
            }
        }

        // change tmp directory permission
        FileObject tmp = phpModule.getSourceDirectory().getFileObject("app/tmp"); // NOI18N
        CakePhpUtils.chmodTmpDirectory(tmp);

        return files;
    }

    public NewProjectConfigurationPanel getPanel() {
        if (panel == null) {
            panel = new NewProjectConfigurationPanel();
        }
        return panel;
    }
}
