//
//  face_recognition.hpp
//  dlib-ios
//
//  Created by 王军建 on 2018/9/30.
//  Copyright © 2018年 王军建. All rights reserved.
//

#ifndef face_recognition_hpp
#define face_recognition_hpp

#include <string>
#include <MacTypes.h>

Rect detectFaces(const std::string imageFilename);

#endif /* face_recognition_hpp */
