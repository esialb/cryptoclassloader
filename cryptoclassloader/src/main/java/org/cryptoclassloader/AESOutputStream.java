package org.cryptoclassloader;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class AESOutputStream extends CipherOutputStream {
	private static Cipher getAes(OutputStream out, byte[] key) throws IOException, GeneralSecurityException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = new byte[AES.IV_SIZE];
		new SecureRandom().nextBytes(iv);;
		out.write(iv);
		IvParameterSpec ivps = new IvParameterSpec(iv);
		SecretKey skey = new SecretKeySpec(key, "AES");
		c.init(Cipher.ENCRYPT_MODE, skey, ivps);
		return c;
	}

	public AESOutputStream(OutputStream os, byte[] key) throws IOException, GeneralSecurityException {
		super(os, getAes(os, key));
	}

}
