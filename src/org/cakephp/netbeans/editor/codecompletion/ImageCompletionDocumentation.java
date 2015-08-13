/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.cakephp.netbeans.editor.codecompletion;

import java.awt.Image;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class ImageCompletionDocumentation implements CompletionDocumentation {

    private final ImageCompletionItem item;
    private static final List<String> EXTS = Arrays.asList("jpg", "jpeg", "gif", "png", "bmp", "ico"); // NOI18N

    ImageCompletionDocumentation(ImageCompletionItem item) {
        this.item = item;
    }

    @Override
    public String getText() {
        FileObject fileObject = item.getFileObject();
        StringBuilder imgTag = new StringBuilder();
        if (fileObject != null) {
            String ext = fileObject.getExt().toLowerCase(Locale.ENGLISH);
            if (EXTS.contains(ext)) {
                int height = 0;
                double width = 0;
                Image image = new ImageIcon(fileObject.toURL()).getImage();
                width = image.getWidth(null);
                height = image.getHeight(null);
                imgTag.append("<img src=\"file:").append(fileObject.getPath()).append("\""); // NOI18N
                if (width > 500) {
                    double resizeHeight = 0;
                    double rate = width / 500;
                    resizeHeight = height / rate;
                    height = (int) resizeHeight;
                    imgTag.append(" width=\"").append("500").append("\"") // NOI18N
                            .append(" height=\"").append(height).append("\""); // NOI18N
                }
                imgTag.append(" />"); // NOI18N
            }
        }
        return imgTag.toString();
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public CompletionDocumentation resolveLink(String link) {
        return null;
    }

    @Override
    public Action getGotoSourceAction() {
        return null;
    }
}
