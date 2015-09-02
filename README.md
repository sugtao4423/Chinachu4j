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
1. 局リストの取得  
String[] getChannelList()

2. 番組表（局ごと）の取得  
Program[] getChannelSchedule(String channelId)  
channelId = 放送局のID

3. 予約済みリストの取得  
Program[] getReserves()

4. 録画中のリストの取得  
Program[] getRecording()

5. 録画中のキャプチャの取得（Base64）  
String getRecordingImage(String id, String size)  
id = 番組ID  
ex. size = 1280x720  
if size=null 320x180.

6. 録画済みのリストの取得  
Program[] getRecorded()

7. 録画済みのキャプチャの取得（Base64）  
String getRecordedImage(String id, int pos, String size)  
id = 番組ID  
pos = ポジション（秒数）  
if pos=-1 7.  
ex. size = 1280x720  
if size=null 320x180.

### PUT
1. 予約の追加  
void putReserve(String programId)  
programId = 番組ID

### DELETE
1. 予約の削除  
void delReserve(String programId)  
programId = 番組ID
