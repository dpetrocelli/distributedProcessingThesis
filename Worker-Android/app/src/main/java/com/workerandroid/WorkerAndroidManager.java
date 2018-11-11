package com.workerandroid;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Target;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class WorkerAndroidManager {

    private final String TAG = this.getClass().getPackage() + " - " + this.getClass().getSimpleName();

    private WorkerAndroidLogger logger = null;

    private static final String REQUEST_METHOD_GET = "GET";
    private static final String REQUEST_METHOD_POST = "POST";
    private Context context;

    public void setLogger(WorkerAndroidLogger logger) {
        this.logger = logger;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    private static class LazyHolder {
        private static final WorkerAndroidManager instance = new WorkerAndroidManager();
    }

    public static WorkerAndroidManager getInstance() {
        return LazyHolder.instance;
    }

    public void processNewJob(String serverSource, String serverDestination, String workerName) {

        JobMessage newTask;
        JobMessage taskResult;

        logMessage(TAG, " obteniendo trabajo de la cola ");
        newTask = this.getJobFromQueue(serverSource, workerName);

        newTask.setWorkerName(workerName);
        newTask.setWorkerArchitecture(System.getProperty("os.arch"));

        logMessage(TAG, " procesando tarea ");
        taskResult = this.processTask(newTask);

        logMessage(TAG, " subiendo resultado a la cola ");
        this.putJobResultIntoQueue(serverDestination, workerName, taskResult);

    }

    private JobMessage processTask(JobMessage task) {

            StorageManager sm = StorageManager.getInstance();

            String downloadFolder = Environment.getExternalStorageDirectory() + File.separator + "WorkerAndroid" + File.separator + "temp" + File.separator;
            String outputFolder = Environment.getExternalStorageDirectory() + File.separator + "WorkerAndroid" + File.separator + "temp" + File.separator;
            String extension = ".mp4";

            String downloadedFileName = "temporal_" + task.name + "_" + task.getPart() + extension;
            String outputFileName = task.getService() + "_" + task.name + "_" + task.getPart() + extension;

            File sourceFile = new File(downloadFolder + downloadedFileName);
            File destinationFile = new File(outputFolder + outputFileName);

            JsonUtility jsonUt = new JsonUtility();

            jsonUt.setObject(task);

            if(sourceFile.exists())
                sourceFile.delete();

            //proccess something
            //logMessage(TAG, " guardando archivo " + downloadFolder + downloadedFileName);
            sm.saveToFileSystem(task.getData(), downloadFolder, downloadedFileName);


            // Obtain parameters from msg
            String parametersFromMsg = task.getParamsEncoding();
            //logMessage(TAG, "parameterFromMsg: " + parametersFromMsg);

            parametersFromMsg = parametersFromMsg.substring(1, ((parametersFromMsg.length() - 1)));
            //logMessage(TAG, "parameterFromMsg: " + parametersFromMsg);

            String[] paramsPart = parametersFromMsg.split(Pattern.quote(","));


            String jsonParams = " -loglevel quiet"
                    + " -y"
                    + " -i " + downloadFolder + downloadedFileName
                    + " -s" + paramsPart[1]
                    + " -aspect 16:9 -c:v" + paramsPart[2]
                    + " -g 50 -b:v" + paramsPart[3]
                    + "k -profile:v " + paramsPart[0]
                    + " -level" + paramsPart[4]
                    + " -r" + paramsPart[5]
                    + " -preset" + paramsPart[6]
                    + " -threads 0"
                    + " -c:a aac"
                    + " -strict experimental"
                    + " -b:a" + paramsPart[11] + "k"
                    + " -ar" + paramsPart[12]
                    + " -ac" + paramsPart[13]
                    + " " + outputFolder + outputFileName;

            //logMessage(TAG, "jsonParams : " + jsonParams);

            String[] params =
                    {
                            "-loglevel"
                            , "info"
                            , "-y"
                            , "-i", downloadFolder + downloadedFileName
                            , "-s", paramsPart[1]
                            , "-aspect", "16:9", "-c:v", paramsPart[2].trim()
                            , "-g", "50"
                            , "-b:v", paramsPart[3].trim() + "k"
                            , "-profile:v", paramsPart[0].trim()
                            , "-level", paramsPart[4].trim()
                            , "-r", paramsPart[5].trim()
                            , "-preset", paramsPart[6].trim()
                            , "-threads", "0"
                            , "-c:a", "aac"
                            , "-strict", "experimental"
                            , "-b:a", paramsPart[11].trim() + "k"
                            , "-ar", paramsPart[12].trim()
                            , "-ac", paramsPart[13].trim()
                            , outputFolder + outputFileName
                    };

            //logMessage(TAG,"params: " + params);

            //powerShellProcess = Runtime.getRuntime().exec(params);
            //proccess something
            String[] ffmpegParams = {"-i"
                    , downloadFolder + downloadedFileName
                    , outputFolder + outputFileName};

            if(destinationFile.exists())
                destinationFile.delete();

            FFMpegManager ffm = (FFMpegManager.getInstance());

            //logMessage(TAG, "setting up logger: ");
            ffm.setLogger(logger);
            ffm.setStorageManager(sm);

            long taskInitTime, taskEndTime;

            taskInitTime = System.currentTimeMillis();
            ffm.doWork(context, params, outputFolder + outputFileName);
            taskEndTime = System.currentTimeMillis();

            /*
            int i = 0;

            synchronized (this) {
                while (i < 30 && !destinationFile.exists()) {
                    //logMessage(TAG, " esperando archivo " + outputFolder + outputFileName);

                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logMessage(TAG, e.getMessage());
                    }
                    i++;
                }
            }
            */

            //logMessage(TAG, " obteniendo archivo " + outputFolder + outputFileName);
            byte[] data = sm.getFromFileSystem(outputFolder, outputFileName);

            JobMessage taskResult =
                    new JobMessage(
                            task.getOriginalName()
                            , task.getName()
                            , task.getPart()
                            , task.getqParts()
                            , task.getService()
                            , data
                            , task.getParamsEncoding()
                            , task.getIdForAck()
                            , task.getWorkerName()
                            , task.getWorkerArchitecture()
                            , taskInitTime
                            , taskEndTime
                            ,(int) (taskEndTime-taskInitTime));

            jsonUt.setObject(taskResult);

            sourceFile.delete();
            destinationFile.delete();

            return taskResult;

    }

    private JobMessage getJobFromQueue(final String serverSource,final String workerName) {

        HttpURLConnection httpConn = null;
        InputStreamReader isReader = null;
        BufferedReader bufReader = null;
        StringBuffer readTextBuf = null;
        JobMessage task = null;

        try {

            URL url = new URL("http://" + serverSource + "/getJob?name=" + workerName);

            //logMessage(TAG, " Estableciendo conexion a " + url);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod(REQUEST_METHOD_GET);
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);

            InputStream inputStream = httpConn.getInputStream();
            isReader = new InputStreamReader(inputStream);
            bufReader = new BufferedReader(isReader);
            String line = bufReader.readLine();
            readTextBuf = new StringBuffer();

            while (line != null) {
                readTextBuf.append(line);
                line = bufReader.readLine();
            }

            //logMessage(TAG, " mensaje - " + readTextBuf.toString());

            JsonUtility jsonUt = new JsonUtility();
            jsonUt.setType("JobMessage");

            task = (JobMessage) jsonUt.fromJson(readTextBuf.toString());

        } catch (MalformedURLException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }

        finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                    bufReader = null;
                }

                if (isReader != null) {
                    isReader.close();
                    isReader = null;
                }

                if (httpConn != null) {
                    httpConn.disconnect();
                    httpConn = null;
                }

            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }

        return task;

    }

    private void putJobResultIntoQueue(final String serverDestination,final String workerName, JobMessage message) {

        HttpURLConnection httpConn = null;
        InputStreamReader isReader = null;
        BufferedReader bufReader = null;

        try {

            JsonUtility jsonUt = new JsonUtility();

            jsonUt.setObject(message);

            String msgEncoded = jsonUt.toJson();
            //logMessage(TAG, " mensaje " + msgEncoded);

            /*
            URL url = new URL("http://" + serverDestination + "/uploadFinishedJob?server=" + workerName + "&name=" + message.getName() + "&part=" + (message.getName() + "_part_" + message.getPart() + "&idForAck=" + message.getIdForAck()));
            */
            URL url = new URL("http://" + serverDestination + "/uploadFinishedJob");

            //logMessage(TAG, " Estableciendo conexion a " + url);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod(REQUEST_METHOD_POST);
            httpConn.setRequestProperty("User-Agent", "Java client");
            httpConn.setRequestProperty("Content-Type", "application/json");

            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(httpConn.getOutputStream()))) {

                pw.write(msgEncoded);
            }

            StringBuilder content;

            try(BufferedReader in1 = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()))) {
                String line2;
                content = new StringBuilder();

                while ((line2 = in1.readLine()) != null)

                {
                    content.append(line2);
                    content.append(System.lineSeparator());
                }
            }
        } catch (MalformedURLException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }

        finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                    bufReader = null;
                }

                if (isReader != null) {
                    isReader.close();
                    isReader = null;
                }

                if (httpConn != null) {
                    httpConn.disconnect();
                    httpConn = null;
                }

            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }

    }

    private void logMessage(String TAG, String stringToLog){

        logger.logToUI(TAG,stringToLog);

    }

}