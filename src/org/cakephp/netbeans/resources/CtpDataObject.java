package org.cakephp.netbeans.resources;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;

/**
 * 
 * @author junichi11
 */
public class CtpDataObject extends MultiDataObject {

	public CtpDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
		super(pf, loader);
		CookieSet cookies = getCookieSet();
		cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
	}

	@Override
	protected Node createNodeDelegate() {
		return new CtpDataNode(this, getLookup());
	}

	@Override
	public Lookup getLookup() {
		return getCookieSet().getLookup();
	}
}
