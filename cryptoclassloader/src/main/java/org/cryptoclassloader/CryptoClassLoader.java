package org.cryptoclassloader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public class CryptoClassLoader extends ClassLoader {
	
	protected ZipFile zip;
	protected byte[] key;
	
	public CryptoClassLoader(ZipFile zip, byte[] key) {
		this.zip = zip;
		this.key = key;
	}
	

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.findClass(name);
	}

	@Override
	protected String findLibrary(String libname) {
		// TODO Auto-generated method stub
		return super.findLibrary(libname);
	}

	@Override
	protected URL findResource(String name) {
		// TODO Auto-generated method stub
		return super.findResource(name);
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		// TODO Auto-generated method stub
		return super.findResources(name);
	}

}
