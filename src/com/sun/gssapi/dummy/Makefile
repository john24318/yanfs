#
# Copyright (c) 1999, 2007, Oracle and/or its affiliates. All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
#   - Redistributions of source code must retain the above copyright
#     notice, this list of conditions and the following disclaimer.
#
#   - Redistributions in binary form must reproduce the above copyright
#     notice, this list of conditions and the following disclaimer in the
#     documentation and/or other materials provided with the distribution.
#
#   - Neither the name of Oracle nor the names of its
#     contributors may be used to endorse or promote products derived
#     from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
# IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
# THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
# PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
# CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
# EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
# PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
# LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
# NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

PKGDIR=	com/sun/gssapi/dummy

include Makefile.common

CLASSES= Dummy DummyCtxt DummyName DummyCred

JFILES=	$(CLASSES:%=%.java)

all:	$(TFILE)
$(TFILE):	$(JFILES)
	$(JC) -d $(CDIR) $(JCFLAGS) $?
	@touch $(TFILE)


#
# Name mangling
#
# Properties key=value strings
M4DEF_PROP = -D NAMES=_K872D1AC
M4DEF_PROP += -D NAME=_K532D1BD
M4DEF_PROP += -D CONTEXT=_K1000A49
M4DEF_PROP += -D CRED=_K2102CC5

Dummy.java: Dummy.m4
	/usr/ccs/bin/m4 $(M4DEF_PROP) Dummy.m4 > Dummy.java 

# Service Provider Interface Context Class and its methods/interface
M4DEF_CTXTSPI = -D GSSCtxtSpi=C018FE95
M4DEF_CTXTSPI += -D _setInitOptions=_S235D9C1
M4DEF_CTXTSPI += -D _setAcceptOptions=_S90010CC
M4DEF_CTXTSPI += -D _setChannelBinding=_S9B00AB2
M4DEF_CTXTSPI += -D _getOptions=_S00027C3
M4DEF_CTXTSPI += -D _getLifetime=_S4080EED
M4DEF_CTXTSPI += -D _getMech=_S0200735
M4DEF_CTXTSPI += -D _getSrcName=_S000EEFF
M4DEF_CTXTSPI += -D _getTargName=_S011CEF9
M4DEF_CTXTSPI += -D _getDelegCred=_S0293FFA
M4DEF_CTXTSPI += -D _isInitiator=_S123049E
M4DEF_CTXTSPI += -D _isProtReady=_S1116FAA
M4DEF_CTXTSPI += -D _initSecCtxt=_S0E039DB
M4DEF_CTXTSPI += -D _acceptSecCtxt=_S80A2F2C
M4DEF_CTXTSPI += -D _getWrapSizeLimit=_S808028B
M4DEF_CTXTSPI += -D _wrap=_S1309AFD
M4DEF_CTXTSPI += -D _unwrap=_S1576D09
M4DEF_CTXTSPI += -D _getMIC=_S1513DBA
M4DEF_CTXTSPI += -D _verifyMIC=_S00256CF
M4DEF_CTXTSPI += -D _export=_S725B2DA
M4DEF_CTXTSPI += -D _importSecCtxt=_S0AC8F9E
M4DEF_CTXTSPI += -D _dispose=_S020B957

DummyCtxt.java: DummyCtxt.m4
	/usr/ccs/bin/m4 $(M4DEF_CTXTSPI) DummyCtxt.m4 > DummyCtxt.java

