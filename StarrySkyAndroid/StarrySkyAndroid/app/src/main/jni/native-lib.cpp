//
// Created by sbin on 2017-10-29.
//

#include<jni.h>
#include<string>
#include<android/log.h>
#include<android/bitmap.h>
extern "C"
JNIEXPORT jstring JNICALL Java_com_starrysky_helper_PicHelperExt_stringFromJNI(JNIEnv *env, jobject obj)
{
    return env->NewStringUTF("hello qhy.");
}
int  brightness(int x)
{
    return x & 0x0000FF;
}

JNIEXPORT void  JNICALL Java_com_starrysky_helper_PicHelperExt_colorImageIfNeededCPP(JNIEnv *env, jobject obj, jobject bmpObj)
{
//    AndroidBitmapInfo bmp={0};
//    if(AndroidBitmap_getInfo(env,bmpObj,&bmp)<0)
//    {
//        return ;
//    }
//    int* dataFromBmp = NULL;
//    if(AndroidBitmap_lockPixels(env,bmpObj,(void**)&dataFromBmp))
//
//    int r = 0, g = 0, b = 0;
//    int wVal = bmp.width - 2;
//    int hVal = bmp.height - 2;
//    int tmpVal  = 0;
//    int col = 0;
//    int row = 0;
//    for (int x = 2; x < wVal; x++) {
//        for (int y = 2; y < hVal; y++) {
//            col = x % 2;
//            row = y % 2;
//            tmpVal = col + row ;
//            //0,0 B    1,1 R    0,1 1,0 G
//            if (tmpVal == 0) {
//                // B
//                b = (int) (0.5 * (brightness( *((*(dataFromBmp+x)) + (y - 1))) + brightness(bmp.getPixel(x, y + 1))));
//                g = brightness(bmp.getPixel(x, y));
//                r = (int) (0.5 * (brightness(bmp.getPixel(x - 1, y)) + brightness(bmp.getPixel(x + 1, y))));
//
//            } else if (tmpVal  == 1) {
//                // G
//                if (row == 0) {
//                    r = brightness(bmp.getPixel(x, y));
//                    g = (int) (0.25 * (brightness(bmp.getPixel(x - 1, y)) + brightness(bmp.getPixel(x + 1, y)) + brightness(bmp.getPixel(x, y - 1)) + brightness(bmp.getPixel(x, y + 1))));
//                    b = (int) (0.25 * (brightness(bmp.getPixel(x + 1, y - 1)) + brightness(bmp.getPixel(x + 1, y + 1)) + brightness(bmp.getPixel(x - 1, y - 1)) + brightness(bmp.getPixel(x - 1, y + 1))));
//                } else {
//                    r = (int) (0.25 * (brightness(bmp.getPixel(x + 1, y - 1)) + brightness(bmp.getPixel(x + 1, y + 1)) + brightness(bmp.getPixel(x - 1, y - 1)) + brightness(bmp.getPixel(x - 1, y + 1))));
//                    g = (int) (0.25 * (brightness(bmp.getPixel(x - 1, y)) + brightness(bmp.getPixel(x + 1, y)) + brightness(bmp.getPixel(x, y - 1)) + brightness(bmp.getPixel(x, y + 1))));
//                    b = brightness(bmp.getPixel(x, y));
//                }
//            } else {
//                // R
//                r = (int) (0.5 * (brightness(bmp.getPixel(x, y - 1)) + brightness(bmp.getPixel(x, y + 1))));
//                g = brightness(bmp.getPixel(x, y));
//                b = (int) (0.5 * (brightness(bmp.getPixel(x - 1, y)) + brightness(bmp.getPixel(x + 1, y))));
//
//            }
//            bmp.setPixel(x, y, colorrgb(r, g, b));
//        }
//        AndroidBitmap_unlockPixels(env,bmpObj);
//    }
    return ;
}

JNIEXPORT jint JNICALL Java_com_starrysky_helper_PicHelperExt_brightness(JNIEnv *env,jobject ,jint rgb)
{
    return rgb & 0x0000FF;
}

JNIEXPORT jint JNICALL Java_com_starrysky_helper_PicHelperExt_brightnessADD(JNIEnv *env,jobject ,jint a ,jint b)
{
    return a + b;
}

JNIEXPORT jint JNICALL Java_com_starrysky_helper_PicHelperExt_brightnessADD4(JNIEnv *env,jobject ,jint a ,jint b ,jint c,jint d)
{
    return a + b  + c + d;
}

JNIEXPORT jint JNICALL Java_com_starrysky_helper_PicHelperExt_brightnessfun1(JNIEnv *env,jobject ,jint a )
{
    return  0.5 * a;
}
JNIEXPORT jint JNICALL Java_com_starrysky_helper_PicHelperExt_brightnessfun2(JNIEnv *env,jobject ,jint a )
{
    return  0.25 * a;
}