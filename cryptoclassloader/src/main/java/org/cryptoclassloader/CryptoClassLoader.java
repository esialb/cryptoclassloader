package org.cryptoclassloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CryptoClassLoader extends URLClassLoader {
	
	public static byte[] toKey(String ascii) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			byte[] key = sha1.digest(ascii.getBytes("UTF-8"));
			return Arrays.copyOf(key, 16);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No SHA1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("No UTF-8");
		}
	}
	
	protected byte[] key;
	
	public CryptoClassLoader(byte[] key, URL... urls) throws IOException {
		super(urls);
		this.key = key;
	}
	
	public CryptoClassLoader(byte[] key, ClassLoader parent, URL... urls) throws IOException {
		super(urls, parent);
		this.key = key;
	}
	
	public CryptoClassLoader(String key, URL... urls) throws IOException {
		this(toKey(key), urls);
	}
	
	public CryptoClassLoader(String key, ClassLoader parent, URL... urls) throws IOException {
		this(toKey(key), parent, urls);
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
