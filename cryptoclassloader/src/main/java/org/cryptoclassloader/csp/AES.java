package org.cryptoclassloader.csp;

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
