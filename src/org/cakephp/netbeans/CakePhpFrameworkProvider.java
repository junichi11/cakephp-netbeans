/*
 * TODO: add license
 */

package org.cakephp.netbeans;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import org.cakephp.netbeans.editor.CakePhpEditorExtender;
import org.cakephp.netbeans.commands.CakePhpCommandSupport;
import org.netbeans.modules.php.api.phpmodule.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.spi.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleIgnoredFilesExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

// TODO: in static block, consider registering *.ctp as a php mime-type (can be dangerous, do it only if it's not already set!)
public final class CakePhpFrameworkProvider extends PhpFrameworkProvider {

    // TODO: provide better badge icon
    private static final String ICON_PATH = "org/cakephp/netbeans/ui/resources/cakephp_badge_8.png"; // NOI18N
    private static final CakePhpFrameworkProvider INSTANCE = new CakePhpFrameworkProvider();
    private static final Comparator<File> FILE_COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };

    private final BadgeIcon badgeIcon;

    private CakePhpFrameworkProvider() {
        super(NbBundle.getMessage(CakePhpFrameworkProvider.class, "LBL_CakePhpFramework"), NbBundle.getMessage(CakePhpFrameworkProvider.class, "LBL_CakePhpDescription"));
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                CakePhpFrameworkProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }

    @PhpFrameworkProvider.Registration(position=500)
    public static CakePhpFrameworkProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    @Override
    public boolean isInPhpModule(PhpModule phpModule) {
        // TODO: is this detection enough?
        FileObject cake = phpModule.getSourceDirectory().getFileObject("cake"); // NOI18N
        // cake 2.x.x
	if(cake == null){
		cake = phpModule.getSourceDirectory().getFileObject("lib/Cake");
	}
        return cake != null && cake.isFolder();
    }

    @Override
    public File[] getConfigurationFiles(PhpModule phpModule) {
        // return all php files from app/config
        List<File> configFiles = new LinkedList<File>();

        FileObject config = phpModule.getSourceDirectory().getFileObject("app/config"); // NOI18N
	// cake 2.x.x
	if(config == null){
		config = phpModule.getSourceDirectory().getFileObject("app/Config");
	}
        assert config != null : "app/config or app/Config not found for CakePHP project " + phpModule.getDisplayName();
        if (config != null && config.isFolder()) {
            Enumeration<? extends FileObject> children = config.getChildren(true);
            while (children.hasMoreElements()) {
                FileObject child = children.nextElement();
                if (child.isData() && FileUtils.isPhpFile(child)) {
                    configFiles.add(FileUtil.toFile(child));
                }
            }
        }
        if (!configFiles.isEmpty()) {
            Collections.sort(configFiles, FILE_COMPARATOR);
        }
        return configFiles.toArray(new File[configFiles.size()]);
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule phpModule) {
        // TODO: can we non-interactively create a project via 'cake' command?
        return null;
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule){
	    return new CakePhpModuleCustomizerExtender(phpModule);
    }
    
    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        PhpModuleProperties properties = new PhpModuleProperties();
	FileObject webroot = phpModule.getSourceDirectory().getFileObject("app/webroot");
	if(webroot != null){
	    properties.setWebRoot(webroot);
	}
	FileObject test = phpModule.getSourceDirectory().getFileObject("app/tests");
	if(test == null){
	    test = phpModule.getSourceDirectory().getFileObject("app/Test");
	}
	if(test != null){
	    properties.setTests(test);
	}
        return properties;
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule phpModule) {
        return new CakePhpActionsExtender();
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule phpModule) {
        return new CakePhpIgnoredFilesExtender(phpModule);
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule phpModule) {
        // TODO: provide list of commands (preferably in XML format)
        return new CakePhpCommandSupport(phpModule);
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule phpModule) {
        return new CakePhpEditorExtender();
    }
}
