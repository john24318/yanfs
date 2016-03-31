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

package com.sun.rpc;

/**
 *
 * Handle the RPC "message rejected" class of errors.
 *
 * Note that some of the errors also convey low and high
 * version information or an authentication sub-error.
 *
 * @see RpcException
 * @author Brent Callaghan
 */
public class MsgRejectedException extends RpcException {

    public static final int  RPC_MISMATCH = 0;
    public static final int  AUTH_ERROR   = 1;

    public static final int  AUTH_BADCRED      = 1;
    public static final int  AUTH_REJECTEDCRED = 2;
    public static final int  AUTH_BADVERF      = 3;
    public static final int  AUTH_REJECTEDVERF = 4;
    public static final int  AUTH_TOOWEAK      = 5;
    public static final int  AUTH_INVALIDRESP  = 6;
    public static final int  AUTH_FAILED       = 7;

    public static final int  RPCSEC_GSS_NOCRED = 13;
    public static final int  RPCSEC_GSS_FAILED = 14;

    /*
     * Construct a new Exception for the specified RPC accepted error
     * @param error     The RPC error number
     */
    public MsgRejectedException(int error) {
        super(error);
    }

    /*
     * Construct a new RPC error with the given auth sub-error
     * @param error     The RPC error number
     * @param why       The auth sub-error
     */
    public MsgRejectedException(int error, int why) {
        super(error);
        this.why = why;
    }

    /*
     * Construct a new RPC error with the given low and high parameters
     * @param error     The RPC error number
     * @param lo        The low version number
     * @param hi        The high version number
     */
    public MsgRejectedException(int error, int lo, int hi) {
        super(error);
        this.lo = lo;
        this.hi = hi;
    }

    @Override
    public String toString() {
        switch (error) {

        case RPC_MISMATCH:
            return "Version mismatch: " +
                    "low=" + lo + ",high=" + hi;

        case AUTH_ERROR:
            String msg = "Authentication error: ";
        switch (why) {
            case AUTH_BADCRED:
                msg += "bogus credentials (seal broken)";
                break;
            case AUTH_REJECTEDCRED:
                msg += "client should begin new session";
                break;
            case AUTH_BADVERF:
                msg += "bogus verifier (seal broken)";
                break;
            case AUTH_REJECTEDVERF:
                msg += "verifier expired or was replayed";
                break;
            case AUTH_TOOWEAK:
                msg += "too weak";
                break;
            case AUTH_INVALIDRESP:
                msg += "bogus response verifier";
                break;
            case RPCSEC_GSS_NOCRED:
                msg += "no credentials for user";
                break;
            case RPCSEC_GSS_FAILED:
                msg += "GSS failure, credentials deleted";
                break;
            case AUTH_FAILED:
            default:
                msg += "unknown reason";
                break;
            }
            return msg;

        default:
            return "Unknown RPC Error = " + error;
        }
    }
}
