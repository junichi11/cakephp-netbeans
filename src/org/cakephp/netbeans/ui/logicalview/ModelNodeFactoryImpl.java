/*
 * TODO: add license
 */
package org.cakephp.netbeans.ui.logicalview;

import org.netbeans.api.project.Project;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
@NodeFactory.Registration(projectType = "org-netbeans-modules-php-project", position = 20)
public class ModelNodeFactoryImpl implements NodeFactory{
	@Override
	public NodeList createNodes(Project prj){
		PhpModule module = prj.getLookup().lookup(PhpModule.class);
		FileObject targetDir = module.getSourceDirectory().getFileObject("app/models");
		if(targetDir == null){
			targetDir = module.getSourceDirectory().getFileObject("app/Model");
		}
		if(targetDir != null){
			try{
				ModelNode node = new ModelNode(targetDir);
				return NodeFactorySupport.fixedNodeList(node);
			}catch(DataObjectNotFoundException ex){
				Exceptions.printStackTrace(ex);
			}
		}
		return NodeFactorySupport.fixedNodeList();
	}
}
