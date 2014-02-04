package org.cryptoclassloader.csp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

abstract class AbstractCryptoStreamProvider implements CryptoStreamProvider {

	protected abstract String cipherMode();
	
	protected int ivSize;
	protected byte[] key;
	
	protected AbstractCryptoStreamProvider(int keySize, int ivSize, byte[] key) {
		if(key.length != keySize)
			throw new IllegalArgumentException("invalid key length");
		this.ivSize = ivSize;
		this.key = key;
	}
	
	protected String cipherName() {
		return cipherMode().split("/")[0];
	}
	
	@Override
	public InputStream decrypting(InputStream in) throws IOException, GeneralSecurityException {
		Cipher c = Cipher.getInstance(cipherMode());
		byte[] iv = new byte[ivSize];
		in.read(iv);
		IvParameterSpec ivps = new IvParameterSpec(iv);
		SecretKey skey = new SecretKeySpec(key, cipherName());
		c.init(Cipher.DECRYPT_MODE, skey, ivps);
		return new CipherInputStream(in, c);
	}

	@Override
	public OutputStream encrypting(OutputStream out) throws IOException, GeneralSecurityException {
		Cipher c = Cipher.getInstance(cipherMode());
		byte[] iv = new byte[ivSize];
		new SecureRandom().nextBytes(iv);;
		out.write(iv);
		IvParameterSpec ivps = new IvParameterSpec(iv);
		SecretKey skey = new SecretKeySpec(key, cipherName());
		c.init(Cipher.ENCRYPT_MODE, skey, ivps);
		return new CipherOutputStream(out, c);
	}

}
