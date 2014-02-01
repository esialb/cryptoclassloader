package org.cryptoclassloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class CryptoClassLoader extends ClassLoader {
	
	protected File file;
	protected ZipFile zip;
	protected byte[] key;
	
	public CryptoClassLoader(File zip, byte[] key) throws IOException {
		this.file = file;
		this.zip = new ZipFile(file);
		this.key = key;
	}
	

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String es = name.replaceAll("\\.", "/") + ".class";
		ZipEntry ze = new ZipEntry(es);
		try {
			InputStream in = zip.getInputStream(ze);
			in = new AESInputStream(in, key);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			byte[] buf = new byte[1024];
			for(int r = in.read(buf); r != -1; r = in.read(buf))
				out.write(buf, 0, r);
			
			buf = out.toByteArray();
			return defineClass(name, buf, 0, buf.length);
		} catch(IOException ioe) {
			throw new ClassNotFoundException(name, ioe);
		} catch(GeneralSecurityException gse) {
			throw new ClassNotFoundException(name, gse);
		}
	}

	@Override
	protected String findLibrary(String libname) {
		return null;
	}

	@Override
	protected URL findResource(String name) {
		if(zip.getEntry(name) == null)
			return null;
		try {
			return new URL(file.toURL() + "!" + name);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		URL url = findResource(name);
		if(url == null)
			return Collections.enumeration(Collections.<URL>emptyList());
		return Collections.enumeration(Collections.singleton(url));
	}

}
