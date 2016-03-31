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

/*
 * Sample rpcgss-client application.
 * This program will inter-operate with the "C" rpcsec_gss server sample
 * test program.
 *
 * This java client sample program is invoked as follows:
 *
 *      % java RPCGSSClient host service -m <mech_number>
 *
 *      The -m indicates the mechanism used.
 *      '-m 1' is for kerberos mechanism
 *      '-m 2' is for dummy mechanism
 *      The dummy is used as the default mechanism.
 */

package com.sun.rpc.samples;

import java.io.*;
import com.sun.rpc.*;

class RPCGSSClient {

    private static final int ADDRLISTPROG = 620756992;
    private static final int ADDRLISTVERS = 1;
    private static final int ADDR_SET = 1;
    private static final int ADDR_GET = 2;
    private static final int ADDR_DEL = 3;
    private static final int SVC_SET = 4;
    private static final int CREATE = 5;
    private static final int DESTROY = 6;
    private static final int LOOP = 8;
    private static final int QUIT = 7;

    private static final String mech1 = "1.2.840.113554.1.2.2"; //krb5
    private static final String mech2 = "1.3.6.1.4.1.42.2.26.1.2"; //dummy

    private static String serverHost, serviceName, mech, svcString;
    private static int op = 0, serviceType = CredGss.SVC_PRIVACY, loop_times;
    private static String name = null, addr = null;


    /**
     * Main method for the RPCGSSClient sample application.
     */
    public static void main(String args[]) {

    Rpc rpc = null;
    CredGss cred = null;
    Xdr callmsg = new Xdr(1024), replymsg;
    boolean status;

    if ((args.length < 2) || (args.length > 4)) {
        usage();
        exit(1);
    }

    //set the required command line args
    serverHost = args[0];
    serviceName = args[1];

    // parse -m option; use dummy mech if -m is not specified
    if (args.length > 2) {
        if (args[2].equals("-m")) {
        if (args[3].equals("1")) {
            mech = mech1;
            print("Kerberos mechanism " + mech);
        } else if (args[3].equals("2")) {
            mech = mech2;
            print("Dummy mechanism " + mech);
        } else {
            usage();
            exit(1);
        }
        } else {
        usage();
        exit(1);
        }
    } else {
        mech = mech2;
        print("Dummy mechanism " + mech);
    }

    try {
        rpc = new Rpc(serverHost, 0, ADDRLISTPROG, ADDRLISTVERS,
                "tcp", 512);
    } catch (IOException e) {
        print("\n***RPC ERROR:\t" + e.toString());
        e.printStackTrace();
        exit(-1);
    }

    while (true) {
        try {
        parseargs();

        switch (op) {
        case CREATE:
            cred = new CredGss(serviceName, mech,
                    serviceType, 0);
            rpc.setCred(cred);
            break;

        case SVC_SET:
            if (svcString.equals("none")) {
                serviceType = CredGss.SVC_NONE;
            } else if (svcString.equals("integrity")) {
                serviceType = CredGss.SVC_INTEGRITY;
            } else if (svcString.equals("privacy")) {
                serviceType = CredGss.SVC_PRIVACY;
            }
            if (cred != null) {
                cred.serviceType = serviceType;
            }
            break;

        case ADDR_SET:
            rpc.rpc_header(callmsg, ADDR_SET);
            callmsg.xdr_string(name);
            callmsg.xdr_string(addr);
            replymsg = rpc.rpc_call(callmsg, 3 * 1000, 3);
            status = replymsg.xdr_bool();
            if (status) {
                print("set ok \n");
            } else {
                print("set failed\n");
            }
            break;

        case ADDR_GET:
            rpc.rpc_header(callmsg, ADDR_GET);
            callmsg.xdr_string(name);
            replymsg = rpc.rpc_call(callmsg, 3 * 1000, 3);
            name = replymsg.xdr_string();
            addr = replymsg.xdr_string();
            if (addr.getBytes().length != 0) {
                print(name + " = " + addr);
            } else {
                print("no value");
            }
            break;

        case ADDR_DEL:
            rpc.rpc_header(callmsg, ADDR_DEL);
            callmsg.xdr_string(name);
            replymsg = rpc.rpc_call(callmsg, 3 * 1000, 3);
            status = replymsg.xdr_bool();
            if (status) {
                print("delete ok");
            } else {
                print("delete failed");
            }
            break;

        case DESTROY:
            if (cred != null) {
                rpc.delCred();
                print("Context destroyed");
                cred = null;
            } else {
                print("No Context to be destroyed");
            }
            break;

        case LOOP:
            if (cred != null) {
                rpc.delCred();
                cred = null;
            }
            int i = 0;
            while (loop_times-- > 0) {
                i++;
                print("\n***LOOP " + i + "***");
                // create-context
                cred = new CredGss(serviceName, mech,
                    serviceType, 0);
                rpc.setCred(cred);

                // destroy-context
                rpc.delCred();
                cred = null;
                print("Context destroyed");
            }
            break;

        case QUIT:
            exit(0);

        } // switch

        } catch (IOException e) {

        print("\n**IO ERRORS**:\t" + e.toString());
        e.printStackTrace();
        }
    } // while (true)
    } // main()

    /**
     * Parse command line input
     */
    private static void parseargs() {

    InputStream in = System.in;
    byte[] argbuf = new byte[128];
    String[] args = new String[4];
    int len, offset, n, i;

    usage1();
    System.out.print("enter cmd -> ");

    try {
        len = in.read(argbuf);
        offset = 0; n = 0; i = 0;

        while (i < (len - 1)) {
            while (Character.isWhitespace((char) argbuf[i])) {
            i++;
            }
            offset = i;
            while ((char) argbuf[i] != '\n' &&
               (char) argbuf[i] != ' ') {
               //!Character.isSpace((char) argbuf[i]))
            i++;
            }
            args[n++] = new String(argbuf, offset, i-offset);
        }
        args[n] = null;

    } catch (IOException e) {
        print(e.toString());
        parseargs();
    }

    if (args[0] == null)
        parseargs();

    if (args[0].equals("set")) {
        op = ADDR_SET;
        name = args[1];
        addr = args[2];
        if (name == null || addr == null) {
            print("syntax error");
            parseargs();
        }

    } else if (args[0].equals("get")) {
        op = ADDR_GET;
        name = args[1];
        if (name == null) {
            print("syntax error");
            parseargs();
        }

    } else if (args[0].equals("del")) {
        op = ADDR_DEL;
        if (args[1] == null) {
            print("syntax error");
            parseargs();
        }
        name = args[1];

    } else if (args[0].equals("service")) {
        op = SVC_SET;
        if (args[1] == null) {
            print("syntax error");
            parseargs();
        }
        svcString = args[1];

    } else if (args[0].equals("create-context")) {
        op = CREATE;

    } else if (args[0].equals("destroy-context")) {
        op = DESTROY;

    } else if (args[0].equals("loop")) {
        op = LOOP;
        if (args[1] != null) {
            loop_times = Integer.parseInt(args[1]);
        } else {
            loop_times = 5;
        }

    } else if (args[0].equals("quit") || args[0].equals("exit")) {
        op = QUIT;

    } else {
        print("syntax error");
        parseargs();
    }
    }


    private static void usage1() {
    print("\ncommands are:");
    print("\tset <name> <value>");
        print("\tget <name>");
        print("\tdel <name>");
        print("\tservice (integrity | privacy | none)");
        print("\tcreate-context");
        print("\tdestroy-context");
        print("\tloop n");
        print("\tquit");
    }

    /**
     * Utility method to display application usage string.
     */
    private static void usage() {
    print("\nUsage: ");
    print("\tjava RPCGSSClient serverHost serviceName -m mech#\n");
    print("\t-m 1 is for kerberos_v5");
    print("\t-m 2 is for dummy");
    print("\tdefault mech is dummy if -m is not specified\n");
    }


    /**
     * Utility method to display information to the screen.
     */
    private static void print(String msg) {

    System.out.println(msg);
    }

/*
    private static void print(int x) {

    System.out.println(x);
    }
*/

    /**
     * Utility method to gracefully shut down the connection and
     * terminate the application.
     */
    private static void exit(int status) {
    System.exit(status);
    }

}
