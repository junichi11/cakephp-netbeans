package org.cakephp.netbeans.util;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;

/**
 *
 * @author junichi11
 */
public class CakePhpCodeUtilsTest extends NbTestCase{
    
    public CakePhpCodeUtilsTest(String name) {
        super(name);
    }

    /**
     * Test of getStringValue method, of class CakePhpCodeUtils.
     */
    @Test
    public void testGetStringValue() {
        Expression e = null;
        String result = CakePhpCodeUtils.getStringValue(e);
        assertEquals("", result);

        Scalar scalar = new Scalar(0, 0, "1", Scalar.Type.INT);
        assertEquals("", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "test", Scalar.Type.REAL);
        assertEquals("", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "test", Scalar.Type.UNKNOWN);
        assertEquals("", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "test", Scalar.Type.SYSTEM);
        assertEquals("", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "'test'", Scalar.Type.STRING);
        assertEquals("test", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "\"test\"", Scalar.Type.STRING);
        assertEquals("test", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "'\"test\"'", Scalar.Type.STRING);
        assertEquals("\"test\"", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "'tes\"t'", Scalar.Type.STRING);
        assertEquals("tes\"t", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "\"t'est\"", Scalar.Type.STRING);
        assertEquals("t'est", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "''", Scalar.Type.STRING);
        assertEquals("", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "\"\"", Scalar.Type.STRING);
        assertEquals("", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "'", Scalar.Type.STRING);
        assertEquals("", CakePhpCodeUtils.getStringValue(scalar));

        scalar = new Scalar(5, 5, "", Scalar.Type.STRING);
        assertEquals("", CakePhpCodeUtils.getStringValue(scalar));
    }

    /**
     * Test of getEntity method, of class CakePhpCodeUtils.
     */
    @Test
    public void testGetEntity() {
        ArrayCreation ac = null;
        assertEquals(null, CakePhpCodeUtils.getEntity(ac));
        
        Scalar scalarValue = new Scalar(5, 5, "3", Scalar.Type.INT);
        List<ArrayElement> list = new ArrayList<ArrayElement>();
        list.add(new ArrayElement(0, 0, scalarValue));
        ac = new ArrayCreation(0, 0, list);
        assertEquals(null, CakePhpCodeUtils.getEntity(ac));

        Scalar scalarKey = new Scalar(5, 5, "\"test\"", Scalar.Type.STRING);
        scalarValue = new Scalar(5, 5, "", Scalar.Type.STRING);
        list = new ArrayList<ArrayElement>();
        list.add(new ArrayElement(0, 0, scalarKey, scalarValue));
        ac = new ArrayCreation(0, 0, list);
        assertEquals(null, CakePhpCodeUtils.getEntity(ac));

        scalarKey = new Scalar(1, 3, "key", Scalar.Type.STRING);
        scalarValue = new Scalar(5, 5, "5", Scalar.Type.INT);
        list = new ArrayList<ArrayElement>();
        list.add(new ArrayElement(0, 0, scalarKey));
        list.add(new ArrayElement(0, 0, new Scalar(10, 12, "test", Scalar.Type.STRING), scalarValue));
        ac = new ArrayCreation(0, 0, list);
        assertEquals(null, CakePhpCodeUtils.getEntity(ac));
        
        scalarValue = new Scalar(5, 5, "className", Scalar.Type.STRING);
        list = new ArrayList<ArrayElement>();
        list.add(new ArrayElement(0, 0, scalarValue));
        ac = new ArrayCreation(0, 0, list);
        assertEquals(null, CakePhpCodeUtils.getEntity(ac));
        
        scalarKey = new Scalar(1, 3, "className", Scalar.Type.STRING);
        scalarValue = new Scalar(5, 5, "5", Scalar.Type.INT);
        list = new ArrayList<ArrayElement>();
        list.add(new ArrayElement(0, 0, scalarKey, scalarValue));
        ac = new ArrayCreation(0, 0, list);
        assertEquals(null, CakePhpCodeUtils.getEntity(ac));
        
        scalarKey = new Scalar(1, 3, "'className'", Scalar.Type.STRING);
        scalarValue = new Scalar(5, 5, "Entity", Scalar.Type.STRING);
        list = new ArrayList<ArrayElement>();
        list.add(new ArrayElement(0, 0, scalarKey, scalarValue));
        ac = new ArrayCreation(0, 0, list);
        assertEquals(scalarValue, CakePhpCodeUtils.getEntity(ac));
        
        scalarKey = new Scalar(1, 3, "\"className\"", Scalar.Type.STRING);
        scalarValue = new Scalar(5, 5, "Entity", Scalar.Type.STRING);
        list = new ArrayList<ArrayElement>();
        list.add(new ArrayElement(0, 0, scalarKey, scalarValue));
        ac = new ArrayCreation(0, 0, list);
        assertEquals(scalarValue, CakePhpCodeUtils.getEntity(ac));
    }
}
