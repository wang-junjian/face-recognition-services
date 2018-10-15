//
//  face_recognition.cpp
//  dlib-ios
//
//  Created by 王军建 on 2018/9/30.
//  Copyright © 2018年 王军建. All rights reserved.
//

#include "face_recognition.hpp"

#include "dlib/image_processing/frontal_face_detector.h"
#include "dlib/image_processing.h"
#include "dlib/image_io.h"

using namespace std;
using namespace dlib;

Rect detectFaces(const std::string imageFilename)
{
    Rect rect = {0};
    
    static frontal_face_detector detector = get_frontal_face_detector();
    array2d<unsigned char> img;
    load_image(img, imageFilename);
    
    std::vector<rectangle> dets = detector(img, 0);
    
    if (dets.size() > 0)
    {
        rectangle faceRect = dets[0];

        rect.left = faceRect.left();
        rect.top = faceRect.top();
        rect.right = faceRect.right();
        rect.bottom = faceRect.bottom();
    }
    
    return rect;
}
