package org.cakephp.netbeans.commands;

import java.lang.ref.WeakReference;
import org.cakephp.netbeans.CakeScript;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.commands.FrameworkCommand;

/**
 *
 * @author junichi11
 */
public class CakePhpCommand extends FrameworkCommand{
	private final WeakReference<PhpModule> phpModule;
	
	public CakePhpCommand(PhpModule phpModule, String command, String description, String displayName){
		super(command, description, displayName);
		assert phpModule != null;
		this.phpModule = new WeakReference<PhpModule>(phpModule);
	}

	public CakePhpCommand(PhpModule phpModule, String[] command, String description, String displayName){
		super(command, description, displayName);
		assert phpModule != null;
		this.phpModule = new WeakReference<PhpModule>(phpModule);
	}

	@Override
	protected String getHelpInternal() {
		PhpModule module = phpModule.get();
		if (module == null) {
			return ""; // NOI18N
		}
		return CakeScript.getHelp(module, this);
	}
}
