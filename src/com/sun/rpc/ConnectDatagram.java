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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Sets up a UDP connection to the server.
 * Since UDP is really connectionless, we
 * don't really have a connection, so perhaps
 * describing this as an <i>association</i>
 * is more accurate.
 *
 * This class lets us transmit and receive buffers
 * of data to a port on a remote server.
 *
 * @see Connection
 * @author Brent Callaghan
 */
public class ConnectDatagram extends Connection {

    DatagramSocket ds;
    DatagramPacket dp;
    InetAddress addr;

    /**
     * Construct a new connection to a specified server and port.
     * @param server    The hostname of the server
     * @param port      The port number on the server
     * @param maxSize   The maximum size in bytes of the received message
     * @exception IOException if the server does not exist
     */
    public ConnectDatagram (String server, int port, int maxSize)
        throws IOException {

        super(server, port, "udp", maxSize);

        ds = new DatagramSocket();
        addr = InetAddress.getByName(server);
        start();
    }

    @Override
    void sendOne(Xdr x) throws IOException {

        /*
         * The interrupt call here is to break the listener
         * thread from its socket read.  For some unknown
         * reason a datagram socket read blocks threads
         * attempting to send.  The interrupt unblocks the
         * listener briefly so we can do the send.
         *
         * The blocking problem appears to be fixed as
         * of JDK 1.1.6, so the interrupt is skipped removed.
         */
    //interrupt();

        ds.send(new DatagramPacket(x.xdr_buf(), x.xdr_offset(), addr, port));
    }

    @Override
    void receiveOne(Xdr x, int timeout) throws IOException {
        ds.setSoTimeout(timeout);
        dp = new DatagramPacket(x.xdr_buf(), x.xdr_buf().length);
        ds.receive(dp);
    }

    @Override
    InetAddress getPeer() {
        return dp.getAddress();
    }

    /*
     * No connection to drop.
     */
    @Override
    void dropConnection() {
    }

    /*
     * No connection to check
     */
    @Override
    void checkConnection() {
    }

    @Override
    protected void finalize() throws Throwable {
        if (ds != null) {
            ds.close();
            ds = null;
        }
        super.finalize();
    }
}
