package org.cryptoclassloader.csp;

class Blowfish extends AbstractCryptoStreamProvider {
	public static final int MIN_KEYSIZE = 4;
	public static final int MAX_KEY_SIZE = 56;
	public static final int IV_SIZE = 8;
	
	@Override
	protected String cipherMode() {
		return "Blowfish/CBC/PKCS5Padding";
	}
	
	protected Blowfish(byte[] key) {
		super(IV_SIZE, key);
		if(key.length < MIN_KEYSIZE || key.length > MAX_KEY_SIZE)
			throw new IllegalArgumentException("invalid key length");
	}

}
