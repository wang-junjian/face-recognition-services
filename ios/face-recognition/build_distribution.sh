#!/bin/sh

#  build_distribution.sh
#  face-recognition
#
#  Created by 王军建 on 2018/10/11.
#  Copyright © 2018年 王军建. All rights reserved.

PROJ=${PROJECT_NAME}.xcodeproj
LIB_STATIC_NAME=${PROJECT_NAME}     #把项目名改为自己的即可使用
DISTRIBUTION_DIR=./distribution     #打包出来的文件的目录

IPHONE_OS_DIR=${DISTRIBUTION_DIR}/${CONFIGURATION}-iphoneos
IPHONE_SIMULATOR_DIR=${DISTRIBUTION_DIR}/${CONFIGURATION}-iphonesimulator

#创建真机库文件目录
if [[ ! -d ${IPHONE_OS_DIR} ]]; then
mkdir -p ${IPHONE_OS_DIR}
fi

#创建模拟器库文件目录
if [[ ! -d ${IPHONE_SIMULATOR_DIR} ]]; then
mkdir -p ${IPHONE_SIMULATOR_DIR}
fi


#error: error: accessing build database "/Users/wjj/Library/Developer/Xcode/DerivedData/face-recognition-ardscqnlcsvcvtgjntliogvokvae/Build/Intermediates.noindex/XCBuildData/build.db": disk I/O error
#-UseModernBuildSystem=NO 解决上面的问题（https://stackoverflow.com/questions/51153525/xcode-10-unable-to-attach-db-error）
#编译真机库文件
xcodebuild -project ${PROJ} \
-scheme ${LIB_STATIC_NAME} \
-configuration ${CONFIGURATION} \
-sdk iphoneos \
-UseModernBuildSystem=NO \
clean \
build \
CONFIGURATION_BUILD_DIR=${IPHONE_OS_DIR}
# -archivePath ${IPHONE_OS_DIR}

#编译模拟器库文件
xcodebuild build -project ${PROJ} \
-scheme ${LIB_STATIC_NAME} \
-configuration ${CONFIGURATION} \
-sdk iphonesimulator \
-UseModernBuildSystem=NO \
clean \
build \
CONFIGURATION_BUILD_DIR=${IPHONE_SIMULATOR_DIR}
# -archivePath ${IPHONE_SIMULATOR_DIR}

##建立SDK目录
SDK_DIR=${DISTRIBUTION_DIR}/${LIB_STATIC_NAME}
if [[ -d ${SDK_DIR} ]]; then
rm -fR ${SDK_DIR}
fi
mkdir -p ${SDK_DIR}

# 静态库文件
LIB_NAME=lib${LIB_STATIC_NAME}.a

#合并模拟器文件和真机文件
lipo -create ${IPHONE_OS_DIR}/${LIB_NAME} ${IPHONE_SIMULATOR_DIR}/${LIB_NAME} -output ${SDK_DIR}/${LIB_NAME}
#lipo -info ${SDK_DIR}/${LIB_NAME}

#拷贝头文件
cp -R ${IPHONE_OS_DIR}/include/${LIB_STATIC_NAME}/* ${SDK_DIR}/


#打包为zip文件
PACKAGE_DATE=`date '+%Y%m%d%H'`
#GIT_VERSION=`git log --abbrev-commit|head -1|cut -d' ' -f 2`
#SDK_ZIP_NAME=iOS_${LIB_STATIC_NAME}_${PACKAGE_DATE}_${GIT_VERSION}_${CONFIGURATION}.zip

pushd ${DISTRIBUTION_DIR}
SDK_ZIP_NAME=iOS_${LIB_STATIC_NAME}_${PACKAGE_DATE}_${CONFIGURATION}.zip
zip -qr ${SDK_ZIP_NAME} ${LIB_STATIC_NAME}
popd ${DISTRIBUTION_DIR}
