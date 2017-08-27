# ColorPicker

作者：王鑫

[TOC]
## 简介

[![jitpack][jitpack-version]][jitpack] [![api][apisvg]][api] [![build][buildsvg]][build]

ColorPicker是一个仿PhotoShop取色板的颜色拾取组件。

![主界面](./image/colorpicker.gif)

## 引用方法

### 使用gradle加载依赖

#### 1 在project的build.gradle中
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

#### 2 在你app的build.gradle中
```groovy
compile 'com.github.relish-wang:ColorPicker:0.0.2-SNAPSHOT'
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

[jitpack-version]: https://jitpack.io/v/relish-wang/ColorPicker.svg
[jitpack]: https://jitpack.io/v/relish-wang/ColorPicker

[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14

[buildsvg]: https://travis-ci.org/relish-wang/ColorPicker.svg?branch=master
[build]: https://travis-ci.org/relish-wang/ColorPicker
