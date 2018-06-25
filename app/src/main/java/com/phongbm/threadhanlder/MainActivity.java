package com.phongbm.threadhanlder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MESSAGE_UPDATE_TIME = 100;
    private static final int MESSAGE_FINISH_TIME = 101;

    private TextView txtTime;
    private ImageView imgAvatar;
    private Button btnStart;
    private Button btnDownload;
    private SeekBar sbTime;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_UPDATE_TIME:
                        txtTime.setText(String.valueOf(msg.arg1));
                        break;

                    case MESSAGE_FINISH_TIME:
                        Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }
        };

        startSongTime();
    }

    private void initializeComponents() {
        txtTime = (TextView) findViewById(R.id.txt_time);
        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnDownload = (Button) findViewById(R.id.btn_download);
        sbTime = (SeekBar) findViewById(R.id.sb_time);

        btnStart.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
    }

    private void startSongTime() {
        sbTime.setMax(30);
        SongTimeUpdater songTimeUpdater = new SongTimeUpdater();
        songTimeUpdater.execute();
    }

    private class SongTimeUpdater extends AsyncTask<Void, Integer, Void> {
        private int max;

        @Override
        protected void onPreExecute() {
            max = sbTime.getMax();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i <= max; i++) {
                publishProgress(i);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            sbTime.setProgress(values[0]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                startTime();
                break;

            case R.id.btn_download:
                ImageDownloader downloader = new ImageDownloader();
                downloader.execute("https://i.ytimg.com/vi/6MA0s1A81lw/hqdefault.jpg");
                break;

            default:
                break;
        }
    }

    private void startTime() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 10; i++) {
                    // txtTime.setText(String.valueOf(i));
                    Message message = new Message();
                    message.what = MESSAGE_UPDATE_TIME;
                    message.arg1 = i;
                    handler.sendMessage(message);

                    if (i == 10) {
                        // Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                        // Message msg = new Message();
                        // msg.what = MESSAGE_FINISH_TIME;
                        // handler.sendMessage(msg);
                        handler.sendEmptyMessage(MESSAGE_FINISH_TIME);

                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            /*for(int i = 1; i <= 10; i++) {
                publishProgress(i);
            }*/

            try {
                String link = params[0];
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                connection.disconnect();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /*@Override
        protected void onProgressUpdate(Integer... values) {
        }*/

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // bitmap.getWidth();
            // bitmap.getHeight();
            // imgAvatar.setLayoutParams(...);
            imgAvatar.setImageBitmap(bitmap);
        }
    }

}