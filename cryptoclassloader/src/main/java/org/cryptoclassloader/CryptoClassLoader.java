package org.cryptoclassloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.GeneralSecurityException;

public class CryptoClassLoader extends URLClassLoader {
	
	protected byte[] key;
	
	public CryptoClassLoader(byte[] key, URL... urls) throws IOException {
		super(urls);
		this.key = key;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String resource = name.replaceAll("\\.", "/") + ".class";
		URL url = findResource(resource);
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


}
