package com.workerandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

public class ActivityHome extends AppCompatActivity {

    // Debug log tag.
    private final String TAG = this.getClass().getPackage() + " - " + this.getClass().getSimpleName();

    private EditText serverSourceEditor = null;
    private EditText serverDestinationEditor = null;
    private EditText iterationsEditor = null;
    private Button processTaskButton = null;
    private TextView progressLogTextView = null;

    private WorkerAndroidLogger logger= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Worker Android - TEST");
        initHandlers();
        initControls();
        setupButton();

    }

    private void initHandlers() {

        logger = new WorkerAndroidLogger(this);
        //logger = new WorkerAndroidLogger(null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Initialize app controls.
    private void initControls() {
        if (serverSourceEditor == null) {
            serverSourceEditor = (EditText) findViewById(R.id.server_source_editor);
        }

        if (serverDestinationEditor == null) {
            serverDestinationEditor = (EditText) findViewById(R.id.server_destination_editor);
        }

        if (iterationsEditor == null) {
            iterationsEditor = (EditText) findViewById(R.id.iterations_editor);
        }

        if (processTaskButton == null) {
            processTaskButton = (Button) findViewById(R.id.process_task_button);
        }

        if (progressLogTextView == null) {
            progressLogTextView = (TextView) findViewById(R.id.progress_log_text_view);

        }

    }

    private void setupButton() {

        processTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if SD card is present or not
                if (CheckForSDCard.isSDCardPresent()) {

                    String serverSource = serverSourceEditor.getText().toString();
                    String serverDestination = serverDestinationEditor.getText().toString();
                    int iterations= Integer.parseInt(iterationsEditor.getText().toString());

                    String workerName;
                    try {
                        InetAddress addr = InetAddress.getLocalHost();
                        workerName = addr.getHostName();
                    } catch (Exception e) {
                        workerName = "noName";
                    }

                    if (!TextUtils.isEmpty(serverSource) && !TextUtils.isEmpty(serverDestination)) {

                        progressLogTextView.setText("");

                        startNewJobRequestThread(serverSource,serverDestination,workerName,iterations);
                    } else {
                        Toast.makeText(getApplicationContext(), "The request url can not be empty.", Toast.LENGTH_LONG).show();
                    }

                }else

                {
                    Toast.makeText(getApplicationContext(),
                            "SD Card not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /* Start a thread to send http request to web server use HttpURLConnection object. */
    private void startNewJobRequestThread(final String serverSource, final String serverDestination, final String workerName, final int iterations) {

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {

                    WorkerAndroidManager wam = (WorkerAndroidManager.getInstance());

                    wam.setLogger(logger);
                    wam.setContext(getApplicationContext());

                    String it;

                    logger.logToUI(TAG, "Iniciando ciclo de " + iterations + " iteraciones");

                    for (int i = 0; i < iterations; i++) {

                        //logger
                        it = "Inicio Iteracion " + (i + 1);
                        //String it = new String(new char[10]).replace("\0", Integer.toString(i + 1));
                        logger.logToUI(TAG, it);
                        //logger

                        try {
                            wam.processNewJob(serverSource, serverDestination, workerName);
                        } catch (Exception ex) {
                            logger.logToUI(TAG, "Ocurrió un error - " + ex.getMessage());
                            Thread.sleep(1000);
                        }

                        //logger
                        it = "Fin Iteracion " + (i + 1);
                        logger.logToUI(TAG, it);
                        //logger

                    }

                    logger.logToUI(TAG, "Finalizado ciclo de " + iterations + " iteraciones");

                } catch (Exception ex) {

                    Log.e(TAG, ex.getMessage(), ex);

                    logger.logToUI(TAG, ex.getMessage());

                }

            }

        };
        Thread startNewJobRequestThread = new Thread(runnable);

        startNewJobRequestThread.start();
    }

    public void updateResults(String result) {

        progressLogTextView.append(result + "\n" + "\n");

    }

}
