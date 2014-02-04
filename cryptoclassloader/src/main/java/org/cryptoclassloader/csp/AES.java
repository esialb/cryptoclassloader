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

	public AES(byte[] key) {
		super(KEY_SIZE, IV_SIZE, key);
	}

	@Override
	protected String cipherMode() {
		return "AES/CBC/PKCS5Padding";
	}
	

}
