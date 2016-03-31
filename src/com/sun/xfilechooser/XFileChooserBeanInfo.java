/*
 * Copyright (c) 1999, 2007, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.xfilechooser;

import java.beans.*;
import com.sun.xfilechooser.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.*;

/**
 * Describes the XFileChooser properties that is modifiable by
 * user in the beans editor. Also will get the icon of
 * XFileChooser that will be shown on the palette of editor.
 */
public class XFileChooserBeanInfo extends SimpleBeanInfo {

    /* For I18N */
    private static ResourceBundle rb =
    ResourceBundle.getBundle("com.sun.xfilechooser.EditorResource"/*NOI18N*/);
    PropertyDescriptor[] beanProps;

    /*
     * Properties that are modifiable via a bean editor during
     * customization
     */
    private static Object xfBeanPropInits[][] = {
    {rb.getString("Dialog Type"), "getDialogType", "setDialogType", DialogEditor.class},
    {rb.getString("Dialog Title"), "getDialogTitle", "setDialogTitle", XFileChooser.class},
    {rb.getString("File Selection Mode"), "getFileSelectionMode", "setFileSelectionMode", FileModeEditor.class},
    {rb.getString("Show Hidden Files"), "isFileHidingEnabled", "setFileHidingEnabled", XFileChooser.class},
    {rb.getString("Approve Button Text"), "getApproveButtonText", "setApproveButtonText", XFileChooser.class},
    {rb.getString("Approve Button Tooltip"), "getApproveButtonToolTipText", "setApproveButtonToolTipText", XFileChooser.class},
    /* Currently commented out until bug 4206915 is fixed in the introspector */
    //{rb.getString("Approve Button Mnemonic"), "getApproveButtonMnemonic", "setApproveButtonMnemonic", XFileChooser.class},
    {rb.getString("Set Current Directory"), "getCurrentXDirectory", "setCurrentXDirectory", XFileChooserEditor.class},
    {rb.getString("Set Background Color"), "getBackground", "setBackground", XFileChooser.class},
    {rb.getString("Set Foreground Color"), "getForeground", "setForeground", XFileChooser.class},
    };

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
    beanProps = new PropertyDescriptor[xfBeanPropInits.length];
    for (int i=0; i < xfBeanPropInits.length; i++) {
        try {
        beanProps[i] = new PropertyDescriptor(
                    (String) xfBeanPropInits[i][0],
                (Class) XFileChooser.class,
                (String) xfBeanPropInits[i][1],
                (String) xfBeanPropInits[i][2]);
        } catch (IntrospectionException fatal) {
        System.out.println("name " + (String) xfBeanPropInits[i][0]);

        System.err.println("getProps() is flawed! " + i);
        }

        if (xfBeanPropInits[i][3] != null)
        beanProps[i].setPropertyEditorClass((Class) xfBeanPropInits[i][3]);
    }
    return beanProps;
    }

    @Override
    public java.awt.Image getIcon(int iconKind) {
    if (iconKind == BeanInfo.ICON_COLOR_16x16) {
        java.awt.Image img = loadImage("images/422LOGO5_16x16.gif");
        return img;
    }

    if (iconKind == BeanInfo.ICON_MONO_32x32) {
        java.awt.Image img = loadImage("images/422LOGO5_32x32.gif");
        return img;
    }
    return null;
    }
}

