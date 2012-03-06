package org.cakephp.netbeans.options;

/**
 *
 * @author junichi11
 */
public class CakePhpPlugin {
        private String name;
        private String url;
        private boolean install;
        
        CakePhpPlugin(String name, String url){
                this.name = name;
                this.url = url;
        }
        
        public String getName(){
                return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         * @return the url
         */
        public String getUrl() {
                return url;
        }

        /**
         * @param url the url to set
         */
        public void setUrl(String url) {
                this.url = url;
        }

        /**
         * @return the install
         */
        public boolean isInstall() {
                return install;
        }

        /**
         * @param install the install to set
         */
        public void setInstall(boolean install) {
                this.install = install;
        }
}
