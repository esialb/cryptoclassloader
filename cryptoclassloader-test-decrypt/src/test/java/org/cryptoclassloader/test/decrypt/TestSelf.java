package org.cryptoclassloader.test.decrypt;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.cryptoclassloader.CryptoClassLoader;
import org.junit.Assert;
import org.junit.Test;

public class TestSelf {
	@Test
	public void testSelf() throws Exception {
		byte[] key = "foobar".getBytes("UTF-8");
		key = Arrays.copyOf(key, 16);
		CryptoClassLoader cl = new CryptoClassLoader(key, new File("target/dependency/cryptoclassloader-test-encrypt.jar").getCanonicalFile().toURL());
		
		Class<?> Self = cl.loadClass("org.cryptoclassloader.test.encrypt.Self");
		Method Self_self = Self.getMethod("self", Object.class);
		
		Assert.assertEquals("foo", Self_self.invoke(null, "foo"));
		
	}
}
