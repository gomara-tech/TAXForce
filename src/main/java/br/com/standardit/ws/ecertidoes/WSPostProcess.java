package br.com.standardit.ws.ecertidoes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.standardit.util;


public class WSPostProcess {
	public static boolean returnFile(String AdrressFile, String status, String validade,String evento) {

		try {
			byte[] array1 = new byte[10];
			String fileName = AdrressFile;
			//String teste = encodeFileToBase64Binary(fileName);
			String teste = util.encoder(fileName);
			
			JSONObject json = new JSONObject();
			json.put("status", status);
			json.put("validade", validade);
			json.put("File", teste);

			if (sendHTTPData(json, evento) == false)
				return false;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	//---------------------------------------------------------------------------------------------------------------------------	
		public static boolean sendHTTPData(JSONObject json, String evento) {
			HttpURLConnection connection = null;

			Properties prop = new Properties();
			InputStream input;

			try {
				input = new FileInputStream("config.properties");
				prop.load(input);

				String urlpath = prop.getProperty("urlPostEcertidoes");
				String usuarioWS = prop.getProperty("UsuarioWS");
				String senhaWS = prop.getProperty("SenhaWS");
				String servicoWS = prop.getProperty("ServicoWS");

				URL url = new URL(urlpath+"/"+evento);
				connection = (HttpURLConnection) url.openConnection();

				connection.setRequestProperty("ServiceName", servicoWS);
				connection.setRequestProperty("Usuario", usuarioWS);
				connection.setRequestProperty("Senha", senhaWS);
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
				streamWriter.write(json.toString());
				streamWriter.flush();
				StringBuilder stringBuilder = new StringBuilder();
				System.out.println(connection.getResponseMessage());
				System.out.println("Sending POST return file [Status Code]" + connection.getResponseCode());
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
					BufferedReader bufferedReader = new BufferedReader(streamReader);
					String response = null;
					while ((response = bufferedReader.readLine()) != null) {
						stringBuilder.append(response + "\n");
					}
					bufferedReader.close();

					connection.disconnect();
					// return stringBuilder.toString();
					return true;
				}
			} catch (Exception exception) {
				System.out.println(exception.getMessage());
				return false;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
			return false;
		}

}
