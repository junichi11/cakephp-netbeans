/*
 */
package org.cakephp.netbeans.resources;

import java.awt.Image;
import org.openide.loaders.DataNode;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public class CtpDataNode extends DataNode {

    private static final String CTP_ICON = "org/cakephp/netbeans/resources/ctp_icon.png"; // NOI18N
    Image icon;

    public CtpDataNode(MultiDataObject dataObject, Lookup lookup) {
        super(dataObject, Children.LEAF, lookup);
        if (dataObject.getPrimaryFile().getExt().equals("ctp")) { // NOI18N
            icon = ImageUtilities.loadImage(CTP_ICON);
        }
    }

    @Override
    public Image getIcon(int arg0) {
        if (icon == null) {
            icon = super.getIcon(arg0);
        }
        return icon;
    }
}
