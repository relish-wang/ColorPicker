## <img src="./image/colorpicker_logo.png" alt="ColorPicker" width="80" height="80" align="bottom"/>ColorPicker

作者：景三

[**English Document**](README.md)

[TOC]
## 简介

![Download](https://api.bintray.com/packages/relish-wang/maven/colorpicker/images/download.svg) [![api][apisvg]][api] [![build][buildsvg]][build]

ColorPicker是一个仿PhotoShop取色板的颜色拾取组件。

![主界面](./image/colorpicker.gif)

## 引用方法

### 使用gradle加载依赖

#### 1 在project的build.gradle中
```groovy
allprojects {
    repositories {
        // maven { url 'https://jitpack.io' } // 0.0.x 老版本发布在jitpack上(建议使用新版本)
        jcenter() // 1.x.x 支持AndroidX
    }
}
```

#### 2 在你app的build.gradle中
```groovy
// implementation 'com.github.relish-wang:ColorPicker:0.0.2-SNAPSHOT' // 0.x.x 老版本
implementation 'wang.relish.widget:colorpicker:1.0.0' // 1.x.x 新版本(支持AndroidX)
```

### 更新历史

[**CHANGELOG.md**](CHANGELOG.md)

## 使用方法

### 启动选择弹窗
```java
new ColorPickerDialog.Builder(context, mColor)   //mColor:初始颜色
        .setHexValueEnabled(mHexValueEnable)               //是否显示颜色值
        .setOnColorChangedListener(onColorChangedListener) //设置监听颜色改变的监听器
        .build()
        .show();//展示
```

### 或者

或者你可以直接把library下的关键文件拷走使用
- [ColorPickerDialog.java](https://github.com/relish-wang/ColorPicker/blob/master/library/src/main/java/wang/relish/colorpicker/ColorPickerDialog.java)
- [ColorPickerView.java](https://github.com/relish-wang/ColorPicker/blob/master/library/src/main/java/wang/relish/colorpicker/ColorPickerView.java)
- [Utils.java](https://github.com/relish-wang/ColorPicker/blob/master/library/src/main/java/wang/relish/colorpicker/Utils.java)
- [layout/dialog_color_picker.xml](https://github.com/relish-wang/ColorPicker/blob/master/library/src/main/res/layout/dialog_color_picker.xml)
- [layout-land/dialog_color_picker.xml](https://github.com/relish-wang/ColorPicker/blob/master/library/src/main/res/layout-land/dialog_color_picker.xml)

放置到你项目的对应文件夹即可。
注意：
*记得把dialog_color_picker.xml布局文件中ColorPickerView的包名改为你自己的包名*

### 再或者

直接下载了此仓库的源码，再将library作为一个module导入你的工程


## 注意事项

- 要求API Level 14及以上

## 常见问题

#### Manifest文件合并问题

> Manifest merger failed : Attribute meta-data#android.support.VERSION@value value=(25.3.1) ...
 
解决方法：

在app的build.gradle文件末尾添加以下代码

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

## 混淆配置

无

[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14

[buildsvg]: https://travis-ci.org/relish-wang/ColorPicker.svg?branch=master
[build]: https://travis-ci.org/relish-wang/ColorPicker
