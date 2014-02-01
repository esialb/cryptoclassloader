package org.cryptoclassloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.CipherInputStream;

/**
 * {@link ClassLoader} that decrypts classes and resources using AES-128
 * and {@link CipherInputStream}s.  Takes as a key either a 16-byte array
 * or a string.  If a string, the string is hashed with SHA-1 and truncated
 * to 16 bytes. <p>
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
	
	/**
	 * Convert a string to a key to be used with {@link CryptoClassLoader}.
	 * Computes a SHA-1 hash of the string and then truncates the hash
	 * to 16 bytes
	 * @param ascii
	 * @return
	 */
	public static byte[] toKey(String ascii) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			byte[] key = sha1.digest(ascii.getBytes("UTF-8"));
			return Arrays.copyOf(key, AES.KEY_SIZE);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No SHA1", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("No UTF-8", e);
		}
	}
	
	/**
	 * The key used by the {@link CryptoClassLoader}
	 */
	protected byte[] key;
	
	/**
	 * Create a new {@link CryptoClassLoader} with the specified 16-byte key
	 * and {@link URL}s
	 * @param key
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(byte[] key, URL... urls) throws IOException {
		super(urls);
		this.key = key;
		if(key.length != AES.KEY_SIZE)
			throw new IllegalArgumentException("invalid key length");
	}
	
	/**
	 * Create a new {@link CryptoClassLoader} with the specified 16-byte key, 
	 * parent {@link ClassLoader}, and {@link URL}s
	 * 
	 * @param key
	 * @param parent
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(byte[] key, ClassLoader parent, URL... urls) throws IOException {
		super(urls, parent);
		this.key = key;
		if(key.length != AES.KEY_SIZE)
			throw new IllegalArgumentException("invalid key length");
	}
	
	/**
	 * Create a {@link CryptoClassLoader} from a {@link String} key and {@link URL}s
	 * @param key
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(String key, URL... urls) throws IOException {
		this(toKey(key), urls);
	}
	
	/**
	 * Create a {@link CryptoClassLoader} from a {@link String} key and {@link URL}s with
	 * a parent {@link ClassLoader}
	 * @param key
	 * @param parent
	 * @param urls
	 * @throws IOException
	 */
	public CryptoClassLoader(String key, ClassLoader parent, URL... urls) throws IOException {
		this(toKey(key), parent, urls);
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
		return cryptoURL(super.findResource(name));
	}
	
	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		List<URL> urls = Collections.list(super.findResources(name));
		for(int i = 0; i < urls.size(); i++)
			urls.set(i, cryptoURL(urls.get(i)));
		return Collections.enumeration(urls);
	}
	
	protected class Handler extends URLStreamHandler {

		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			return new CryptoConnection(u);
		}
		
	}
	
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
				return new AESInputStream(new URL(url.toString()).openStream(), key);
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
