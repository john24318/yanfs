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

/**
 * This is an abstract class to intended to be extended with
 * filesystem-specific methods.
 * <p>
 * An XFileExtensionAccessor class must be associated with an
 * XFileAccessor. An XFileExtensionAccessor can be used to
 * provide access to filesystem-specific methods that are not
 * defined within the XFileAccessor interface.
 * A subclass of XFileExtensionAccessor must be declared as:
 * <pre><code>
 *     import com.sun.xfile.*;
 *     <p>
 *     public class XFileExtensionAccessor extends com.sun.xfile.XFileExtensionAccessor {
 *      :
 *
 * </code></pre>
 * <p>
 * An XFileExtensionAccessor class is loaded when the
 * <code>XFile.getExtensionAccessor()</code> method is invoked.  The
 * class loading process is identical to that of an
 * XFileAccessor except for the final component of the package
 * name: "XFileExtensionAccessor" instead of "XFileAccessor".
 * <p>
 * An application that needs to use the methods within the
 * XFileExtensionAccessor must cast the result of XFile.getExtensionAccessor.
 * <pre><code>
 *    import com.sun.xfile.*;
 *    <p>
 *    XFile xf = new XFile("ftp://server/path");
 *    com.acme.ftp.XFileExtensionAccessor xftp =
 *        (com.acme.ftp.XFileExtensionAccessor) xf.getExtensionAccessor();
 *    xftp.login();
 *      :
 * </code></pre>
 *
 *
 * @author  Brent Callaghan
 * @see     com.sun.xfile.XFile#getExtensionAccessor()
 * @see     com.sun.xfile.XFileAccessor
 */
public abstract class XFileExtensionAccessor {

    private XFile xf;

    /*
     * Constructor for the XFileExtensionAccessor.
     *
     * Invoked by the XFile class when its getExtensionAccessor
     * method is called.  The <code>XFile</code> argument of
     * the constructor provides context for the methods
     * within the class.
     */
    public XFileExtensionAccessor(XFile xf) {
        this.xf = xf;
    }
}
