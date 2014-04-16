package org.cryptoclassloader.mp;

import org.cryptoclassloader.csp.CryptoStreamProviderFactories;
import org.cryptoclassloader.csp.CryptoStreamProviderFactory;

public enum Algorithm {
	aes(CryptoStreamProviderFactories.getAES()),
	blowfish(CryptoStreamProviderFactories.getBlowfish())
	;
	
	private Algorithm(CryptoStreamProviderFactory cryptoFactory) {
		this.cryptoFactory = cryptoFactory;
	}
	
	private CryptoStreamProviderFactory cryptoFactory;
	
	public CryptoStreamProviderFactory getCryptoFactory() {
		return cryptoFactory;
	}
}
