package com.orangecoder.androidffmpegcmdtool.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xpmbp on 16/1/19.
 */
public class StreamGobbler extends Thread{
    private InputStream is;
    private String type;
    private ShellCallback sc;

    StreamGobbler(InputStream is, String type, ShellCallback sc) {
        this.is = is;
        this.type = type;
        this.sc = sc;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null){
                if (sc != null){
                    sc.shellOut(line);
                }
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
