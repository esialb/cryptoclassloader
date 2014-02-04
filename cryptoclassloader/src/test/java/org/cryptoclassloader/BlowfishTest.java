package org.cryptoclassloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.cryptoclassloader.csp.CryptoStreamProvider;
import org.cryptoclassloader.csp.CryptoStreamProviderFactories;
import org.junit.Assert;
import org.junit.Test;

public class BlowfishTest {
	@Test
	public void testStreams() throws Exception {
		CryptoStreamProvider aes = CryptoStreamProviderFactories.getBlowfish().newCryptoStreamProvider("foo");
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStream out = aes.encrypting(bout);
		out.write("foo".getBytes());
		out.close();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		InputStream in = aes.decrypting(bin);
		Assert.assertEquals("foo", new String(IOUtils.toByteArray(in)));
		
	}

}
