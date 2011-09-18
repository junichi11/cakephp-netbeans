package org.cakephp.netbeans.preferences;

import java.util.prefs.Preferences;
import org.netbeans.modules.php.api.phpmodule.PhpModule;

/**
 *
 * @author junichi11
 */
public class CakePreferences {
	private static final String APP_NAME = "app-name";
	
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
	
	private static Preferences getPreferences(PhpModule phpModule){
		return phpModule.getPreferences(CakePreferences.class, true);
	}
}
