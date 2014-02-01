package org.cryptoclassloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

/**
 * Interface for objects which can wrap {@link InputStream}s and {@link OutputStream}s
 * in some sort of cryptographic manner
 * @author robin
 *
 */
public interface CryptoStreamProvider {
	/**
	 * Return an {@link InputStream} that is the decrypted version of the argument
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public InputStream decrypting(InputStream in) throws IOException, GeneralSecurityException;
	/**
	 * Return an {@link OutputStream} that encrypts before writing to the argument
	 * @param out
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public OutputStream encrypting(OutputStream out) throws IOException, GeneralSecurityException;
}
