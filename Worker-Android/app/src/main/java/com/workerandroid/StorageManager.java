package com.workerandroid;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class StorageManager {

    private final String TAG = this.getClass().getPackage() + " - " + this.getClass().getSimpleName();

    private static class LazyHolder {
        private static final StorageManager instance = new StorageManager();
    }

    public static StorageManager getInstance() {
        return StorageManager.LazyHolder.instance;
    }

    public void saveToFileSystem(byte[] data, String storePath, String fileName){

        try {

            File directory = new File(storePath);
            int i=0;
            Log.d(TAG, " guardando archivo " + storePath + fileName );

            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (FileOutputStream output = new FileOutputStream(storePath + fileName)) {
                output.write(data);
            }
            i++;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    public byte[] getFromFileSystem(String storePath, String fileName){

        Log.d(TAG, " obteniendo archivo " + storePath + fileName );

        File file = new File(storePath+fileName);
        int size = (int) file.length();
        byte[] data = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(data, 0, data.length);
            buf.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return data;

    }

    public boolean renameFile(String sourceFilename, String destinationFilename){

        File sourceFile = new File(sourceFilename);
        File destinationFile = new File(destinationFilename);

        if(sourceFile.exists()) {
            sourceFile.renameTo(destinationFile);
            return true;
        }

        return false;

    }

    public boolean fileExists(String fileName){
        File file = new File(fileName);
        return file.exists();
    }

}