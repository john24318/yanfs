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

import java.io.File;
import com.sun.xfile.*;
import java.io.IOException;

/**
 *  The BeanXFile class is the interface that makes an XFile object
 *  look like a File object.  This class is needed to support the
 *  UI of the JFileChooser which accesses file objects.
 *  Thus all the methods would call the corresponding XFile methods.
 *
 *  @see #XFile
 */
public class BeanXFile extends File {

    private XFile beanXF;

    /*
     * BeanXFile constructors which mirror the File I/O constructors.
     */
    public BeanXFile(String path) {
    super(path);
    beanXF = new XFile(path);
    }

    public BeanXFile(File dir, String name) {
    super(dir, name);

    XFile parentXF = new XFile(dir.getAbsolutePath());
    beanXF = new XFile(parentXF, name);
    }

    /*
     * XFile Methods that can be accessed.
     */
    @Override
    public String getPath() {
    String path = beanXF.getPath();

    // For nfs URLs, if the url is nfs://<server_name>, path is ""
    if (path.equals(""))
        path = beanXF.getAbsolutePath();

    return path;
    }

    @Override
    public String getAbsolutePath() {
    return beanXF.getAbsolutePath();
    }

    @Override
    public String getCanonicalPath() {
    try {
        String path = beanXF.getCanonicalPath();
        return path;
    } catch (IOException e) {
        String path = beanXF.getAbsolutePath();
        return path;
    }

    }

    @Override
    public String getName() {
    String fname = beanXF.getName();
    if (fname == null)
        return(beanXF.getAbsolutePath());
    else
        return(fname);
    }

    @Override
    public boolean renameTo(File dest) {
    XFile tmpFile = new XFile(dest.getAbsolutePath());
    return (beanXF.renameTo(tmpFile));
    }

    @Override
    public String getParent() {
    return beanXF.getParent();
    }

    @Override
    public boolean exists() {
    return beanXF.exists();
    }

    @Override
    public boolean canWrite(){
    return beanXF.canWrite();
    }

    @Override
    public boolean canRead() {
    return beanXF.canRead();
    }

    @Override
    public boolean isFile() {
    return beanXF.isFile();
    }

    @Override
    public boolean isDirectory() {
    return beanXF.isDirectory();
    }

    @Override
    public boolean isAbsolute() {
       // For nfs urls: isAbsolute is always true
       return beanXF.isAbsolute();
    }

    @Override
    public boolean equals(Object obj) {
    /*
     * Need to pass the XFile object to *.equals because
     * it checks for instance of XFile
     */
    XFile xf = new XFile(((File)obj).getAbsolutePath());
    return beanXF.equals(xf);
    }

    @Override
    public long lastModified() {
    return beanXF.lastModified();
    }

    @Override
    public long length() {
    return beanXF.length();
    }

    @Override
    public boolean mkdir() {
    return beanXF.mkdir();
    }

    @Override
    public boolean mkdirs() {
    return beanXF.mkdirs();
    }

    @Override
    public String[] list() {
    return beanXF.list();
    }

    @Override
    public String toString() {
    return beanXF.toString();
    }

    @Override
    public boolean delete() {
    return beanXF.delete();
    }

}
