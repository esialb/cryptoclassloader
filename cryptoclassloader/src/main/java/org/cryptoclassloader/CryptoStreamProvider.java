package org.cryptoclassloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public interface CryptoStreamProvider {
	public InputStream decrypting(InputStream in) throws IOException, GeneralSecurityException;
	public OutputStream encrypting(OutputStream out) throws IOException, GeneralSecurityException;
}
