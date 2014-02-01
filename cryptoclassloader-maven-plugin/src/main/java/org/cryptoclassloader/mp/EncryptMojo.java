package org.cryptoclassloader.mp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipFile;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.cryptoclassloader.AES;
import org.cryptoclassloader.CryptoClassLoader;
import org.cryptoclassloader.CryptoZipConverter;

/**
 * Plugin to encrypt the primary artifact, suitable for use
 * with {@link CryptoClassLoader}
 * @author robin
 *
 */
@Mojo(name = "encrypt")
public class EncryptMojo extends AbstractMojo {

	@Parameter(property = "project.build.directory", readonly = true)
	private String buildDirectory;
	
	@Parameter(property = "project.build.finalName", readonly = true)
	private String finalName;
	
	@Parameter(property = "project.packaging", readonly = true)
	private String packaging;

	/**
	 * The string representation of the encryption key to use.
	 * The actual key is a truncated SHA-1 hash of this string.
	 */
	@Parameter(property = "cryptoclassloader.key", required=true)
	private String key;
	
	/**
	 * Encrypt the primary artifact
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File packaged = new File(buildDirectory + "/" + finalName + "." + packaging);
		File precrypto = new File(buildDirectory + "/" + finalName + "-precrypto." + packaging);
		
		precrypto.delete();
		if(!packaged.renameTo(precrypto))
			throw new MojoFailureException("Unable to rename " + packaged + " to " + precrypto);
		try {
			ZipFile zf = new ZipFile(precrypto);
			OutputStream out = new FileOutputStream(packaged);
			try {
				new CryptoZipConverter(key).convert(zf, out);
			} finally {
				out.close();
			}
		} catch(Exception e) {
			throw new MojoExecutionException(e.toString(), e);
		}
	}

}
