package com.park.play;

/**
 * Speex��Ƶ������ɼ���
 * @author Park
 *
 */
public interface OnSpeexCompletionListener {
    void onCompletion(SpeexDecoder speexdecoder);
    void onError(Exception ex);
}
