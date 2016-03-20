package com.ra.com.audioflasher;

import android.media.audiofx.Visualizer;
import android.util.Log;

import com.ra.com.audioflasher.analyzer.DataAnalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/10/1.
 */
public class AudioReader {
    String TAG = AudioReader.class.getSimpleName();
    private Visualizer mVisualizer;
    private DataAnalyzer mDataAnalyzer;
    private FileWriter fw= null;
    private BufferedWriter buffw = null;
    private PrintWriter pw = null;


    public AudioReader(int SessionID,final boolean writeFile) {
        String folderPath = Tools.getSDCardPath()+"AudioFlash/";
        Tools.ensureFilePathExist(folderPath);
        Tools.clearFolder(folderPath);
        File tofile=new File(folderPath+"data.txt");
        final long initTime = System.currentTimeMillis();
        try {
            fw = new FileWriter(tofile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffw=new BufferedWriter(fw);
        pw=new PrintWriter(buffw);

        mVisualizer = new Visualizer(SessionID);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
//                updateVisualizer(bytes);
                if(mDataAnalyzer!=null)
                {
                    if(mDataAnalyzer.analyzeWave(bytes))
                        mDataAnalyzer.updateView();
                }
                StringBuffer sb = new StringBuffer();
                SimpleDateFormat    formatter = new SimpleDateFormat("HH:mm:ss:SSS -->");
                Date    curDate    =   new Date(System.currentTimeMillis()-initTime);//获取当前时间
                String    str    =    formatter.format(curDate);
                sb.append("wave data-->");
                sb.append(str);
                for(byte b:bytes)
                {
                    sb.append(b);
                    sb.append(" ");
                }
                if(writeFile)
                    pw.println(sb.toString());
//                Log.d(TAG, sb.toString());
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                byte[] model = new byte[bytes.length / 2 + 1];
                model[0] = (byte) Math.abs(bytes[1]);
                int j = 1;

                for (int i = 2; i < (bytes.length - 2) / 2; ) {
                    model[j] = (byte) Math.hypot(bytes[i], bytes[i + 1]);
                    i += 2;
                    j++;
                }

//                if(mDataAnalyzer!=null)
//                {
//                    mDataAnalyzer.analyzeFFT(bytes);
//                }
//                Log.d(TAG, "fft data-->" + bytes[0] + " " + bytes[1] + " " + bytes[2] + " " + bytes[3] + " " + bytes[4] + " " + bytes[5]);
//                updateVisualizerFFT(model);
            }
        };

        mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate(), true, true);

        // Enabled Visualizer and disable when we're done with the stream
        mVisualizer.setEnabled(true);
    }

    public void linkAnalyzer(DataAnalyzer analyzer)
    {
        mDataAnalyzer = analyzer;
    }

    public void release() {
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }

        if(pw!=null&&buffw!=null&&fw!=null)
        {
            pw.close();
            try {
                buffw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
