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

package com.sun.xfile;

import java.io.*;

/**
 * An XFile output stream is an output stream for writing data to an
 * <code>XFile</code>.
 */
public class XFileOutputStream extends OutputStream {

    private long fp;    /* File Pointer */

    /*
     * File Accessor that implements the underlying filesystem
     */
    private XFileAccessor xfa;


    /**
     * Creates an XFile output stream to write to the specified
     * <code>XFile</code> object.
     *
     * @param      file the XFile to be opened for writing.
     * @exception  java.io.IOException if the XFile could not
     *             be opened for writing.
     */
    public XFileOutputStream(XFile xfile) throws IOException {

        xfa = xfile.newAccessor();

        if (xfa.open(xfile, true, false)) { // serial, not readonly
            if (!xfa.isFile())
                throw new IOException("not a file");

            if (!xfa.canWrite())
                throw new IOException("no write permission");
        }

        if (!xfa.mkfile())
            throw new IOException("no write permission");
    }

    /**
     * Creates an output XFile stream to write to the file with the
     * specified name.
     *
     * @param      name   the system-dependent filename.
     * @exception  java.io.IOException if the file could
     *             not be opened for writing.
     */
    public XFileOutputStream(String name) throws IOException {
        this(new XFile(name));
    }


    /**
     * Creates an output file for the specified XFile object.
     *
     * @param xfile the XFile to be opened for writing.
     * @param append true if writes begin at the end of the file
     * @exception java.io.IOException If the file is not found.
     */
    public XFileOutputStream(XFile xfile, boolean append)
        throws IOException {

    boolean isExist;

        xfa = xfile.newAccessor();

        if ((isExist = xfa.open(xfile, true, false))) { // serial, not readonly
            if (!xfa.isFile())
                throw new IOException("not a file");

            if (!xfa.canWrite())
                throw new IOException("no write permission");
        }

        /*
         * If file doesn't exist or append is False create the file
         */
        if (!isExist || !append) {
            if (!xfa.mkfile())
                throw new IOException("no write permission");
        }

        if (append)
            fp = xfa.length();
    }


    /**
     * Creates an output file with the specified name or URL.
     *
     * @param name the native name or URL
     * @param append true if writes begin at the end of the file
     * @exception java.io.IOException If the file is not found.
     */
    public XFileOutputStream(String name, boolean append)
        throws IOException {

        this(new XFile(name), append);

    }


    /*
     * All writes to the Accessor go through here.
     */
    synchronized private void XFAwrite(byte b[], int off, int len)
        throws IOException {

        if (b == null)
            throw new NullPointerException();

        if (len == 0)
            return;

    if (off < 0 || len < 0 || off >= b.length || (off + len) > b.length)
            throw new IllegalArgumentException("Invalid argument");

        xfa.write(b, off, len, fp);
        fp += len;
    }


    /**
     * Writes the specified byte to this file output stream.
     *
     * @param      b   the byte to be written.
     * @exception  java.io.IOException  if an I/O error occurs.
     */
    @Override
    public void write(int b) throws IOException {
        XFAwrite(new byte[] {(byte)b}, 0, 1);
    }


    /**
     * Writes <code>b.length</code> bytes from the specified byte array
     * to this file output stream.
     *
     * @param      b   the data.
     * @exception  java.io.IOException  if an I/O error occurs.
     */
    @Override
    public void write(byte b[]) throws IOException {
    XFAwrite(b, 0, b.length);
    }


    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this XFile output stream.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  java.io.IOException  if an I/O error occurs.
     */
    @Override
    public void write(byte b[], int off, int len) throws IOException {
    XFAwrite(b, off, len);
    }


    /**
     * Flushes this output stream and forces any buffered output bytes
     * to be written out.
     * <p>
     *
     * @exception  java.io.IOException  if an I/O error occurs.
     */
    @Override
    public void flush() throws IOException {
        xfa.flush();
    }


    /**
     * Closes this file output stream, flushes any buffered,
     * unwritten data, and releases any system resources
     * associated with this stream.
     *
     * After the file is closed further I/O operations may
     * throw IOException.
     *
     * @exception  java.io.IOException  if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        xfa.close();
    }


    /**
     * Ensures that the <code>close</code> method of this XFile
     * output stream is called when there are no more references
     * to this stream.
     *
     * @exception  java.io.IOException  if an I/O error occurs.
     * @see        com.sun.xfile.XFileInputStream#close()
     */
    @Override
    protected void finalize() throws IOException {
     close();
    }
}
