package org.cryptoclassloader;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class AESInputStream extends CipherInputStream {
	private static Cipher getAes(InputStream in, byte[] key) throws IOException, GeneralSecurityException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = new byte[16];
		in.read(iv);
		IvParameterSpec ivps = new IvParameterSpec(iv);
		SecretKey skey = new SecretKeySpec(key, "AES");
		c.init(Cipher.DECRYPT_MODE, skey, ivps);
		return c;
	}
	
	
	public AESInputStream(InputStream is, byte[] key) throws IOException, GeneralSecurityException {
		super(is, getAes(is, key));
	}

}
