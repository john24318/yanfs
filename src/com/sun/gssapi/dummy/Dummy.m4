/*
 * Copyright (c) 1999, 2007, Oracle and/or its affiliates. All rights reserved.
 *
package com.sun.gssapi.dummy;

import java.security.Provider;

import com.sun.gssapi.Oid;
import com.sun.gssapi.GSSException;


/**
 * Dummy Mechanism plug in for JGSS
 * This is the properties object required by the JGSS framework.
 * All mechanism specific information is defined here.
 */

public final class Dummy extends Provider {

	private static String info = "JGSS Dummy Mechanism Provider";
	
	public Dummy() {

		super("JGSS Dummy Provider 1", 1.0, info);


		//list mechs supported
		put("JGSS.Mechs", "1.3.6.1.4.1.42.2.26.1.2");

		//configure 1.3.6.1.4.1.42.2.26.1.2
		put("JGSS.Mech.1.3.6.1.4.1.42.2.26.1.2.Alias", "dummy");
		put("JGSS.Mech.dummy.NAMES", "1.3.6.1.5.6.2:1.2.840.113554.1.2.1.1");
		put("JGSS.Mech.dummy.CRED", "com.sun.gssapi.dummy.DummyCred");
		put("JGSS.Mech.dummy.CONTEXT", "com.sun.gssapi.dummy.DummyCtxt");
		put("JGSS.Mech.dummy.NAME", "com.sun.gssapi.dummy.DummyName");


	}


	/**
	 * Package private method to return the oid of this mech.
	 */
	static Oid getMyOid() {

		return (M_myOid);
	}


	/**
	 * Package private method to return the number of tokens
	 * to be used in the context creation exchange.
	 */
	static int getNumOfTokExchanges() {

		return (M_tokNum);
	}

	
	//private variables
	private static Oid M_myOid;
	private static final int M_tokNum = 2;


	static {
		try {
                        M_myOid = new Oid("1.3.6.1.4.1.42.2.26.1.2");
		} catch (GSSException e) {
                        throw new NumberFormatException();
		}
	}
}
