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


/**
 * This class represents a mechanism specific name element. One or
 * more instances of this class are contained in an instance of
 * a GSSName object. Each GSSNameSpi object represents at most 1
 * mechanism specific name element.
 */

public interface GSSNameSpi {

    /**
     * Initializer for the GSSNameSpi object using a byte array.
     *
     * @param byte[] name bytes which is to be interpreted based
     *    on the nameType
     * @param nameType - oid representing the type of name supplied
     * @exception GSSException The major codes can be BAD_NAMETYPE,
     *    BAD_NAME, and FAILURE.
     * @see #init(String,Oid)
     */
    public void init(byte[] externalName, Oid nameType) throws GSSException;


    /**
     * Initializer for the GSSNameSpi object using a String.
     *
     * @param name string which is to be interpreted based
     *    on the nameType
     * @param nameType - oid representing the type of name supplied
     * @exception GSSException The major codes can be BAD_NAMETYPE,
     *    BAD_NAME, and FAILURE.
     * @see #init(String,Oid)
     */
    public void init(String name, Oid nameType) throws GSSException;


    /**
     * Equal method for the GSSNameSpi objects.
     * If either name denotes an anonymous principal, the call should
     * return false.
     *
     * @param name to be compared with
     * @returns true if they both refer to the same entity, else false
     * @exception GSSException with major codes of BAD_NAMETYPE,
     *    BAD_NAME, FAILURE
     */
    public boolean equals(GSSNameSpi name) throws GSSException;


    /**
     * Returns a flat name representation for this object. The name
     * format is defined in RFC 2078.
     *
     * @return the flat name representation for this object
     * @exception GSSException with major codes NAME_NOT_MN, BAD_NAME,
     *    BAD_NAME, FAILURE.
     */
    public byte[] export() throws GSSException;


    /**
     * Get the mechanism type that this NameElement corresponds to.
     *
     * @return the Oid of the mechanism type
     */
    public Oid getMech();


    /**
     * Returns the name type oid for this name.
     *
     * @return the name type oid for this name
     */
    public Oid getNameType();


    /**
     * Returns a string representation for this name. The printed
     * name type can be obtained by calling getStringNameType().
     *
     * @return string form of this name
     * @see #getStringNameType()
     * @overrides Object#toString
     */
    public String toString();


    /**
     * Returns the oid describing the format of the printable name.
     *
     * @return the Oid for the format of the printed name
     */
    public Oid getStringNameType();


    /**
     * Produces a copy of this object.
     */
    public Object clone();


    /**
     * Indicates if this name object represents an Anonymous name.
     */
    public boolean isAnonymousName();
}
