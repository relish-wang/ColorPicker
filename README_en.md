## <img src="./image/colorpicker_logo.png" alt="ColorPicker" width="80" height="80" align="bottom"/>ColorPicker

Author: Relish Wang

[**中文文档**](README_zh-rCN.md)

[TOC]
## Brief Introduction

[![jitpack][jitpack-version]][jitpack] [![api][apisvg]][api] [![build][buildsvg]][build]

ColorPicker is A imitation color picker just like Photoshop's for Android that based on HSV color space.

![主界面](./image/colorpicker.gif)

## How to start

### Add dependency in gradle

#### 1 In the build.gradle of project
```groovy
allprojects {
    repositories {
        // maven { url 'https://jitpack.io' } // for old versions(0.x.x)
        jcenter() // for new versions(1.x.x)
    }
}
```

#### 2 In the build.gradle of app
```groovy
// compile 'com.github.relish-wang:ColorPicker:0.0.2-SNAPSHOT' // for old versions(0.x.x)
implementation 'wang.relish.widget:colorpicker:1.0.0' // for new versions(1.x.x: support AndroidX)
```

### Log of version change

[**CHANGELOG.md**](CHANGELOG.md)

## How to use

### launch a color picker
```java
new ColorPickerDialog.Builder(context, mColor)              //mColor:init color
        .setHexValueEnabled(mHexValueEnable)                //whether show the color value(Hexadecimal) or not
        .setOnColorChangedListener(onColorChangedListener)  //set a listener for listening color changing
        .build()
        .show();//show
```

## attention

- API Level 14+

## Q&A

#### while Manifest files merging

> Manifest merger failed : Attribute meta-data#android.support.VERSION@value value=(25.3.1) ...
 
attention：

add code below at the end of the build.gradle of app
   
   ```
configurations.all {
    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
        def requested = details.requested
        if (requested.group == 'com.android.support') {
            if (!requested.name.startsWith("multidex")) {
                details.useVersion '25.3.0'
            }
        }
    }
}
```


## Proguard Configuration

Nothing.

[jitpack-version]: https://jitpack.io/v/relish-wang/ColorPicker.svg
[jitpack]: https://jitpack.io/v/relish-wang/ColorPicker

[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14

[buildsvg]: https://travis-ci.org/relish-wang/ColorPicker.svg?branch=master
[build]: https://travis-ci.org/relish-wang/ColorPicker
