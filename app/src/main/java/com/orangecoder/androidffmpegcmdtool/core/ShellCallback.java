package com.orangecoder.androidffmpegcmdtool.core;

/**
 * Created by xpmbp on 16/1/19.
 */
public interface ShellCallback {

    public void shellOut(String shellLine);

    public void processComplete(int exitValue);

}
