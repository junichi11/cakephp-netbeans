/*
 * TODO: add license
 */
package org.cakephp.netbeans.ui.logicalview;

import org.cakephp.netbeans.util.CakePhpUtils;
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
@NodeFactory.Registration(projectType = "org-netbeans-modules-php-project", position = 600)
public class ModelNodeFactoryImpl implements NodeFactory {

    @Override
    public NodeList createNodes(Project prj) {
        PhpModule module = prj.getLookup().lookup(PhpModule.class);
        FileObject targetDir = CakePhpUtils.getDirectory(module, CakePhpUtils.DIR.APP, CakePhpUtils.FILE.MODEL, null);
        if (targetDir != null) {
            try {
                ModelNode node = new ModelNode(targetDir);
                return NodeFactorySupport.fixedNodeList(node);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return NodeFactorySupport.fixedNodeList();
    }
}
