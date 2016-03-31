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

package com.sun.gssapi.dummy;

import java.security.Provider;

import com.sun.gssapi.Oid;
import com.sun.gssapi.GSSException;


/**
 * Dummy Mechanism plug in for JGSS
 * This is the properties object required by the JGSS framework.
 * All mechanism specific information is defined here.
 */

public final class Dummy extends Provider {

    private static String info = "JGSS Dummy Mechanism Provider";

    public Dummy() {

        super("JGSS Dummy Provider 1", 1.0, info);


        //list mechs supported
        put("JGSS.Mechs", "1.3.6.1.4.1.42.2.26.1.2");

        //configure 1.3.6.1.4.1.42.2.26.1.2
        put("JGSS.Mech.1.3.6.1.4.1.42.2.26.1.2.Alias", "dummy");
        put("JGSS.Mech.dummy._K872D1AC", "1.3.6.1.5.6.2:1.2.840.113554.1.2.1.1");
        put("JGSS.Mech.dummy._K2102CC5", "com.sun.gssapi.dummy.DummyCred");
        put("JGSS.Mech.dummy._K1000A49", "com.sun.gssapi.dummy.DummyCtxt");
        put("JGSS.Mech.dummy._K532D1BD", "com.sun.gssapi.dummy.DummyName");


    }


    /**
     * Package private method to return the oid of this mech.
     */
    static Oid getMyOid() {

        return (M_myOid);
    }


    /**
     * Package private method to return the number of tokens
     * to be used in the context creation exchange.
     */
    static int getNumOfTokExchanges() {

        return (M_tokNum);
    }


    //private variables
    private static Oid M_myOid;
    private static final int M_tokNum = 2;


    static {
        try {
                        M_myOid = new Oid("1.3.6.1.4.1.42.2.26.1.2");
        } catch (GSSException e) {
                        throw new NumberFormatException();
        }
    }
}
