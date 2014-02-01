package org.cryptoclassloader.test.decrypt;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.cryptoclassloader.CryptoClassLoader;
import org.junit.Assert;
import org.junit.Test;

public class TestFoo {
	@Test
	public void testFoo() throws Exception {
		ClassLoader cl = new CryptoClassLoader(
				"foobar", 
				new File("target/dependency/cryptoclassloader-test-encrypt.jar").getCanonicalFile().toURL());
		
		Assert.assertEquals("foo", IOUtils.toString(cl.getResourceAsStream("org/cryptoclassloader/test/encrypt/foo.txt")));
	}
	
	@Test
	public void testFooWithoutDecryption() throws Exception {
		ClassLoader cl = new URLClassLoader(new URL[] {new File("target/dependency/cryptoclassloader-test-encrypt.jar").getCanonicalFile().toURL()});

		Assert.assertFalse("foo".equals(IOUtils.toString(cl.getResourceAsStream("org/cryptoclassloader/test/encrypt/foo.txt"))));
	}
}
