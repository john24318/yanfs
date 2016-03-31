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

package com.sun.gssapi;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

/**
 * This is a package private class used to decode/encode ASN.1 DER
 * oids.
 */
class DERParser {

    /**
     * Returns the DER encoded length from the InputStream.
     */
    static int readLength(InputStream is) throws GSSException {

    int length, tmp;

    //get the length of Oid - check if short form
    try {
        if (((tmp = is.read()) & 0x080) == 0)
        length = tmp;
        else {
        //must be long form
        tmp &= 0x7f;
        for (length = 0; tmp > 0; tmp--) {
            length <<= 8;
            length += (0xff & is.read());
        }
        }
    } catch (IOException e) {
        throw new GSSException(GSSException.DEFECTIVE_TOKEN);
    }

    return (length);
    }


    /**
     * Decodes a DER encoding of an Oid object into vector components.
     */
    static Vector decodeOid(InputStream is) throws GSSException {

    //check the tag first
    try {
        if (is.read() != 0x06)
        throw new GSSException(GSSException.DEFECTIVE_TOKEN);
    } catch (IOException e) {
        throw new GSSException(GSSException.DEFECTIVE_TOKEN);
    }

    return (decodeOidOctets(is, readLength(is)));
    }


    /**
     * Decodes the specified number of oid octets.
     * Returns a vector of integer components.
     */

    static Vector decodeOidOctets(InputStream is, int numOfOctets)
                throws GSSException {

    Vector v = new Vector(9, 3);

    //first octet is combination of first two numbers
    try {
        int comp, tmp = is.read();
        if (tmp < 40)
        comp = 0;
        else if (tmp < 80)
        comp = 1;
        else
        comp = 2;

        v.addElement(new Integer(comp));
        v.addElement(new Integer(tmp - (40 * comp)));

        //get the rest of the octets
        for (int i = 1; i < numOfOctets; i++) {
        comp = 0;

        //assume that at most 4 octets make up each component
        for (int j=0; j < 4; j++) {
            comp <<= 7;
            tmp = is.read();
            comp |= (tmp & 0x7f);
            if ((tmp & 0x80) == 0)
            break;
            i++;
        }
        v.addElement(new Integer(comp));
        }

    } catch (IOException e) {
        throw new GSSException(GSSException.DEFECTIVE_TOKEN);
    }

    return (v);
    }

    /**
     * Encodes DER length.
     */
    static void writeLength(OutputStream os, int len) throws IOException {

    //encode the length - for all practical purposes, the length
    //should always be less then 0x80 (128)
    if (len < 0x80)
        os.write(len);
    else if (len < 0x100) {
        os.write(0x081);
        os.write(len);
    } else if (len < 0x80000) {
        os.write(0x082);
        os.write(len >> 8);
        os.write(len & 0xff);
    } else if (len < 0x1000000) {
        os.write(0x083);
        os.write(len >> 16);
        os.write((len >> 8) & 0xff);
        os.write(len & 0xff);
    } else {
        os.write(0x084);
        os.write(len >>> 24);
        os.write((len >> 16) & 0xff);
        os.write((len >> 8) & 0xff);
        os.write(len & 0xff);
    }
    }


    /**
     * Produces ASN.1 DER encoding for the object.
     * @return byte[] DER encoding for the object
     */
    static byte[] encodeOid(Vector v) throws GSSException {

    //use byte array output stream - 32 initial bytes should be enough
    ByteArrayOutputStream o = new ByteArrayOutputStream();

    int length = 1;

    try {
        //start with Oid tag
        o.write(0x06);

        //figure our the length - must have at least 2 elements X.208
        if (v.size() < 2)
        throw new IllegalArgumentException();

        for (int i = 2; i < v.size(); i++) {
        int compLen = 0;
        int nextComp = ((Integer)v.elementAt(i)).intValue();

        //count # of 7 bit octets this will occupy
        for (compLen = 0; nextComp > 0; nextComp >>= 7, compLen++)
            ;//nothing to do

        //must have at least 1 octet
        if (compLen == 0)
            length += 1;
        else
            length += compLen;
        }

        writeLength(o, length);

        //now write the components
        writeOidOctets(o, v);
    } catch (IOException e) {

        throw new GSSException(GSSException.DEFECTIVE_TOKEN);
    }
    return (o.toByteArray());
    }


    /**
     * Encodes the oid octets onto the stream.
     */
    static void writeOidOctets(OutputStream o, Vector v) throws IOException {

    //first 2 components occupy 1 octet
    o.write(((Integer)v.elementAt(0)).intValue() * 40 +
        ((Integer)v.elementAt(1)).intValue());

    for (int i = 2; i < v.size(); i++) {

        int tmp, nextComp = ((Integer)v.elementAt(i)).intValue();

        //each component may be at most 4 octets long
        for (int c = 0; c < 4; c++) {
        tmp = (nextComp & 0x7f);
        nextComp >>>= 7;

        //every octet except for last has bit 8 on
        if (nextComp > 0)
            o.write(tmp | 0x80);
        else {
            o.write(tmp);
            break;
        }
        }
    }
    }
}
