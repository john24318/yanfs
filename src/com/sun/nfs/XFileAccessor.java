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

package com.sun.nfs;

import com.sun.xfile.*;
import java.io.*;
import java.net.*;

/**
 * The XFileAccessor interface is implemented by filesystems that
 * need to be accessed via the XFile API.
 *
 * @author  Brent Callaghan
 * @version 1.0, 04/08/98
 * @see     com.sun.xfile.XFile
 */
public
class XFileAccessor implements com.sun.xfile.XFileAccessor {

    XFile xf;
    boolean serial;
    boolean readOnly;
    Nfs nfs;

    /**
     * Open this NFS object
     *
     * @param xf the XFile object
     * @param serial   true if serial access
     * @param readOnly true if read only
     */
    public boolean open(XFile xf, boolean serial, boolean readOnly) {
        this.xf = xf;
        try {
            nfs = NfsConnect.connect(xf.getAbsolutePath());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public XFile getXFile() {
        return xf;
    }

    protected Nfs getParent(XFile xf) throws IOException {
        XFile xfp = new XFile(xf.getParent());
        XFileAccessor nfsx = new XFileAccessor();
        nfsx.open(xfp, serial, readOnly);

        return nfsx.getNfs();
    }

    protected Nfs getNfs() {
        return nfs;
    }

    /**
     * Tests if this XFileAccessor object exists.
     *
     * @return <code>true</code> if the file specified by this object
     *         exists; <code>false</code> otherwise.
     */
    public boolean exists() {
        try {
            return nfs.exists();
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Tests if the application can write to this file.
     *
     * @return <code>true</code> if the application is allowed to
     *         write to a file whose name is specified by this
     *         object; <code>false</code> otherwise.
     */
    public boolean canWrite() {
        try {
            return nfs.canWrite();
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Tests if the application can read from the specified file.
     *
     * @return <code>true</code> if the file specified by this
     *         object exists and the application can read the file;
     *         <code>false</code> otherwise.
     */
    public boolean canRead() {
        try {
            return nfs.canRead();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Tests if the file represented by this
     * object is a "normal" nfs.
     * <p>
     * A file is "normal" if it is not a directory and, in
     * addition, satisfies other system-dependent criteria. Any
     * non-directory file created by a Java application is
     * guaranteed to be a normal nfs.
     *
     * @return <code>true</code> if the file specified by this
     *         <code>XFile</code> object exists and is a "normal"
     *         file; <code>false</code> otherwise.
     */
    public boolean isFile() {
        try {
            return nfs.isFile();
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Tests if the file represented by this XFileAccessor
     * object is a directory.
     *
     * @return <code>true</code> if this XFileAccessor object
     *         exists and is a directory; <code>false</code>
     *         otherwise.
     */
    public boolean isDirectory() {
        try {
            return nfs.isDirectory();
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Returns the time that the file represented by this
     * <code>XFile</code> object was last modified.
     * <p>
     * The return value is system dependent and should only be
     * used to compare with other values returned by last modified.
     * It should not be interpreted as an absolute time.
     *
     * @return the time the file specified by this object was last
     *         modified, or <code>0L</code> if the specified file
     *         does not exist.
     */
    public long lastModified() {
        try {
            return nfs.mtime();
        } catch (IOException e) {
            return 0L;
        }
    }


    /**
     * Returns the length of the file represented by this
     * XFileAccessor object.
     *
     * @return the length, in bytes, of the file specified by
     *         this object, or <code>0L</code> if the specified
     *         file does not exist.
     */
    public long length() {
        try {
            return nfs.length();
        } catch (IOException e) {
            return 0L;
        }
    }


    /**
     * Creates a file whose pathname is specified by this
     * XFileAccessor object.
     *
     * @return <code>true</code> if the file could be created;
     *         <code>false</code> otherwise.
     */
    public boolean mkfile() {
        try {
            nfs = getParent(xf).create(xf.getName(), (long)0666);
            return true;

        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Creates a directory whose pathname is specified by this
     * XFileAccessor object.
     *
     * @return <code>true</code> if the directory could be created;
     *         <code>false</code> otherwise.
     */
    public boolean mkdir() {
        try {
            nfs = getParent(xf).mkdir(xf.getName(), (long)0777);
            return true;

        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Renames the file specified by this XFileAccessor object to
     * have the pathname given by the XFileAccessor object argument.
     *
     * @param  dest   the new filename.
     * @return <code>true</code> if the renaming succeeds;
     *         <code>false</code> otherwise.
     */
    public boolean renameTo(XFile dest) {
        try {
            Nfs sParent = getParent(xf);
            Nfs dParent = getParent(dest);

            return sParent.rename(dParent, xf.getName(), dest.getName());
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Returns a list of the files in the directory specified by
     * this XFileAccessor object.
     *
     * @return an array of file names in the specified directory.
     *         This list does not include the current directory or
     *         the parent directory ("<code>.</code>" and
     *         "<code>..</code>" on Unix systems).
     */
    public String[] list() {
        try {
            return nfs.readdir();
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * Deletes the file specified by this object.  If the target
     * file to be deleted is a directory, it must be empty for
     * deletion to succeed.
     *
     * @return <code>true</code> if the file is successfully deleted;
     *         <code>false</code> otherwise.
     */
    public boolean delete() {
        boolean ok;

        try {
            if (isFile())
                ok = getParent(xf).remove(xf.getName());
            else
                ok = getParent(xf).rmdir(xf.getName());

            //  Purge cached attrs & filehandle

            if (ok) {
                nfs.invalidate();
                nfs = null;
            }

            return ok;

        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Reads a subarray as a sequence of bytes.
     *
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @param foff the offset into the file
     * @exception java.io.IOException If an I/O error has occurred.
     */
    public int read(byte b[], int off, int len, long foff)
        throws IOException {

        int c = nfs.read(b, off, len, foff);
        return c;
    }


    /**
     * Writes a sub array as a sequence of bytes.
     *
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @param foff the offset into the file
     * @exception java.io.IOException If an I/O error has occurred.
     */
    public void write(byte b[], int off, int len, long foff)
        throws IOException {

        nfs.write(b, off, len, foff);
    }


    /**
     * Forces any buffered output bytes to be written out.
     * <p>
     *
     * @exception  java.io.IOException  if an I/O error occurs.
     */
    public void flush() throws IOException {
        nfs.flush();
    }


    /**
     * Close the file
     *
     * Since NFS has no concept of file close, we just
     * flush any buffered data.
     *
     * @exception java.io.IOException If an I/O error has occurred.
     */
    public void close() throws IOException {
        nfs.close();
    }


    /**
     * Returns a string representation of this object.
     *
     * @return a string giving the pathname of this object.
     */
    @Override
    public String toString() {
        return nfs.toString();
    }
}
