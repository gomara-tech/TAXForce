package br.com.standardit;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class util {

	public static final Logger log = (Logger) LoggerFactory.getLogger(util.class);

	public static String getFileToProcess(final File folder) {

		util.log.info("[" + util.getMetod() + "] - Lendo diretorio " + folder.toString());
		for (final File fileEntry : folder.listFiles())
			if (!fileEntry.isDirectory())
				return fileEntry.getName();
		return null;
	}

	public static void CreateFolder(String dir) {
		File theDir = new File(dir);
		if (!theDir.exists())
			theDir.mkdir();
	}

	public static void MoveFiles(String sourceDir, String destDir) throws InterruptedException {
		Path source = Paths.get(sourceDir);
		Path dest = Paths.get(destDir);
		try {
			if (source.toFile().isDirectory()) {
				File[] content = source.toFile().listFiles();
				for (File file : content) {
					util.log.info("[" + util.getMetod() + "] - Moving file from: " + file.getAbsolutePath() + " to: "
							+ destDir + "\\" + file.getName());
					Files.move(Paths.get(file.getAbsolutePath()), Paths.get(destDir + "\\" + file.getName()),
							StandardCopyOption.REPLACE_EXISTING);
					// Aquarda um segundo para evitar erro ao copiar arquivo PDF
					TimeUnit.SECONDS.sleep(3);
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
		}
	}

	public static void DeleteFiles(String sourceDir, boolean withFolders) throws FileSystemException {
		Path source = Paths.get(sourceDir);
		try {
			if (source.toFile().isDirectory()) {
				File[] content = source.toFile().listFiles();
				for (File file : content) {
					DeleteFiles(file.getAbsolutePath(), withFolders);
					util.log.info("[" + util.getMetod() + "] - Delete File: " + file.getAbsolutePath());
				}
				if (withFolders)
					if (Files.exists(source))
						Files.delete(source);
			} else if (Files.exists(source))
				Files.delete(source);
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void DeleteOneFile(String sourceDir, String nomeArq) throws FileSystemException {
		Path source = Paths.get(sourceDir);
		try {
			if (source.toFile().isDirectory()) {
				File[] content = source.toFile().listFiles();
				for (File file : content) {
					if (file.getName().equals(nomeArq)) {
						Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
						/// Files.delete(file.getName());
					}
					// DeleteFiles(file.getAbsolutePath(), withFolders);
					// util.log.info("[" + util.getMetod() + "] - Delete File: " +
					// file.getAbsolutePath());
				}
				// if (withFolders)
				// if (Files.exists(source))
				// Files.delete(source);
			}
			// else if (Files.exists(source))
			// Files.delete(source);
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void ValidaSeGerouLog(String sourceDir) {
		Path source = Paths.get(sourceDir);
		Boolean gerouArquivo = false;
		try {
			if (source.toFile().isDirectory()) {
				File[] content = source.toFile().listFiles();
				for (File file : content) {
					if (file.getName().contains(".png")) {
						gerouArquivo = true;
					}
				}
				if (!gerouArquivo) {
					PrintErro(sourceDir + "\\Erro.png");
				}
			}
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
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

	public static void ClearLog(String adrressLog, String textToClear) throws IOException {
		// util.ClearLog(watchPath + "\\log.txt", pass);
		Path path = Paths.get(adrressLog);
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll(textToClear, "");
		Files.write(path, content.getBytes(charset));
	}

	public static void PrintErro(String file) {
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension screenSize = toolkit.getScreenSize();
			Rectangle screenRect = new Rectangle(screenSize);
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenRect);

			ImageIO.write(image, "png", new File(file));
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (AWTException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void PrintImage(String file) {
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension screenSize = toolkit.getScreenSize();
			util.log.info("[" + util.getMetod() + "] - toolkit.getScreenSize():" + toolkit.getScreenSize());
			Rectangle screenRect = new Rectangle(screenSize);
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenRect);

			ImageIO.write(image, "png", new File(file));
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (AWTException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void sleep(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean isNullOrBlank(String param) {
		return param == null || param.trim().length() == 0;
	}

	public static boolean isCNPJ(String CNPJ) {
		// considera-se erro CNPJ's formados por uma sequencia de numeros iguais
		if (CNPJ.equals("00000000000000") || CNPJ.equals("11111111111111") || CNPJ.equals("22222222222222")
				|| CNPJ.equals("33333333333333") || CNPJ.equals("44444444444444") || CNPJ.equals("55555555555555")
				|| CNPJ.equals("66666666666666") || CNPJ.equals("77777777777777") || CNPJ.equals("88888888888888")
				|| CNPJ.equals("99999999999999") || (CNPJ.length() != 14))
			return (false);

		char dig13, dig14;
		int sm, i, r, num, peso;

		// "try" - protege o código para eventuais erros de conversao de tipo (int)
		try {
			// Calculo do 1o. Digito Verificador
			sm = 0;
			peso = 2;
			for (i = 11; i >= 0; i--) {
				// converte o i-ésimo caractere do CNPJ em um número:
				// por exemplo, transforma o caractere '0' no inteiro 0
				// (48 eh a posição de '0' na tabela ASCII)
				num = (int) (CNPJ.charAt(i) - 48);
				sm = sm + (num * peso);
				peso = peso + 1;
				if (peso == 10)
					peso = 2;
			}

			r = sm % 11;
			if ((r == 0) || (r == 1))
				dig13 = '0';
			else
				dig13 = (char) ((11 - r) + 48);

			// Calculo do 2o. Digito Verificador
			sm = 0;
			peso = 2;
			for (i = 12; i >= 0; i--) {
				num = (int) (CNPJ.charAt(i) - 48);
				sm = sm + (num * peso);
				peso = peso + 1;
				if (peso == 10)
					peso = 2;
			}

			r = sm % 11;
			if ((r == 0) || (r == 1))
				dig14 = '0';
			else
				dig14 = (char) ((11 - r) + 48);

			// Verifica se os dígitos calculados conferem com os dígitos informados.
			if ((dig13 == CNPJ.charAt(12)) && (dig14 == CNPJ.charAt(13)))
				return (true);
			else
				return (false);
		} catch (InputMismatchException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			return (false);
		}
	}

	public static String paste(Object object) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable content = clipboard.getContents(object); // como parametro do getContents, tem que ser o objeto q
																// vai usar a area de transferencia
		String value = "";
		if (content != null) {
			try {
				value = content.getTransferData(DataFlavor.stringFlavor).toString();
			} catch (UnsupportedFlavorException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				e.printStackTrace();
			}

		}
		return value;
	}

//	public static void SaveStringToFile(String text, String file) {
//		try (PrintWriter out = new PrintWriter(file)) {
//			out.println(text);
//		} catch (FileNotFoundException e) {
//			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
//			e.printStackTrace();
//		}
//	}

	public static void SaveStringToFile(String text, String file) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.println(text);
	}

	public static void MouseMove() {
		Screen s = new Screen();
		s.mouseMove(0, 10);
		s.mouseMove(0, -10);
	}

	public static void MouseMove2() {
		Screen s = new Screen();
		// s.mouseMove(0, 100);
		s.mouseMove(0, -100);
	}

	public static int geraAleatorio(int max, int min) {
		Random random = new Random();
		return (random.nextInt(max - (min - 1)) + min);
	}

	public static String getMetod() {
		String methodName2 = Thread.currentThread().getStackTrace()[2].getMethodName();
		String methodName3 = Thread.currentThread().getStackTrace()[3].getMethodName(); //
		// Pega o método atual
		return methodName3 + "][" + methodName2;
	}

	public static Boolean fileExists(String destDir) {
		File dir = new File(destDir);
		Boolean retorno = false;
		if (dir.exists())
			retorno = true;
		return retorno;
	}

	static void unzip(String zipFilePath, String destDir) {
		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + fileName);
				// System.out.println("Unzipping to " + newFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			// zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			zip.putNextEntry(new ZipEntry(folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}

//---------------------------------------------------------------------------------------------------
	private static String getComputerName() {
		Map<String, String> env = System.getenv();
		if (env.containsKey("COMPUTERNAME"))
			return env.get("COMPUTERNAME");
		else if (env.containsKey("HOSTNAME"))
			return env.get("HOSTNAME");
		else
			return "Unknown Computer";
	}

	// --------------------------------------------------------------------------------
	public static String encoder(String filePath) {
		String base64File = "";
		File file = new File(filePath);
		try (FileInputStream imageInFile = new FileInputStream(file)) {
			// Reading a file from file system
			byte fileData[] = new byte[(int) file.length()];
			imageInFile.read(fileData);
			base64File = Base64.getEncoder().encodeToString(fileData);
		} catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the file " + ioe);
		}
		return base64File;
	}

//--------------------------------------------------------------------------------
	public static String getDateWithPlusDays(Integer days) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime plusDays = now.plusDays(days);

		return dtf.format(plusDays);
	}

//-------------------------------------------------------------------------------
	public static void SaveChromeToPdf(String fileAdrress, String imageToClick) {
		Screen s = new Screen();

		UtilAutomation utilAut = new UtilAutomation(s);
		WaitResult waitResult;

		waitResult = utilAut.WaitFor(10, new String[] { imageToClick });
		if (waitResult.region != null) {
			waitResult.region.rightClick();
			util.sleep(2);
			s.type(Key.DOWN);
			util.sleep(1);
			s.type(Key.DOWN);
			util.sleep(1);
			s.type(Key.DOWN);
			util.sleep(1);
			waitResult = utilAut.WaitFor(2, new String[] { "ImprimirPDF\\ValidacaoCnrlP" });
			if (waitResult.region == null) {
				s.type(Key.DOWN);
				util.sleep(1);
			}
			s.type(Key.ENTER);
			util.sleep(2);
		}

		waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\ButtonPrint", "ImprimirPDF\\Imprimir" });
		if (waitResult.region != null) {
			waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\MicrosoftPrintToPdf" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(1);
				s.type(Key.DOWN);
				util.sleep(1);
				s.type(Key.ENTER);
				util.sleep(2);

				waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\ButtonSave" });
				if (waitResult.region != null) {
					waitResult.region.click();
					util.sleep(1);
					s.type(Key.BACKSPACE);
					util.sleep(2);

					waitResult = utilAut.WaitFor(10,
							new String[] { "ImprimirPDF\\FileName", "ImprimirPDF\\FileName2" });
					if (waitResult.region != null) {
						waitResult.region.click();
						util.sleep(1);
						s.type(Key.BACKSPACE);
						util.sleep(2);
						s.type(fileAdrress);
						waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\ButtonSaveAs" });
						if (waitResult.region != null) {
							waitResult.region.click();

							waitResult = utilAut.WaitFor(1, new String[] { "ImprimirPDF\\ConfirmSaveAs" });
							if (waitResult.region != null) {
								s.type(Key.LEFT);
								util.sleep(1);
								s.type(Key.ENTER);
								util.sleep(2);
							}
						}
					}
				}
			}
		} else {
			System.out.println("Erro no Botao Imprimir");
		}
	}

//---------------------------------------------------------------------------------------------------------
	public static void SaveChromeToPdf2(String fileAdrress, String[] imageToClick) {
		Screen s = new Screen();

		UtilAutomation utilAut = new UtilAutomation(s);
		WaitResult waitResult;

		waitResult = utilAut.WaitFor(20, imageToClick);
		if (waitResult.region != null) {
			waitResult.region.click();
			util.sleep(2);

			s.type("p", Key.CTRL);

			// altera o destino da funcao imprimir
			waitResult = utilAut.WaitFor(8,
					new String[] { "ImprimirPDF\\MicrosoftPrintToPDF", "ImprimirPDF\\MicrosoftPrintToPDF2" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(2);
				s.type(Key.DOWN);
				util.sleep(3);
				s.type(Key.ENTER);

			}

			waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\ButtonPrint", "ImprimirPDF\\Imprimir" });
			if (waitResult.region != null) {
				waitResult.region.click();
			} else {
				waitResult = utilAut.WaitFor(1, new String[] { "ImprimirPDF\\ClicaNoCentroParaTirarOFoco" });
				if (waitResult.region != null) {
					waitResult.region.click();
					s.type(Key.ENTER);
				}
			}

			waitResult = utilAut.WaitFor(5, new String[] { "ImprimirPDF\\ButtonSaveAs" });
			if (waitResult.region != null) {
				util.sleep(5);
				s.type(Key.BACKSPACE);
			}

			waitResult = utilAut.WaitFor(5,
					new String[] { "ImprimirPDF\\FileName", "ImprimirPDF\\FileName2", "ImprimirPDF\\FileName3" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(1);
				s.type(Key.BACKSPACE);
				util.sleep(2);
				s.type(fileAdrress);

				waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\ButtonSaveAs" });
				if (waitResult.region != null) {
					waitResult.region.click();

					waitResult = utilAut.WaitFor(1, new String[] { "ImprimirPDF\\ConfirmSaveAs" });
					if (waitResult.region != null) {
						s.type(Key.LEFT);
						util.sleep(1);
						s.type(Key.ENTER);
						util.sleep(2);
					}
				}
				waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\ButtonSaveAs" });
				if (waitResult.region != null) {
					waitResult.region.click();

					waitResult = utilAut.WaitFor(1, new String[] { "ImprimirPDF\\ConfirmSaveAs" });
					if (waitResult.region != null) {
						s.type(Key.LEFT);
						util.sleep(1);
						s.type(Key.ENTER);
						util.sleep(2);
					}
				}
			}
		}
	}
//---------------------------------------------------------------------------------------------------------

	public static String LeitorDeImagens(Logger log, String caminhoDoArquivo) {

		String result = "";
		File imageFile = new File(caminhoDoArquivo);

		Tesseract instance = Tesseract.getInstance();
		instance.setLanguage("eng");

		try {

			result = instance.doOCR(imageFile);

		} catch (TesseractException e) {
			System.out.println("Erro:" + e.getMessage());
			log.error(e.getMessage());
		}

		return result;
	}

//----------------------------------------------------------------------------------------------------------
	public static void closeOldSessions() {
		Screen s = new Screen();
		String pidMain = getPIDProcess().toString();
		Properties prop = new Properties();
		String watchPath;
		String processadoPath;
		InputStream input;

		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			watchPath = prop.getProperty("watchPath");
			processadoPath = prop.getProperty("processadoPath");

			Runtime.getRuntime().exec("taskkill /F /IM jucheck.exe /T");
			Runtime.getRuntime().exec("taskkill /F /IM jusched.exe /T");
			Runtime.getRuntime().exec("taskkill /F /IM chrome.exe /T");
			Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
			Runtime.getRuntime().exec("taskkill /F /IM GIA9.exe /T");// GiaRS
			Runtime.getRuntime().exec("taskkill /F /IM pAutoUpdater.exe /T");// GiaRS
			Runtime.getRuntime().exec("taskkill /F /IM DIEF2020.exe /T");// GiaMA
			Runtime.getRuntime().exec("taskkill /F /IM DIEF2019.exe /T");// GiaMA
			Runtime.getRuntime().exec("taskkill /F /IM DIEF-2018.exe /T");// GiaMA
			Runtime.getRuntime().exec("taskkill /F /IM Receitanet.exe /T");
			Runtime.getRuntime().exec("taskkill /F /IM ted.exe /T");// Ted
			Runtime.getRuntime().exec("taskkill /F /IM GIA.External.Windows.exe /T");// GiaSP

			Runtime runtime = Runtime.getRuntime();
			String cmds[] = { "cmd", "/c", "tasklist" };
			Process proc = runtime.exec(cmds);
			InputStream inputstream = proc.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
			BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
			String line;

			while ((line = bufferedreader.readLine()) != null) {
				if (line.contains(".exe")) {
					String[] parts = line.split(" ");
					for (int i = 1; i < parts.length; i++) {
						if (parts[i].matches("[a-zA-Z0-9]+")) {
							String name = parts[0];
							String pid = parts[i];
							if (name.contains("java") & !pid.equals(pidMain)) {
								Runtime.getRuntime().exec("taskkill /F /PID " + pid);
								break;
							}
						}
					}
				}
			}

			util.DeleteOneFile(watchPath, "cert.pfx");
			util.DeleteOneFile(watchPath, "Obrigacao.zip");
			util.DeleteOneFile(watchPath, "Erro.png");
			util.DeleteOneFile(processadoPath, "form.png");
			util.DeleteOneFile(processadoPath, "Certidao.png");
			util.DeleteOneFile(processadoPath, "log.txt");

			// Runtime.getRuntime().exec("certutil -user -p Imi@2018 -importPFX
			// C:\\Dropbox\\StandardIT\\TaxForce\\Certificados\\Extrafarma\\teste.pfx");
			// DeleteFiles("C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Roaming\\Microsoft\\SystemCertificates\\My\\Certificates",
			// false);
			if (!getComputerName().equals("DIMED-RPA4")) {
				// if (!getComputerName().equals("EC2AMAZ-ETHF8B8")) {
				s.type("m", Key.WIN);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Cannot query the tasklist for some reason.");
		}
	}

//-----------------------------------------------------------------------------------------------------
	public static Integer getPIDProcess() {
		int pid = 0;
		try {
			java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
			java.lang.reflect.Field jvm;
			jvm = runtime.getClass().getDeclaredField("jvm");
			jvm.setAccessible(true);
			sun.management.VMManagement mgmt = (sun.management.VMManagement) jvm.get(runtime);
			java.lang.reflect.Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
			pid_method.setAccessible(true);
			pid = (Integer) pid_method.invoke(mgmt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pid;
	}

//------------------------------------------------------------------------------------------------------
	public static Boolean ProcuraArquivoEmPasta(String sourceDir, String fileName) {
		Path source = Paths.get(sourceDir);
		try {
			if (source.toFile().isDirectory()) {
				File[] content = source.toFile().listFiles();
				for (File file : content) {
					if (file.getName().contains(fileName))
						return true;
				}
			}
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

//-----------------------------------------------------------------------------------
	public static Boolean getMaiorData(String data1, String data2) {
		Boolean retorno = false;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Calendar dataFormatada1 = new GregorianCalendar();
			Calendar dataFormatada2 = new GregorianCalendar();

			dataFormatada1.setTime(format.parse(data1));
			dataFormatada2.setTime(format.parse(data2));

			if (dataFormatada1.getTimeInMillis() > dataFormatada2.getTimeInMillis()) {
				retorno = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return retorno;
	}

}