package br.com.standardit.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class WSPostFile2Captcha {

	public static void postFile(String zipAdrress, String fileName, Boolean resultado) {
		try {

			byte[] array1 = new byte[10];
			String fileZip = encoder(zipAdrress);
			String[] numeroObrigacao = fileName.split("_");

			JSONObject json = new JSONObject();
//			if(resultado) {
//				json.put("Status", "3");
//			}else {
//				json.put("Status", "4");
//			}

			json.put("file", fileZip);

			sendHTTPData("http://2captcha.com/in.php", json);

		} catch (JSONException e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}

	}

	public static String sendHTTPData(String urlpath, JSONObject json) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(urlpath);
			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestProperty("key", "a5b676671c3d9cb4bee13db5b94aa42e");
			// connection.setRequestProperty("submit", "Upload and get the ID");
			connection.setRequestProperty("regsense", "1");
			connection.setRequestProperty("json", "1");

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
			streamWriter.write(json.toString());
			streamWriter.flush();
			StringBuilder stringBuilder = new StringBuilder();
			System.out.println(connection.getResponseCode());
			connection.getContentEncoding();
			System.out.println(connection.getResponseMessage());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(streamReader);
				String response = null;
				while ((response = bufferedReader.readLine()) != null) {
					stringBuilder.append(response + "\n");
					System.out.println(response);
				}
				bufferedReader.close();
				System.out.println(stringBuilder.toString());
				return stringBuilder.toString();
			} else {
				return null;
			}
		} catch (Exception exception) {
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

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

	private static String encodeFileToBase64Binary(String fileName) throws IOException {

		File file = new File(fileName);
		// Base64.getDecoder().decode(myResponse.getString("file"));
		byte[] encoded = Base64.getDecoder().decode(FileUtils.readFileToByteArray(file));
		return new String(encoded, StandardCharsets.UTF_8);
	}

	private static byte[] loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}

		is.close();
		return bytes;
	}
}
