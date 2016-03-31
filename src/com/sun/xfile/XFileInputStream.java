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

package com.sun.xfile;

import java.io.*;

/**
 * An XFile input stream is an input stream for reading data from an
 * <code>XFile</code>.
 */
public class XFileInputStream extends InputStream {

    private long fp;    /* File Pointer */

    /**
     * File Accessor that implements the underlying filesystem
     */
    private XFileAccessor xfa;


    /**
     * Creates an input file stream to read from the specified
     * <code>XFile</code> object.
     *
     * @param      xfile   the file to be opened for reading.
     * @exception  java.io.FileNotFoundException if the file is
     *             not found.
     */
    public XFileInputStream(XFile xfile) throws IOException {
        xfa = xfile.newAccessor();
        if (! xfa.open(xfile, true, true))      // serial, read-only
            throw new FileNotFoundException("no file");

        if (!xfa.canRead())
            throw new IOException("no read permission");
    }


    /**
     * Creates an input file stream to read from a file with the
     * specified name.
     *
     * @param      name   the system-dependent file name.
     * @exception  java.io.FileNotFoundException if the file is
     *             not found.
     */
    public XFileInputStream(String name) throws IOException {
        this(new XFile(name));
    }


    /*
     * Reads a subarray as a sequence of bytes.
     *
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception java.io.IOException If an I/O error has occurred.
     */
    synchronized private int XFAread(byte b[], int off, int len)
        throws IOException {

        if (b == null)
            throw new NullPointerException();

        if (len == 0)
            return 0;

    if (off < 0 || len < 0 || off >= b.length || (off + len) > b.length)
            throw new IllegalArgumentException("Invalid argument");

        int c = xfa.read(b, off, len, fp);

        if (c <= 0)
            return (-1);

        fp += c;

        return (c);
    }

    /**
     * Reads a byte of data from this XFile.
     *
     * @return the next byte of data, or <code>-1</code>
     *         if the end of the file is reached.
     * @exception  java.io.IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];

        if (XFAread(b, 0, 1) != 1)
            return (-1);

        return b[0] & 0xff;
    }

    /**
     * Reads up to <code>b.length</code> bytes of data from this file
     * into an array of bytes.
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because
     *             the end of the file has been reached.
     * @exception  java.io.IOException if an I/O error occurs.
     */
    @Override
    public int read(byte b[]) throws IOException {
    return XFAread(b, 0, b.length);
    }


    /**
     * Reads up to <code>len</code> bytes of data from this file
     * into an array of bytes.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because
     *             the end of the file has been reached.
     * @exception  java.io.IOException  if an I/O error occurs.
     */
    @Override
    public int read(byte b[], int off, int len) throws IOException {
    return XFAread(b, off, len);
    }


    /**
     * Returns the number of bytes yet to be read from this file.
     *
     * @return the number of bytes yet to be read from this file
     *         without blocking.
     * @exception java.io.IOException  if an I/O error occurs.
     */
    @Override
    public int available() throws IOException {
        return (int)(xfa.length() - fp);
    }


    /**
     * Skips over and discards <code>n</code> bytes of data from the
     * file.
     *
     * The <code>skip</code> method may, for a variety of
     * reasons, end up skipping over some smaller number of bytes,
     * possibly <code>0</code>.
     * The actual number of bytes skipped is returned.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  java.io.IOException  if an I/O error occurs.
     */
    @Override
    public long skip(long n) throws IOException {
        if (n < 0)
            throw new IllegalArgumentException("illegal skip: " + n);

        fp += n;

        return n;
    }


    /**
     * Closes this file input stream and releases any system resources
     * associated with the stream.
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
}
