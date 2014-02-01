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

public class CryptoZipConverter {
	protected static class NoCloseOutputStream extends FilterOutputStream {
		public NoCloseOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void close() throws IOException {
			flush();
		}
	}
	
	protected byte[] key;
	
	public CryptoZipConverter(byte[] key) {
		this.key = key;
	}
	
	public void convert(ZipFile zip, OutputStream out) throws IOException, GeneralSecurityException {
		ZipOutputStream zout = new ZipOutputStream(out);
		Enumeration<? extends ZipEntry> zee = zip.entries();
		while(zee.hasMoreElements()) {
			ZipEntry ze = zee.nextElement();
			if(ze.isDirectory())
				continue;
			InputStream in = zip.getInputStream(ze);
			zout.putNextEntry(new ZipEntry(ze.getName()));
			OutputStream eout = new NoCloseOutputStream(zout);
			eout = new AESOutputStream(eout, key);
			
			byte[] buf = new byte[1024];
			for(int r = in.read(buf); r != -1; r = in.read(buf))
				eout.write(buf, 0, r);
			
			eout.close();
			zout.closeEntry();
		}
		zout.putNextEntry(new ZipEntry("META-INF/org.cryptoclassloader/true"));
		zout.closeEntry();
		zout.finish();
	}
}
