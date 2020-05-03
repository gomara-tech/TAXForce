package br.com.standardit.ws.ecertidoes;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class WSgetProcess {

	public static JSONObject getEventInWS() {
		Properties prop = new Properties();
		InputStream input;

		JSONObject jsonRetorno = new JSONObject();
		try {

			input = new FileInputStream("config.properties");
			prop.load(input);

			String stringURL = prop.getProperty("urlGetEcertidoes");
			String usuarioWS = prop.getProperty("UsuarioWS");
			String senhaWS = prop.getProperty("SenhaWS");
			String servicoWS = prop.getProperty("ServicoWS");

			jsonRetorno.put("status", 404);
			jsonRetorno.put("cnpj", "");
			jsonRetorno.put("inscricao", "");
			jsonRetorno.put("estado", "");
			jsonRetorno.put("tipo", "");
			jsonRetorno.put("evento", "");
			//jsonRetorno.put("validade", "");

			URL url = new URL(stringURL);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("ServiceName", servicoWS);
			con.setRequestProperty("Usuario", usuarioWS);
			con.setRequestProperty("Senha", senhaWS);
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("GET");

			jsonRetorno.put("status", con.getResponseCode());

			System.out.println("Finding Event in E-Certidoes...[Return Status]:" + con.getResponseCode());
			if (con.getResponseCode() == 200) {
				InputStream in = new BufferedInputStream(con.getInputStream());
				String result = IOUtils.toString(in, "UTF-8");

				JSONObject myResponse = new JSONObject(result);

				jsonRetorno.put("cnpj", myResponse.getString("cnpj"));
				jsonRetorno.put("inscricao", myResponse.getString("inscricao"));
				jsonRetorno.put("estado", myResponse.getString("estado"));
				jsonRetorno.put("tipo", myResponse.getString("tipo"));
				jsonRetorno.put("evento", myResponse.getString("evento"));
				jsonRetorno.put("esfera", myResponse.getString("esfera"));

			}
			
			con.disconnect();
			// System.out.println("desconectado do WS");

		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonRetorno;

	}

}
