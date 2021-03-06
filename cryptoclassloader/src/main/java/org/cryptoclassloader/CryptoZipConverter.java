package org.cryptoclassloader;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.cryptoclassloader.csp.CryptoStreamProvider;

/**
 * Utility class to encrypt a {@link ZipFile} to an {@link OutputStream},
 * writing a new zip file to the stream. <p>
 * 
 * All entries are encrypted unless they begin with META-INF/
 * @author robin
 *
 */
public class CryptoZipConverter {
	/**
	 * Stream which doesn't close its wrapped stream when {@link #close()}
	 * is called
	 * @author robin
	 *
	 */
	private static class NoCloseOutputStream extends FilterOutputStream {
		public NoCloseOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void close() throws IOException {
			// flush only
			flush();
		}
	}
	
	protected CryptoStreamProvider crypto;
	
	public CryptoZipConverter(CryptoStreamProvider crypto) {
		this.crypto = crypto;
	}
	
	/**
	 * Convert the {@link ZipFile} argument to a new zip file, written to the argument
	 * {@link OutputStream}, with all but the META-INF/ entries encrypted.
	 * @param zip
	 * @param out
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@SuppressWarnings("resource")
	public void convert(ZipFile zip, OutputStream out) throws IOException, GeneralSecurityException {
		ZipOutputStream zout = new ZipOutputStream(out);
		Enumeration<? extends ZipEntry> zee = zip.entries();
		while(zee.hasMoreElements()) {
			ZipEntry ze = zee.nextElement();
			if(ze.isDirectory())
				continue;
			InputStream in = zip.getInputStream(ze);
			String ename = ze.getName();
			if(!ename.startsWith("META-INF/"))
				ename += CryptoClassLoader.SUFFIX;
			zout.putNextEntry(new ZipEntry(ename));
			OutputStream eout = new NoCloseOutputStream(zout);
			if(!ename.startsWith("META-INF/"))
				eout = crypto.encrypting(eout);
			
			byte[] buf = new byte[1024];
			for(int r = in.read(buf); r != -1; r = in.read(buf))
				eout.write(buf, 0, r);
			
			eout.close();
			zout.closeEntry();
		}
		zout.finish();
	}
}
