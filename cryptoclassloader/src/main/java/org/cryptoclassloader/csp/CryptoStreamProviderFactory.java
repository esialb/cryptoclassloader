package org.cryptoclassloader.csp;

public interface CryptoStreamProviderFactory {
	public byte[] toKey(String ascii);
	public CryptoStreamProvider newCryptoStreamProvider(String key);
	public CryptoStreamProvider newCryptoStreamProvider(byte[] key);
}
