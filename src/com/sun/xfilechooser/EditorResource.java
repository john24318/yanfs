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

import java.util.ListResourceBundle;

/**
 * EditorResource class is needed for I8N. It defines
 * the strings that should be internationalized.
 */
public class EditorResource extends ListResourceBundle {
    @Override
    public Object[][] getContents() {
    return contents;
    }

    static final Object[][] contents = {
    {"Dialog Type", "Dialog Type"},
    {"Dialog Title", "Dialog Title"},
    {"File Selection Mode", "File Selection Mode"},
    {"Show Hidden Files", "Show Hidden Files"},
    {"Approve Button Text", "Approve Button Text"},
    {"Approve Button Tooltip", "Approve Button Tooltip"},
    {"Approve Button Mnemonic", "Approve Button Mnemonic"},
    {"Set Current Directory", "Set Current Directory"},
    {"Set Background Color", "Set Background Color"},
    {"Set Foreground Color", "Set Foreground Color"},
    {"Open", "Open"},
    {"Save", "Save"},
    {"Custom", "Custom"},
    {"Files Only", "Files Only"},
    {"Directories Only", "Directories Only"},
    {"Files/Directories", "Files/Directories"},
    {"New Folder", "New Folder"},
    {"NewFolder", "NewFolder"}
    };
}












