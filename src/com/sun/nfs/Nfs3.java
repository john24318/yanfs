/*
 * Copyright (c) 1997, 2007, Oracle and/or its affiliates. All rights reserved.
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

/**
 * This class contains the methods specific to
 * NFS version 3.
 *
 * @see Nfs
 * @see Nfs2
 * @see Fattr
 * @author Brent Callaghan
 * @author Ricardo Labiaga
 */
class Nfs3 extends Nfs {

    Fattr3 attr;

    int accessBits = -1;        // Cache access bits
    long accessTime;            // Time when accessBits was cached

    /*
     * NFS version 3 procedure numbers
     */
    private final static int NFSPROC3_NULL        = 0;
    private final static int NFSPROC3_GETATTR     = 1;
    private final static int NFSPROC3_SETATTR     = 2;
    private final static int NFSPROC3_LOOKUP      = 3;
    private final static int NFSPROC3_ACCESS      = 4;
    private final static int NFSPROC3_READLINK    = 5;
    private final static int NFSPROC3_READ        = 6;
    private final static int NFSPROC3_WRITE       = 7;
    private final static int NFSPROC3_CREATE      = 8;
    private final static int NFSPROC3_MKDIR       = 9;
    private final static int NFSPROC3_SYMLINK     = 10;
    private final static int NFSPROC3_MKNOD       = 11;
    private final static int NFSPROC3_REMOVE      = 12;
    private final static int NFSPROC3_RMDIR       = 13;
    private final static int NFSPROC3_RENAME      = 14;
    private final static int NFSPROC3_LINK        = 15;
    private final static int NFSPROC3_READDIR     = 16;
    private final static int NFSPROC3_READDIRPLUS = 17;
    private final static int NFSPROC3_FSSTAT      = 18;
    private final static int NFSPROC3_FSINFO      = 19;
    private final static int NFSPROC3_PATHCONF    = 20;
    private final static int NFSPROC3_COMMIT      = 21;

    private final static int NFS_OK = 0;
    private final static int NFS3ERR_NOTSUPP = 10004;

    private final static int RWSIZE = 32768;

    private final static int DIRCOUNT = 1024;
    private final static int MAXBSIZE = 8192;

    /*
     * Used to set time in create and mkdir
     */
    private final static int DONT_CHANGE        = 0;
    private final static int SERVER_TIME = 1;
    private final static int CLIENT_TIME = 2;

    /*
     * ACCESS bits
     */
    private final static int ACCESS3_READ      = 0x0001;
    private final static int ACCESS3_LOOKUP    = 0x0002;
    private final static int ACCESS3_MODIFY    = 0x0004;
    private final static int ACCESS3_EXTEND    = 0x0008;
    private final static int ACCESS3_DELETE    = 0x0010;
    private final static int ACCESS3_EXECUTE   = 0x0020;

    /*
     * Types of create or mkdir
     */
    private final static int UNCHECKED = 0;
    private final static int GUARDED   = 1;
    private final static int EXCLUSIVE = 2;

    /*
     * Types of write
     */
    private final static int UNSTABLE = 0;
    private final static int DATA_SYNC = 1;
    private final static int FILE_SYNC = 2;

    int nra;    // current reads-ahead
    int nwb;    // current writes-behind

    int prevWriteIndex = -1;

    Nfs3(Rpc rpc, byte[] fh, String name, Fattr3 attr) {
        this.rpc = rpc;
        this.fh = fh;
        if (name.startsWith("./"))      // normalize for cache lookup
            name = name.substring(2);
        this.name = name;
        this.attr = attr == null ? new Fattr3() : attr;
    this.rsize = RWSIZE;
        NRA = 1; // Max reads-ahead
        NWB = 4; // Max writes-behind
        NWC = 10; // Max writes committed
    }

    @Override
    public void getattr() throws IOException {
    Xdr reply;

        Xdr call = new Xdr(rsize + 512);

        rpc.rpc_header(call, NFSPROC3_GETATTR);
        call.xdr_bytes(fh);

        try {
            reply = rpc.rpc_call(call, 2 * 1000, 2);
        } catch (IOException e) {
            // don't let a mere getattr hang
            // the app if the server is down.
            return;
        }

        int status = reply.xdr_int();
        if (status != NFS_OK)
            throw new NfsException(status);

        attr.getFattr(reply);
    }

    @Override
    public void checkAttr() throws IOException {

        if (! attr.valid())
            getattr();
    }

    @Override
    public boolean cacheOK(long t) throws IOException {
        checkAttr();

        return t == attr.mtime;
    }

    @Override
    public void invalidate() {
        attr.validtime = 0;
    }

    /**
     * Return the mode.
     * @return the mode
     */
    @Override
    public int mode() throws IOException {
        checkAttr();
        return (int) attr.mode & 0xFFFFFFFF;
    }

    /**
     * Set the mode.
     * @param mode new mode bits
     */
    @Override
    public void mode(int mode) throws IOException {
        Xdr call = new Xdr(rsize + 512);

        rpc.rpc_header(call, NFSPROC3_SETATTR);
        call.xdr_bytes(fh);

        // sattr3
        call.xdr_bool(true);            // mode3
        call.xdr_u_int(mode);
        call.xdr_bool(false);            // uid3
        call.xdr_bool(false);            // gid3
        call.xdr_bool(false);             // size3
        call.xdr_int(DONT_CHANGE);      // atime
        call.xdr_int(DONT_CHANGE);      // mtime

        // sattrguard3
        call.xdr_bool(false);

        Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

        int status = reply.xdr_int();
        wcc_data(reply);
        if (status != NFS_OK) {
            throw new NfsException(status);
        }
    }

    /*
     * Get the file modification time
     * @return the time in milliseconds
     */
    @Override
    public long mtime() throws IOException {
        checkAttr();

        return attr.mtime;
    }

    /**
     * Set the file modification time.
     * @param time the time in milliseconds
     */
    @Override
    public void mtime(long time) throws IOException {
        Xdr call = new Xdr(rsize + 512);

        rpc.rpc_header(call, NFSPROC3_SETATTR);
        call.xdr_bytes(fh);

        // sattr3
        call.xdr_bool(false);            // mode3
        call.xdr_bool(false);            // uid3
        call.xdr_bool(false);            // gid3
        call.xdr_bool(false);             // size3
        call.xdr_int(DONT_CHANGE);      // atime
        call.xdr_int(CLIENT_TIME);      // mtime
        call.xdr_hyper(time);

        // sattrguard3
        call.xdr_bool(false);

        Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

        int status = reply.xdr_int();
        wcc_data(reply);
        if (status != NFS_OK) {
            throw new NfsException(status);
        }
    }

    /*
     * Get the file size in bytes.
     *
     * Note that the size may be greater than that
     * shown in the attributes if the file is being written.
     *
     * @return file size
     */
    @Override
    public long length() throws IOException {
        checkAttr();

        return maxLength > attr.size ? maxLength : attr.size;
    }

    /**
     * Set  the file size in bytes
     * @param size new size in bytes
     */
    @Override
    public void length(long size)  throws IOException {
        Xdr call = new Xdr(rsize + 512);

        rpc.rpc_header(call, NFSPROC3_SETATTR);
        call.xdr_bytes(fh);

        // sattr3
        call.xdr_bool(false);            // mode3
        call.xdr_bool(false);            // uid3
        call.xdr_bool(false);            // gid3
        call.xdr_bool(true);             // size3
        call.xdr_hyper(size);
        call.xdr_int(DONT_CHANGE);      // atime
        call.xdr_int(DONT_CHANGE);      // mtime

        // sattrguard3
        call.xdr_bool(false);

        Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

        int status = reply.xdr_int();
        wcc_data(reply);
        if (status != NFS_OK) {
            throw new NfsException(status);
        }
    }

    /**
     * Process the wcc_data for an operation
     * @param reply
     */
    private void wcc_data(Xdr reply) {
        // wcc_data
         if (reply.xdr_bool()) {     // pre_op_attr
            reply.xdr_hyper();       // size3;
            reply.xdr_u_int();         // mtime
            reply.xdr_u_int();
            reply.xdr_u_int();         // ctime
            reply.xdr_u_int();
        }
        if (reply.xdr_bool()) {      // post_op_attr
            attr.getFattr(reply);
        }
    }

    /*
     * Verify if file exists
     * @return true if file exists
     */
    @Override
    public boolean exists() throws IOException {
        checkAttr();

        return true;
    }

    /*
     * Check access permission to file or directory
     */
    private boolean check_access(int mode) throws IOException {

        int rBits = ACCESS3_READ;
        int wBits = ACCESS3_MODIFY | ACCESS3_EXTEND | ACCESS3_DELETE;
        int xBits = ACCESS3_EXECUTE;

        /*
         * Get access bits from the server if
         * they're not already cached.
         */
        if (accessBits < 0 || !cacheOK(accessTime)) {
            Xdr call = new Xdr(rsize + 512);
            rpc.rpc_header(call, NFSPROC3_ACCESS);
            call.xdr_bytes(fh);
            call.xdr_int(rBits | wBits | xBits);

            Xdr reply = rpc.rpc_call(call, 5 * 1000, 0);

            int status = reply.xdr_int();

            if (reply.xdr_bool())       // post-op attrs
                attr.getFattr(reply);

            if (status != NFS_OK)
                throw new NfsException(status);

            accessBits = reply.xdr_int();
            accessTime = attr.mtime;
        }

        if ((mode & RBIT) != 0)
            return (accessBits & rBits) != 0;

        if ((mode & WBIT) != 0)
            return (accessBits & wBits) != 0;

        if ((mode & XBIT) != 0)
            return (accessBits & xBits) != 0;

        return true;
    }

    /*
     * Verify if file can be created/updated
     * @return true if file can be created/updated
     */
    @Override
    public boolean canWrite() throws IOException {

        return check_access(WBIT);
    }

    /*
     * Verify if file can be read
     * @return true if file can be read
     */
    @Override
    public boolean canRead() throws IOException {

        return check_access(RBIT);
    }

    /*
     * Verify if file can be executed
     * @return true if file can be executed
     */
    @Override
    public boolean canExecute() throws IOException {

        return check_access(XBIT);
    }

    /*
     * Verify if this is a file (not a directory)
     * @return true if a file
     */
    @Override
    public boolean isFile() throws IOException {
        checkAttr();

        return attr.ftype == NFREG;
    }

    /*
     * Verify if this is a directory (not a file)
     * @return true if a directory
     */
    @Override
    public boolean isDirectory() throws IOException {
        checkAttr();

        return attr.ftype == NFDIR;
    }

    /*
     * Verify if this is a symbolic link
     * @return true if a symbolic link
     */
    @Override
    public boolean isSymlink() throws IOException {
        checkAttr();

        return attr.ftype == NFLNK;
    }

    /*
     * @return file attributes
     */
    @Override
    public Fattr getAttr() throws IOException {
        checkAttr();

        return (Fattr)attr;
    }

    /*
     * Lookup a name in a directory
     *
     * If its a symbolic link - follow it
     *
     * @param name      Name of entry in directory
     * @returns         Nfs object
     * @exception java.io.IOException
     */
    @Override
    public Nfs lookup(String name)
        throws IOException {
        byte[] newFh;
        Fattr3 newattrs = null;
        Nfs nfs;
        String pathname;

        /* For multi-component lookup, the name would already be
         * filled in when object is created and
         * thus name passed in will be null.
         */
        if (name == null) {
        pathname = this.name;
        name = this.name;
        } else { /* Single component case  */
            if (this.name == null)
                pathname = name;
            else
                pathname = this.name + "/" + name;
        }

        /*
         * First check the cache to see
         * if we already have this file/dir
         */
        nfs = cache_get(rpc.conn.server, pathname);
        if (nfs != null && nfs.cacheOK(cacheTime)) {

        // If a symbolic link then follow it

            if (((Nfs3)nfs).attr.ftype == NFLNK)
                nfs = NfsConnect.followLink(nfs);

            return nfs;
        }

        Xdr call = new Xdr(rsize + 512);
    Xdr reply = null;

    /*
     * If needed, give one try to get the security information
     * from the server.
     */
    for (int sec_tries = 1; sec_tries >= 0; sec_tries--) {
        rpc.rpc_header(call, NFSPROC3_LOOKUP);
        call.xdr_bytes(fh);
        call.xdr_string(name);

        try {
        reply = rpc.rpc_call(call, 5 * 1000, 0);
        break;
            } catch (MsgRejectedException e) {
                if (fh.length == 0 &&
            e.why == MsgRejectedException.AUTH_TOOWEAK) {
                    String secKey = lookupSec();
                    if (secKey != null &&
                NfsSecurity.getMech(secKey) != null) {
                        rpc.setCred(new CredGss("nfs",
                                NfsSecurity.getMech(secKey),
                                NfsSecurity.getService(secKey),
                                NfsSecurity.getQop(secKey)));
                        continue;
                    } else if (secKey != null && secKey.equals("1")) {
            rpc.setCred(new CredUnix());
            continue;
            }
                }
                throw e;
            } catch (IOException e) {
                throw e;
            }
    } // for

        int status = reply.xdr_int();
        if (status != NFS_OK) {
            if (reply.xdr_bool())
                attr.getFattr(reply);

            throw new NfsException(status);
        }

        newFh = reply.xdr_bytes();
        if (reply.xdr_bool())
            newattrs = new Fattr3(reply);
        if (reply.xdr_bool())
            attr.getFattr(reply);

        nfs = new Nfs3(rpc, newFh, pathname, newattrs);
        cache_put(nfs);

    // If a symbolic link then follow it

        if (((Nfs3)nfs).attr.ftype == NFLNK)
            nfs = NfsConnect.followLink(nfs);

        return nfs;
    }

    /*
     *  lookupSec() uses the WebNFS security negotiation protocol to
     *  to get nfs security flavor numbers from the server.
     *
     *  Null string is returned if the server fails to return any
     *  security flavor numbers.
     *
     *  If the server successfully returns a list of security modes,
     *  the client will use the preferred security mode that matches
     *  any security number found in the list, otherwise, it
     *  will use the first security number from the list that the
     *  client supports.
     *
     *  Null string is returned if the client does not support any
     *  security numbers that the server requests.
     *
     *  Here is an example of the WebNFS security negotiation protocol:
     *
     *      Suppose the server shares /export/home as follows:
     *
     *           share -o sec=sec_1:sec_2:sec_3 /export/secure
     *
     *      Here is how to READ a file from server:/export/secure:
     *
     *         Client                                  Server
     *          ------                                  ------
     *
     *       LOOKUP 0x0, foo, "path"
     *                              ----------->
     *                              <-----------
     *                                                  AUTH_TOOWEAK
     *
     *       LOOKUP 0x0, foo, 0x81, <sec_index>, "path"
     *                              ----------->
     *                              <-----------
     *                                          FH = {...,sec_1, ..., sec_3}
     *       LOOKUP 0x0, foo, "path"
     *          use a selected sec flavor (sec_x)
     *                              ----------->
     *                              <-----------
     *                                          FH = 0x01
     *       READ 0x01, sec_x, offset=0 for 32k
     *                              ----------->
     *                              <-----------
     *
     *  Here is the contents of the FH returned from the server for the
     *  "0x81" V3 LOOKUP request:
     *
     *  1        4
     * +--+--+--+--+
     * |    len    |
     * +--+--+--+--+
     *                                               up to 64
     * +--+--+--+--+--+--+--+--+--+--+--+--+     +--+--+--+--+
     * |s |  |  |  |   sec_1   |   sec_2   | ... |   sec_n   |
     * +--+--+--+--+--+--+--+--+--+--+--+--+     +--+--+--+--+
     *
     * len = 4 * (n+1), where n is the number of security flavors
     * sent in the current overloaded filehandle.
     *
     * the status s indicates whether there are more security
     * mechanisms (1 means yes, 0 means no) that require the client
     * to perform another 0x81 LOOKUP to get them. Client uses
     * <sec_index> to indicate the offset of the flavor number
     * (sec_index + n).
     *
     * Three bytes are padded after s.
     *
     */
    @Override
    public String lookupSec() throws IOException {

        int sec_index = 1;
        boolean more = false;
        String secmode, first_secmode = null;
        Xdr call = new Xdr(rsize + 512);

        do {
            rpc.rpc_header(call, NFSPROC3_LOOKUP);
            call.xdr_bytes(new byte[0]);  // v3 public file handle

            // send "0x81/sec_inext/pathname" over the wire
            int len = name.getBytes().length + 2;
            byte[] b = new byte[len];
            b[0] = (byte) 0x81;
            b[1] = (byte) sec_index;
            System.arraycopy(name.getBytes(), 0, b, 2,
                        name.getBytes().length);
            call.xdr_bytes(b);

            Xdr reply = rpc.rpc_call(call, 5 * 1000, 3);

            int status = reply.xdr_int();

            // If the server does not support the MClookup security
            // negotiation, return null.
            if (status != NFS_OK) {
                return null;
            }

            int numsec = reply.xdr_int()/4 - 1;
            byte[] s = reply.xdr_raw(1);
            if (s[0] == (byte) 0) {
                more = false;
            } else {
                more = true;
                sec_index = sec_index + numsec;
            }

            String prefer = NfsSecurity.getPrefer();
            while (numsec-- > 0) {
                secmode = Integer.toString(reply.xdr_int());

                if ((prefer != null) && prefer.equals(secmode)) {
                    return prefer;
                }

                if ((first_secmode == null) &&
                        NfsSecurity.hasValue(secmode)) {
                    first_secmode = secmode;
                }
            }
        } while (more);

        return first_secmode;
    }

    /*
     * Read a buffer from a file
     */
    @Override
    public  void read_otw(Buffer buf) throws IOException {

        Xdr call = new Xdr(rsize + 512);

        rpc.rpc_header(call, NFSPROC3_READ);
        call.xdr_bytes(fh);
        call.xdr_hyper(buf.foffset);
        call.xdr_int(rsize);

        Xdr reply = rpc.rpc_call(call, 1 * 1000, 0);

        int status = reply.xdr_int();

        if (reply.xdr_bool())           // post-op attrs
            attr.getFattr(reply);

        if (status != NFS_OK)
            throw new NfsException(status);

        int bytesread = reply.xdr_int();
        buf.eof = reply.xdr_bool();
        if (bytesread != reply.xdr_u_int())
            throw new NfsException(NfsException.NFSERR_TOOSMALL);

        buf.buf = reply.xdr_buf();
        buf.bufoff = reply.xdr_offset();
    buf.buflen = bytesread;
        cacheTime = attr.mtime;
    }

    /*
     * Write a buffer
     */
    @Override
    public int write_otw(Buffer buf) throws IOException {

        Xdr call = new Xdr(wsize + 512);

        rpc.rpc_header(call, NFSPROC3_WRITE);
        call.xdr_bytes(fh);
        call.xdr_hyper(buf.foffset + buf.minOffset);
        call.xdr_u_int(buf.maxOffset - buf.minOffset);
        call.xdr_int(buf.syncType);
        call.xdr_bytes(buf.buf, buf.bufoff + buf.minOffset,
            buf.maxOffset - buf.minOffset);

        Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

        int status = reply.xdr_int();

        /*
         * wcc_data
         */
// XXX if pre_op_attr show file changed then
//     should invalidate cached non-dirty blocks.
        if (reply.xdr_bool()) {         // pre_op_attr
            reply.xdr_hyper();          // size3;
            reply.xdr_u_int();          // mtime
            reply.xdr_u_int();
            reply.xdr_u_int();          // ctime
            reply.xdr_u_int();
        }

        if (reply.xdr_bool()) {                 // post_op_attr
            attr.getFattr(reply);
            cacheTime = attr.mtime;
        }

        if (status != NFS_OK)
            throw new NfsException(status);

        int bytesWritten = reply.xdr_int();
    if (reply.xdr_int() == FILE_SYNC)   // stable_how
            buf.status = buf.LOADED;
        else
            buf.status = buf.COMMIT;
        buf.writeVerifier = reply.xdr_hyper();  // writeverf3

        return bytesWritten;
    }

    /*
     * Read a directory including entry filehandles and attributes.
     *
     * Entries are cached as Nfs objects - preempting any need
     * for lookups within the directory - entries need be validated
     * only with getattr.
     *
     * XXX Large directories or a file tree walk
     * XXX could run us out of memory because of
     * XXX the aggressive caching.
     *
     */
    @Override
    public String[] readdir()
        throws IOException {

        long cookie = 0;
        long cookieverf = 0;
    boolean eof = false;
        String[] s = new String[32];
        int i = 0;
        Fattr3 eattr;
        byte[] efh;
        String ename;
        String pathname;

        /*
         * If we already have the directory entries
         * cached then return them.
         */
        if (dircache != null) {
            if (cacheOK(cacheTime))
                return dircache;

            dircache = null;

            return readdir_old();
        }

        Xdr call = new Xdr(rsize + 512);

        while (!eof) {
            rpc.rpc_header(call, NFSPROC3_READDIRPLUS);
            call.xdr_bytes(fh);
            call.xdr_hyper(cookie);
            call.xdr_hyper(cookieverf);
            call.xdr_u_int(DIRCOUNT);   // number of directory bytes
            call.xdr_u_int(MAXBSIZE);   // max number of directory bytes

            Xdr reply = rpc.rpc_call(call, 3 * 1000, 0);

            int status = reply.xdr_int();

            if (reply.xdr_bool())               // post-op dir attrs
                attr.getFattr(reply);

            /*
             * Some implementations don't support readdirplus
             * so fall back to the old readdir if necessary.
             */
            if (status == NFS3ERR_NOTSUPP)
                return readdir_old();

            if (status != NFS_OK)
                throw new NfsException(status);

            cookieverf = reply.xdr_hyper();

            /*
             * Get directory entries
             */
            while (reply.xdr_bool()) {
                reply.xdr_hyper();              // skip fileid
                ename = reply.xdr_string();     // entry filename

                cookie = reply.xdr_hyper();

                eattr = null;
                efh = null;

                if (reply.xdr_bool())           // entry attrs
                    eattr = new Fattr3(reply);

                if (reply.xdr_bool())           // entry filehandle
                    efh = reply.xdr_bytes();

                if (ename.equals(".") || ename.equals(".."))    // ignore entry
                    continue;

                s[i++] = ename;
                if (i >= s.length) {            // last elem in array ?
                    String[] tmp = s;

                    s = new String[i*2];        // double its size
                    System.arraycopy(tmp, 0, s, 0, i);
                }

                /*
                 * If we have both filehandle and attrs
                 * then stash the entry object in the cache
                 */
                if (efh != null && eattr != null) {
                    if (this.name == null)
                        pathname = ename;
                    else
                        pathname = this.name + "/" + ename;

                    cache_put(new Nfs3(rpc, efh, pathname, eattr));
                }
            }
            eof = reply.xdr_bool();     // end of directory
        }

        /*
         * Trim array to exact size
         */
        if (i < s.length) {
            String[] tmp = s;

            s = new String[i];
            System.arraycopy(tmp, 0, s, 0, i);
        }

        dircache = s;
        cacheTime = attr.mtime;

        return s;
    }

    /**
     * Read a directory with just names and fileids
     *
     * @returns byte array of directory entries
     * @exception java.io.IOException
     */
    String[] readdir_old() throws IOException {

        long cookie = 0;
        long cookieverf = 0;
        boolean eof = false;
        String ename;
        String[] s = new String[32];
        int i = 0;

        /*
         * If we already have the directory entries
         * cached then return them.
         */
        if (dircache != null && cacheOK(cacheTime))
            return (dircache);

        Xdr call = new Xdr(rsize + 512);

        while (!eof) {
            rpc.rpc_header(call, NFSPROC3_READDIR);
            call.xdr_bytes(fh);
            call.xdr_hyper(cookie);
            call.xdr_hyper(cookieverf);
            call.xdr_u_int(MAXBSIZE);   // number of directory bytes

            Xdr reply = rpc.rpc_call(call, 3 * 1000, 0);

            int status = reply.xdr_int();

            if (reply.xdr_bool())               // post-op dir attrs
                attr.getFattr(reply);

            if (status != NFS_OK)
                throw new NfsException(status);

            cookieverf = reply.xdr_hyper();

            /*
             * Get directory entries
             */
            while (reply.xdr_bool()) {
                reply.xdr_hyper();              // skip fileid
                ename = reply.xdr_string();     // filename

                if (! ename.equals(".") && ! ename.equals(".."))
                    s[i++] = ename;

                if (i >= s.length) {            // last elem in array ?
                    String[] tmp = s;

                    s = new String[i*2];        // double its size
                    System.arraycopy(tmp, 0, s, 0, i);
                }

                cookie = reply.xdr_hyper();
            }
            eof = reply.xdr_bool();     // end of directory
        }

        if (i == 0)
            return (null);
        /*
         * Trim array to exact size
         */
        if (i < s.length) {
            String[] tmp = s;

            s = new String[i];
            System.arraycopy(tmp, 0, s, 0, i);
        }

        dircache = s;
        cacheTime = attr.mtime;

        return (s);
    }

    /*
     * Read a symbolic link
     *
     */
    @Override
    public String readlink() throws IOException {
        /*
         * If we've already read the symlink
         * then return the cached text.
         */
        if (symlink != null && cacheOK(cacheTime))
            return symlink;

        Xdr call = new Xdr(rsize + 512);

        rpc.rpc_header(call, NFSPROC3_READLINK);
        call.xdr_bytes(fh);

        Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

        int status = reply.xdr_int();
        if (reply.xdr_bool())           // post-op attr
            attr.getFattr(reply);

        if (status != NFS_OK)
            throw new NfsException(status);

        symlink = reply.xdr_string();
        cacheTime = attr.mtime;

        return symlink;
    }

    /*
     * Create a file
     *
     */
    @Override
    public Nfs create(String name, long mode) throws IOException {

    byte[] newFh = null;
    Fattr3 newattrs = null;
    Nfs nfs;

    Xdr call = new Xdr(rsize + 512);

    rpc.rpc_header(call, NFSPROC3_CREATE);
    call.xdr_bytes(fh);
    call.xdr_string(name);
    call.xdr_int(UNCHECKED);

    // sattr3
    call.xdr_bool(true);                // mode3
    call.xdr_u_int(mode);
    call.xdr_bool(true);                // uid3
    call.xdr_u_int(NfsConnect.getCred().getUid());
    call.xdr_bool(true);                // gid3
    call.xdr_u_int(NfsConnect.getCred().getGid());
    call.xdr_bool(true);                // size3
    call.xdr_hyper(0);
    call.xdr_int(SERVER_TIME);  // atime
    call.xdr_int(SERVER_TIME);  // mtime

    Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

    int status = reply.xdr_int();
    if (status != NFS_OK) {

        /*
         * CREATE3resfail
         */
        // wcc_data
        // XXX If this were an exlusive create, then we
        // should check whether the directory was modified,
        // in such case, the file may have been created by another
        // client.

        wcc_data(reply);
        throw new NfsException(status);
    }
    /*
     * CREATE3resok
     */
    if (reply.xdr_bool())               // post_op_fh3
        newFh = reply.xdr_bytes();

    if (reply.xdr_bool())               // post_op_attr
        newattrs = new Fattr3(reply);
    /*
     * wcc_data
     */
    wcc_data(reply);

    if (newFh != null && newattrs != null) {
        String pathname = this.name + "/" + name;
        nfs = new Nfs3(rpc, newFh, pathname, newattrs);
        cache_put(nfs);
    } else
            nfs = null;

    return nfs;
    }

    /*
     * Create a directory
     *
     * @param name      name of directory to create
     * @returns         true if successful, false otherwise
     */
    @Override
    public Nfs mkdir(String name, long mode) throws IOException {

    byte[] newFh = null;
    Fattr3 newattrs = null;
    Nfs nfs = null;

    Xdr call = new Xdr(rsize + 512);

    rpc.rpc_header(call, NFSPROC3_MKDIR);
    call.xdr_bytes(fh);
    call.xdr_string(name);

    // sattr3
    call.xdr_bool(true);                // mode3
    call.xdr_u_int(mode);
    call.xdr_bool(true);                // uid3
    call.xdr_u_int(NfsConnect.getCred().getUid());
    call.xdr_bool(true);                // gid3
    call.xdr_u_int(NfsConnect.getCred().getGid());
    call.xdr_bool(true);                // size3
    call.xdr_hyper(0);
    call.xdr_int(SERVER_TIME);  // atime
    call.xdr_int(SERVER_TIME);  // mtime

    Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

    int status = reply.xdr_int();
    if (status != NFS_OK) {
        /*
         * MKDIR3resfail
         */
        // wcc_data
        if (reply.xdr_bool()) { // pre_op_attr
        reply.xdr_hyper();      // size3;
        reply.xdr_u_int();      // mtime
        reply.xdr_u_int();
        reply.xdr_u_int();      // ctime
        reply.xdr_u_int();
        }
        if (reply.xdr_bool())   // post_op_attr
        attr.getFattr(reply);
        throw new NfsException(status);
    }
    /*
     * MKDIR3resok
     */
    if (reply.xdr_bool())               // post_op_fh3
        newFh = reply.xdr_bytes();
    if (reply.xdr_bool())               // post_op_attr
        newattrs = new Fattr3(reply);
    /*
     * wcc_data
     */
    if (reply.xdr_bool()) {             // pre_op_attr
        reply.xdr_hyper();              // size3;
        reply.xdr_u_int();              // mtime
        reply.xdr_u_int();
        reply.xdr_u_int();              // ctime
        reply.xdr_u_int();
    }
    if (reply.xdr_bool())               // post_op_attr
        attr.getFattr(reply);

    if (newFh != null && newattrs != null) {
        String pathname = this.name + "/" + name;
        nfs = new Nfs3(rpc, newFh, pathname, newattrs);
        cache_put(nfs);
    }
    dircache = null;

    return nfs;
    }

    /*
     * Get Filesystem Information
     *
     */
    @Override
    public void fsinfo() throws IOException {

        Xdr call = new Xdr(rsize + 512);

        rpc.rpc_header(call, NFSPROC3_FSINFO);
        call.xdr_bytes(fh);

        Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

        int status = reply.xdr_int();

        if (reply.xdr_bool())           // post_op_attr
            attr.getFattr(reply);

        if (status != NFS_OK)
            throw new NfsException(status);

        reply.xdr_u_int();              // rtmax
        reply.xdr_u_int();              // rtpref
        reply.xdr_u_int();              // rtmult
        reply.xdr_u_int();              // wtmax:  maximum write size
        wsize = reply.xdr_int();        // wtpref: preferred write size

        /*
         * More attributes follow but we don't
         * Need them so we don't XDR decode them.
         */

         //reply.xdr_hyper();           // maxfilesize
         //reply.xdr_u_int();           // seconds
         //reply.xdr_u_int();           // nseconds
         //reply.xdr_u_int();           // properties
    }

    /*
     * Commit previous async writes to stable storage
     */
    @Override
    public long commit(int foffset, int length) throws IOException {

        Xdr call = new Xdr(wsize + 512);

        rpc.rpc_header(call, NFSPROC3_COMMIT);
        call.xdr_bytes(fh);
        call.xdr_hyper(foffset);
        call.xdr_u_int(length);

        Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

        int status = reply.xdr_int();

        /*
         * wcc_data
         */
// XXX if pre_op_attr show file changed then
//     should invalidate cached non-dirty blocks.
        if (reply.xdr_bool()) {         // pre_op_attr
            reply.xdr_hyper();          // size3;
            reply.xdr_u_int();          // mtime
            reply.xdr_u_int();
            reply.xdr_u_int();          // ctime
            reply.xdr_u_int();
        }

        if (reply.xdr_bool())           // post_op_attr
            attr.getFattr(reply);

        if (status != NFS_OK)
            throw new NfsException(status);

    return reply.xdr_hyper();   // return verifier
    }

    /*
     * Remove file
     *
     * @returns true if the file was removed
     */
    @Override
    public boolean remove(String name) throws IOException {
    return remove_otw(NFSPROC3_REMOVE, name);
    }

    /**
     * Remove directory
     *
     * @returns true if the directory could be deleted
     * @exception java.io.IOException
     */
    @Override
    public boolean rmdir(String name) throws IOException {
    return remove_otw(NFSPROC3_RMDIR, name);
    }

    /*
     * Remove Nfs Object over-the-wire
     * @param NfsOperation over-the-wire operation
     * @param NfsP         Nfs object of Parent directory
     * @param name         Name of file to delete (for Mount protocol, the
     *                     name will not be filled out for this Nfs object)
     * @returns true if the Nfs Object was deleted
     */
    private boolean remove_otw(int NfsOperation, String name) throws IOException {

    Xdr call = new Xdr(rsize + 512);

    rpc.rpc_header(call, NfsOperation);
    call.xdr_bytes(fh);
    call.xdr_string(name);

    Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

    int status = reply.xdr_int();

    /*
     * wcc_data
     */
    // should check whether the directory was modified,
    // in such case, the file may have been deleted by another
    // client.
    if (reply.xdr_bool()) {             // pre_op_attr
        reply.xdr_hyper();              // size3
        reply.xdr_u_int();              // mtime
        reply.xdr_u_int();
        reply.xdr_u_int();              // ctime
        reply.xdr_u_int();
    }
    if (reply.xdr_bool())               // post_op_attr
        attr.getFattr(reply);
    if (status != NFS_OK)
        throw new NfsException(status);

    // Remove Nfs object from cache
    cache_remove(this, name);
    dircache = null;
    return true;
    }

    /*
     * Rename file
     * @param srcP      Nfs obj of parent of src
     * @param dstP      Nfs obj of parent of dst
     * @param sName     src Name.
     * @param dName     destination filename. May be 'path/filename' or simply
     *                  'filename'
     * @returns true if the file/directory was renamed
     */
    @Override
    public boolean rename(Nfs dstP, String sName, String dName) throws IOException{

    Xdr call = new Xdr(rsize + 512);

    rpc.rpc_header(call, NFSPROC3_RENAME);
    call.xdr_bytes(fh);         // Source dir filehandle
    call.xdr_string(sName);                     // Source filename
    call.xdr_bytes(dstP.getFH());               // Dest dir filehandle
    call.xdr_string(dName);                     // Dest filename

    Xdr reply = rpc.rpc_call(call, 2 * 1000, 0);

    int status = reply.xdr_int();
    /*
     * wcc_data - fromdir_wcc
     */
    if (reply.xdr_bool()) {                     // pre_op_attr
        reply.xdr_hyper();                      // size3
        reply.xdr_u_int();                      // mtime
        reply.xdr_u_int();
        reply.xdr_u_int();                      // ctime
        reply.xdr_u_int();
    }
    if (reply.xdr_bool())
        attr.getFattr(reply);

    /*
     * Don't bother getting wcc_data for destDir since we have
     * no method to update its attributes.
     */

    if (status != NFS_OK)
        throw new NfsException(status);

    cache_remove(this, sName);  // Remove Nfs object from cache
    dircache = null;
    dstP.dircache = null;
    return true;
    }
}
