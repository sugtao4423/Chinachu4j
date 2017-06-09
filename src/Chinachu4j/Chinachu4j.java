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
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Chinachu4j{

	private String baseURL, username, password;

	public Chinachu4j(String baseURL, String username, String password){
		if(!baseURL.endsWith("/"))
			baseURL += "/";
		this.baseURL = baseURL + "api/";
		this.username = username;
		this.password = password;
	}

	/*
	 * +-+-+-+
	 * |G|E|T|
	 * +-+-+-+
	 */

	// 各チャンネルの番組表
	public Program[] getChannelSchedule(String channelId) throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		String channelSchedule = getServer(baseURL + "schedule/" + channelId + "/programs.json");
		JSONArray jprogram = new JSONArray(channelSchedule);
		Program[] programs = new Program[jprogram.length()];
		for(int i = 0; i < jprogram.length(); i++)
			programs[i] = getProgram(jprogram.getJSONObject(i));
		return programs;
	}

	// 全チャンネルの番組表
	public Program[] getAllSchedule() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		String allSchedule = getServer(baseURL + "schedule/programs.json");
		JSONArray jAll = new JSONArray(allSchedule);
		Program[] allPrograms = new Program[jAll.length()];
		for(int i = 0; i < jAll.length(); i++)
			allPrograms[i] = getProgram(jAll.getJSONObject(i));
		return allPrograms;
	}

	// 全チャンネルから番組検索
	public Program[] searchProgram(String query) throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		Program[] allSchedule = getAllSchedule();
		ArrayList<Program> array = new ArrayList<Program>();
		for(Program p : allSchedule){
			if(p.getFullTitle().matches(".*" + query + ".*"))
				array.add(p);
		}
		return (Program[])array.toArray(new Program[0]);
	}

	// 現在放送されている番組から局名のみを抽出
	// 「局名,局id」形式
	// for example "ＮＨＫ総合１・東京,GR_1024"
	public String[] getChannelList() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		JSONArray channelJson = new JSONArray(getServer(baseURL + "schedule/broadcasting.json"));
		String[] channelList = new String[channelJson.length()];
		for(int i = 0; i < channelJson.length(); i++){
			channelList[i] = channelJson.getJSONObject(i).getJSONObject("channel").getString("name") + ","
					+ channelJson.getJSONObject(i).getJSONObject("channel").getString("id");
		}
		return channelList;
	}

	// 予約済の取得
	public Reserve[] getReserves() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		String reserves = getServer(baseURL + "reserves.json");
		JSONArray jreserves = new JSONArray(reserves);
		Reserve[] reserve = new Reserve[jreserves.length()];
		for(int i = 0; i < jreserves.length(); i++)
			reserve[i] = getReserve(jreserves.getJSONObject(i));
		return reserve;
	}

	// 録画中の取得
	public Program[] getRecording() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		String recording = getServer(baseURL + "recording.json");
		JSONArray jrecording = new JSONArray(recording);
		Program[] programs = new Program[jrecording.length()];
		for(int i = 0; i < jrecording.length(); i++)
			programs[i] = getProgram(jrecording.getJSONObject(i));
		return programs;
	}

	// 録画中のキャプチャを取得
	public String getRecordingImage(String id, String size) throws KeyManagementException, NoSuchAlgorithmException, IOException{
		if(id == null)
			return null;
		if(size == null)
			size = "320x180";

		return getServer(baseURL + "recording/" + id + "/preview.txt" + "?size=" + size);
	}

	// 録画済みの取得
	public Recorded[] getRecorded() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		String recorded = getServer(baseURL + "recorded.json");
		JSONArray jrecorded = new JSONArray(recorded);
		Recorded[] _recorded = new Recorded[jrecorded.length()];
		for(int i = 0; i < jrecorded.length(); i++)
			_recorded[i] = getRecorded(jrecorded.getJSONObject(i));
		return _recorded;
	}

	// 録画済みのキャプチャを取得
	public String getRecordedImage(String id, int pos, String size) throws KeyManagementException, NoSuchAlgorithmException, IOException{
		if(id == null)
			return null;
		if(pos == -1)
			pos = 7;
		if(size == null)
			size = "320x180";

		return getServer(baseURL + "recorded/" + id + "/preview.txt" + "?pos=" + pos + "&size=" + size);
	}

	// ライブストリーミング再生（エンコなし）
	public String getNonEncLiveMovieURL(String channelId){
		return getIncludeUserPass() + "channel/" + channelId + "/watch.m2ts?f=mpegts&c:v=copy&c:a=copy";
	}

	// ライブストリーミング再生（エンコ有り）
	public String getEncLiveMovieURL(String channelId, String type, String[] params){
		String base = getIncludeUserPass() + "channel/" + channelId + "/watch." + type + "?";
		return getIncludeEncParams(base, params);
	}

	// 録画中のストリーミング再生（エンコなし）
	public String getNonEncRecordingMovieURL(String programId){
		return getIncludeUserPass() + "recording/" + programId + "/watch.m2ts?f=mpegts&c:v=copy&c:a=copy";
	}

	// 録画中のストリーミング再生（エンコ有り）
	// type: m2ts, f4v, flv, webm, asf
	public String getEncRecordingMovieURL(String programId, String type, String[] params){
		String base = getIncludeUserPass() + "recording/" + programId + "/watch." + type + "?";
		return getIncludeEncParams(base, params);
	}

	// 録画済みのストリーミング再生（エンコなし）
	public String getNonEncRecordedMovieURL(String programId){
		return getIncludeUserPass() + "recorded/" + programId + "/watch.m2ts?f=mpegts&c:v=copy&c:a=copy";
	}

	// 録画済みのストリーミング再生（エンコ有り）
	// type: m2ts, f4v, flv, webm, asf
	public String getEncRecordedMovieURL(String programId, String type, String[] params){
		String base = getIncludeUserPass() + "recorded/" + programId + "/watch." + type + "?";
		return getIncludeEncParams(base, params);
	}

	// ルールの取得
	public Rule[] getRules() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
		String rule = getServer(baseURL + "rules.json");
		JSONArray jrule = new JSONArray(rule);
		Rule[] rules = new Rule[jrule.length()];
		for(int i = 0; i < jrule.length(); i++)
			rules[i] = getRule(jrule.getJSONObject(i));
		return rules;
	}

	// UsernameとPasswordを含んだbaseURLを返却
	private String getIncludeUserPass(){
		String includeURL = null;
		if(baseURL.startsWith("https://")) {
			includeURL = baseURL.substring(8);
			includeURL = "https://" + username + ":" + password + "@" + includeURL;
		}else if(baseURL.startsWith("http://")) {
			includeURL = baseURL.substring(7);
			includeURL = "http://" + username + ":" + password + "@" + includeURL;
		}
		return includeURL;
	}

	// エンコ有りストリーミングURLにパラメータを付与
	// それぞれnull値が来た場合はURLに含めない
	// [0]: コンテナフォーマット
	// [1]: 動画コーデック
	// [2]: 音声コーデック
	// [3]: 動画ビットレート
	// [4]: 音声ビットレート
	// [5]: 映像サイズ(例:1280x720)
	// [6]: 映像フレームレート(例:24)
	private String getIncludeEncParams(String base, String[] params){
		if(params[0] != null)
			base += "f=" + params[0] + "&";
		if(params[1] != null)
			base += "c:v=" + params[1] + "&";
		if(params[2] != null)
			base += "c:a=" + params[2] + "&";
		if(params[3] != null)
			base += "b:v=" + params[3] + "&";
		if(params[4] != null)
			base += "b:a=" + params[4] + "&";
		if(params[5] != null)
			base += "s=" + params[5] + "&";
		if(params[6] != null)
			base += "r=" + params[6] + "&";

		return base.substring(0, base.length() - 1);
	}

	// JSONObjectからProgramを返却
	private Program getProgram(JSONObject obj) throws JSONException{
		String id, category, title, subTitle, fullTitle, detail, episode;
		long start, end;
		int seconds;
		String[] flags;
		Channel channel;

		id = obj.getString("id");
		category = obj.has("category") ? obj.getString("category") : "";
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
		channel = new Channel(ch.getInt("n"), ch.getString("type"), ch.getString("channel"), ch.getString("name"),
				ch.getString("id"), ch.getInt("sid"));

		Program program = new Program(id, category, title, subTitle, fullTitle, detail, episode, start, end, seconds,
				flags, channel);
		return program;
	}

	// JSONObjectからReserveを返却
	private Reserve getReserve(JSONObject obj) throws JSONException{
		Program program = getProgram(obj);

		boolean isManualReserved = obj.isNull("isManualReserved") ? false : obj.getBoolean("isManualReserved");
		boolean isConflict = obj.isNull("isConflict") ? false : obj.getBoolean("isConflict");
		String recordedFormat = obj.isNull("recordedFormat") ? null : obj.getString("recordedFormat");
		boolean isSkip = obj.isNull("isSkip") ? false : obj.getBoolean("isSkip");

		Reserve reserve = new Reserve(program, isManualReserved, isConflict, recordedFormat, isSkip);
		return reserve;
	}

	// JSONObjectからRecordedを返却
	private Recorded getRecorded(JSONObject obj) throws JSONException{
		Program program = getProgram(obj);
		Tuner tuner = getTuner(obj.getJSONObject("tuner"));

		boolean isManualReserved = obj.isNull("isManualReserved") ? false : obj.getBoolean("isManualReserved");
		boolean isConflict = obj.isNull("isConflict") ? false : obj.getBoolean("isConflict");
		String recordedFormat = obj.isNull("recordedFormat") ? null : obj.getString("recordedFormat");

		boolean isSigTerm = obj.isNull("isSigTerm") ? false : obj.getBoolean("isSigTerm");
		String recorded = obj.getString("recorded");
		String command = obj.getString("command");

		Recorded _recorded = new Recorded(program, isManualReserved, isConflict, recordedFormat, isSigTerm, tuner, recorded, command);
		return _recorded;
	}

	// JSONObjectからTunerを返却（Recordedの取得に使用）
	private Tuner getTuner(JSONObject obj) throws JSONException{
		String name = obj.getString("name");
		boolean isScrambling = obj.getBoolean("isScrambling");
		JSONArray typesArray = obj.isNull("types") ? new JSONArray() : obj.getJSONArray("types");
		String[] types = new String[typesArray.length()];
		for(int i = 0; i < typesArray.length(); i++)
			types[i] = typesArray.getString(i);
		String command = obj.getString("command");
		int n = obj.isNull("n") ? -1 : obj.getInt("n");

		Tuner tuner = new Tuner(name, isScrambling, types, command, n);
		return tuner;
	}

	// JSONObjectからRuleを返却
	private Rule getRule(JSONObject obj) throws JSONException{
		String[] types, categories, channels, ignore_channels, reserve_flags, ignore_flags;
		int start, end, min, max;
		String[] reserve_titles, ignore_titles, reserve_descriptions, ignore_descriptions;
		String recorded_format;
		boolean isDisabled;

		boolean exists_types = obj.isNull("types");
		boolean exists_categories = obj.isNull("categories");
		boolean exists_channels = obj.isNull("channels");
		boolean exists_ignore_channels = obj.isNull("ignore_channels");
		boolean exists_reserve_flags = obj.isNull("reserve_flags");
		boolean exists_ignore_flags = obj.isNull("ignore_flags");
		boolean exists_hour = obj.isNull("hour");
		boolean exists_duration = obj.isNull("duration");
		boolean exists_reserve_titles = obj.isNull("reserve_titles");
		boolean exists_ignore_titles = obj.isNull("ignore_titles");
		boolean exists_reserve_descriptions = obj.isNull("reserve_descriptions");
		boolean exists_ignore_descriptions = obj.isNull("ignore_descriptions");

		recorded_format = obj.isNull("recorded_format") ? null : obj.getString("recorded_format");
		isDisabled = obj.isNull("isDisabled") ? false : obj.getBoolean("isDisabled");

		if(exists_types)
			types = new String[0];
		else{
			JSONArray array = obj.getJSONArray("types");
			types = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				types[i] = array.getString(i);
		}

		if(exists_categories)
			categories = new String[0];
		else{
			JSONArray array = obj.getJSONArray("categories");
			categories = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				categories[i] = array.getString(i);
		}

		if(exists_channels)
			channels = new String[0];
		else{
			JSONArray array = obj.getJSONArray("channels");
			channels = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				channels[i] = array.getString(i);
		}

		if(exists_ignore_channels)
			ignore_channels = new String[0];
		else{
			JSONArray array = obj.getJSONArray("ignore_channels");
			ignore_channels = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				ignore_channels[i] = array.getString(i);
		}

		if(exists_reserve_flags)
			reserve_flags = new String[0];
		else{
			JSONArray array = obj.getJSONArray("reserve_flags");
			reserve_flags = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				reserve_flags[i] = array.getString(i);
		}

		if(exists_ignore_flags)
			ignore_flags = new String[0];
		else{
			JSONArray array = obj.getJSONArray("ignore_flags");
			ignore_flags = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				ignore_flags[i] = array.getString(i);
		}

		if(exists_hour){
			start = -1;
			end = -1;
		}else{
			JSONObject o = obj.getJSONObject("hour");
			start = o.isNull("start") ? -1 : o.getInt("start");
			end = o.isNull("end") ? -1 : o.getInt("end");
		}

		if(exists_duration){
			min = -1;
			max = -1;
		}else{
			JSONObject o = obj.getJSONObject("duration");
			min = o.isNull("min") ? -1 : o.getInt("min");
			max = o.isNull("max") ? -1 : o.getInt("max");
		}

		if(exists_reserve_titles)
			reserve_titles = new String[0];
		else{
			JSONArray array = obj.getJSONArray("reserve_titles");
			reserve_titles = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				reserve_titles[i] = array.getString(i);
		}

		if(exists_ignore_titles)
			ignore_titles = new String[0];
		else{
			JSONArray array = obj.getJSONArray("ignore_titles");
			ignore_titles = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				ignore_titles[i] = array.getString(i);
		}

		if(exists_reserve_descriptions)
			reserve_descriptions = new String[0];
		else{
			JSONArray array = obj.getJSONArray("reserve_descriptions");
			reserve_descriptions = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				reserve_descriptions[i] = array.getString(i);
		}

		if(exists_ignore_descriptions)
			ignore_descriptions = new String[0];
		else{
			JSONArray array = obj.getJSONArray("ignore_descriptions");
			ignore_descriptions = new String[array.length()];
			for(int i = 0; i < array.length(); i++)
				ignore_descriptions[i] = array.getString(i);
		}

		Rule rule = new Rule(types, categories, channels, ignore_channels, reserve_flags, ignore_flags, start, end, min, max,
				reserve_titles, ignore_titles, reserve_descriptions, ignore_descriptions, recorded_format, isDisabled);
		return rule;
	}

	/*
	 * +-+-+-+
	 * |P|U|T|
	 * +-+-+-+
	 */

	// 予約する 引数は予約する番組ID
	public ChinachuResponse putReserve(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
		return putDelServer(baseURL + "program/" + programId + ".json", 0);
	}

	// 自動予約された番組をスキップ
	public ChinachuResponse reserveSkip(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
		return putDelServer(baseURL + "reserves/" + programId + "/skip.json", 0);
	}

	// スキップの取り消し
	public ChinachuResponse reserveUnskip(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
		return putDelServer(baseURL + "reserves/" + programId + "/unskip.json", 0);
	}

	// 録画済みリストのクリーンアップ
	public ChinachuResponse recordedCleanUp() throws KeyManagementException, NoSuchAlgorithmException, IOException{
		return putDelServer(baseURL + "recorded.json", 0);
	}

	/*
	 * +-+-+-+-+-+-+
	 * |D|E|L|E|T|E|
	 * +-+-+-+-+-+-+
	 */

	// 予約削除 引数は予約を削除する番組ID
	public ChinachuResponse delReserve(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
		return putDelServer(baseURL + "reserves/" + programId + ".json", 1);
	}

	// ルール削除 引数は削除するルールの番号（0開始）
	public ChinachuResponse delRule(String ruleNum) throws KeyManagementException, NoSuchAlgorithmException, IOException{
		return putDelServer(baseURL + "rules/" + ruleNum + ".json", 1);
	}

	// 録画済みファイルの削除 引数は削除する録画済みファイルの番組ID
	public ChinachuResponse delRecordedFile(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
		return putDelServer(baseURL + "recorded/" + programId + "/file.json", 1);
	}

	// GET URL
	public String getServer(String url) throws NoSuchAlgorithmException, KeyManagementException, IOException{
		boolean isSSL = url.startsWith("https://");

		SSLContext sslcontext = null;
		if(isSSL) {
			TrustManager[] tm = {new X509TrustManager(){
				@Override
				public X509Certificate[] getAcceptedIssuers(){
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException{
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException{
				}
			}};
			sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, tm, null);

			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
				@Override
				public boolean verify(String hostname, SSLSession session){
					return true;
				}
			});
		}

		URL connectUrl = new URL(url);
		InputStream is;
		HttpURLConnection http = null;
		HttpsURLConnection https = null;
		Authenticator.setDefault(new BasicAuthenticator(username, password));
		if(isSSL) {
			https = (HttpsURLConnection)connectUrl.openConnection();
			https.setRequestMethod("GET");
			https.setSSLSocketFactory(sslcontext.getSocketFactory());
			https.connect();
			is = https.getInputStream();
		}else{
			http = (HttpURLConnection)connectUrl.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			is = http.getInputStream();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"));
		String line;
		StringBuilder sb = new StringBuilder();
		while((line = reader.readLine()) != null){
			sb.append(line);
		}
		reader.close();
		if(isSSL)
			https.disconnect();
		else
			http.disconnect();
		is.close();
		return sb.toString();
	}

	// PUT or DELETE
	// 0: PUT 1: DELETE
	public ChinachuResponse putDelServer(String url, int type) throws NoSuchAlgorithmException, KeyManagementException, IOException{
		boolean isSSL = url.startsWith("https://");

		SSLContext sslcontext = null;
		if(isSSL) {
			TrustManager[] tm = {new X509TrustManager(){
				@Override
				public X509Certificate[] getAcceptedIssuers(){
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException{
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException{
				}
			}};
			sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, tm, null);

			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
				@Override
				public boolean verify(String hostname, SSLSession session){
					return true;
				}
			});
		}

		URL connectUrl = new URL(url);
		HttpURLConnection http = null;
		HttpsURLConnection https = null;
		Authenticator.setDefault(new BasicAuthenticator(username, password));
		if(isSSL) {
			https = (HttpsURLConnection)connectUrl.openConnection();
			switch(type){
			case 0:
				https.setRequestMethod("PUT");
				break;
			case 1:
				https.setRequestMethod("DELETE");
				break;
			}
			https.setSSLSocketFactory(sslcontext.getSocketFactory());
			https.connect();
		}else{
			http = (HttpURLConnection)connectUrl.openConnection();
			switch(type){
			case 0:
				http.setRequestMethod("PUT");
				break;
			case 1:
				http.setRequestMethod("DELETE");
				break;
			}
			http.connect();
		}

		ChinachuResponse response;
		if(isSSL){
			response = new ChinachuResponse(https.getResponseCode());
			https.disconnect();
		}else{
			response = new ChinachuResponse(http.getResponseCode());
			http.disconnect();
		}
		return response;
	}
}