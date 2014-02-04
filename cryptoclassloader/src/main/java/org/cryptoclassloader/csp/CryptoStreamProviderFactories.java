package org.cryptoclassloader.csp;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CryptoStreamProviderFactories {
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
	
	public static CryptoStreamProviderFactory getAES() {
		return new CryptoStreamProviderFactory() {
			@Override
			public byte[] toKey(String ascii) {
				return Arrays.copyOf(sha256(ascii), AES.KEY_SIZE);
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
				return sha256(ascii);
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
