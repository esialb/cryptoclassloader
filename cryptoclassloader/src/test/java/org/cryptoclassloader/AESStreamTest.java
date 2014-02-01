package org.cryptoclassloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class AESStreamTest {
	@Test
	public void testStreams() throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStream out = new AESOutputStream(bout, new byte[16]);
		out.write("foo".getBytes());
		out.close();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		InputStream in = new AESInputStream(bin, new byte[16]);
		Assert.assertEquals("foo", new String(IOUtils.toByteArray(in)));
		
	}
}
