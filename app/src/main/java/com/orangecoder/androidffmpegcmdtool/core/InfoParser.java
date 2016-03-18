package com.orangecoder.androidffmpegcmdtool.core;

/**
 * Created by xpmbp on 16/1/19.
 */
public class InfoParser implements ShellCallback{
    private Clip mMedia;
    private int retValue;

    public InfoParser (Clip media) {
        mMedia = media;
    }

    @Override
    public void shellOut(String shellLine) {
        if (shellLine.contains("Duration:")){
            //	Duration: 00:01:01.75, start: 0.000000, bitrate: 8184 kb/s
            String[] timecode = shellLine.split(",")[0].split(":");
            double duration = 0;
            duration = Double.parseDouble(timecode[1].trim())*60*60; //hours
            duration += Double.parseDouble(timecode[2].trim())*60; //minutes
            duration += Double.parseDouble(timecode[3].trim()); //seconds
            mMedia.duration = duration;

            String bitrateline = shellLine.substring(shellLine.indexOf("bitrate:"));
            String[] bitrateInfo = bitrateline.split(":");
            String[] bitrate = bitrateInfo[1].trim().split(" ");
            mMedia.videoBitrate = Integer.parseInt(bitrate[0]);
        }
        //   Stream #0:0(eng): Video: h264 (High) (avc1 / 0x31637661), yuv420p, 1920x1080, 16939 kb/s, 30.02 fps, 30 tbr, 90k tbn, 180k tbc
        else if (shellLine.contains(": Video:")){
            String[] line = shellLine.split(":");
            String[] videoInfo = line[3].split(",");
            mMedia.videoCodec = videoInfo[0];

            String[] Line1 = shellLine.split(",");

            String[] resolutionInfo = Line1[2].trim().split(" ");
            String resolution = resolutionInfo[0].trim();
            String[] wh = resolution.split("x");
            mMedia.width = Integer.parseInt(wh[0]);
            mMedia.height = Integer.parseInt(wh[1]);

            String[] fpsInfo = Line1[4].trim().split(" ");
            String fps = fpsInfo[0].trim();
            mMedia.videoFps = fps;
        }
        //Stream #0:1(eng): Audio: aac (mp4a / 0x6134706D), 48000 Hz, stereo, s16, 121 kb/s
        else if (shellLine.contains(": Audio:")){
            String[] line = shellLine.split(":");
            String[] audioInfo = line[3].split(",");

            mMedia.audioCodec = audioInfo[0];
        }
        //rotate  : 90
        else if (shellLine.contains("rotate")) {
            String[] rotate = shellLine.split(":");
            mMedia.rotate = rotate[1].trim();
        }
        //Stream #0.0(und): Video: h264 (Baseline), yuv420p, 1280x720, 8052 kb/s, 29.97 fps, 90k tbr, 90k tbn, 180k tbc
        //Stream #0.1(und): Audio: mp2, 22050 Hz, 2 channels, s16, 127 kb/s
    }

    @Override
    public void processComplete(int exitValue) {
        retValue = exitValue;
    }
}
