# Chinachu4j
[![](https://jitpack.io/v/sugtao4423/Chinachu4j.svg)](https://jitpack.io/#sugtao4423/Chinachu4j)

[Chinachu](https://github.com/kanreisa/Chinachu/)のAPIを叩くJavaライブラリ。  
初めてライブラリを作ったので完成度はお察し  
動作にはorg.json.jarが必要

## なにこれ
[Chinachu](https://github.com/kanreisa/Chinachu/)がREST APIに対応してるということを知って、これはAndroidAppから操作できるようにするしかねえ！って思った。  
とりあえずライブラリ作らないと面倒だと思ったから作った。  
まだまだ初期段階。  
今後に期待（？）

## できること
詳しいことは[Wiki](https://github.com/sugtao4423/Chinachu4j/wiki)を見て下さい

## Usage

* build.gradle

```
repositories {
    maven {
        url 'https://jitpack.io'
    }
}

dependencies {
    implementation 'com.github.sugtao4423:Chinachu4j:3.0.1'
}
```
