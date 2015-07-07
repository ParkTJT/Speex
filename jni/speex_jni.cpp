#include <jni.h>

#include <string.h>
#include <unistd.h>

#include <speex/speex.h>
#include <speex/speex_preprocess.h>
#include "android/log.h"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"Speex_Park",__VA_ARGS__)


static int codec_open = 0;

static int dec_frame_size;
static int enc_frame_size;

static SpeexBits ebits, dbits;
void *enc_state;
void *dec_state;
SpeexPreprocessState *preprocess_state;//预处理接口
static JavaVM *gJavaVM;

extern "C"
JNIEXPORT jint JNICALL Java_com_park_speex_Speex_open
  (JNIEnv *env, jobject obj, jint compression) {
    int tmp;

    if (codec_open++ != 0)
        return (jint)0;

    speex_bits_init(&ebits);
    speex_bits_init(&dbits);

    enc_state = speex_encoder_init(&speex_nb_mode);
    dec_state = speex_decoder_init(&speex_nb_mode);
    tmp = compression;
    speex_encoder_ctl(enc_state, SPEEX_SET_QUALITY, &tmp);
    speex_encoder_ctl(enc_state, SPEEX_GET_FRAME_SIZE, &enc_frame_size);

   //降噪处理  需要引入<speex/speex_preprocess.h> 库       init方法传入去的是两个int类型 不是int的指针类型 int*
    preprocess_state = speex_preprocess_state_init(enc_frame_size, 8000);
    int denoise = 1;//原始值 ： 1
    int noiseSuppress = -25;//原始值 ： 25
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_DENOISE, &denoise); //降噪
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_NOISE_SUPPRESS, &noiseSuppress); //设置噪声的dB

    //增益处理
    int agc = 1;//原始值 ： 1
    int q=24000;//原始值 ：24000
    //actually default is 8000(0,32768),here make it louder for voice is not loudy enough by default. 8000
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_AGC, &agc);//增益
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_AGC_LEVEL,&q);


    //静音检测
    int vad = 1;//原始值 ： 1
    int vadProbStart = 80;//原始值 ：80
    int vadProbContinue = 65;//原始值 ： 65
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_VAD, &vad); //静音检测
            //Set probability required for the VAD to go from silence to voice
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_PROB_START , &vadProbStart);
            //Set probability required for the VAD to stay in the voice state (integer percent)
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_PROB_CONTINUE, &vadProbContinue);



    //解码处理器
    speex_decoder_ctl(dec_state, SPEEX_GET_FRAME_SIZE, &dec_frame_size);

    return (jint)0;
}

extern "C"
JNIEXPORT jint Java_com_park_speex_Speex_encode
    (JNIEnv *env, jobject obj, jshortArray lin, jint offset, jbyteArray encoded, jint size) {

        jshort buffer[enc_frame_size];
        jbyte output_buffer[enc_frame_size];
    int nsamples = (size-1)/enc_frame_size + 1;
    int i, tot_bytes = 0;

    if (!codec_open)
        return 0;

    speex_bits_reset(&ebits);

    for (i = 0; i < nsamples; i++) {
        env->GetShortArrayRegion(lin, offset + i*enc_frame_size, enc_frame_size, buffer);
        speex_preprocess_run(preprocess_state, buffer); //加了这句    降噪增益处理
        speex_encode_int(enc_state, buffer, &ebits);

    }
    //env->GetShortArrayRegion(lin, offset, enc_frame_size, buffer);
    //speex_encode_int(enc_state, buffer, &ebits);

    tot_bytes = speex_bits_write(&ebits, (char *)output_buffer,
                     enc_frame_size);
    env->SetByteArrayRegion(encoded, 0, tot_bytes,
                output_buffer);

        return (jint)tot_bytes;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_park_speex_Speex_decode
    (JNIEnv *env, jobject obj, jbyteArray encoded, jshortArray lin, jint size) {



        jbyte buffer[dec_frame_size];
        jshort output_buffer[dec_frame_size];
        jsize encoded_length = size;

    if (!codec_open)
        return 0;

    env->GetByteArrayRegion(encoded, 0, encoded_length, buffer);
    speex_bits_read_from(&dbits, (char *)buffer, encoded_length);
    speex_decode_int(dec_state, &dbits, output_buffer);
    env->SetShortArrayRegion(lin, 0, dec_frame_size,
                 output_buffer);

    return (jint)dec_frame_size;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_park_speex_Speex_getFrameSize
    (JNIEnv *env, jobject obj) {

    if (!codec_open)
        return 0;
    return (jint)enc_frame_size;

}

extern "C"
JNIEXPORT void JNICALL Java_com_park_speex_Speex_close
    (JNIEnv *env, jobject obj) {

    if (--codec_open != 0)
        return;

    speex_bits_destroy(&ebits);
    speex_bits_destroy(&dbits);
    speex_decoder_destroy(dec_state);
    speex_encoder_destroy(enc_state);
}
