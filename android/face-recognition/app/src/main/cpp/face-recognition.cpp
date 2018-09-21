#include <jni.h>
#include <string>
#include <dlib/image_processing/frontal_face_detector.h>
#include <dlib/image_io.h>

#include "jni/com_wangjunjian_facerecognition_FaceRecognition.h"

using namespace dlib;
using namespace std;

JNIEXPORT void JNICALL Java_com_wangjunjian_facerecognition_FaceRecognition_detect
        (JNIEnv *env, jobject clazz, jstring filename, jobject rect)
{
    const char* pfilename = env->GetStringUTFChars(filename, JNI_FALSE);

    static frontal_face_detector detector = get_frontal_face_detector();
    array2d<unsigned char> img;
    load_image(img, pfilename);

    env->ReleaseStringUTFChars(filename, pfilename);

    std::vector<rectangle> dets = detector(img, 0);

    if (dets.size() > 0)
    {
        rectangle faceRect = dets[0];

        jclass rectClass = env->GetObjectClass(rect);

        jfieldID fidLeft = env->GetFieldID(rectClass, "left", "I");
        env->SetIntField(rect, fidLeft, faceRect.left());
        jfieldID fidTop = env->GetFieldID(rectClass, "top", "I");
        env->SetIntField(rect, fidTop, faceRect.top());
        jfieldID fidRight = env->GetFieldID(rectClass, "right", "I");
        env->SetIntField(rect, fidRight, faceRect.right());
        jfieldID fidBottom = env->GetFieldID(rectClass, "bottom", "I");
        env->SetIntField(rect, fidBottom, faceRect.bottom());
    }
}
