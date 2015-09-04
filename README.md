# Chinachu4j
初めてライブラリなんて作ったゾ・・・  
動作にはorg.json.jarが必要。どっかからDLしてきて

## なにこれ
[Chinachu](https://github.com/kanreisa/Chinachu/)がREST APIに対応してるということを知って、これはAndroidAppから操作できるようにするしかねえ！って思った。  
とりあえずライブラリ作らないと面倒だと思ったから作った。  
まだまだ初期段階。  
今後に期待（？）

## できること
### GET
1. 番組表（局ごと）の取得  
Program[] getChannelSchedule(String channelId)  
channelId = 放送局のID

2. 局リストの取得  
String[] getChannelList()  
局のリストをString配列で返却  
ex. String[0]: ＮＨＫ総合１・東京,GR_1024

3. 予約済みリストの取得  
Program[] getReserves()

4. 録画中のリストの取得  
Program[] getRecording()

5. 録画中のキャプチャの取得（Base64）  
String getRecordingImage(String id, String size)  
Base64エンコードされたStringを返却  
id = 番組ID  
ex. size = 1280x720  
if size=null 320x180.

6. 録画済みのリストの取得  
Program[] getRecorded()

7. 録画済みのキャプチャの取得（Base64）  
String getRecordedImage(String id, int pos, String size)  
Base64エンコードされたStringを返却  
id = 番組ID  
pos = ポジション（秒数）  
if pos=-1 7.  
ex. size = 1280x720  
if size=null 320x180.

8. 録画中のストリーミング再生  
String getNonEncRecordingMovie(String programId)  
URLを返却  
programId = 番組ID

9. 録画中のストリーミング再生（エンコード有り）  
String getEncRecordingMovie(String programId, String type, String[] params)  
URLを返却  
programId = 番組ID  
type = m2ts, f4v, flv, webm, asf  
params: [下のエンコードパラメータ参照](#エンコードパラメータ)

10. 録画済みのストリーミング再生
String getNonEncRecordedMovie(String programId)  
URLを返却  
programId = 番組ID

11. 録画済みのストリーミング再生（エンコード有り）  
String getEncRecordedMovie(String programId, String type, String[] params)  
URLを返却  
programId = 番組ID  
type = type: m2ts, f4v, flv, webm, asf
params: [下のエンコードパラメータ参照](#エンコードパラメータ)

##### エンコードパラメータ
null値の場合はURLに含めません  
値がない場合は必ずnull値を設定してください  
[0]: コンテナフォーマット   mpegts, flv, asf, webm  
[1]: 動画コーデック         copy, libvpx, flv, libx264, wmv2  
[2]: 音声コーデック         copy, libvorbis, libfdk_aac, wmav2  
\[3\]: 動画ビットレート  
\[4\]: 音声ビットレート  
\[5\]: 映像サイズ(例:1280x720)  
\[6\]: 映像フレームレート(例:24)

### PUT
1. 予約の追加  
void putReserve(String programId)  
programId = 番組ID

### DELETE
1. 予約の削除  
void delReserve(String programId)  
programId = 番組ID
