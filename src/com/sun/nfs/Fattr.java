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

import com.sun.rpc.*;

/**
 *
 * NFS file attributes
 *
 * This is essentially a container class that holds
 * the attributes, but it also has methods to encode
 * and decode the attributes in an Xdr buffer.
 *
 * Note that the time at which the attributes were
 * retrieved is automatically updated and the cache
 * time varies according to the frequency of file
 * modification.
 *
 * There are two subclasses: Fattr2 for NFS v2
 * attributes and Fattr3 for v3.
 *
 * @see Nfs
 * @see Fattr2
 * @see Fattr3
 * @author Brent Callaghan
 */
public abstract class Fattr {

    long validtime;                     // time when attrs were new
    long cachetime;                     // max cache duration in ms
    static final int ACMIN = 3  * 1000; // 3 sec - min cache time
    static final int ACMAX = 60 * 1000; // 1 min - max cache time

    static final int NOBODY = 60001;    // Svr4 UID/GID "nobody"
    static final int NFS_NOBODY = -2;   // NFS  UID/GID "nobody"


    /**
     * Check if the attributes are "fresh" enough.
     *
     * If not, then the caller will likely update
     * the attributes with an NFS getattr request.
     *
     * @returns true if the attributes are valid
     */
    boolean valid() {
        long timenow = System.currentTimeMillis();

        return (timenow <= validtime + cachetime);
    }

    abstract void putFattr(Xdr x);

    abstract void getFattr(Xdr x);
}
