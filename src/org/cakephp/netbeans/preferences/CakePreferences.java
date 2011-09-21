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
	
	public static void setAppName(PhpModule phpModule, String appName){
		getPreferences(phpModule).put(APP_NAME, appName);
	}
	public static String getAppName(PhpModule phpModule){
		String appName = getPreferences(phpModule).get(APP_NAME, "");
		if(appName.equals("")){
			appName = "app";
		}
		return appName;
	}
	
	public static void setAutoCreateView(PhpModule phpModule, boolean isAuto){
		getPreferences(phpModule).putBoolean(AUTO_CREATE_VIEW, isAuto);
	}
	
	public static boolean getAutoCreateView(PhpModule phpModule){
		return getPreferences(phpModule).getBoolean(AUTO_CREATE_VIEW, false);
	}
		
	private static Preferences getPreferences(PhpModule phpModule){
		return phpModule.getPreferences(CakePreferences.class, true);
	}
}
