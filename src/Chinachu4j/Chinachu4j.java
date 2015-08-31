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
	
	//各チャンネルの番組表
	public Program[] getChannelSchedule(String channelId) throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException {
		String channelSchedule = getPage(baseURL + "schedule/" + channelId + "/programs.json");
		JSONArray jprogram = new JSONArray(channelSchedule);
		Program[] programs = getPrograms(jprogram);
		return programs;
	}
	
	//現在放送されている番組から局名のみを抽出
	//「局名,局id」形式
	//for example "ＮＨＫ総合１・東京,GR_1024"
	public String[] getChannelList() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		JSONArray channelJson = new JSONArray(getPage(baseURL + "schedule/broadcasting.json"));
		String[] channelList = new String[channelJson.length()];
		for(int i = 0; i < channelJson.length(); i++){
			channelList[i] = channelJson.getJSONObject(i).getJSONObject("channel").getString("name")
					+ "," +
					channelJson.getJSONObject(i).getJSONObject("channel").getString("id");
		}
		return channelList;
	}
	
	//予約済の取得
	public Program[] getReserves() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		String reserves = getPage(baseURL + "reserves.json");
		JSONArray jreserves = new JSONArray(reserves);
		Program[] programs = getPrograms(jreserves);
		return programs;
	}
	
	//録画中の取得
	public Program[] getRecording() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		String recording = getPage(baseURL + "recording.json");
		JSONArray jrecording = new JSONArray(recording);
		Program[] programs = getPrograms(jrecording);
		return programs;
	}
	
	//録画中のキャプチャを取得
	public String getRecordingImage(String id, String size) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		if(id == null)
			return null;
		if(size == null)
			size = "320x180";
		
		return getPage(baseURL + "recording/" + id + "/preview.txt" + "?size=" + size);
	}
	
	//録画済みの取得
	public Program[] getRecorded() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		String recorded = getPage(baseURL + "recorded.json");
		JSONArray jrecorded = new JSONArray(recorded);
		Program[] programs = getPrograms(jrecorded);
		return programs;
	}
	
	//録画済みのキャプチャを取得
	public String getRecordedImage(String id, int pos, String size) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		if(id == null)
			return null;
		if(pos == -1)
			pos = 7;
		if(size == null)
			size = "320x180";
		
		return getPage(baseURL + "recorded/" + id + "/preview.txt" + "?pos=" + pos + "&size=" + size);
	}
	
	private Program[] getPrograms(JSONArray array) throws JSONException{
		Program[] programs = new Program[array.length()];
		
		for(int i = 0; i < array.length(); i++){
			String id, category, title, subTitle, fullTitle, detail, episode;
			long start, end;
			int seconds;
			String[] flags;
			Channel channel;
			
			JSONObject obj = array.getJSONObject(i);
			
			id = obj.getString("id");
			category = obj.getString("category");
			title = obj.getString("title");
			subTitle = obj.getString("subTitle");
			fullTitle = obj.getString("fullTitle");
			detail = obj.getString("detail");
			episode = obj.getString("episode");
			start = obj.getLong("start");
			end = obj.getLong("end");
			seconds = obj.getInt("seconds");
			
			JSONArray flagArray = obj.getJSONArray("flags");
			flags = new String[flagArray.length()];
			for(int ii = 0; ii < flagArray.length(); ii++)
				flags[ii] = flagArray.getString(ii);
			
			JSONObject ch = obj.getJSONObject("channel");
			channel = new Channel(ch.getInt("n"), ch.getString("type"), ch.getInt("channel"),
						ch.getString("name"), ch.getString("id"), ch.getInt("sid"));
			
			programs[i] = new Program(id, category, title, subTitle, fullTitle, detail, episode, start, end, seconds, flags, channel);
		}
		return programs;
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