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
	 * The actual key is a (possibly) truncated SHA-256 hash of this string.
	 */
	@Parameter(property = "cryptoclassloader.key", required=true)
	private String key;
	
	@Parameter(property = "cryptoclassloader.algorithm", defaultValue="aes")
	private Algorithm algorithm;
	
	/**
	 * Encrypt the primary artifact
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File packaged = new File(buildDirectory + "/" + finalName + "." + packaging);
		File precrypto = new File(buildDirectory + "/" + finalName + "-precrypto." + packaging);
		
		if(precrypto.exists()) {
			getLog().info("Deleting old pre-crypto backup:" + precrypto.getName());
			precrypto.delete();
		}
		getLog().info("Renaming " + packaged.getName() + " to " + precrypto.getName());
		if(!packaged.renameTo(precrypto))
			throw new MojoFailureException("Unable to rename " + packaged + " to " + precrypto);
		
		try {
			ZipFile zf = new ZipFile(precrypto);
			OutputStream out = new FileOutputStream(packaged);
			try {
				getLog().info("Encrypting " + precrypto.getName() + " to " + packaged.getName());
				new CryptoZipConverter(algorithm.getCryptoFactory().newCryptoStreamProvider(key)).convert(zf, out);
			} finally {
				out.close();
			}
		} catch(Exception e) {
			throw new MojoExecutionException(e.toString(), e);
		}
		
	}

}
