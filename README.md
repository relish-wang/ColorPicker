# ColorPicker

作者：王鑫

[TOC]
## 简介

ColorPicker是一个仿PhotoShop取色板的颜色拾取组件。

![主界面](./image/image.png)!

## 引用方法

### 使用gradle加载依赖

```groovy
compile 'wang.relish.widgets:colorpicker:0.0.1-SNAPSHOT'
```

### 更新历史

[**CHANGELOG.md**](CHANGELOG.md)

## 使用方法

### 启动选择弹窗
```
ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(MainActivity.this, mColor);//mColor:初始颜色
builder.setHexValueEnabled(mHexValueEnable)//是否显示颜色值
        .setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {//设置监听颜色改变的监听器
            @Override
            public void onColorChanged(int color) {
                mColor = color;
                mViewColor.setBackgroundColor(mColor);
            }
        })
        .build()
        .show();//展示
```

## 注意事项

- 要求API Level 14以上


## 混淆配置

无
