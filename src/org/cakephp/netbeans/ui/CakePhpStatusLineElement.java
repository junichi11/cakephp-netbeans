/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.cakephp.netbeans.CakePhp;
import org.cakephp.netbeans.modules.CakePhpModule;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.versions.CakeVersion;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.awt.StatusLineElementProvider;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public class CakePhpStatusLineElement implements StatusLineElementProvider {

    private static final String DEBUG_CORE_REGEX = "^\\tConfigure::write\\('debug', (.+)\\)"; // NOI18N
    private static final String DEBUG_CORE_FORMAT = "\tConfigure::write('debug', %s);"; // NOI18N
    private final ImageIcon icon = new ImageIcon(getClass().getResource("/" + CakePhp.CAKE_ICON_16)); // NOI18N
    private final JLabel debugLabel = new JLabel(""); // NOI18N
    private final JLabel cakeVersionLabel = new JLabel(""); // NOI18N
    private static final Map<String, String> DEBUG_LEVELS = new HashMap<>();
    private Lookup.Result<FileObject> result = null;
    private PhpModule phpModule = null;
    private String level = ""; // NOI18N
    private JList<String> list;
    private final DefaultListModel<String> model;
    private Popup popup;
    private boolean popupFlg = false;
    private FileChangeAdapterImpl fileChangeListener;

    static {
        DEBUG_LEVELS.put("0", "0"); // NOI18N
        DEBUG_LEVELS.put("1", "1"); // NOI18N
        DEBUG_LEVELS.put("2", "2"); // NOI18N
    }

    public CakePhpStatusLineElement() {
        // add lookup listener
        result = Utilities.actionsGlobalContext().lookupResult(FileObject.class);
        result.addLookupListener(new LookupListenerImpl());

        // create list
        model = new DefaultListModel<>();
        for (String debugLv : DEBUG_LEVELS.keySet()) {
            model.addElement(debugLv);
        }
        list = new JList<>(model);

        // add mouse listener
        debugLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Point labelStart = debugLabel.getLocationOnScreen();
                int x = Math.min(labelStart.x, labelStart.x + debugLabel.getSize().width - list.getPreferredSize().width);
                int y = labelStart.y - list.getPreferredSize().height;
                if (popup == null) {
                    popup = PopupFactory.getSharedInstance().getPopup(debugLabel, list, x, y);
                }
                list.addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        String debugLv = list.getSelectedValue();
                        if (!debugLv.equals(level)) {
                            writeConfig(debugLv);
                        }
                        popupFlg = false;
                        if (popup != null) {
                            popup.hide();
                            popup = null;
                        }
                    }
                });
                if (!popupFlg) {
                    popupFlg = true;
                    popup.show();
                } else {
                    popupFlg = false;
                    popup.hide();
                    popup = null;
                }
            }
        });
    }

    @Override
    public Component getStatusLineElement() {
        return panelWithSeparator(debugLabel);
    }

    /**
     * Create Component(JPanel) and add separator and JLabel to it.
     *
     * @param cell JLabel
     * @return panel
     */
    private Component panelWithSeparator(JLabel cell) {
        // create separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL) {
            private static final long serialVersionUID = -6385848933295984637L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(3, 3);
            }
        };
        separator.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // create panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(separator, BorderLayout.WEST);
        panel.add(cell, BorderLayout.EAST);
        panel.add(cakeVersionLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Get debug level
     *
     * @param config app/Config/{core|app}
     * @return debug level
     */
    public String getDebugLevel(FileObject config) {
        String debugLv = ""; // NOI18N
        Pattern pattern = Pattern.compile(getDebugRegex(config));

        try {
            List<String> lines = config.asLines("UTF-8"); // NOI18N
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    debugLv = matcher.group(1);
                    break;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return debugLv;
    }

    /**
     * Get regex for debug value.
     *
     * @param config core.php
     * @return regex for debug value.
     */
    private String getDebugRegex(@NonNull FileObject config) {
        return DEBUG_CORE_REGEX;
    }

    /**
     * Set debug debugLv
     *
     * @param debugLv
     */
    private void setDebugLevelLabel(String debugLv) {
        if (debugLv.matches("^[012]$")) { // NOI18N
            debugLabel.setText(DEBUG_LEVELS.get(debugLv));
        } else {
            debugLabel.setText(debugLv);
        }
    }

    /**
     * Clear debug label
     */
    private void clearLabel() {
        debugLabel.setText(""); //NOI18N
        cakeVersionLabel.setText(""); //NOI18N
        cakeVersionLabel.setIcon(null);
    }

    /**
     * Write config file. change debug level.
     *
     * @param debugLv
     */
    private void writeConfig(String debugLv) {
        FileObject config = getConfigFile();
        if (config == null) {
            return;
        }
        try {
            List<String> lines = config.asLines("UTF-8"); // NOI18N
            Pattern pattern = Pattern.compile(getDebugRegex(config));
            OutputStream outputStream = config.getOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true); // NOI18N
            try {
                for (String line : lines) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        line = String.format(getDebugFormat(config), debugLv);
                    }
                    pw.println(line);
                }
            } finally {
                outputStream.close();
                pw.close();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Get debug format.
     *
     * @param config
     * @return format for debug value.
     */
    private String getDebugFormat(@NonNull FileObject config) {
        return DEBUG_CORE_FORMAT;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    /**
     * Get app/Config/{core.php|app.php} file
     *
     * @return {core.php|app.php} if it exists, otherwise null
     */
    public FileObject getConfigFile() {
        CakePhpModule module = CakePhpModule.forPhpModule(phpModule);
        if (module == null) {
            return null;
        }
        return module.getConfigFile();
    }

    //~ Inner classes
    private class LookupListenerImpl implements LookupListener {

        @Override
        public void resultChanged(LookupEvent lookupEvent) {
            // get FileObject
            FileObject fileObject = getFileObject(lookupEvent);
            if (fileObject == null) {
                clearLabel();
                return;
            }

            // get PhpModule
            PhpModule currentPhpModule = PhpModule.Factory.forFileObject(fileObject);
            if (!CakePhpUtils.isCakePHP(currentPhpModule)) {
                clearLabel();
                phpModule = null;
                return;
            }

            // check whether move to other project
            if (phpModule == currentPhpModule) {
                String lv = getLevel();
                setDebugLevelLabel(lv);
                list.setSelectedValue(lv, false);
                setCakePHPVersion(phpModule);
                return;
            } else {
                // phpModule is null at first time
                if (phpModule != null) {
                    FileObject config = getConfigFile();
                    if (config != null && fileChangeListener != null) {
                        config.removeFileChangeListener(fileChangeListener);
                    }
                }
                phpModule = currentPhpModule;

                // set CakePHP version
                setCakePHPVersion(phpModule);
            }

            // get conifg file
            FileObject config = getConfigFile();
            if (config == null) {
                return;
            }

            // add FileChangeListener to config file
            if (fileChangeListener == null) {
                fileChangeListener = new FileChangeAdapterImpl();
            }
            config.addFileChangeListener(fileChangeListener);
            config.refresh();

            // set debug level
            String level = getDebugLevel(config);
            setLevel(level);
            setDebugLevelLabel(level);
            list.setSelectedValue(level, false);
        }

        /**
         * Get FileObject
         *
         * @param lookupEvent
         * @return current FileObject if exists, otherwise null
         */
        private FileObject getFileObject(LookupEvent lookupEvent) {
            Lookup.Result<?> lookupResult = (Lookup.Result<?>) lookupEvent.getSource();
            Collection<?> c = (Collection<?>) lookupResult.allInstances();
            FileObject fileObject = null;
            if (!c.isEmpty()) {
                fileObject = (FileObject) c.iterator().next();
            }
            return fileObject;
        }

        /**
         * Set CakePHP version.
         *
         * @param phpModule
         */
        private void setCakePHPVersion(PhpModule phpModule) {
            CakePhpModule cakeModule = CakePhpModule.forPhpModule(phpModule);
            if (cakeModule == null) {
                clearLabel();
                return;
            }
            // TODO better checking
            CakeVersion version = cakeModule.getCakeVersion();
            if (version == null) {
                clearLabel();
                return;
            }
            String versionNumber = version.getVersion();
            cakeVersionLabel.setText(versionNumber + ":"); // NOI18N
            cakeVersionLabel.setIcon(icon);
        }
    }

    private class FileChangeAdapterImpl extends FileChangeAdapter {

        public FileChangeAdapterImpl() {
        }

        @Override
        public void fileChanged(FileEvent fe) {
            String level = getDebugLevel(fe.getFile());
            setLevel(level);
            setDebugLevelLabel(level);
            list.setSelectedValue(level, false);
        }
    }
}
