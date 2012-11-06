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
import org.cakephp.netbeans.CakePhpFrameworkProvider;
import org.cakephp.netbeans.util.CakePhpUtils;
import org.cakephp.netbeans.util.CakePhpUtils.DIR;
import org.cakephp.netbeans.util.CakePhpUtils.FILE;
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

    private static final String DEBUG_REGEX = "^\\tConfigure::write\\('debug', (.+)\\)"; // NOI18N
    private static final String CONFIGURE_WRITE_DEBUG = "\tConfigure::write('debug', %s);"; // NOI18N
    private final ImageIcon icon = new ImageIcon(getClass().getResource("/org/cakephp/netbeans/ui/resources/cakephp_icon_16.png")); // NOI18N
    private final JLabel debugLabel = new JLabel(""); // NOI18N
    private static final Map<String, String> debugLevel = new HashMap();
    private Lookup.Result result = null;
    private PhpModule phpModule = null;
    private String level = "";
    private JList list;
    private DefaultListModel model;
    private Popup popup;
    private boolean popupFlg = false;

    static {
        debugLevel.put("0", "0");
        debugLevel.put("1", "1");
        debugLevel.put("2", "2");
    }

    public CakePhpStatusLineElement() {
        result = Utilities.actionsGlobalContext().lookupResult(FileObject.class);
        result.addLookupListener(new LookupListenerImpl());

        model = new DefaultListModel();
        for (String debugLv : debugLevel.keySet()) {
            model.addElement(debugLv);
        }
        list = new JList(model);

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
                        String debugLv = list.getSelectedValue().toString();
                        if (!debugLv.equals(level)) {
                            writeCore(debugLv);
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

    private Component panelWithSeparator(JLabel cell) {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL) {
            private static final long serialVersionUID = -6385848933295984637L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(3, 3);
            }
        };
        separator.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(separator, BorderLayout.WEST);
        panel.add(cell);
        return panel;
    }

    /**
     * Get debug level
     *
     * @param core app/Config/core
     * @return debug level
     */
    public String getDebugLevel(FileObject core) {
        String debubLv = "";
        Pattern pattern = Pattern.compile(DEBUG_REGEX);

        try {
            List<String> lines = core.asLines();
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    debubLv = matcher.group(1);
                    break;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return debubLv;
    }

    /**
     * Set debug debugLv
     *
     * @param debugLv
     */
    private void setDebugLevelLabel(String debugLv) {
        if (debugLv.matches("^[012]$")) { // NOI18N
            debugLabel.setText(debugLevel.get(debugLv));
        } else {
            debugLabel.setText(debugLv);
        }
        debugLabel.setIcon(icon);
    }

    /**
     * Clear debug label
     */
    private void clearLabel() {
        debugLabel.setText(""); //NOI18N
        debugLabel.setIcon(null);
    }

    /**
     * Write core file.
     * change debug level.
     *
     * @param debugLv
     */
    private void writeCore(String debugLv) {
        FileObject config = CakePhpUtils.getDirectory(phpModule, DIR.APP, FILE.CONFIG, null);
        if (config == null) {
            return;
        }
        FileObject core = config.getFileObject("core.php"); // NOI18N
        if (core == null) {
            return;
        }
        try {
            List<String> lines = core.asLines();
            Pattern pattern = Pattern.compile(DEBUG_REGEX);
            PrintWriter pw = new PrintWriter(core.getOutputStream());
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    line = String.format(CONFIGURE_WRITE_DEBUG, debugLv);
                }
                pw.println(line);
            }
            pw.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void setLevel(String level){
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public void setPhpModule(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    public PhpModule getPhpModule() {
        return phpModule;
    }

    private class LookupListenerImpl implements LookupListener {

        @Override
        public void resultChanged(LookupEvent lookupEvent) {
            Lookup.Result lookupResult = (Lookup.Result) lookupEvent.getSource();
            Collection c = lookupResult.allInstances();
            FileObject fileObject = null;
            if (!c.isEmpty()) {
                fileObject = (FileObject) c.iterator().next();
            } else {
                clearLabel();
                return;
            }

            PhpModule pmTemp = PhpModule.forFileObject(fileObject);
            if (pmTemp == null || !CakePhpFrameworkProvider.getInstance().isInPhpModule(pmTemp)) {
                clearLabel();
                return;
            }
            PhpModule pm = getPhpModule();
            if (pm == pmTemp) {
                setDebugLevelLabel(getLevel());
                return;
            } else {
                pm = pmTemp;
                setPhpModule(pm);
            }

            FileObject config = CakePhpUtils.getDirectory(pm, DIR.APP, FILE.CONFIG, null);
            FileObject core = config.getFileObject("core.php"); // NOI18N
            if (core == null) {
                return;
            }

            core.addFileChangeListener(new FileChangeAdapter() {
                @Override
                public void fileChanged(FileEvent fe) {
                    String level = getDebugLevel(fe.getFile());
                    setLevel(level);
                    setDebugLevelLabel(level);
                }
            });

            String level = getDebugLevel(core);
            setLevel(level);
            setDebugLevelLabel(level);
        }
    }
}
