[![](https://jitpack.io/v/Nicrob64/ImageSelector.svg)](https://jitpack.io/#Nicrob64/ImageSelector)

# ImageSelector
Image selector library for Android. Support single choice、multi-choice、cropping image and preview image.

![](https://raw.githubusercontent.com/ioneday/ImageSelector/master/screenshot/Screenshot1.jpg)
![](https://raw.githubusercontent.com/ioneday/ImageSelector/master/screenshot/Screenshot2.jpg)
![](https://raw.githubusercontent.com/ioneday/ImageSelector/master/screenshot/Screenshot3.jpg)
![](https://raw.githubusercontent.com/ioneday/ImageSelector/master/screenshot/Screenshot4.jpg)
![](https://raw.githubusercontent.com/ioneday/ImageSelector/master/screenshot/Screenshot5.jpg)
![](https://raw.githubusercontent.com/Nicrob64/ImageSelector/master/screenshot/Screenshot-6.png)


## Changes to the original version

- Fixed issues with bad touch recognition on cropping activity
- Added rotation support to the cropping activity
- Changed the cropping crop mode to circle
- Fixed bugs with some images not loading in
- Added basic video support. Don't know how this will work with cropping or preview activities, so make sure that is handled correctly.

## TODO

- Update library to request READ and WRITE permissions on android Marshmallow+
- Add a Video + Image picker
- Add video playback in thumbnails/preview (not sure if I should do that or not)
- Change photo icon to a video icon for the camera preview while in video mode

## Quick start

1) Add Library module as a dependency in your build.gradle file.

or

```xml
dependencies {
    compile 'com.github.Nicrob64:ImageSelector:1.0.12'
}
```

2) Declare permission in your AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

3) Call ImageSelectorActivity in your code (Manifest merger might handle that? not too sure)

```java
ImageSelectorActivity.start(MainActivity.this, maxSelectNum, mode, isShow,isPreview,isCrop);
```
Which is equivalent to:

```java
public static void start(Activity activity, int maxSelectNum, int mode, boolean isShow, boolean enablePreview, boolean enableCrop) {
    Intent intent = new Intent(activity, ImageSelectorActivity.class);
    intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
    intent.putExtra(EXTRA_SELECT_MODE, mode);
    intent.putExtra(EXTRA_SHOW_CAMERA, isShow);
    intent.putExtra(EXTRA_ENABLE_PREVIEW, enablePreview);
    intent.putExtra(EXTRA_ENABLE_CROP, enableCrop);
    activity.startActivityForResult(intent, REQUEST_IMAGE);
}
```

Other options:
```java
//choose videos instead of images
intent.putExtra(ImageSelectorActivity.EXTRA_MEDIA_TYPE, ImageSelectorActivity.TYPE_VIDEO); 
```


4) Receive result in your onActivityResult Method

``` java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE){
        ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
        // do something
    }
}
```

## Thanks

* [Glide](https://github.com/bumptech/glide)

* [PhotoView](https://github.com/chrisbanes/PhotoView)

* [simplecropview](https://github.com/IsseiAoki/SimpleCropView)

###License
>The MIT License (MIT)

>Modifications Copyright (c) 2016 Nic Robertson
>Original library Copyright (c) 2015 Dee

>Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

>The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
