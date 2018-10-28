package com.workerandroid;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.lang.annotation.Target;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFprobe;
import nl.bravobit.ffmpeg.FFtask;

public class FFMpegManager {

    private final String TAG = this.getClass().getPackage() + " - " + this.getClass().getSimpleName();

    private WorkerAndroidLogger logger = null;

    private Handler handler = new Handler(Looper.getMainLooper());
    private StorageManager storageManager;

    public void setLogger(WorkerAndroidLogger logger) {this.logger = logger;}

    public void setStorageManager(StorageManager storageManager){this.storageManager = storageManager;}

    private static class LazyHolder {
        private static final FFMpegManager instance = new FFMpegManager();
    }

    public static FFMpegManager getInstance() {
        return LazyHolder.instance;
    }

    public void doWork(final Context context, final String[] ffmpegParams, final String destinationFilename){

        //logger.logToUI(TAG,"isSupported = " + FFmpeg.getInstance(context).isSupported());

        if (FFmpeg.getInstance(context).isSupported()) {

            FFmpeg ffmpeg = FFmpeg.getInstance(context);
            String result = ffmpeg.execute(ffmpegParams);

            //logger.logToUI(TAG,result);

            /*
            FFmpeg.getInstance(context).execute(ffmpegParams, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    //logger.logToUI(TAG, "on start");
                }

                @Override
                public void onFinish() {
                    //logger.logToUI(TAG, "on finish");
                    logger.logToUI(TAG,"archivo temporal " + ffmpegParams[ffmpegParams.length-1]);
                    logger.logToUI(TAG,"renombrando archivo temporal a " + destinationFilename);
                    storageManager.renameFile(ffmpegParams[ffmpegParams.length-1],destinationFilename);
                }

                @Override
                public void onSuccess(String message) {
                    //logger.logToUI(TAG, message);
                }

                @Override
                public void onProgress(String message) {
                    //logger.logToUI(TAG, message);
                }

                @Override
                public void onFailure(String message) {
                    logger.logToUI(TAG, message);
                }
            });
*/
        }else{

            logger.logToUI(TAG,"FFMPEG Not supported");

        }

    }
/*
    private void versionFFmpeg() {
        FFmpeg.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                logger.logToUI(TAG,message);
            }

            @Override
            public void onProgress(String message) {
                logger.logToUI(TAG,message);
            }
        });

    }

    private void versionFFprobe() {
        logger.logToUI(TAG,"version ffprobe");
        FFprobe.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                logger.logToUI(TAG,message);
            }

            @Override
            public void onProgress(String message) {
                logger.logToUI(TAG,message);
            }
        });
    }
*/

}
