package org.cryptoclassloader;

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

public class AES implements CryptoStreamProvider {
	
	public static final int KEY_SIZE = 16;
	public static final int IV_SIZE = 16;

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

	protected byte[] key;
	
	public AES(byte[] key) {
		if(key.length != KEY_SIZE)
			throw new IllegalArgumentException("Illegal key size");
		this.key = key;
	}
	
	public AES(String key) {
		this(toKey(key));
	}
	
	private static Cipher getAesDecipher(InputStream in, byte[] key) throws IOException, GeneralSecurityException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = new byte[IV_SIZE];
		in.read(iv);
		IvParameterSpec ivps = new IvParameterSpec(iv);
		SecretKey skey = new SecretKeySpec(key, "AES");
		c.init(Cipher.DECRYPT_MODE, skey, ivps);
		return c;
	}
	
	@Override
	public InputStream decrypting(InputStream in) throws IOException, GeneralSecurityException {
		return new CipherInputStream(in, getAesDecipher(in, key));
	}

	private static Cipher getAesEncipher(OutputStream out, byte[] key) throws IOException, GeneralSecurityException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = new byte[IV_SIZE];
		new SecureRandom().nextBytes(iv);;
		out.write(iv);
		IvParameterSpec ivps = new IvParameterSpec(iv);
		SecretKey skey = new SecretKeySpec(key, "AES");
		c.init(Cipher.ENCRYPT_MODE, skey, ivps);
		return c;
	}

	@Override
	public OutputStream encrypting(OutputStream out) throws IOException, GeneralSecurityException {
		return new CipherOutputStream(out, getAesEncipher(out, key));
	}

}
