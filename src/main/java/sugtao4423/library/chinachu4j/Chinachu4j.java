package sugtao4423.library.chinachu4j;

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
        this.baseURL = baseURL + "api";
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
        String url = baseURL + "/schedule/" + channelId + "/programs.json";
        JSONArray json = new JSONArray(getServer(url));
        Program[] programs = new Program[json.length()];
        for(int i = 0; i < json.length(); i++)
            programs[i] = getProgram(json.getJSONObject(i));
        return programs;
    }

    // 全チャンネルの番組表
    public Program[] getAllSchedule() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
        String url = baseURL + "/schedule/programs.json";
        JSONArray json = new JSONArray(getServer(url));
        Program[] allPrograms = new Program[json.length()];
        for(int i = 0; i < json.length(); i++)
            allPrograms[i] = getProgram(json.getJSONObject(i));
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
        String url = baseURL + "/schedule/broadcasting.json";
        JSONArray json = new JSONArray(getServer(url));
        String[] channelList = new String[json.length()];
        for(int i = 0; i < json.length(); i++){
            channelList[i] = json.getJSONObject(i).getJSONObject("channel").getString("name") + ","
                    + json.getJSONObject(i).getJSONObject("channel").getString("id");
        }
        return channelList;
    }

    // 予約済の取得
    public Reserve[] getReserves() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
        String url = baseURL + "/reserves.json";
        JSONArray json = new JSONArray(getServer(url));
        Reserve[] reserves = new Reserve[json.length()];
        for(int i = 0; i < json.length(); i++)
            reserves[i] = getReserve(json.getJSONObject(i));
        return reserves;
    }

    // 録画中の取得
    public Program[] getRecording() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
        String url = baseURL + "/recording.json";
        JSONArray json = new JSONArray(getServer(url));
        Program[] programs = new Program[json.length()];
        for(int i = 0; i < json.length(); i++)
            programs[i] = getProgram(json.getJSONObject(i));
        return programs;
    }

    // 録画中のキャプチャを取得
    public String getRecordingImage(String id, String size) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        if(isEmpty(id))
            return null;
        if(isEmpty(size))
            size = "320x180";

        String url = baseURL + "/recording/" + id + "/preview.txt" + "?size=" + size;
        return getServer(url);
    }

    // 録画済みの取得
    public Recorded[] getRecorded() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
        String url = baseURL + "/recorded.json";
        JSONArray json = new JSONArray(getServer(url));
        Recorded[] recordeds = new Recorded[json.length()];
        for(int i = 0; i < json.length(); i++)
            recordeds[i] = getRecorded(json.getJSONObject(i));
        return recordeds;
    }

    // 録画済みのキャプチャを取得
    public String getRecordedImage(String id, int pos, String size) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        if(isEmpty(id))
            return null;
        if(pos <= 0)
            pos = 7;
        if(isEmpty(size))
            size = "320x180";

        String url = baseURL + "/recorded/" + id + "/preview.txt" + "?pos=" + pos + "&size=" + size;
        return getServer(url);
    }

    // ライブストリーミング再生（エンコなし）
    public String getNonEncLiveMovieURL(String channelId){
        return getIncludeUserPass() + "/channel/" + channelId + "/watch.m2ts?f=mpegts&c:v=copy&c:a=copy";
    }

    // ライブストリーミング再生（エンコ有り）
    public String getEncLiveMovieURL(String channelId, String type, String[] params){
        String base = getIncludeUserPass() + "/channel/" + channelId + "/watch." + type + "?";
        return getIncludeEncParams(base, params);
    }

    // 録画中のストリーミング再生（エンコなし）
    public String getNonEncRecordingMovieURL(String programId){
        return getIncludeUserPass() + "/recording/" + programId + "/watch.m2ts?f=mpegts&c:v=copy&c:a=copy";
    }

    // 録画中のストリーミング再生（エンコ有り）
    // type: mp4, m2ts, webm
    public String getEncRecordingMovieURL(String programId, String type, String[] params){
        String base = getIncludeUserPass() + "/recording/" + programId + "/watch." + type + "?";
        return getIncludeEncParams(base, params);
    }

    // 録画済みのストリーミング再生（エンコなし）
    public String getNonEncRecordedMovieURL(String programId){
        return getIncludeUserPass() + "/recorded/" + programId + "/watch.m2ts?f=mpegts&c:v=copy&c:a=copy";
    }

    // 録画済みのストリーミング再生（エンコ有り）
    // type: mp4, m2ts, webm
    public String getEncRecordedMovieURL(String programId, String type, String[] params){
        String base = getIncludeUserPass() + "/recorded/" + programId + "/watch." + type + "?";
        return getIncludeEncParams(base, params);
    }

    // ルールの取得
    public Rule[] getRules() throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException{
        String url = baseURL + "/rules.json";
        JSONArray json = new JSONArray(getServer(url));
        Rule[] rules = new Rule[json.length()];
        for(int i = 0; i < json.length(); i++)
            rules[i] = getRule(json.getJSONObject(i));
        return rules;
    }

    // UsernameとPasswordを含んだbaseURLを返却
    private String getIncludeUserPass(){
        String includeURL = null;
        if(baseURL.startsWith("https://")){
            includeURL = baseURL.substring(8);
            includeURL = "https://" + username + ":" + password + "@" + includeURL;
        }else if(baseURL.startsWith("http://")){
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
        if(!isEmpty(params[0]))
            base += "f=" + params[0] + "&";
        if(!isEmpty(params[1]))
            base += "c:v=" + params[1] + "&";
        if(!isEmpty(params[2]))
            base += "c:a=" + params[2] + "&";
        if(!isEmpty(params[3]))
            base += "b:v=" + params[3] + "&";
        if(!isEmpty(params[4]))
            base += "b:a=" + params[4] + "&";
        if(!isEmpty(params[5]))
            base += "s=" + params[5] + "&";
        if(!isEmpty(params[6]))
            base += "r=" + params[6] + "&";

        return base.substring(0, base.length() - 1);
    }

    private boolean isEmpty(String str){
        return (str == null || str.isEmpty() || str.equalsIgnoreCase("null"));
    }

    // JSONObjectからProgramを返却
    private Program getProgram(JSONObject obj) throws JSONException{
        String id = obj.getString("id");
        String category = obj.has("category") ? obj.getString("category") : "";
        String title = obj.getString("title");
        String subTitle = obj.getString("subTitle");
        String fullTitle = obj.getString("fullTitle");
        String detail = obj.getString("detail");
        int episode = obj.isNull("episode") ? -1 : obj.getInt("episode");
        long start = obj.getLong("start");
        long end = obj.getLong("end");
        int seconds = obj.getInt("seconds");

        JSONArray flagArray = obj.getJSONArray("flags");
        String[] flags = new String[flagArray.length()];
        for(int ii = 0; ii < flagArray.length(); ii++)
            flags[ii] = flagArray.getString(ii);

        JSONObject ch = obj.getJSONObject("channel");
        Channel channel = new Channel(ch.getInt("n"), ch.getString("type"), ch.getString("channel"), ch.getString("name"),
                ch.getString("id"), ch.getInt("sid"));

        return new Program(id, category, title, subTitle, fullTitle, detail, episode, start, end, seconds, flags, channel);
    }

    // JSONObjectからReserveを返却
    private Reserve getReserve(JSONObject obj) throws JSONException{
        Program program = getProgram(obj);

        boolean isManualReserved = obj.isNull("isManualReserved") ? false : obj.getBoolean("isManualReserved");
        boolean isConflict = obj.isNull("isConflict") ? false : obj.getBoolean("isConflict");
        String recordedFormat = obj.isNull("recordedFormat") ? null : obj.getString("recordedFormat");
        boolean isSkip = obj.isNull("isSkip") ? false : obj.getBoolean("isSkip");

        return new Reserve(program, isManualReserved, isConflict, recordedFormat, isSkip);
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

        return new Recorded(program, isManualReserved, isConflict, recordedFormat, isSigTerm, tuner, recorded, command);
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

        return new Tuner(name, isScrambling, types, command, n);
    }

    // JSONObjectからRuleを返却
    private Rule getRule(JSONObject obj) throws JSONException{
        String[] types = new String[0];
        if(!obj.isNull("types")){
            JSONArray json = obj.getJSONArray("types");
            types = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                types[i] = json.getString(i);
        }

        String[] categories = new String[0];
        if(!obj.isNull("categories")){
            JSONArray json = obj.getJSONArray("categories");
            categories = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                categories[i] = json.getString(i);
        }

        String[] channels = new String[0];
        if(!obj.isNull("channels")){
            JSONArray json = obj.getJSONArray("channels");
            channels = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                channels[i] = json.getString(i);
        }

        String[] ignoreChannels = new String[0];
        if(!obj.isNull("ignore_channels")){
            JSONArray json = obj.getJSONArray("ignore_channels");
            ignoreChannels = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                ignoreChannels[i] = json.getString(i);
        }

        String[] reserveFlags = new String[0];
        if(!obj.isNull("reserve_flags")){
            JSONArray json = obj.getJSONArray("reserve_flags");
            reserveFlags = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                reserveFlags[i] = json.getString(i);
        }

        String[] ignoreFlags = new String[0];
        if(!obj.isNull("ignore_flags")){
            JSONArray json = obj.getJSONArray("ignore_flags");
            ignoreFlags = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                ignoreFlags[i] = json.getString(i);
        }

        int start = -1;
        int end = -1;
        if(!obj.isNull("hour")){
            JSONObject o = obj.getJSONObject("hour");
            start = o.isNull("start") ? start : o.getInt("start");
            end = o.isNull("end") ? end : o.getInt("end");
        }

        int min = -1;
        int max = -1;
        if(!obj.isNull("duration")){
            JSONObject o = obj.getJSONObject("duration");
            min = o.isNull("min") ? min : o.getInt("min");
            max = o.isNull("max") ? max : o.getInt("max");
        }

        String[] reserveTitles = new String[0];
        if(!obj.isNull("reserve_titles")){
            JSONArray json = obj.getJSONArray("reserve_titles");
            reserveTitles = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                reserveTitles[i] = json.getString(i);
        }

        String[] ignoreTitles = new String[0];
        if(!obj.isNull("ignore_titles")){
            JSONArray json = obj.getJSONArray("ignore_titles");
            ignoreTitles = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                ignoreTitles[i] = json.getString(i);
        }

        String[] reserveDescriptions = new String[0];
        if(!obj.isNull("reserve_descriptions")){
            JSONArray json = obj.getJSONArray("reserve_descriptions");
            reserveDescriptions = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                reserveDescriptions[i] = json.getString(i);
        }

        String[] ignoreDescriptions = new String[0];
        if(!obj.isNull("ignore_descriptions")){
            JSONArray json = obj.getJSONArray("ignore_descriptions");
            ignoreDescriptions = new String[json.length()];
            for(int i = 0; i < json.length(); i++)
                ignoreDescriptions[i] = json.getString(i);
        }

        String recorded_format = obj.isNull("recorded_format") ? null : obj.getString("recorded_format");
        boolean isDisabled = obj.isNull("isDisabled") ? false : obj.getBoolean("isDisabled");

        return new Rule(types, categories, channels, ignoreChannels, reserveFlags, ignoreFlags, start, end, min, max,
                reserveTitles, ignoreTitles, reserveDescriptions, ignoreDescriptions, recorded_format, isDisabled);
    }

    /*
     * +-+-+-+
     * |P|U|T|
     * +-+-+-+
     */

    // 予約する 引数は予約する番組ID
    public ChinachuResponse putReserve(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return putServer(baseURL + "/program/" + programId + ".json");
    }

    // 自動予約された番組をスキップ
    public ChinachuResponse reserveSkip(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return putServer(baseURL + "/reserves/" + programId + "/skip.json");
    }

    // スキップの取り消し
    public ChinachuResponse reserveUnskip(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return putServer(baseURL + "/reserves/" + programId + "/unskip.json");
    }

    // 録画済みリストのクリーンアップ
    public ChinachuResponse recordedCleanUp() throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return putServer(baseURL + "/recorded.json");
    }

    /*
     * +-+-+-+-+-+-+
     * |D|E|L|E|T|E|
     * +-+-+-+-+-+-+
     */

    // 予約削除 引数は予約を削除する番組ID
    public ChinachuResponse delReserve(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return delServer(baseURL + "/reserves/" + programId + ".json");
    }

    // ルール削除 引数は削除するルールの番号（0開始）
    public ChinachuResponse delRule(String ruleNum) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return delServer(baseURL + "/rules/" + ruleNum + ".json");
    }

    // 録画済みファイルの削除 引数は削除する録画済みファイルの番組ID
    public ChinachuResponse delRecordedFile(String programId) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return delServer(baseURL + "/recorded/" + programId + "/file.json");
    }

    /*
     * +-+-+-+-+-+-+ +-+-+-+-+-+-+
     * |S|e|r|v|e|r| |A|c|c|e|s|s|
     * +-+-+-+-+-+-+ +-+-+-+-+-+-+
     */

    public String getServer(String url) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return (String)accessServer(url, RequestMethod.GET);
    }

    public ChinachuResponse putServer(String url) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return (ChinachuResponse)accessServer(url, RequestMethod.PUT);
    }

    public ChinachuResponse delServer(String url) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        return (ChinachuResponse)accessServer(url, RequestMethod.DELETE);
    }

    private enum RequestMethod{
        GET("GET"), PUT("PUT"), DELETE("DELETE");

        private final String text;

        private RequestMethod(final String text){
            this.text = text;
        }

        @Override
        public String toString(){
            return this.text;
        }
    }

    // GET: return String
    // Other: return ChinachuResponse
    private Object accessServer(String url, RequestMethod reqMethod) throws NoSuchAlgorithmException, KeyManagementException, IOException{
        boolean isSSL = url.startsWith("https://");

        SSLContext sslcontext = null;
        if(isSSL){
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
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tm, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(new TLSSocketFactory(sslcontext.getSocketFactory()));

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                @Override
                public boolean verify(String hostname, SSLSession session){
                    return true;
                }
            });
        }

        URL connectUrl = new URL(url);
        InputStream is = null;
        HttpURLConnection http = null;
        HttpsURLConnection https = null;
        Authenticator.setDefault(new BasicAuthenticator(username, password));
        if(isSSL){
            https = (HttpsURLConnection)connectUrl.openConnection();
            https.setRequestMethod(reqMethod.toString());
            https.connect();
            if(reqMethod == RequestMethod.GET){
                is = https.getInputStream();
            }
        }else{
            http = (HttpURLConnection)connectUrl.openConnection();
            http.setRequestMethod(reqMethod.toString());
            http.connect();
            if(reqMethod == RequestMethod.GET){
                is = http.getInputStream();
            }
        }

        String responseStr = null;
        ChinachuResponse chinachuResponse = null;
        if(reqMethod == RequestMethod.GET){
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            reader.close();
            responseStr = sb.toString();
        }else{
            if(isSSL){
                chinachuResponse = new ChinachuResponse(https.getResponseCode());
            }else{
                chinachuResponse = new ChinachuResponse(http.getResponseCode());
            }
        }

        if(is != null){
            is.close();
        }
        if(http != null){
            http.disconnect();
        }
        if(https != null){
            https.disconnect();
        }

        return (reqMethod == RequestMethod.GET) ? responseStr : chinachuResponse;
    }

}