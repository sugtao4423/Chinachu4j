package Chinachu4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Chinachu4j {
	
	private String baseURL, username, password;

	public Chinachu4j(String baseURL, String username, String password){
		if(!baseURL.endsWith("/"))
			baseURL += "/";
		this.baseURL = baseURL + "api/";
		this.username = username;
		this.password = password;
	}
	
	public Program[] getChannelSchedule(String channelId) throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException {
		//番組表
		String channelSchedule = getPage(baseURL + "schedule/" + channelId + "/programs.json");
		JSONArray jprogram = new JSONArray(channelSchedule);
		
		Program[] programs = new Program[jprogram.length()];
		for(int i = 0; i < jprogram.length(); i++){
			String id, category, title, subTitle, fullTitle, detail, episode;
			long start, end, seconds;
			String[] flags;
			
			JSONObject obj = jprogram.getJSONObject(i);
			
			id = obj.getString("id");
			category = obj.getString("category");
			title = obj.getString("title");
			subTitle = obj.getString("subTitle");
			fullTitle = obj.getString("fullTitle");
			detail = obj.getString("detail");
			episode = obj.getString("episode");
			start = obj.getLong("start");
			end = obj.getLong("end");
			seconds = obj.getLong("seconds");
			
			JSONArray flagArray = obj.getJSONArray("flags");
			flags = new String[flagArray.length()];
			for(int ii = 0; ii < flagArray.length(); ii++)
				flags[ii] = flagArray.getString(ii);
			
			JSONObject ch = obj.getJSONObject("channel");
			Channel channel = new Channel(ch.getInt("n"), ch.getString("type"), ch.getInt("channel"),
						ch.getString("name"), ch.getString("id"), ch.getInt("sid"));
			
			programs[i] = new Program(id, category, title, subTitle, fullTitle, detail, episode, start, end, seconds, flags, channel);
		}
		return programs;
	}
	
	public String[] getChannelList() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		//現在放送されている番組から局名のみを抽出
		//「局名,局id」形式
		//for example "ＮＨＫ総合１・東京,GR_1024"
		JSONArray channelJson = new JSONArray(getPage(baseURL + "schedule/broadcasting.json"));
		String[] channelList = new String[channelJson.length()];
		for(int i = 0; i < channelJson.length(); i++){
			channelList[i] = channelJson.getJSONObject(i).getJSONObject("channel").getString("name")
					+ "," +
					channelJson.getJSONObject(i).getJSONObject("channel").getString("id");
		}
		return channelList;
	}

	public String getPage(String url) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		boolean isSSL = url.startsWith("https://");

		SSLContext sslcontext = null;
		if (isSSL) {
			TrustManager[] tm = { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}
			} };
			sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, tm, null);

			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		}

		URL connectUrl = new URL(url);
		InputStream is;
		HttpURLConnection http = null;
		HttpsURLConnection https = null;
		if (isSSL) {
			https = (HttpsURLConnection) connectUrl.openConnection();
			Authenticator.setDefault(new BasicAuthenticator(username, password));
			https.setRequestMethod("GET");
			https.setSSLSocketFactory(sslcontext.getSocketFactory());
			https.connect();
			is = https.getInputStream();
		} else {
			http = (HttpURLConnection) connectUrl.openConnection();
			Authenticator.setDefault(new BasicAuthenticator(username, password));
			http.setRequestMethod("GET");
			http.connect();
			is = http.getInputStream();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		if (isSSL)
			https.disconnect();
		else
			http.disconnect();
		is.close();
		return sb.toString();
	}
}