package com.ra.com.audioflasher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import com.ra.com.audioflasher.analyzer.DataAnalyzer;


public class MainActivity extends Activity {

    private FlashView flash_view;
    private Button btn_play;
    private Button btn_stop;
    private Button btn_pr;
    private SeekBar seekBar;
    private AudioReader audioReader;
    private TextView tv_fun;
    private static Object MediaLock = new Object();
    private MyMediaPlayer myPlayer;
    private DataAnalyzer mDataAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Uri uri = getIntent().getData();

        myPlayer = new MyMediaPlayer(MainActivity.this,uri);
        myPlayer.linkSeekBar(seekBar, tv_fun);
        btn_play.setEnabled(false);
        audioReader = new AudioReader(myPlayer.getAudioSessionId(),false);
        mDataAnalyzer = new DataAnalyzer();
        mDataAnalyzer.linkFlashView(flash_view);
        audioReader.linkAnalyzer(mDataAnalyzer);
    }


    private void initView()
    {
        flash_view = (FlashView)findViewById(R.id.flash_view);
        tv_fun = (TextView) findViewById(R.id.tv_fun);
        btn_play = (Button)findViewById(R.id.btn_play);
        btn_stop = (Button)findViewById(R.id.btn_stop);
        btn_pr = (Button)findViewById(R.id.btn_pr);
        seekBar = (SeekBar)findViewById(R.id.sbar_media);
    }


    public void playPressed(View view) {
        if(myPlayer.play())
        {
            btn_pr.setText(R.string.btn_pause);
            btn_pr.setEnabled(true);
            btn_play.setEnabled(false);
        }
    }

    public void stopPressed(View view) {
        if(myPlayer.stop())
        {
            btn_pr.setText(R.string.btn_pause);
            btn_pr.setEnabled(false);
            btn_play.setEnabled(true);
        }
    }

    public void prPressed(View view) {

        if(myPlayer.getState() == MyMediaPlayer.Play_State.PAUSE)
        {
            if(myPlayer.resume())
                btn_pr.setText(R.string.btn_pause);
        }else if(myPlayer.getState() == MyMediaPlayer.Play_State.PLAYING)
        {
            if(myPlayer.pause())
                btn_pr.setText(R.string.btn_resume);
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            release();
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }

    private void release()
    {
        if(myPlayer != null)
            myPlayer.release();
        if(audioReader != null)
            audioReader.release();
    }
}
