package sugtao4423.library.chinachu4j

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Authenticator
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Suppress("UNUSED")
class Chinachu4j(private var baseURL: String, private val username: String, private val password: String) {

    init {
        baseURL = if (baseURL.endsWith("/")) "${baseURL}api" else "$baseURL/api"
    }

    /*
     * +-+-+-+
     * |G|E|T|
     * +-+-+-+
     */

    // 各チャンネルの番組表
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class, JSONException::class)
    fun getChannelSchedule(channelId: String): Array<Program> {
        val url = "$baseURL/schedule/$channelId/programs.json"
        val programs = arrayListOf<Program>()
        JSONArray(getServer(url)).let {
            for (i in 0 until it.length()) {
                programs.add(getProgram(it.getJSONObject(i)))
            }
        }
        return programs.toTypedArray()
    }

    // 全チャンネルの番組表
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class, JSONException::class)
    fun getAllSchedule(): Array<Program> {
        val url = "$baseURL/schedule/programs.json"
        val allPrograms = arrayListOf<Program>()
        JSONArray(getServer(url)).let {
            for (i in 0 until it.length()) {
                allPrograms.add(getProgram(it.getJSONObject(i)))
            }
        }
        return allPrograms.toTypedArray()
    }

    // 全チャンネルから番組検索
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class, JSONException::class)
    fun searchProgram(query: String): Array<Program> {
        val allSchedule = getAllSchedule()
        return allSchedule.filter {
            it.fullTitle.matches(Regex(".*$query.*"))
        }.toTypedArray()
    }

    // 現在放送されている番組から局名のみを抽出
    // 「局名,局id」形式
    // for example "ＮＨＫ総合１・東京,GR_1024"
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class, JSONException::class)
    fun getChannelList(): Array<String> {
        val url = "$baseURL/schedule/broadcasting.json"
        val channelList = arrayListOf<String>()
        JSONArray(getServer(url)).let {
            for (i in 0 until it.length()) {
                it.getJSONObject(i).getJSONObject("channel").apply {
                    val channelName = getString("name")
                    val channelId = getString("id")
                    channelList.add("$channelName,$channelId")
                }
            }
        }
        return channelList.toTypedArray()
    }

    // 予約済の取得
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class, JSONException::class)
    fun getReserves(): Array<Reserve> {
        val url = "$baseURL/reserves.json"
        val reserves = arrayListOf<Reserve>()
        JSONArray(getServer(url)).let {
            for (i in 0 until it.length()) {
                reserves.add(getReserve(it.getJSONObject(i)))
            }
        }
        return reserves.toTypedArray()
    }

    // 録画中の取得
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class, JSONException::class)
    fun getRecording(): Array<Program> {
        val url = "$baseURL/recording.json"
        val recording = arrayListOf<Program>()
        JSONArray(getServer(url)).let {
            for (i in 0 until it.length()) {
                recording.add(getProgram(it.getJSONObject(i)))
            }
        }
        return recording.toTypedArray()
    }

    // 録画済みの取得
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class, JSONException::class)
    fun getRecorded(): Array<Recorded> {
        val url = "$baseURL/recorded.json"
        val recordeds = arrayListOf<Recorded>()
        JSONArray(getServer(url)).let {
            for (i in 0 until it.length()) {
                recordeds.add(getRecorded(it.getJSONObject(i)))
            }
        }
        return recordeds.toTypedArray()
    }

    // ルールの取得
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class, JSONException::class)
    fun getRules(): Array<Rule> {
        val url = "$baseURL/rules.json"
        val rules = arrayListOf<Rule>()
        JSONArray(getServer(url)).let {
            for (i in 0 until it.length()) {
                rules.add(getRule(it.getJSONObject(i)))
            }
        }
        return rules.toTypedArray()
    }

    // 録画中のキャプチャを取得
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun getRecordingImage(programId: String, size: String = "320x180"): String {
        val url = "$baseURL/recording/$programId/preview.txt?size=$size"
        return getServer(url)
    }

    // 録画済みのキャプチャを取得
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun getRecordedImage(programId: String, pos: Int = 7, size: String = "320x180"): String {
        val url = "$baseURL/recorded/$programId/preview.txt?pos=$pos&size=$size"
        return getServer(url)
    }

    // ライブストリーミング再生（エンコなし）
    fun getNonEncLiveMovieURL(channelId: String): String {
        return "${getIncludeUserPassUrl()}/channel/$channelId/watch.m2ts?f=mpegts&c:v=copy&c:a=copy"
    }

    // ライブストリーミング再生（エンコ有り）
    // type: mp4, m2ts, webm
    fun getEncLiveMovieURL(channelId: String, type: String, params: Array<out String?>): String {
        val base = "${getIncludeUserPassUrl()}/channel/$channelId/watch.$type?"
        return getIncludeEncParamsUrl(base, params)
    }

    // 録画中のストリーミング再生（エンコなし）
    fun getNonEncRecordingMovieURL(programId: String): String {
        return "${getIncludeUserPassUrl()}/recording/$programId/watch.m2ts?f=mpegts&c:v=copy&c:a=copy"
    }

    // 録画中のストリーミング再生（エンコ有り）
    // type: mp4, m2ts, webm
    fun getEncRecordingMovieURL(programId: String, type: String, params: Array<out String?>): String {
        val base = "${getIncludeUserPassUrl()}/recording/$programId/watch.$type?"
        return getIncludeEncParamsUrl(base, params)
    }

    // 録画済みのストリーミング再生（エンコなし）
    fun getNonEncRecordedMovieURL(programId: String): String {
        return "${getIncludeUserPassUrl()}/recorded/$programId/watch.m2ts?f=mpegts&c:v=copy&c:a=copy"
    }

    // 録画済みのストリーミング再生（エンコ有り）
    // type: mp4, m2ts, webm
    fun getEncRecordedMovieURL(programId: String, type: String, params: Array<out String?>): String {
        val base = "${getIncludeUserPassUrl()}/recorded/$programId/watch.$type?"
        return getIncludeEncParamsUrl(base, params)
    }

    // UsernameとPasswordを含んだbaseURLを返却
    private fun getIncludeUserPassUrl(): String {
        val protocol = if (baseURL.startsWith("https://")) "https" else "http"
        val noProtocolUrl = baseURL.substring(protocol.count() + 3)
        return "$protocol://$username:$password@$noProtocolUrl"
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
    private fun getIncludeEncParamsUrl(base: String, params: Array<out String?>): String {
        var url = base
        if (!params[0].isNullOrEmpty())
            url += "f=${params[0]}&"
        if (!params[1].isNullOrEmpty())
            url += "c:v=${params[1]}&"
        if (!params[2].isNullOrEmpty())
            url += "c:a=${params[2]}&"
        if (!params[3].isNullOrEmpty())
            url += "b:v=${params[3]}&"
        if (!params[4].isNullOrEmpty())
            url += "b:a=${params[4]}&"
        if (!params[5].isNullOrEmpty())
            url += "s=${params[5]}&"
        if (!params[6].isNullOrEmpty())
            url += "r=${params[6]}&"

        return url.substring(0, url.count() - 1)
    }

    // JSONObjectからProgramを返却
    @Throws(JSONException::class)
    private fun getProgram(obj: JSONObject): Program {
        obj.apply {
            val id = getString("id")
            val category = optString("category")
            val title = getString("title")
            val subTitle = getString("subTitle")
            val fullTitle = getString("fullTitle")
            val detail = getString("detail")
            val episode = optInt("episode", -1)
            val start = getLong("start")
            val end = getLong("end")
            val seconds = getInt("seconds")

            val flags = arrayListOf<String>()
            getJSONArray("flags").let {
                for (i in 0 until it.length()) {
                    flags.add(it.getString(i))
                }
            }

            val channel: Channel = getJSONObject("channel").let {
                val n = it.getInt("n")
                val type = it.getString("type")
                val channel = it.getString("channel")
                val name = it.getString("name")
                val channelId = it.getString("id")
                val sid = it.getInt("sid")
                Channel(n, type, channel, name, channelId, sid)
            }

            return Program(
                    id,
                    category,
                    title,
                    subTitle,
                    fullTitle,
                    detail,
                    episode,
                    start,
                    end,
                    seconds,
                    flags.toTypedArray(),
                    channel
            )
        }
    }

    // JSONObjectからReserveを返却
    @Throws(JSONException::class)
    private fun getReserve(obj: JSONObject): Reserve {
        obj.apply {
            val program = getProgram(this)

            val isManualReserved = optBoolean("isManualReserved")
            val isConflict = optBoolean("isConflict")
            val recordedFormat = optString("recordedFormat")
            val isSkip = optBoolean("isSkip")

            return Reserve(program, isManualReserved, isConflict, recordedFormat, isSkip)
        }
    }

    // JSONObjectからRecordedを返却
    @Throws(JSONException::class)
    private fun getRecorded(obj: JSONObject): Recorded {
        obj.apply {
            val program = getProgram(this)
            val tuner = getTuner(getJSONObject("tuner"))

            val isManualReserved = optBoolean("isManualReserved")
            val isConflict = optBoolean("isConflict")
            val recordedFormat = optString("recordedFormat")

            val isSigTerm = optBoolean("isSigTerm")
            val recorded = getString("recorded")
            val command = getString("command")

            return Recorded(program, isManualReserved, isConflict, recordedFormat, isSigTerm, tuner, recorded, command)
        }
    }

    // JSONObjectからTunerを返却（Recordedの取得に使用）
    @Throws(JSONException::class)
    private fun getTuner(obj: JSONObject): Tuner {
        obj.apply {
            val name = getString("name")
            val isScrambling = getBoolean("isScrambling")

            val types = arrayListOf<String>()
            optJSONArray("types")?.let {
                for (i in 0 until it.length()) {
                    types.add(it.getString(i))
                }
            }

            val command = getString("command")
            val n = optInt("n", -1)

            return Tuner(name, isScrambling, types.toTypedArray(), command, n)
        }
    }

    // JSONObjectからRuleを返却
    @Throws(JSONException::class)
    private fun getRule(obj: JSONObject): Rule {
        obj.apply {
            val types = arrayListOf<String>()
            optJSONArray("types")?.let {
                for (i in 0 until it.length()) {
                    types.add(it.getString(i))
                }
            }

            val categories = arrayListOf<String>()
            optJSONArray("categories")?.let {
                for (i in 0 until it.length()) {
                    categories.add(it.getString(i))
                }
            }

            val channels = arrayListOf<String>()
            optJSONArray("channels")?.let {
                for (i in 0 until it.length()) {
                    channels.add(it.getString(i))
                }
            }

            val ignoreChannels = arrayListOf<String>()
            optJSONArray("ignore_channels")?.let {
                for (i in 0 until it.length()) {
                    ignoreChannels.add(it.getString(i))
                }
            }

            val reserveFlags = arrayListOf<String>()
            optJSONArray("reserve_flags")?.let {
                for (i in 0 until it.length()) {
                    reserveFlags.add(it.getString(i))
                }
            }

            val ignoreFlags = arrayListOf<String>()
            optJSONArray("ignore_flags")?.let {
                for (i in 0 until it.length()) {
                    ignoreFlags.add(it.getString(i))
                }
            }

            var start = -1
            var end = -1
            optJSONObject("hour")?.let {
                start = it.optInt("start", start)
                end = it.optInt("end", end)
            }

            var min = -1
            var max = -1
            optJSONObject("duration")?.let {
                min = it.optInt("min", min)
                max = it.optInt("max", max)
            }

            val reserveTitles = arrayListOf<String>()
            optJSONArray("reserve_titles")?.let {
                for (i in 0 until it.length()) {
                    reserveTitles.add(it.getString(i))
                }
            }

            val ignoreTitles = arrayListOf<String>()
            optJSONArray("ignore_titles")?.let {
                for (i in 0 until it.length()) {
                    ignoreTitles.add(it.getString(i))
                }
            }

            val reserveDescriptions = arrayListOf<String>()
            optJSONArray("reserve_descriptions")?.let {
                for (i in 0 until it.length()) {
                    reserveDescriptions.add(it.getString(i))
                }
            }

            val ignoreDescriptions = arrayListOf<String>()
            optJSONArray("ignore_descriptions")?.let {
                for (i in 0 until it.length()) {
                    ignoreDescriptions.add(it.getString(i))
                }
            }

            val recordedFormat = optString("recorded_format")
            val isDisabled = optBoolean("isDisabled")

            return Rule(
                    types.toTypedArray(),
                    categories.toTypedArray(),
                    channels.toTypedArray(),
                    ignoreChannels.toTypedArray(),
                    reserveFlags.toTypedArray(),
                    ignoreFlags.toTypedArray(),
                    start,
                    end,
                    min,
                    max,
                    reserveTitles.toTypedArray(),
                    ignoreTitles.toTypedArray(),
                    reserveDescriptions.toTypedArray(),
                    ignoreDescriptions.toTypedArray(),
                    recordedFormat,
                    isDisabled
            )
        }
    }

    /*
     * +-+-+-+
     * |P|U|T|
     * +-+-+-+
     */

    // 予約する 引数は予約する番組ID
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun putReserve(programId: String): ChinachuResponse {
        return putServer("$baseURL/program/$programId.json")
    }

    // 自動予約された番組をスキップ
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun reserveSkip(programId: String): ChinachuResponse {
        return putServer("$baseURL/reserves/$programId/skip.json")
    }

    // スキップの取り消し
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun reserveUnskip(programId: String): ChinachuResponse {
        return putServer("$baseURL/reserves/$programId/unskip.json")
    }

    // 録画済みリストのクリーンアップ
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun recordedCleanUp(): ChinachuResponse {
        return putServer("$baseURL/recorded.json")
    }

    /*
     * +-+-+-+-+-+-+
     * |D|E|L|E|T|E|
     * +-+-+-+-+-+-+
     */

    // 予約削除 引数は予約を削除する番組ID
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun delReserve(programId: String): ChinachuResponse {
        return delServer("$baseURL/reserves/$programId.json")
    }

    // ルール削除 引数は削除するルールの番号（0開始）
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun delRule(ruleNum: String): ChinachuResponse {
        return delServer("$baseURL/rules/$ruleNum.json")
    }

    // 録画済みファイルの削除 引数は削除する録画済みファイルの番組ID
    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun delRecordedFile(programId: String): ChinachuResponse {
        return delServer("$baseURL/recorded/$programId/file.json")
    }

    /*
     * +-+-+-+-+-+-+ +-+-+-+-+-+-+
     * |S|e|r|v|e|r| |A|c|c|e|s|s|
     * +-+-+-+-+-+-+ +-+-+-+-+-+-+
     */

    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun getServer(url: String): String {
        return accessServer(url, RequestMethod.GET) as String
    }

    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun putServer(url: String): ChinachuResponse {
        return accessServer(url, RequestMethod.PUT) as ChinachuResponse
    }

    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class, IOException::class)
    fun delServer(url: String): ChinachuResponse {
        return accessServer(url, RequestMethod.DELETE) as ChinachuResponse
    }

    private enum class RequestMethod {
        GET, PUT, DELETE
    }

    // GET: return String
    // Other: return ChinachuResponse
    @Throws(NoSuchAlgorithmException::class, KeyManagementException::class, IOException::class)
    private fun accessServer(url: String, requestMethod: RequestMethod): Any {
        val isGET = requestMethod == RequestMethod.GET
        val isSSL = url.startsWith("https://")

        if (isSSL) {
            val x509TrustManager = object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }
            }
            val tm = arrayOf<TrustManager>(x509TrustManager)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tm, null)
            HttpsURLConnection.setDefaultSSLSocketFactory(TLSSocketFactory(sslContext.socketFactory))

            HttpsURLConnection.setDefaultHostnameVerifier { _, _ ->
                true
            }
        }

        val connectUrl = URL(url)
        var inputStream: InputStream? = null
        var http: HttpURLConnection? = null
        var https: HttpsURLConnection? = null
        Authenticator.setDefault(BasicAuthenticator(username, password))
        if (isSSL) {
            https = connectUrl.openConnection() as HttpsURLConnection
            https.apply {
                setRequestMethod(requestMethod.toString())
                connect()
            }
            if (isGET) {
                inputStream = https.inputStream
            }
        } else {
            http = connectUrl.openConnection() as HttpURLConnection
            http.apply {
                setRequestMethod(requestMethod.toString())
                connect()
            }
            if (isGET) {
                inputStream = http.inputStream
            }
        }

        var responseStr: String? = null
        var chinachuResponse: ChinachuResponse? = null
        if (isGET) {
            responseStr = BufferedReader(InputStreamReader(inputStream)).readText()
        } else {
            chinachuResponse = ChinachuResponse(
                    if (isSSL) {
                        https!!.responseCode
                    } else {
                        http!!.responseCode
                    }
            )
        }

        inputStream?.close()
        http?.disconnect()
        https?.disconnect()

        return if (isGET) responseStr!! else chinachuResponse!!
    }

}