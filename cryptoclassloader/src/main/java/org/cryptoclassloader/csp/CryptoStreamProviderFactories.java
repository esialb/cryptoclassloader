package org.cryptoclassloader.csp;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CryptoStreamProviderFactories {
	public static final int JCE_MAX_KEY_LENGTH = 16;
	
	private static byte[] sha256(String ascii) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
			return sha1.digest(ascii.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No SHA1", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("No UTF-8", e);
		}
	}
	
	public static final CryptoStreamProviderFactory getAES() {
		return new CryptoStreamProviderFactory() {
			@Override
			public byte[] toKey(String ascii) {
				byte[] key = Arrays.copyOf(sha256(ascii), AES.KEY_SIZE);
				key = Arrays.copyOf(key, Math.min(key.length, JCE_MAX_KEY_LENGTH));
				return key;
			}
			
			@Override
			public CryptoStreamProvider newCryptoStreamProvider(byte[] key) {
				return new AES(key);
			}
			
			@Override
			public CryptoStreamProvider newCryptoStreamProvider(String key) {
				return new AES(toKey(key));
			}
		};
	}
	
	public static CryptoStreamProviderFactory getBlowfish() {
		return new CryptoStreamProviderFactory() {
			
			@Override
			public byte[] toKey(String ascii) {
				byte[] key = sha256(ascii);
				key = Arrays.copyOf(key, Math.min(key.length, JCE_MAX_KEY_LENGTH));
				return key;
			}
			
			@Override
			public CryptoStreamProvider newCryptoStreamProvider(byte[] key) {
				return new Blowfish(key);
			}
			
			@Override
			public CryptoStreamProvider newCryptoStreamProvider(String key) {
				return new Blowfish(toKey(key));
			}
		};
	}
}
