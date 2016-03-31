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

package com.sun.rpc;

/**
 * RPC Credentials
 *
 * Extended by each credential class
 */

public abstract class Cred {

    // service types: authentication, integrity and privacy
    public static final int SVC_NONE = 1;
    public static final int SVC_INTEGRITY = 2;
    public static final int SVC_PRIVACY = 3;

    /**
     * Put creds into an XDR buffer
     */
    abstract void putCred(Xdr x) throws RpcException;

    /**
     * Get creds from an XDR buffer
     */
    abstract void getCred(Xdr x);

    /**
     * Initiate a security context with peers
     */
    abstract void init(Connection conn, int prog, int vers)
        throws RpcException;

    /**
     * Refresh the cred
     */
    abstract boolean refresh(Connection conn, int prog, int vers);

    /**
     * Encrypt an XDR buffer
     */
    abstract void wrap(Xdr x, byte[] arg) throws RpcException;

    /**
     * Descrypt an XDR buffer
     */
    abstract int unwrap(Xdr x) throws RpcException;

    /**
     * Validate the response verifier from server
     */
    abstract void validate(byte[] verifier, int verifiee)
        throws RpcException;

    /**
     * Destroy the cred data and its security context with the server
     */
    abstract void destroy(Rpc rpc) throws RpcException;

}
