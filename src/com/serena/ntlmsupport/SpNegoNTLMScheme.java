package com.serena.ntlmsupport;

import android.annotation.SuppressLint;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.auth.AuthSchemeBase;
import org.apache.http.impl.auth.NTLMEngine;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.CharArrayBuffer;

@SuppressLint("DefaultLocale")
public class SpNegoNTLMScheme extends AuthSchemeBase {

	enum State {
		UNINITIATED, CHALLENGE_RECEIVED, MSG_TYPE1_GENERATED, MSG_TYPE2_RECEVIED, MSG_TYPE3_GENERATED, FAILED,
	}

	private final NTLMEngine engine;

	private State state;
	private String challenge;

	public SpNegoNTLMScheme(final NTLMEngine engine) {
		super();
		if (engine == null) {
			throw new IllegalArgumentException("NTLM engine may not be null");
		}
		this.engine = engine;
		this.state = State.UNINITIATED;
		this.challenge = null;
	}

	@Override
	public String getSchemeName() {
		return AuthPolicy.NTLM;
	}

	public String getParameter(String name) {
		// String parameters not supported
		return null;
	}

	public String getRealm() {
		// NTLM does not support the concept of an authentication realm
		return null;
	}

	public boolean isConnectionBased() {
		return true;
	}

	@Override
	protected void parseChallenge(final CharArrayBuffer buffer, int beginIndex,
			int endIndex) throws MalformedChallengeException {
		String challenge = buffer.substringTrimmed(beginIndex, endIndex);
		if (challenge.length() == 0) {
			if (this.state == State.UNINITIATED) {
				this.state = State.CHALLENGE_RECEIVED;
			} else {
				this.state = State.FAILED;
			}
			this.challenge = null;
		} else {
			this.state = State.MSG_TYPE2_RECEVIED;
			this.challenge = challenge;
		}
	}

	public Header authenticate(final Credentials credentials,
			final HttpRequest request) throws AuthenticationException {
		NTCredentials ntcredentials = null;
		try {
			ntcredentials = (NTCredentials) credentials;
		} catch (ClassCastException e) {
			throw new InvalidCredentialsException(
					"Credentials cannot be used for NTLM authentication: "
							+ credentials.getClass().getName());
		}

		String response = null;
		if (this.state == State.CHALLENGE_RECEIVED
				|| this.state == State.FAILED) {
			response = this.engine.generateType1Msg(ntcredentials.getDomain(),
					ntcredentials.getWorkstation());
			this.state = State.MSG_TYPE1_GENERATED;
		} else if (this.state == State.MSG_TYPE2_RECEVIED) {
			response = this.engine.generateType3Msg(
					ntcredentials.getUserName(), ntcredentials.getPassword(),
					ntcredentials.getDomain(), ntcredentials.getWorkstation(),
					this.challenge);
			this.state = State.MSG_TYPE3_GENERATED;
		} else {
			throw new AuthenticationException("Unexpected state: " + this.state);
		}

		CharArrayBuffer buffer = new CharArrayBuffer(32);
		if (isProxy()) {
			buffer.append(AUTH.PROXY_AUTH_RESP);
		} else {
			buffer.append(AUTH.WWW_AUTH_RESP);
		}
		buffer.append(": ");
		buffer.append(getSchemeName().toUpperCase());
		buffer.append(" ");
		buffer.append(response);
		return new BufferedHeader(buffer);
	}

	public boolean isComplete() {
		return this.state == State.MSG_TYPE3_GENERATED
				|| this.state == State.FAILED;
	}

}
