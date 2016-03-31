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

import java.io.*;
import com.sun.rpc.*;
import java.util.Date;

/**
 *
 * NFS version 2 file attributes
 *
 */
class Fattr2 extends Fattr {
    int         ftype;
    long        mode;
    long        nlink;
    long        uid;
    long        gid;
    long        size;
    long        blocksz;
    long        rdev;
    long        blocks;
    long        fsid;
    long        fileid;
    long        atime;
    long        mtime;
    long        ctime;

    Fattr2() {
    }

    Fattr2 (Xdr x) {
        this.getFattr(x);
    }

    @Override
    void putFattr(Xdr x) {
        x.xdr_int(ftype);
        x.xdr_u_int(mode);
        x.xdr_u_int(nlink);
        x.xdr_u_int(uid);
        x.xdr_u_int(gid);
        x.xdr_u_int(size);
        x.xdr_u_int(blocksz);
        x.xdr_u_int(rdev);
        x.xdr_u_int(blocks);
        x.xdr_u_int(fsid);
        x.xdr_u_int(fileid);
        x.xdr_u_int(atime / 1000);      // sec
        x.xdr_u_int(atime % 1000);      // msec
        x.xdr_u_int(mtime / 1000);      // sec
        x.xdr_u_int(mtime % 1000);      // msec
        x.xdr_u_int(ctime / 1000);      // sec
        x.xdr_u_int(ctime % 1000);      // msec
    }

    @Override
    void getFattr(Xdr x) {
        long oldmtime = mtime;

        ftype  = x.xdr_int();
        mode    = x.xdr_u_int();
        nlink   = x.xdr_u_int();
        uid     = x.xdr_u_int(); if (uid == NFS_NOBODY) uid = NOBODY;
        gid     = x.xdr_u_int(); if (gid == NFS_NOBODY) gid = NOBODY;
        size    = x.xdr_u_int();
        blocksz = x.xdr_u_int();
        rdev    = x.xdr_u_int();
        blocks  = x.xdr_u_int();
        fsid    = x.xdr_u_int();
        fileid  = x.xdr_u_int();
        atime   = x.xdr_u_int() * 1000 + x.xdr_u_int();
        mtime   = x.xdr_u_int() * 1000 + x.xdr_u_int();
        ctime   = x.xdr_u_int() * 1000 + x.xdr_u_int();

        /*
         * We want the cache time to be short
         * for files/dirs that change frequently
         * and long for files/dirs that change
         * infrequently. So set the cache time to
         * the delta between file modifications
         * limited by ACMIN and ACMAX
         */
        long delta = mtime - oldmtime;
        if (delta > 0) {
            cachetime = delta;
            if (cachetime < ACMIN)
                cachetime = ACMIN;
            else if (cachetime > ACMAX)
                cachetime = ACMAX;
        }
        validtime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
         return (
             "  ftype = " + ftype + "\n" +
             "   mode = 0" + Long.toOctalString(mode) + "\n" +
             "  nlink = " + nlink + "\n" +
             "    uid = " + uid + "\n" +
             "    gid = " + gid + "\n" +
             "   size = " + size + "\n" +
             "blocksz = " + blocksz + "\n" +
             "   rdev = 0x" + Long.toHexString(rdev) + "\n" +
             " blocks = " + blocks + "\n" +
             "   fsid = " + fsid + "\n" +
             " fileid = " + fileid + "\n" +
             "  atime = " + new Date(atime) + "\n" +
             "  mtime = " + new Date(mtime) + "\n" +
             "  ctime = " + new Date(ctime)
         );
    }
}
