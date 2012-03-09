package org.cakephp.netbeans.preferences;

import java.util.prefs.Preferences;
import org.netbeans.modules.php.api.phpmodule.PhpModule;

/**
 *
 * @author junichi11
 */
public class CakePreferences {
	private static final String APP_NAME = "app-name";
	private static final String AUTO_CREATE_VIEW = "auto-create-view";
        private static final String CAKE_PHP_DIR_PATH = "cake-php-dir-path";
        private static final String DEFAULT_APP_NAME = "app";
        private static final String USE_PROJECT_DIRECTORY = "use-project-directory";
	
	public static void setAppName(PhpModule phpModule, String appName){
		getPreferences(phpModule).put(APP_NAME, appName);
	}
	public static String getAppName(PhpModule phpModule){
		String appName = getPreferences(phpModule).get(APP_NAME, "");
		if(appName.equals("")){
			appName = DEFAULT_APP_NAME;
		}
		return appName;
	}
	
	public static void setAutoCreateView(PhpModule phpModule, boolean isAuto){
		getPreferences(phpModule).putBoolean(AUTO_CREATE_VIEW, isAuto);
	}
	
	public static boolean getAutoCreateView(PhpModule phpModule){
		return getPreferences(phpModule).getBoolean(AUTO_CREATE_VIEW, false);
	}
		
        public static void setCakePhpDirPath(PhpModule phpModule, String path){
                getPreferences(phpModule).put(CAKE_PHP_DIR_PATH, path);
        }
        
        public static String getCakePhpDirPath(PhpModule phpModule){
                return getPreferences(phpModule).get(CAKE_PHP_DIR_PATH, "");
        }
        
        public static void setUseProjectDirectory(PhpModule phpModule, boolean isProject){
                getPreferences(phpModule).putBoolean(USE_PROJECT_DIRECTORY, isProject);
        }
        
        public static boolean useProjectDirectory(PhpModule phpModule){
                return getPreferences(phpModule).getBoolean(USE_PROJECT_DIRECTORY, false);
        }
        
	private static Preferences getPreferences(PhpModule phpModule){
		return phpModule.getPreferences(CakePreferences.class, true);
	}
}
