# 人脸识别服务

## 人脸识别
* 基于开源库[dlib](http://dlib.net)。
* 下载[源代码](https://github.com/davisking/dlib)
* 注释DLIB_NO_GUI_SUPPORT的error指令。打开dlib/gui_core/gui_core_kernel_2.h，修改为下面的代码。

```java
#ifdef DLIB_NO_GUI_SUPPORT
//#error "DLIB_NO_GUI_SUPPORT is defined so you can't use the GUI code.  Turn DLIB_NO_GUI_SUPPORT off if you want to use it."
//#error "Also make sure you have libx11-dev installed on your system"
#endif
```
* [#error指令(c/C++)](https://msdn.microsoft.com/zh-cn/magazine/c8tk0xsk)


## Android
* [NDK](http://nbviewer.jupyter.org/github/wang-junjian/face-recognition-services/blob/master/face_recognition_android.ipynb)
  [例子](http://nbviewer.jupyter.org/github/wang-junjian/face-recognition-services/blob/master/face_recognition_android_example.ipynb)

## iOS
* [静态库](http://nbviewer.jupyter.org/github/wang-junjian/face-recognition-services/blob/master/face_recognition_ios_static.ipynb)
