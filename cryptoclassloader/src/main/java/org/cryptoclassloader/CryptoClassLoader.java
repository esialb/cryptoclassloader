package org.cryptoclassloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.CipherInputStream;

import org.cryptoclassloader.csp.CryptoStreamProvider;

import static org.cryptoclassloader.csp.CryptoStreamProviderFactories.*;

/**
 * {@link ClassLoader} that decrypts classes and resources using AES-128
 * and {@link CipherInputStream}s.  Takes a {@link CryptoStreamProvider}
 * as an argument for handling the decryption.  If instead a {@code byte[]}
 * or {@link String} is provided they are used as keys for {@link AES}.
 * 
 * {@link CryptoClassLoader} extends {@link URLClassLoader} and will decrypt the
 * contents of the URLs provided at construction time. <p>
 * 
 * Resources beginning with {@code META-INF/} are treated as plaintext and
 * not decrypted.
 * @author robin
 *
 */
public class CryptoClassLoader extends URLClassLoader {
	public static final String SUFFIX = "$ccl";
	
	/**
	 * The object responsible for providing decryption streams
	 */
	protected CryptoStreamProvider crypto;
	
	/**
	 * Create a {@link CryptoClassLoader} with a {@link CryptoStreamProvider} and an
	 * array of {@link URL}s to encrypted jars
	 * @param crypto
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(CryptoStreamProvider crypto, URL... urls) throws IOException {
		super(urls);
		this.crypto = crypto;
	}
	
	/**
	 * Create a {@link CryptoClassLoader} with a {@link CryptoStreamProvider}, a specified parent
	 * {@link ClassLoader}, and an array of {@link URL}s to encrypted jars
	 * @param crypto
	 * @param parent
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(CryptoStreamProvider crypto, ClassLoader parent, URL... urls) throws IOException {
		super(urls, parent);
		this.crypto = crypto;
	}

	/**
	 * Create a {@link CryptoClassLoader} using {@link AES}
	 * @param key
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(byte[] key, URL... urls) throws IOException {
		this(getAES().newCryptoStreamProvider(key), urls);
	}
	
	/**
	 * Create a {@link CryptoClassLoader} using {@link AES} and a parent {@link ClassLoader}
	 * @param key
	 * @param parent
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(byte[] key, ClassLoader parent, URL... urls) throws IOException {
		this(getAES().newCryptoStreamProvider(key), parent, urls);
	}
	
	/**
	 * Create a {@link CryptoClassLoader} using {@link AES}
	 * @param key
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(String key, URL... urls) throws IOException {
		this(getAES().newCryptoStreamProvider(key), urls);
	}
	
	/**
	 * Create a {@link CryptoClassLoader} using {@link AES} and a parent {@link ClassLoader}
	 * @param key
	 * @param parent
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(String key, ClassLoader parent, URL... urls) throws IOException {
		this(getAES().newCryptoStreamProvider(key), parent, urls);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// convert the class name to a resource ...
		String resource = name.replaceAll("\\.", "/") + ".class";
		// ... and find the resource
		URL url = findResource(resource);
		if(url == null)
			throw new ClassNotFoundException(name);
		
		// open and decrypt the resource's input stream
		try {
			InputStream in = url.openStream();
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				byte[] buf = new byte[1024];
				for(int r = in.read(buf); r != -1; r = in.read(buf))
					out.write(buf, 0, r);

				// define and return the class
				buf = out.toByteArray();
				return defineClass(name, buf, 0, buf.length);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new ClassNotFoundException(name, e);
		}
	}
	
	/**
	 * Return a {@link URL} that, when opened, decrypts the normal contents
	 * @param url
	 * @return
	 */
	protected URL cryptoURL(URL url) {
		if(url == null)
			return null;
		try {
			return new URL(url, url.toString(), new Handler());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public URL findResource(String name) {
		if(name.startsWith("META-INF/"))
			return super.findResource(name);
		URL found = super.findResource(name + SUFFIX);
		if(found != null)
			return cryptoURL(found);
		return cryptoURL(getParent().getResource(name + SUFFIX));
	}
	
	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		if(name.startsWith("META-INF/"))
			return super.findResources(name);
		List<URL> urls = Collections.list(super.findResources(name + SUFFIX));
		for(int i = 0; i < urls.size(); i++)
			urls.set(i, cryptoURL(urls.get(i)));
		return Collections.enumeration(urls);
	}
	
	/**
	 * {@link URLStreamHandler} that makes its {@link URL}s open their
	 * {@link URLConnection}s with {@link CryptoConnection}
	 * @author robin
	 *
	 */
	protected class Handler extends URLStreamHandler {

		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			return new CryptoConnection(u);
		}
		
	}
	
	/**
	 * {@link URLConnection} that decrypts the normal content of the {@link URL}
	 * @author robin
	 *
	 */
	protected class CryptoConnection extends URLConnection {

		protected CryptoConnection(URL url) {
			super(url);
		}

		@Override
		public void connect() throws IOException {
		}
		
		@Override
		public InputStream getInputStream() throws IOException {
			try {
				return crypto.decrypting(new URL(url.toString()).openStream());
			} catch (GeneralSecurityException e) {
				throw new IOException(e);
			}
		}
		
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		try {
			if(name.startsWith("META-INF/"))
				return super.findResource(name).openStream();
			return findResource(name).openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
