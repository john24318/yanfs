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

package com.sun.xfile;

import java.net.MalformedURLException;

/**
 * This is just a dumb URL parser class.
 * I wrote it because I got fed up with the
 * JDK URL class calling NFS URL's "invalid"
 * simply because the Handler wasn't installed.
 *
 * @author Brent Callaghan
 */
class XFurl {

    private String url;
    private String protocol;
    private String location;
    private String path;

    XFurl(String url) throws MalformedURLException {
        int p, q, r;

        url = url.trim();       // remove leading & trailing spaces
        this.url = url;
        int end = url.length();

        p = url.indexOf(':');
        if (p < 0)
            throw new MalformedURLException("colon expected");
        protocol = url.substring(0, p);
        q = p;
        p++;    // skip colon
        if (url.regionMatches(p, "//", 0, 2)) { // have hostname
            p += 2;
            q = url.indexOf('/', p);
            if (q < 0)
                q = end;
            location = url.substring(p, q);
        }

        path = q < end ? url.substring(q + 1, end) : "";

        // Remove trailing slashes from path

        while (path.endsWith("/"))
            path = path.substring(0, path.length()-1);
    }

    XFurl(XFurl base, String rpath) throws MalformedURLException {

        protocol = base.getProtocol();
        location = base.getLocation();
        path = base.getPath();

        rpath = rpath.trim();

        if (rpath.indexOf("://") > 0) { // URL - ignore base
            url = rpath;
            XFurl u = new XFurl(rpath);
            protocol = u.getProtocol();
            location =  u.getLocation();
            path = u.getPath();

        } else if (rpath.startsWith("/")) {     // absolute path
            path = rpath.substring(1);

        } else {

            /*
             * Escape any "%" characters in the name
             * e.g. "%markup" -> "%25markup"
             */
            String npath = "";
            int len = rpath.length();
            int p1 = 0, p2;

            while (true) {
                p2 = rpath.indexOf('%', p1);  // look for %
                if (p2 < 0)
                    p2 = len;

                npath += rpath.substring(p1, p2);
                if (p2 >= len)
                    break;

                npath += "%25"; // replace % with %25
                p1 = p2 + 1;
            }
            rpath = npath;
            len = rpath.length();

            /*
             * Combine base path with relative path
             * according to rules in RFCs 1808 & 2054
             *
             * e.g.  /a/b/c + x    = /a/b/c/x
             *       /a/b/c + /y   = /y
             *       /a/b/c + ../z = /a/b/z
             *       /a/b/c + d/.  = /a/b/c/d
             */
            String bpath = base.getPath();
            p1 = 0;

            while (p1 <= len) {
                p2 = rpath.indexOf("/", p1);
                if (p2 < 0)
                    p2 = len;
                String component = rpath.substring(p1, p2);

                if (component.equals(".") || component.equals("")) {
                    // ignore
                } else if (component.equals("..")) {
                    int q = bpath.lastIndexOf("/");
                    bpath = q < 0 ? "" : bpath.substring(0, q);
                } else {
                    if (bpath.equals(""))
                        bpath = component;
                    else
                        bpath += "/" + component;
                }
                p1 = p2 + 1;
            }
            path = bpath;
        }
    }

    String getProtocol() {
        return (protocol);
    }

    String getLocation() {
        return (location);
    }

    String getPath() {
        return (path);
    }

    String getParent() {

        if (path.equals(""))
            return null;        // no parent

        String s = protocol + ":";

        if (location != null)
            s += "//" + location;

        int index = path.lastIndexOf('/');
        if (index >= 0)
            s += "/" + path.substring(0, index);

        return s;
    }

    String getName() {
        int index = path.lastIndexOf('/');
        return index < 0 ? path : path.substring(index + 1);
    }

    @Override
    public String toString() {
        String s = protocol + ":";

        if (location != null)
            s += "//" + location;

        if (path != null)
            s += "/" + path;

        return (s);
    }
}
