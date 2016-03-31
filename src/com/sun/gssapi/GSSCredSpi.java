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
 * This interface is implemented by each mechanism to provide the
 * functionality of a credential. Each GSSCredential uses provider
 * objects implementing this interface. A GSSCredential may have
 * several credential elements underneath it, but each GSSCredSpi
 * object can represent at most 1 credential element.
 */
public interface GSSCredSpi {

    /**
     * Initialized the credential object. Called after the
     * object is first instantiated.
     *
     * @param desiredName - desired name of the principal
     * @param initLifetime - desired lifetime for the init
     *  credential; 0 signals use mechanism default
     * @param acceptLifetime - desired lifetime for the accept
     *  credential; 0 signals use mechanism default
     * @param usage - the desired usage for this credential
     * @exception - GSSException may be thrown
     */
    public void init(GSSNameSpi desiredName, int initLifetime,
        int acceptLifetime, int usage) throws GSSException;


    /**
     * Called to invalidate this credential element and release
     * any system recourses and cryptographic information owned
     * by the credential.
     *
     * @exception GSSException with major codes NO_CRED and FAILURE
     */
    public void dispose() throws GSSException;


    /**
     * Returns the principal name for this credential. The name
     * is in mechanism specific format.
     *
     * @return GSSNameSpi representing principal name of this credential
     * @exception GSSException may be thrown
     */
    public GSSNameSpi getName() throws GSSException;


    /**
     * Returns the init lifetime remaining.
     *
     * @return the init lifetime remaining in seconds
     * @exception GSSException may be thrown
     */
    public int getInitLifetime() throws GSSException;


    /**
     * Returns the accept lifetime remaining.
     *
     * @return the accept lifetime remaining in seconds
     * @exception GSSException may be thrown
     */
    public int getAcceptLifetime() throws GSSException;


    /**
     * Returns the lifetime remaining. This should take
     * into account the credential usage, and return the
     * appropriate lifetime. See RFC 2078 for details.
     *
     * @return the lifetime remaining in seconds
     * @exception GSSException may be thrown
     */
    public int getLifetime() throws GSSException;


    /**
     * Returns the credential usage. This must be one
     * GSSCredential.ACCEPT_ONLY, GSSCredential.INITIATE_ONLY,
     * or GSSCredential.INITIATE_AND_ACCEPT.
     *
     * @return the credential usage
     * @exception GSSException may be thrown
     */
    public int getUsage() throws GSSException;


    /**
     * Returns the oid representing the underlying credential
     * mechanism oid.
     *
     * @return the Oid for this credential mechanism
     * @exception GSSException may be thrown
     */
    public Oid getMechanism();
}
