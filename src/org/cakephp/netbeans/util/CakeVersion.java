package org.cakephp.netbeans.util;

import java.io.IOException;
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public class CakeVersion {

    private int mejor;
    private int minor;
    private int revision;
    private String notStable;
    private static CakeVersion INSTANCE = null;
    private static PhpModule pm;

    private CakeVersion(PhpModule pm) {
        CakeVersion.pm = pm;
        String[] split = getCakePhpVersionSplit(pm);
        switch (split.length) {
            case 4:
                notStable = split[3];
            case 3:
                revision = Integer.parseInt(split[2]);
            case 2:
                minor = Integer.parseInt(split[1]);
            case 1:
                mejor = Integer.parseInt(split[0]);
                break;
            case 0:
            default:
                mejor = -1;
                minor = -1;
                revision = -1;
                notStable = ""; // NOI18N
                break;
        }
    }

    public static CakeVersion getInstance(PhpModule pm) {
        if (INSTANCE == null || CakeVersion.pm != pm) {
            INSTANCE = new CakeVersion(pm);
        }
        return INSTANCE;
    }

    public int getMejor() {
        return mejor;
    }

    public int getMinor() {
        return minor;
    }

    public int getRevision() {
        return revision;
    }

    public String getNotStable() {
        return notStable;
    }

    /**
     * Check CakePHP mejor version
     *
     * @param mejorVersion
     * @return
     */
    public boolean isCakePhp(int mejorVersion) {
        return mejor == mejorVersion;
    }

    /**
     * Get CakePHP version.
     *
     * @param PhpModule phpModule
     * @return String If can't get the version file, return null.
     */
    private String getCakePhpVersion(PhpModule phpModule) {
        FileObject root = CakePhpFrameworkProvider.getCakePhpDirectory(phpModule);
        FileObject cake = root.getFileObject("cake"); // NOI18N
        FileObject version;
        if (cake != null) {
            version = root.getFileObject("cake/VERSION.txt"); // NOI18N
        } else {
            version = root.getFileObject("lib/Cake/VERSION.txt"); // NOI18N
        }
        if (version == null) {
            return null;
        }
        try {
            String versionNumber = null;
            for (String line : version.asLines("UTF-8")) { // NOI18N
                if (!line.contains("//") && !line.equals("")) { // NOI18N
                    line = line.trim();
                    versionNumber = line;
                    break;
                }
            }
            return versionNumber;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private String[] getCakePhpVersionSplit(PhpModule phpModule) {
        String version = getCakePhpVersion(phpModule);
        if (version == null) {
            return null;
        }
        return version.split("[., -]"); // NOI18N
    }
}
