/*
 * TODO: add license
 */
package org.cakephp.netbeans.ui.logicalview;

import java.awt.Image;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.util.ImageUtilities;

/**
 * @author junichi11
 */
public class ModelNode extends FilterNode{
	private static Image smallImage = ImageUtilities.loadImage("org/cakephp/netbeans/ui/resources/cakephp_icon_8.png");
	
	public ModelNode(FileObject fo) throws DataObjectNotFoundException{
            super(DataObject.find(fo).getNodeDelegate());
	}
	
	@Override
	public String getDisplayName(){
		return "Model";
	}
	
	@Override
	public Image getIcon(int type){
		DataFolder root = DataFolder.findFolder(FileUtil.getConfigRoot());
		Image original = root.getNodeDelegate().getIcon(type);
		return ImageUtilities.mergeImages(original, smallImage, 7, 7);
	}

	public Image getOpenIcon(int type){
		DataFolder root = DataFolder.findFolder(FileUtil.getConfigRoot());
		Image original = root.getNodeDelegate().getIcon(type);
		return ImageUtilities.mergeImages(original, smallImage, 7, 7);
	}
}
