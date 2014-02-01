package org.cryptoclassloader;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class SystemCryptoClassLoader extends CryptoClassLoader {
	private static byte[] propertyKey() throws UnsupportedEncodingException {
		byte[] key = System.getProperty("cryptoclassloader.key").getBytes("UTF-8");
		return Arrays.copyOf(key, 16);
	}
	
	protected ClassLoader bootstrap;
	
	public SystemCryptoClassLoader(ClassLoader bootstrap) throws UnsupportedEncodingException {
		super(bootstrap, propertyKey());
		this.bootstrap = bootstrap;
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		String resource = name.replaceAll("\\.", "/") + ".class";
		// if there's no resource, then delegate to the bootstrap classloader
		URL url;
		if((url = getResource(resource)) == null)
			return bootstrap.loadClass(name);
		
		try {
			URL jar = new URL(url.toString().replaceAll("!.*", ""));
			URLClassLoader ucl = new URLClassLoader(new URL[] {jar}, new EmptyClassLoader());
			if(ucl.getResource("META-INF/org.cryptoclassloader/true") == null)
				return bootstrap.loadClass(name);
			return super.loadClass(name);
		} catch (MalformedURLException e) {
			return bootstrap.loadClass(name);
		}
	}
}
