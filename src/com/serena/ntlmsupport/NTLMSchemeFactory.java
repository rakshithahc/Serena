package com.serena.ntlmsupport;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.params.HttpParams;

import com.serana.MyApplication;

public class NTLMSchemeFactory implements AuthSchemeFactory {
	public AuthScheme newInstance(final HttpParams params) {
		System.out.println("NTLM Scheme is using");
		MyApplication.setCheckforAuthScheme(true);
		return new NTLMScheme(new JCIFSEngine());
	}

}