package org.cryptoclassloader.mp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.ZipFile;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.cryptoclassloader.CryptoZipConverter;

@Mojo(name = "encrypy")
public class EncryptMojo extends AbstractMojo {

	@Parameter(property = "project.build.directory", readonly = true)
	private String buildDirectory;
	
	@Parameter(property = "project.build.finalName", readonly = true)
	private String finalName;
	
	@Parameter(property = "project.packaging", readonly = true)
	private String packaging;
	
	@Parameter(property = "cryproclassloader.key", required=true)
	private String key;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File packaged = new File(buildDirectory + "/" + finalName + "." + packaging);
		File precrypto = new File(buildDirectory + "/" + finalName + "-precrypto." + packaging);
		
		byte[] key;
		try {
			key = this.key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new MojoExecutionException(e.toString(), e);
		}
		key = Arrays.copyOf(key, 16);
		
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
