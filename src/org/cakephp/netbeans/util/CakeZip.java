package org.cakephp.netbeans.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public class CakeZip {
        private String rootName;
        
        public CakeZip(String rootName){
                this.rootName = rootName;
        }
                
        public void unzip(String url, FileObject fo) throws IOException{
                try {
                        URL zipUrl = new URL(url);
                        ZipInputStream zipInputStream = new ZipInputStream(zipUrl.openStream());
                        ZipEntry zipEntry = null;
                        boolean firstFlg = true;
                        String rootDir = ""; // NOI18N
                        if(fo.getFileObject(rootName) == null){
                                fo.createFolder(rootName);
                        }
                        while((zipEntry = zipInputStream.getNextEntry()) != null){
                                if(firstFlg == true){
                                        rootDir = zipEntry.getName();
                                        firstFlg = false;
                                        zipInputStream.closeEntry();
                                        continue;
                                }
                                
                                // set plugin name
                                String zipName = zipEntry.getName().replace(rootDir, rootName + "/"); // NOI18N
                                File baseDir = FileUtil.toFile(fo);
                                File outFile = new File(baseDir, zipName);
                                
                                // make dir
                                if(fo != null && zipEntry.isDirectory()){
                                        outFile.mkdir();
                                        zipInputStream.closeEntry();
                                        continue;
                                }
                                
                                // write data
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
                } catch (MalformedURLException ex) {
                        Exceptions.printStackTrace(ex);
                }
        }
}
