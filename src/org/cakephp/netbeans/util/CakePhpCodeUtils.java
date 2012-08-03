package org.cakephp.netbeans.util;

import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;

/**
 *
 * @author junichi11
 */
public class CakePhpCodeUtils {
    
        /**
         * Get string name from Expression
         *
         * @param e Expression
         * @return string name | empty string
         */
        public static String getStringValue(Expression e) {
            String name = ""; // NOI18N
            if (e instanceof Scalar) {
                Scalar s = (Scalar) e;
                if (s.getScalarType() == Scalar.Type.STRING) {
                    name = s.getStringValue();
                }
                if(name.length() > 2){
                    name = name.substring(1, name.length() - 1);
                } else {
                    name = "";
                }
            }
            return name;
        }
        
        /**
         * Get entity
         * @param ac ArrayElement object
         * @return entity | null
         */
        public static Expression getEntity(ArrayCreation ac){
            if(ac == null){
                return null;
            }
            for(ArrayElement element : ac.getElements()){
                Expression key = element.getKey();
                String keyName = getStringValue(key);
                if(keyName.equals("className")){ // NOI18N
                    return element.getValue();
                }
            }
            return null;
        }
}
