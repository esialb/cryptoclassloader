package org.cryptoclassloader.csp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * {@link CryptoStreamProvider} that uses AES-128
 * @author robin
 *
 */
class AES extends AbstractCryptoStreamProvider {
	
	public static final int KEY_SIZE = 16;
	public static final int IV_SIZE = 16;

	/**
	 * Convert a {@link String} to a {@code byte[]} of the appropriate
	 * length to be an AES-128 key by truncating a SHA-1 hash of the string
	 * @param ascii
	 * @return
	 */
	public static byte[] toKey(String ascii) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			byte[] key = sha1.digest(ascii.getBytes("UTF-8"));
			return Arrays.copyOf(key, KEY_SIZE);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No SHA1", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("No UTF-8", e);
		}
	}

	/**
	 * Create an AES-1238 {@link CryptoStreamProvider}
	 * @param key
	 */
	public AES(byte[] key) {
		super(KEY_SIZE, IV_SIZE, key);
	}

	@Override
	protected String cipherMode() {
		return "AES/CBC/PKCS5Padding";
	}
	

}
