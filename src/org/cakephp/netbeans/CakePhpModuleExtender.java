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
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 * @author juncihi11
 */
public class CakePhpModuleExtender extends PhpModuleExtender{
	
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
		Map<String, String> tagsMap = getPanel().getTagsMap();
		String url = tagsMap.get(getPanel().getVersionList().getSelectedValue().toString());
		// create cakephp app from zip file.
		try {
			URL zipUrl = new URL(url);
			ZipInputStream zipInputStream = new ZipInputStream(zipUrl.openStream());
			ZipEntry zipEntry = null;
			boolean firstFlg = true;
			String rootDir = ""; // NOI18N
			while((zipEntry = zipInputStream.getNextEntry()) != null){
				if(firstFlg == true){
					rootDir = zipEntry.getName();
					firstFlg = false;
					zipInputStream.closeEntry();
					continue;
				}
				String zipName = zipEntry.getName().replace(rootDir, ""); // NOI18N
				FileObject fo = phpModule.getSourceDirectory();
				File baseDir = FileUtil.toFile(fo);
				File outFile = new File(baseDir, zipName);
				if(fo != null && zipEntry.isDirectory()){
					outFile.mkdir();
					zipInputStream.closeEntry();
					continue;
				}
				if(fo != null){
					BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile));
					int data = 0;
					while((data = zipInputStream.read()) != -1){
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
		
		Set<FileObject> files = new HashSet<FileObject>();
		FileObject config = phpModule.getSourceDirectory().getFileObject("app/config/core.php"); // NOI18N
		if(config != null){
			files.add(config);
		}
		if (files.isEmpty()) {
			FileObject index = phpModule.getSourceDirectory().getFileObject("app/webroot/index.php"); // NOI18N
			if (index != null) {
				files.add(index);
			}
		}

		return files;
	}
	
	public NewProjectConfigurationPanel getPanel(){
		if(panel == null){
			panel = new NewProjectConfigurationPanel();
		}
		return panel;
	}
	
	
}
