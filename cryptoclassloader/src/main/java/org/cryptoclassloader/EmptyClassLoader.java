package org.cryptoclassloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

class EmptyClassLoader extends ClassLoader {

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		throw new ClassNotFoundException(name);
	}

	@Override
	public URL getResource(String name) {
		return null;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return Collections.enumeration(Collections.<URL>emptySet());
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		return null;
	}
	
}
