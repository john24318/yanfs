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
package com.sun.zip;

import java.net.MalformedURLException;

/**
 * @author Brent Callaghan
 */

public class ZipURL {

    private String url;
    private String protocol;
    private String location;
    private String path;

    /*
     * Undocumented testing options
     */
    private int version;
    private String proto;
    private boolean pub = true;

    public ZipURL(String url) throws MalformedURLException {
        int p, q, r;
        int end = url.length();

        url = url.trim();       // remove leading & trailing spaces
        this.url = url;

        p = url.indexOf(':');
        if (p < 0)
            throw new MalformedURLException("colon expected");
        protocol = url.substring(0, p);
        p++;    // skip colon
        if (url.regionMatches(p, "//", 0, 2)) { // have location
            p += 2;
            q = url.indexOf('/', p);
            if (q < 0)
                q = end;
            location = url.substring(0, q);
            r = q;      // no port
            if (p < r)
                location = url.substring(p, r);
        } else {
             q = p;
        }

        if (q < end)
            path = url.substring(q + 1, end);

    }

    public String getProtocol() {
        return (protocol);
    }

    public String getLocation() {
        return (location);
    }

    public String getPath() {
        return (path);
    }

    @Override
    public String toString() {
        String s = getProtocol() + ":";

        if (location != null)
            s += "//" + location;

        if (path != null)
            s += "/" + path;

        return (s);
    }
}
