package org.cakephp.netbeans.commands;

import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.commands.FrameworkCommand;

/**
 *
 * @author junichi11
 */
public class CakePhpCommand extends FrameworkCommand{
	
	public CakePhpCommand(PhpModule phpModule, String command, String description, String displayName){
		super(command, description, displayName);
	}

	@Override
	protected String getHelpInternal() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
