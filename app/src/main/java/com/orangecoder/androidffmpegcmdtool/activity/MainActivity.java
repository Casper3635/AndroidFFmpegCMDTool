package com.orangecoder.androidffmpegcmdtool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.orangecoder.androidffmpegcmdtool.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_gif).setOnClickListener(this);
        findViewById(R.id.btn_pic).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gif:
                clickedGIF();
                break;
            case R.id.btn_pic:
                clickedPIC();
                break;
        }
    }


    private void clickedGIF() {
        String url = "http://7o50tq.com2.z0.glb.qiniucdn.com/LE19rw_rBz22oQfJG-5ImnhDHq8=/Fj_h4GcPQfRwp5bvTyWbdAjcPYeF";
        String videoAuthor = "orangecoder";

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), VideoProcessActivity.class);
        intent.putExtra(VideoProcessActivity.KEY_PROCESS_TYPE,
                VideoProcessActivity.VALUE_GENERATION_GIF);
        intent.putExtra(VideoProcessActivity.KEY_VIDEO＿URL, url);
        intent.putExtra(VideoProcessActivity.KEY_VIDEO_AUTHOR, videoAuthor);
        startActivity(intent);
    }

    private void clickedPIC() {
        String shareUrl = "http://orangercoder.com";
        String url = "http://7o50tq.com2.z0.glb.qiniucdn.com/LE19rw_rBz22oQfJG-5ImnhDHq8=/Fj_h4GcPQfRwp5bvTyWbdAjcPYeF";
        String videoAuthor = "orangecoder";

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), VideoProcessActivity.class);
        intent.putExtra(VideoProcessActivity.KEY_PROCESS_TYPE,
                VideoProcessActivity.VALUE_GENERATION_PIC);
        intent.putExtra(VideoProcessActivity.KEY_SHARE_URL, shareUrl);
        intent.putExtra(VideoProcessActivity.KEY_VIDEO＿URL, url);
        intent.putExtra(VideoProcessActivity.KEY_VIDEO_AUTHOR, videoAuthor);
        startActivity(intent);
    }


}
