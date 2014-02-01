package org.cryptoclassloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class SystemCryptoClassLoader extends URLClassLoader {
	private static byte[] propertyKey() throws UnsupportedEncodingException {
		byte[] key = System.getProperty("cryptoclassloader.key").getBytes("UTF-8");
		return Arrays.copyOf(key, 16);
	}
	
	protected byte[] key;
	protected ClassLoader bootstrap;
	
	public SystemCryptoClassLoader(ClassLoader bootstrap) throws UnsupportedEncodingException {
		super(new URL[0], bootstrap);
		this.bootstrap = bootstrap;
		this.key = propertyKey();
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

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String resource = name.replaceAll("\\.", "/") + ".class";
		URL url = getResource(resource);
		try {
			InputStream in = url.openStream();
			try {
				in = new AESInputStream(in, key);
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				byte[] buf = new byte[1024];
				for(int r = in.read(buf); r != -1; r = in.read(buf))
					out.write(buf, 0, r);

				buf = out.toByteArray();
				return defineClass(name, buf, 0, buf.length);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new ClassNotFoundException(name, e);
		} catch (GeneralSecurityException e) {
			throw new ClassNotFoundException(name, e);
		}
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		try {
			InputStream in = super.getResourceAsStream(name);
			if(in == null)
				return null;
			return new AESInputStream(in, key);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
