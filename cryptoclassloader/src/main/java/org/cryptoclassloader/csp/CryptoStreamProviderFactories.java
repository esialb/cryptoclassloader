package org.cryptoclassloader.csp;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CryptoStreamProviderFactories {
	public static CryptoStreamProviderFactory getAES() {
		return new CryptoStreamProviderFactory() {
			@Override
			public byte[] toKey(String ascii) {
				try {
					MessageDigest sha1 = MessageDigest.getInstance("SHA1");
					byte[] key = sha1.digest(ascii.getBytes("UTF-8"));
					return Arrays.copyOf(key, AES.KEY_SIZE);
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException("No SHA1", e);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("No UTF-8", e);
				}
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
}
