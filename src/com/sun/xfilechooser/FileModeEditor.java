/*
 * Copyright (c) 1998, 2007, Oracle and/or its affiliates. All rights reserved.
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
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.*;
import com.sun.xfilechooser.*;

/**
 * An editor to set the File Selection Mode of XFileChooser. Used during
 * customization via a bean editor.
 * @see #XFileChooserBeanInfo
 */
public class FileModeEditor extends PropertyEditorSupport
{
    /* For I18N */
    private static ResourceBundle rb =
    ResourceBundle.getBundle("com.sun.xfilechooser.EditorResource"/*NOI18N*/);
    /* Valid Selection File modes */
    int[] fileModeValues = {JFileChooser.FILES_ONLY,
                JFileChooser.DIRECTORIES_ONLY,
                JFileChooser.FILES_AND_DIRECTORIES};

    /* Corresponding string names for the file modes */
    String[] fileModeNames = {rb.getString("Files Only"),
                  rb.getString("Directories Only"),
                  rb.getString("Files/Directories")};

    /**
     *  Provides the valid selection file modes: Files, Directories or
     *  Files/Directories
     *  @return String name of the valid file modes
     */
    @Override
    public String[] getTags() {
    return fileModeNames;
    }

    /**
     *  Gets the integer value of current selection file mode and returns the
     *  corresponding string of file mode.
     *  @return String name of file mode setting
     */
    @Override
    public String getAsText() {
    int s = ((Integer)getValue()).intValue();
    for (int i=0; i<fileModeNames.length; i++) {
        if (s == fileModeValues[i])
        return fileModeNames[i];
    }
    return null;
    }

    /**
     *  Sets the selected file mode
     *  @param text name of selected file mode
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
    for (int i=0; i<fileModeNames.length; i++) {
        if (text.equals(fileModeNames[i])) {
        setValue(new Integer(fileModeValues[i]));
        return;
        }
    }
    throw new IllegalArgumentException(text);
    }

}
