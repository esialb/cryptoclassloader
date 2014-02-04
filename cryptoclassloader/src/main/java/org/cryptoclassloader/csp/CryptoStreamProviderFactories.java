package org.cryptoclassloader.csp;

public class CryptoStreamProviderFactories {
	public static CryptoStreamProviderFactory getAES() {
		return new CryptoStreamProviderFactory() {
			@Override
			public byte[] toKey(String ascii) {
				return AES.toKey(ascii);
			}
			
			@Override
			public CryptoStreamProvider newCryptoStreamProvider(byte[] key) {
				return new AES(key);
			}
			
			@Override
			public CryptoStreamProvider newCryptoStreamProvider(String key) {
				return new AES(key);
			}
		};
	}
}
