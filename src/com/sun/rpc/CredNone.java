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

import java.io.*;

/**
 *  This is the "NONE" credential, i.e. no credential
 *  It's the default credential for RPC unless set
 *  to something else.
 */

public class CredNone extends Cred {

    static final int AUTH_NONE = 0;

    /**
     * Put "no" creds into an XDR buffer
     */
    @Override
    void putCred(Xdr x) {

        x.xdr_int(AUTH_NONE);
        x.xdr_int(0);           // no cred data
        x.xdr_int(0);           // no verifier
        x.xdr_int(0);           // no verifier
    }

    /**
     * Get "no" creds from an XDR buffer
     */
    @Override
    void getCred(Xdr x) {

        x.xdr_int();    // assume it's AUTH_NONE
        x.xdr_int();    // cred length == 0
        x.xdr_int();    // no verifier
        x.xdr_int();    // no verifier
    }

    @Override
    void init(Connection conn, int prog, int vers) {
        // No-op
    }


    @Override
    boolean refresh(Connection conn, int prog, int vers) {
        // No-op
    return true;
    }

    @Override
    void wrap(Xdr x, byte[] arg) {
        // No-op
    }

    @Override
    int unwrap(Xdr x) {
        // No-op
    return 0;
    }

    @Override
    void validate(byte[] verifier, int verifiee) {
        // No-op
    }

    @Override
    void destroy(Rpc rpc) {
    // No-op
    }

}
