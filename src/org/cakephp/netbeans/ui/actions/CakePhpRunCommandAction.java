package org.cakephp.netbeans.ui.actions;

import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.actions.RunCommandAction;
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class CakePhpRunCommandAction extends RunCommandAction{

	private static final CakePhpRunCommandAction INSTANCE = new CakePhpRunCommandAction();
	private static final long serialVersionUID = 3814938231105448415L;
	
	private CakePhpRunCommandAction(){
	}
	
	public static CakePhpRunCommandAction getInstance(){
		return INSTANCE;
	}
	
	@Override
	public void actionPerformed(PhpModule phpModule) {
		if(!CakePhpFrameworkProvider.getInstance().isInPhpModule(phpModule)){
			return;
		}
		CakePhpFrameworkProvider.getInstance().getFrameworkCommandSupport(phpModule).runCommand();
	}

	@Override
	protected String getFullName() {
		return NbBundle.getMessage(CakePhpRunCommandAction.class, "LBL_CakePhpAction", getPureName());
	}
	
}
