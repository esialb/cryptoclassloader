package org.cryptoclassloader.test.decrypt;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.cryptoclassloader.CryptoClassLoader;
import org.junit.Assert;
import org.junit.Test;

public class TestSelf {
	@Test
	public void testSelf() throws Exception {
		ClassLoader cl = new CryptoClassLoader("foobar", TestSelf.class.getClassLoader());
		
		Class<?> Self = cl.loadClass("org.cryptoclassloader.test.encrypt.Self");
		Method Self_self = Self.getMethod("self", Object.class);
		
		Assert.assertEquals("foo", Self_self.invoke(null, "foo"));
	}
	
	@Test
	public void testSelfWithoutDecryption() throws Exception {
		ClassLoader cl = new URLClassLoader(new URL[] {new File("target/dependency/cryptoclassloader-test-encrypt.jar").getCanonicalFile().toURI().toURL()});

		try {
			cl.loadClass("org.cryptoclassloader.test.encrypt.Self");
			Assert.fail("class wasn't encrypted");
		} catch(Throwable t) {
			// expected
		}
	}
}
