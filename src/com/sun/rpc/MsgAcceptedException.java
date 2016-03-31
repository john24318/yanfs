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
 * Handle the RPC "message accepted" class of errors.
 *
 * Note that some of the errors also convey low and high
 * version information.
 *
 * @see RpcException
 * @author Brent Callaghan
 */
public class MsgAcceptedException extends RpcException {
    int lo, hi;

    public static final int  PROG_UNAVAIL   = 1;
    public static final int  PROG_MISMATCH  = 2;
    public static final int  PROC_UNAVAIL   = 3;
    public static final int  GARBAGE_ARGS   = 4;
    public static final int  SYSTEM_ERR     = 5;

    /*
     * Construct a new Exception for the specified RPC accepted error
     * @param error     The RPC error number
     */
    public MsgAcceptedException(int error) {
        super(error);
    }

    /*
     * Construct a new RPC error with the given low and high parameters
     * @param error     The RPC error number
     * @param lo        The low version number
     * @param hi        The high version number
     */
    public MsgAcceptedException(int error, int lo, int hi) {
        super(error);
        this.lo = lo;
        this.hi = hi;
    }

    @Override
    public String toString() {
        switch (error) {

        case PROG_UNAVAIL:
            return "Program unavailable";

        case PROG_MISMATCH:
            return "Program number mismatch: " +
                    "low=" + lo + ",high=" + hi;

        case PROC_UNAVAIL:
            return "Procedure Unavailable: " +
                    "low=" + lo + ",high=" + hi;

        case GARBAGE_ARGS:
            return "Garbage Arguments";

        case SYSTEM_ERR:
            return "System error";

        default:
            return "Unknown RPC Error = " + error;
        }
    }
}
