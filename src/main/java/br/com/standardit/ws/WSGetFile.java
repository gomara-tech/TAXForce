package br.com.standardit.ws;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class WSGetFile {
	public static String userObrigacao = "";
	public static String passObrigacao = "";
	public static String certificate = "";
	public static String certificateName = "";
	public static String certificatePass = "";

	public static String getFile(String folderAdrress) {
		Properties prop = new Properties();
		InputStream input;

		String fileName = null;
		try {

			input = new FileInputStream("config.properties");
			prop.load(input);

			String urlGet = prop.getProperty("WSUrlGet");
			String servico = prop.getProperty("WSServiceName");
			String usuario = prop.getProperty("WSUser");
			String senha = prop.getProperty("WSPass");

			URL url = new URL(urlGet);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("ServiceName", servico);
			con.setRequestProperty("Usuario", usuario);
			con.setRequestProperty("Senha", senha);
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("GET");

			System.out.println("Finding GET Event...[Return Status]:" + con.getResponseCode());
			if (con.getResponseCode() == 200) {

				InputStream in = new BufferedInputStream(con.getInputStream());
				String result = IOUtils.toString(in, "UTF-8");
				JSONObject myResponse = new JSONObject(result);

				certificate = myResponse.getString("certificate");
				certificateName = myResponse.getString("certificateName");
				certificatePass = myResponse.getString("certificatePass");
				userObrigacao = myResponse.getString("userObrigacao");
				passObrigacao = myResponse.getString("passObrigacao");

				// System.out.println(DatatypeConverter.printBase64Binary(str.getBytes()));
				// String str = myResponse.getString("certificatePassCrypt");
				// String str2 = DatatypeConverter.printBase64Binary(str.getBytes());
				// System.out.println(AES.decrypt2(str,"285D7768708F24165244494A2491039D"));

				util.log.info("Get Obrigacao File");
				byte[] b = Base64.getDecoder().decode(myResponse.getString("file"));

				fileName = myResponse.getString("fileName");
				File someFile = new File(folderAdrress + "\\" + fileName);
				FileOutputStream fos = new FileOutputStream(someFile);
				fos.write(b);
				fos.flush();
				fos.close();
				in.close();

				util.log.info("Get Certificate file");
				byte[] bb = Base64.getDecoder().decode(myResponse.getString("certificate"));

				fileName = myResponse.getString("certificateName");
				File someFile2 = new File(folderAdrress + "\\" + fileName);
				FileOutputStream fos2 = new FileOutputStream(someFile2);
				fos2.write(bb);
				fos2.flush();
				fos2.close();
				in.close();

				UnistallOldCertificates();
				InstallCertificate(myResponse.getString("certificatePass"), folderAdrress + "\\" + fileName);
				DeleleCertificateFile(folderAdrress, fileName);
			}
			con.disconnect();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return fileName;
	}

	// ----------------------------------------------------------------------------------------------------------------------
	public static void DeleleCertificateFile(String folderCertificateInstallAdrress, String certificateName) {
		try {
			util.log.info("[" + util.getMetod() + "] -  DeleleCertificateFile");

			util.DeleteOneFile(folderCertificateInstallAdrress, certificateName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------
	public static void InstallCertificate(String cetificatePass, String CertificateInstallAdrress) {
		try {
			Screen s = new Screen();
			// comando de teste certmgr
			// Runtime.getRuntime().exec("certutil -user -p Imi@2018 -importPFX
			util.log.info("[" + util.getMetod() + "] -  InstallCertificate");
			System.out.println("certutil -user -p " + cetificatePass + " -importPFX " + CertificateInstallAdrress);
			Runtime.getRuntime()
					.exec("certutil -user -p " + cetificatePass + " -importPFX " + CertificateInstallAdrress);

			CloseSecurityWarning("NovoPVA//CertificateAuthority");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------
	public static void UnistallOldCertificates() {
		try {
			util.DeleteFiles("C:\\Users\\" + System.getProperty("user.name")
					+ "\\AppData\\Roaming\\Microsoft\\SystemCertificates\\My\\Certificates", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------------------------------------
	public static void CloseSecurityWarning(String imageToClick) {
		Screen s = new Screen();

		UtilAutomation utilAut = new UtilAutomation(s);
		WaitResult waitResult;

		waitResult = utilAut.WaitFor(2, new String[] { imageToClick });
		if (waitResult.region != null) {
			s.type(Key.LEFT);
			util.sleep(1);
			s.type(Key.ENTER);
		}
	}
}
