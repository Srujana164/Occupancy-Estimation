package com.debugandroid.VideoGallery;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
public class RecordActivity extends AppCompatActivity {
    public static final String LOG_TAG = "YOUR-TAG-NAME";
    public static final int RequestPermissionCode = 1;
    public static final double level0 = 1.31785114214E-5;
    public static final double level1 = 5.37057756314E-5;
    public static final double level2 = 9.73482039538E-5;
    public String userId;
    public AudioManager myAudioManager;
    public static String noise;
    WavRecorder wavRecorder;
    String filePath;
    //String s11;
    static double[] y_axis, y_axis1;
    double x_value = 0;
    static double[] x_axis;
    LineGraphSeries<DataPoint> series;
    String s,email;
    //String url = "http://127.0.0.1/users/index.php";
    String url = "http://172.16.144.194/users/index.php";
    //String url = "http://52a0190c.ngrok.io/users/index.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        s = getIntent().getStringExtra("location");
        email = getIntent().getStringExtra("email");
 //       final TextView t = (TextView) findViewById(R.id.textView);
 //       final TextView t1 = (TextView) findViewById(R.id.textView1);
        //        System.out.print(s);
//        String s = "Time in seconds";
//        String s1 = "noise value in 10^-5";
//            t1.setText(s);
//            t.setText(s1);
        record();  // recording of background noise starts in this function

        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        mFirebaseInstance = FirebaseDatabase.getInstance();
//        mFirebaseDatabase = mFirebaseInstance.getReference("noise_table");
//        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String appTitle = dataSnapshot.getValue(String.class);
////                getSupportActionBar().setTitle(appTitle);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//            }
//        });
    }

// record function: start recording of background noise for 20seconds and automatically stops after that and displays the graph which shows level of noise
    public void record() {
        Log.d(RecordActivity.LOG_TAG, "Application started");

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        if (checkPermission()) {
            try {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fileName.wav";
                Log.d(RecordActivity.LOG_TAG, filePath);
                wavRecorder = new WavRecorder(filePath);
                wavRecorder.startRecording(); //Recording started
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Toast.makeText(RecordActivity.this, "Recording started", Toast.LENGTH_LONG).show();
            final DateTime dateTime = new DateTime();
            //after 20s recording stopped and the audio file is processed to get sample values and calculated the noise
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    wavRecorder.stopRecording();
                    try {
                        y_axis1 = getAudioSample(filePath,dateTime); //Processing of audio file to get individual samples and find the noise level
                        y_axis = new double[y_axis1.length * 5];
                        for (int i5 = 0; i5 < y_axis1.length; i5++) {
                            int temp_var = i5 * 5;
                            for (int i6 = temp_var; i6 < temp_var + 5; i6++) {
                                y_axis[i6] = y_axis1[i5] * 100000;
                            }
                        }
                        x_axis = new double[y_axis.length];
                        for (int g = 0; g < y_axis.length; g++) {
                            x_axis[g] = x_value + 1;
                            x_value = x_axis[g];
                        }
                        series = new LineGraphSeries<>(data());
                        graph.addSeries(series);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScalableY(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setXAxisBoundsManual(true);
                        graph.getViewport().setMinX(0.0);
                        graph.getViewport().setMaxX(22.0);
                        graph.setTitle("graph");
                        File file = new File(filePath);
                        boolean deleted = file.delete();
                        } catch (IOException ex) {
                        }
                    record();

                }

            }, 20000);

        } else {
            requestPermission();
            record();
        }

    }
    public DataPoint[] data() {
        int n = x_axis.length;
        DataPoint[] values = new DataPoint[n];     //creating an object of type DataPoint[] of size 'n'
        for (int i = 0; i < n; i++) {
            DataPoint v = new DataPoint(x_axis[i], y_axis[i]);
            values[i] = v;
        }
        return values;
    }
// getAudioSample function : To process the audio file taking every 5seconds out of total 20seconds and calculating noise level for every 5s and sending to database
    public double[] getAudioSample(String path, DateTime dt) throws IOException {
        int i;
        FileInputStream is = null;
        is = new FileInputStream(path);
        byte[] data;
        try {
            data = IOUtils.toByteArray(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        int num_samples = (data.length / 2);
        double time = ((double) num_samples / (double) 8000);
        byte[] data1 = new byte[((int) Math.ceil(time)) * 16000];
        double[] arr_noise = new double[(int) Math.ceil(time) / 5];
        double[] x_axis = new double[(int) Math.ceil(time) / 5];

        System.arraycopy(data, 0, data1, 0, data.length);
        int y = 0;
        double noise;
        double temp1 = 0;

        for (i = 1; i <= ((int) Math.ceil(time) / 5); i++) {

            double noise1 = 0;
            double temp = 0;
            byte[] temp_data = new byte[80000];
            System.arraycopy(data1, y, temp_data, 0, 80000);
            ShortBuffer sb = ByteBuffer.wrap(temp_data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            short[] samples = new short[sb.limit()];
            sb.get(samples);
            for (int x = 0; x < samples.length; x++) {
                double h = (samples[x] / Math.pow(2, 15));
                temp = temp + Math.pow(h, 2);

            }
            temp1 = temp1 + temp;
            noise1 = Math.pow(temp, 0.5) / samples.length;
            String level="";

            if (noise1 <= level0) {
                level = "0";
                System.out.println("level0");
            } else if (noise1 > level0 && noise1 <= level1) {
                level = "1";
                System.out.println("level1");}
                else if (noise1 > level1 && noise1 <= level2){
                level = "2";
                System.out.println("level2");
                }
             else if (noise1 > level2) {
                level = "3";
                System.out.println("level3");
            }

        //    System.out.println(i);
            if (i == 1) {
                org.joda.time.format.DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
                String dtStr = fmt.print(dt);
                insertData(noise1, dtStr,s,email,level);
            } else {
                dt = dt.plusSeconds(5);
                org.joda.time.format.DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
                String dtStr = fmt.print(dt);
                insertData(noise1, dtStr,s,email,level);

            }
            arr_noise[i - 1] = noise1;


            y = y + 80000;

        }
        noise = Math.pow(temp1, 0.5) / num_samples;
        double n = 0;
        int k = 0;
        for (int h = 0; h < arr_noise.length; h++) {

            System.out.println(arr_noise[h]);

            if (arr_noise[h] <= level0) {
                System.out.println("level0");
            } else if (arr_noise[h] > level0 && arr_noise[h] <= level1) {
                System.out.println("level1");
            } else if (arr_noise[h] > level1 && arr_noise[h] <= level2) {
                System.out.println("level2");
            }
        }

        return arr_noise;
    }
    public void insertData(double noise, String TimeValue, String room,String emailid,String level){
        final String n = Double.toString(noise);
        final String t = TimeValue;
        final  String r = room;
        final String e = emailid;
        final String l = level;
        RequestQueue queue = Volley.newRequestQueue(RecordActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(RecordActivity.this, response, Toast.LENGTH_SHORT).show();
                Log.i("My success",""+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(RecordActivity.this, "my error :"+error, Toast.LENGTH_LONG).show();
                Log.i("My error",""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new HashMap<String, String>();
                map.put("noiselevel",n);
                map.put("TimeValue",t);
                map.put("room",r);
                map.put("email",e);
                map.put("level",l);

                return map;
            }
        };
        queue.add(request);
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(RecordActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(RecordActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RecordActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}


