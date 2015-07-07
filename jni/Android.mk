# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := speex
LOCAL_CFLAGS = -DFIXED_POINT -DUSE_KISS_FFT -DEXPORT="" -UHAVE_CONFIG_H  
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include  

LOCAL_SRC_FILES := \
./speex_jni.cpp \
./libspeex/buffer.c \
./libspeex/bits.c \
./libspeex/cb_search.c \
./libspeex/exc_10_16_table.c \
./libspeex/exc_10_32_table.c \
./libspeex/exc_20_32_table.c \
./libspeex/exc_5_256_table.c \
./libspeex/exc_5_64_table.c \
./libspeex/exc_8_128_table.c \
./libspeex/fftwrap.c \
./libspeex/filterbank.c \
./libspeex/filters.c \
./libspeex/gain_table.c \
./libspeex/gain_table_lbr.c \
./libspeex/hexc_10_32_table.c \
./libspeex/hexc_table.c \
./libspeex/high_lsp_tables.c \
./libspeex/jitter.c \
./libspeex/kiss_fft.c \
./libspeex/kiss_fftr.c \
./libspeex/lpc.c \
./libspeex/lsp.c \
./libspeex/lsp_tables_nb.c \
./libspeex/ltp.c \
./libspeex/mdf.c \
./libspeex/modes.c \
./libspeex/modes_wb.c \
./libspeex/nb_celp.c \
./libspeex/preprocess.c \
./libspeex/quant_lsp.c \
./libspeex/resample.c \
./libspeex/sb_celp.c \
./libspeex/scal.c \
./libspeex/smallft.c \
./libspeex/speex.c \
./libspeex/speex_callbacks.c \
./libspeex/speex_header.c \
./libspeex/stereo.c \
./libspeex/vbr.c \
./libspeex/vq.c \
./libspeex/window.c \

  


include $(BUILD_SHARED_LIBRARY)
