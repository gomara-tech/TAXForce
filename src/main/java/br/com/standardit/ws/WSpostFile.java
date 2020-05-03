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
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class WSpostFile {

	public static void postFile(String zipAdrress,String fileName,Boolean resultado) {
		try {
			
			byte[] array1 = new byte[10];
			String fileZip = encoder(zipAdrress);
			String[] numeroObrigacao = fileName.split("_");
		        
			JSONObject json = new JSONObject();
			if(resultado) {
				json.put("Status", "3");
			}else {
				json.put("Status", "4");
			}

			json.put("file", fileZip);

			sendHTTPData(numeroObrigacao[0], json);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String sendHTTPData(String numObrigacao, JSONObject json) {
		Properties prop = new Properties();
		InputStream input;

		HttpURLConnection connection = null;
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);

			String urlPost = prop.getProperty("WSUrlPost");
			String servico = prop.getProperty("WSServiceName");
			String usuario = prop.getProperty("WSUser");
			String senha = prop.getProperty("WSPass");
			
			URL url = new URL(urlPost+numObrigacao);
			connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestProperty("ServiceName", servico);
			connection.setRequestProperty("Usuario", usuario);
			connection.setRequestProperty("Senha", senha);
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
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(streamReader);
				String response = null;
				while ((response = bufferedReader.readLine()) != null) {
					stringBuilder.append(response + "\n");
				}
				bufferedReader.close();

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
	
	
	
	private static String encodeFileToBase64Binary(String fileName)
	        throws IOException {

		 File file = new File(fileName);
		 //Base64.getDecoder().decode(myResponse.getString("file"));
		    byte[] encoded = Base64.getDecoder().decode(FileUtils.readFileToByteArray(file));
		    return new String(encoded, StandardCharsets.UTF_8);
	}
	private static byte[] loadFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);

	    long length = file.length();
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }
	    byte[] bytes = new byte[(int)length];

	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    is.close();
	    return bytes;
	}
}
