package br.com.standardit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class UpdateLocal {

	public static void main(String[] args) {
		try {

			CopyJarFile("C:\\Taxforce\\RPA\\Automation", "T:\\RPA\\Automation");
			CopyFile("C:\\Taxforce\\RPA\\Automation\\Drivers", "T:\\RPA\\Automation\\Drivers");
			CopyFile("C:\\Taxforce\\RPA\\Automation\\Automation_lib", "T:\\RPA\\Automation\\Automation_lib");
			CopyFile("C:\\Dropbox\\StandardIT\\TaxForce\\Workspace\\new_automate-pva\\images",
					"C:\\Taxforce\\RPA\\Automation\\images");
			CopyFile("C:\\Taxforce\\RPA\\Automation\\images", "T:\\RPA\\Automation\\images");

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void CopyFile(String source, String dest) throws InterruptedException {
		try {
			util.log.info("[" + util.getMetod() + "] - Copying file from: " + source + " to: " + source);
			Files.copy(Paths.get(source), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void CopyJarFile(String sourceDir, String destDir) throws InterruptedException {
		Path source = Paths.get(sourceDir);
		Path dest = Paths.get(destDir);
		try {
			if (source.toFile().isDirectory()) {
				File[] content = source.toFile().listFiles();
				for (File file : content) {
					if (file.getName().contains(".jar")) {
						// util.log.info("[" + util.getMetod() + "] - Moving file from: " +
						// file.getAbsolutePath()
						// + " to: " + destDir + "\\" + file.getName());
						// Files.move(Paths.get(file.getAbsolutePath()), Paths.get(destDir + "\\" +
						// file.getName()),
						// StandardCopyOption.REPLACE_EXISTING);
						Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(destDir + "\\" + file.getName()),
								StandardCopyOption.REPLACE_EXISTING);
						// new File(destDir + "\\" + file.getName()).renameTo(new File(destDir +
						// "\\Certidao.pdf"));
						// TimeUnit.SECONDS.sleep(3);
					}
				}
			} else {
				util.log.info("[" + util.getMetod() + "] - Replace existing file.");
				Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (java.nio.file.FileSystemException fsEx) {
			util.log.error("[" + util.getMetod() + "] - FileSystemException: " + fsEx.getMessage());
			// TimeUnit.SECONDS.sleep(5);
			// MoveFiles(sourceDir, destDir);
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
	}
}
