package org.cakephp.netbeans.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public class CakePhpOptions {
        private static final int NAME = 0;
        private static final int URL = 1;
        private static final String PLUGINS = "plugins"; // NOI18N
        private static CakePhpOptions INSTANCE = new CakePhpOptions();
        
        private CakePhpOptions(){
                
        }
        
        public static CakePhpOptions getInstance(){
                return INSTANCE;
        }

        public List<CakePhpPlugin> getPlugins(){
                ArrayList<CakePhpPlugin> plugins = new ArrayList<CakePhpPlugin>();
                Preferences p = getPreferences().node(PLUGINS);
                String s = "";
                if(p != null){
                        s = p.get(PLUGINS, "");
                }
                if(s.isEmpty()){
                        s = NbBundle.getMessage(CakePhpOptions.class, "CakePhpOptions.defaultPlugins"); // NOI18N
                }

                String[] rows = s.split(";");
                Arrays.sort(rows);
                for (String row : rows) {
                        String[] cells = row.split(",");
                        if (cells.length == 2) {
                                plugins.add(new CakePhpPlugin(cells[NAME], cells[URL]));
                        }
                }
                return plugins;
        }
        
        public void setPlugins(List<CakePhpPlugin> plugins){
                Preferences p = getPreferences().node(PLUGINS);
                String lists = "";
                boolean first = true;
                for(CakePhpPlugin plugin: plugins){
                        if(first){
                                first = false;
                        }else{
                                lists += ";";
                        }
                        lists += plugin.getName() + "," + plugin.getUrl();
                }
                p.put(PLUGINS, lists);
        }
        
        public Preferences getPreferences(){
                return NbPreferences.forModule(CakePhpOptions.class).node(PLUGINS);
        }
}
