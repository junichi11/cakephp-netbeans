/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cakephp.netbeans.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.cakephp.netbeans.ui.ViewPathPanel;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "View",
id = "org.cakephp.netbeans.ui.actions.ViewPathAction")
@ActionRegistration(displayName = "#CTL_ViewPathAction")
@ActionReferences({
	@ActionReference(path = "Toolbars/View", position = 0)
})
@Messages("CTL_ViewPathAction=View Path")
public final class ViewPathAction extends AbstractAction implements Presenter.Toolbar {
	private static final long serialVersionUID = -8271031482726036137L;
	
	@Override
	public Component getToolbarPresenter(){
		return new ViewPathPanel();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO implement action body
	}
	
}
