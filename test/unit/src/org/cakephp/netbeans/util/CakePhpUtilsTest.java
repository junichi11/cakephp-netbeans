/*
 * TODO: add license
 */

package org.cakephp.netbeans.util;

import org.junit.Test;
import org.netbeans.junit.NbTestCase;

public class CakePhpUtilsTest extends NbTestCase {

    public CakePhpUtilsTest(String name) {
        super(name);
    }
    
    @Test
    public void testActionName() {
        assertEquals("index", CakePhpUtils.getActionName("index"));
        assertEquals("myIndex", CakePhpUtils.getActionName("my_index"));
    }

    @Test
    public void testViewName() {
        assertEquals("index", CakePhpUtils.getViewFileName("index"));
        assertEquals("my_index", CakePhpUtils.getViewFileName("myIndex"));
    }
    
    @Test
    public void testIsControllerName(){
        assertTrue(CakePhpUtils.isControllerName("PostsController"));
        
	assertFalse(CakePhpUtils.isControllerName("Postscontroller"));
	assertFalse(CakePhpUtils.isControllerName("PostsHelper"));
    }
    
    @Test
    public void testIsControllerFileName(){
        assertTrue(CakePhpUtils.isControllerFileName("PostsController"));
        assertTrue(CakePhpUtils.isControllerFileName("posts_controller"));
	
        assertFalse(CakePhpUtils.isControllerFileName("PostsComponent"));
        assertFalse(CakePhpUtils.isControllerFileName("postscontroller"));
        assertFalse(CakePhpUtils.isControllerFileName("posts_helper"));
    }
}
