package com.orangecoder.androidffmpegcmdtool.core;


import android.content.Context;

import com.orangecoder.androidffmpegcmdtool.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FfmpegController {

	private String mFfmpegBin;
	private String mVideoLogo;
	
	private static FfmpegController mInstance;
	public static FfmpegController getInstance(Context context) {
		if(mInstance == null) {
			try {
				mInstance = new FfmpegController(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mInstance;
	}
	
	private FfmpegController(Context context) throws Exception {
		mFfmpegBin = copyRawFile(context, R.raw.ffmpeg, "ffmpeg");
		// Change the permissions
		Runtime.getRuntime().exec("chmod 0755 "+mFfmpegBin).waitFor();

		mVideoLogo = copyRawFile(context, R.raw.videologo, "videologo.png");
	}
	
	private String copyRawFile(Context ctx, int resid, String filename)
								throws IOException, InterruptedException {
		File file = new File(ctx.getDir("ffmpeg", 0), filename);
		if (file.exists()) {
			file.delete();
		}

		final FileOutputStream out = new FileOutputStream(file);
		final InputStream is = ctx.getResources().openRawResource(resid);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();

		return file.getCanonicalPath();
	}

	private void execFFMPEG (List<String> cmd, ShellCallback sc)
							throws IOException, InterruptedException {
		execFFMPEG(cmd, sc, new File(mFfmpegBin).getParentFile());
	}
	
	private void execFFMPEG (List<String> cmd, ShellCallback sc, File fileExec)
							throws IOException, InterruptedException {
		execProcess(cmd, sc, fileExec);
	}
	
	private int execProcess(List<String> cmds, ShellCallback sc, File fileExec)
							throws IOException, InterruptedException {
		//ensure that the arguments are in the correct Locale format
		for (String cmd : cmds) {
			cmd = String.format(Locale.US, "%s", cmd);
		}

		StringBuffer cmdlog = new StringBuffer();
		for (String cmd : cmds) {
			cmdlog.append(cmd);
			cmdlog.append(' ');
		}
		sc.shellOut(cmdlog.toString());

		ProcessBuilder pb = new ProcessBuilder(cmds);
		pb.directory(fileExec);
		//pb.redirectErrorStream(true);
		Process process = pb.start();

		// any error message?
		StreamGobbler errorGobbler = new StreamGobbler(
				process.getErrorStream(), "ERROR", sc);

    	 // any output?
        StreamGobbler outputGobbler = new 
            StreamGobbler(process.getInputStream(), "OUTPUT", sc);

        errorGobbler.start();
        outputGobbler.start();

        int exitVal = process.waitFor();
        
        sc.processComplete(exitVal);
        
        return exitVal;
	}

	public void processVideo(Clip in, Clip out, boolean enableExperimental,
							 ShellCallback sc) throws Exception {
    	ArrayList<String> cmd = new ArrayList<String>();

		cmd.add(mFfmpegBin);
		cmd.add("-y");
		
		if (in.format != null) {
			cmd.add(Argument.FORMAT);
			cmd.add(in.format);
		}
	
		if (in.videoCodec != null) {
			cmd.add(Argument.VIDEOCODEC);
			cmd.add(in.videoCodec);
		}
		
		if (in.audioCodec != null) {
			cmd.add(Argument.AUDIOCODEC);
			cmd.add(in.audioCodec);
		}
		
		cmd.add("-i");
		cmd.add(new File(in.path).getCanonicalPath());
		
		if (out.videoBitrate > 0) {
			cmd.add(Argument.BITRATE_VIDEO);
			cmd.add(out.videoBitrate + "k");
		}
		
		if (out.width > 0) {
			cmd.add(Argument.SIZE);
			cmd.add(out.width + "x" + out.height);
		}
		
		if (out.videoFps != null) {
			cmd.add(Argument.FRAMERATE);
			cmd.add(out.videoFps);
		}
		
		if (out.videoCodec != null) {
			cmd.add(Argument.VIDEOCODEC);
			cmd.add(out.videoCodec);
		}
		
		if (out.videoBitStreamFilter != null) {
			cmd.add(Argument.VIDEOBITSTREAMFILTER);
			cmd.add(out.videoBitStreamFilter);
		}
		
		if (out.videoFilter != null) {
			cmd.add("-vf");
			cmd.add(out.videoFilter);
		}
		
		if (out.audioCodec != null) {
			cmd.add(Argument.AUDIOCODEC);
			cmd.add(out.audioCodec);
		}
		
		if (out.audioBitStreamFilter != null) {
			cmd.add(Argument.AUDIOBITSTREAMFILTER);
			cmd.add(out.audioBitStreamFilter);
		}
		if (out.audioChannels > 0) {
			cmd.add(Argument.CHANNELS_AUDIO);
			cmd.add(out.audioChannels+"");
		}
		
		if (out.audioBitrate > 0) {
			cmd.add(Argument.BITRATE_AUDIO);
			cmd.add(out.audioBitrate + "k");
		}
		
		if (out.format != null) {
			cmd.add("-f");
			cmd.add(out.format);
		}
		
		if (enableExperimental) {
			cmd.add("-strict");
			cmd.add("-2");//experimental
		}
		
		cmd.add(new File(out.path).getCanonicalPath());

		execFFMPEG(cmd, sc);
	}
	
	public Clip combineAudioAndVideo (Clip videoIn, Clip audioIn, Clip out,
									  ShellCallback sc) throws Exception {
		ArrayList<String> cmd = new ArrayList<String>();

		cmd.add(mFfmpegBin);
		cmd.add("-y");

		cmd.add("-i");
		cmd.add(new File(videoIn.path).getCanonicalPath());
		
		cmd.add("-i");
		cmd.add(new File(audioIn.path).getCanonicalPath());

		cmd.add("-strict");
		cmd.add("-2");//experimental
		
		cmd.add(Argument.AUDIOCODEC);
		if (out.audioCodec != null){
			cmd.add(out.audioCodec);
		}else{
			cmd.add("copy");
		}

		cmd.add(Argument.VIDEOCODEC);
		if (out.videoCodec != null){
			cmd.add(out.videoCodec);
		}else{
			cmd.add("copy");
		}
		
		if (out.videoBitrate != -1){
			cmd.add(Argument.BITRATE_VIDEO);
			cmd.add(out.videoBitrate + "k");
		}
		
		if (out.videoFps != null){
			cmd.add(Argument.FRAMERATE);
			cmd.add(out.videoFps);
		}
		
		if (out.audioBitrate != -1){
			cmd.add(Argument.BITRATE_AUDIO);
			cmd.add(out.audioBitrate + "k");
		}
		cmd.add("-y");
		
		cmd.add("-cutoff");
		cmd.add("15000");
		
		if (out.width > 0){
			cmd.add(Argument.SIZE);
			cmd.add(out.width + "x" + out.height);
		}
		
		if (out.format != null){
			cmd.add("-f");
			cmd.add(out.format);
		}

		File fileOut = new File(out.path);
		cmd.add(fileOut.getCanonicalPath());
		
		execFFMPEG(cmd, sc);
		return out;
	}
	
	public Clip convertImageToMP4 (Clip mediaIn, String outPath,
									ShellCallback sc) throws Exception {
		// ffmpeg -loop 1 -i IMG_1338.jpg -t 10 -r 29.97 -s 640x480 -qscale 5 test.mp4
		Clip result = new Clip ();
		
		ArrayList<String> cmd = new ArrayList<String>();
		cmd = new ArrayList<String>();
		cmd.add(mFfmpegBin);
		cmd.add("-y");
		
		cmd.add("-loop");
		cmd.add("1");
		
		cmd.add("-i");
		cmd.add(new File(mediaIn.path).getCanonicalPath());
		
		cmd.add(Argument.FRAMERATE);
		cmd.add(mediaIn.videoFps);
		
		cmd.add("-t");
		cmd.add(mediaIn.duration + "");
		
//		cmd.add("-qscale");
//		cmd.add("5"); //a good value 1 is best 30 is worst
		
		if (mediaIn.width != -1){
			cmd.add(Argument.SIZE);
			cmd.add(mediaIn.width + "x" + mediaIn.height);
//			cmd.add("-vf");
//			cmd.add("\"scale="+ mediaIn.width+":-1" + "\"");
		}
		
		if (mediaIn.videoBitrate != -1){
			cmd.add(Argument.BITRATE_VIDEO);
			cmd.add(mediaIn.videoBitrate + "");
		}
	
	//	-ar 44100 -acodec pcm_s16le -f s16le -ac 2 -i /dev/zero -acodec aac -ab 128k \ 
	//	-map 0:0 -map 1:0
		
		result.path = outPath;
		result.videoBitrate = mediaIn.videoBitrate;
		result.videoFps = mediaIn.videoFps;
		result.mimeType = "video/mp4";

		cmd.add(new File(result.path).getCanonicalPath());
		
		execFFMPEG(cmd, sc);
		
		return result;
	}

	public Clip convertToMP4Stream (Clip mediaIn, String outPath,
									ShellCallback sc) throws Exception {
		//based on this gist: https://gist.github.com/3757344
		//ffmpeg -i input1.mp4 -vcodec copy -vbsf h264_mp4toannexb -acodec copy part1.ts
		//ffmpeg -i input2.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts intermediate2.ts

		Clip mediaOut = new Clip();
		mediaOut.path = outPath;
		
		ArrayList<String> cmd = new ArrayList<String>();
		cmd = new ArrayList<String>();
		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-i");
		cmd.add(new File(mediaIn.path).getCanonicalPath());
		cmd.add("-f");
		cmd.add("mpegts");
//		cmd.add("-vcoder");
//		cmd.add("h264");
//		cmd.add("-acoder");
//		cmd.add("copy");
		cmd.add("-c");
		cmd.add("copy");
		cmd.add("-bsf:v");
		cmd.add("h264_mp4toannexb");
		cmd.add("-bsf:a");
		cmd.add("aac_adtstoasc");

		File fileOut = new File(mediaOut.path);
		mediaOut.path = fileOut.getCanonicalPath();
		cmd.add(mediaOut.path);

		execFFMPEG(cmd, sc);
		return mediaOut;
	}
	
	public Clip convertToWaveAudio (Clip mediaIn, String outPath, int sampleRate,
									int channels, ShellCallback sc) throws Exception {
		ArrayList<String> cmd = new ArrayList<String>();

		cmd.add(mFfmpegBin);
		cmd.add("-y");
		
		if (mediaIn.startTime != null) {
			cmd.add("-ss");
			cmd.add(mediaIn.startTime);
		}
		
		if (mediaIn.duration != -1) {
			cmd.add("-t");
			cmd.add(String.format(Locale.US,"%f",mediaIn.duration));
		}
		
		cmd.add("-i");
		cmd.add(new File(mediaIn.path).getCanonicalPath());
		
		
		cmd.add("-ar");
		cmd.add(sampleRate + "");
		
		cmd.add("-ac");
		cmd.add(channels + "");
		
		cmd.add("-vn");
		
		Clip mediaOut = new Clip();
		
		File fileOut = new File(outPath);
		mediaOut.path = fileOut.getCanonicalPath();
		
		cmd.add(mediaOut.path);

		execFFMPEG(cmd, sc);
		
		return mediaOut;
	}
	
	public Clip convertTo3GPAudio (Clip mediaIn, Clip mediaOut,
								   ShellCallback sc) throws Exception {
		ArrayList<String> cmd = new ArrayList<String>();

		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-i");
		cmd.add(new File(mediaIn.path).getCanonicalPath());
		
		if (mediaIn.startTime != null) {
			cmd.add("-ss");
			cmd.add(mediaIn.startTime);
		}
		
		if (mediaIn.duration != -1) {
			cmd.add("-t");
			cmd.add(String.format(Locale.US,"%f",mediaIn.duration));
		}
		
		cmd.add("-vn");
		
		if (mediaOut.audioCodec != null) {
			cmd.add("-acodec");
			cmd.add(mediaOut.audioCodec);
		}
		
		if (mediaOut.audioBitrate != -1) {
			cmd.add("-ab");
			cmd.add(mediaOut.audioBitrate + "k");
		}
		
		cmd.add("-strict");
		cmd.add("-2");

		File fileOut = new File(mediaOut.path);
		
		cmd.add(fileOut.getCanonicalPath());

		execFFMPEG(cmd, sc);
		
		return mediaOut;
	}
	
	public Clip convert (Clip mediaIn, String outPath,
						 ShellCallback sc) throws Exception {
		ArrayList<String> cmd = new ArrayList<String>();

		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-i");
		cmd.add(new File(mediaIn.path).getCanonicalPath());
		
		if (mediaIn.startTime != null) {
			cmd.add("-ss");
			cmd.add(mediaIn.startTime);
		}
		
		if (mediaIn.duration != -1) {
			cmd.add("-t");
			cmd.add(String.format(Locale.US,"%f",mediaIn.duration));
		}
		
		
		Clip mediaOut = new Clip();
		

		File fileOut = new File(outPath);
		 
		mediaOut.path = fileOut.getCanonicalPath();
		
		cmd.add(mediaOut.path);

		execFFMPEG(cmd, sc);
		
		return mediaOut;
	}
	
	public Clip convertToMPEG (Clip mediaIn, String outPath,
							   ShellCallback sc) throws Exception {
		ArrayList<String> cmd = new ArrayList<String>();

		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-i");
		cmd.add(new File(mediaIn.path).getCanonicalPath());
		
		if (mediaIn.startTime != null) {
			cmd.add("-ss");
			cmd.add(mediaIn.startTime);
		}
		
		if (mediaIn.duration != -1) {
			cmd.add("-t");
			cmd.add(String.format(Locale.US,"%f",mediaIn.duration));
		}
		

		//cmd.add("-strict");
		//cmd.add("experimental");
		
		//everything to mpeg
		cmd.add("-f");
		cmd.add("mpeg");
		
		Clip mediaOut = mediaIn.clone();
		
		File fileOut = new File(outPath);
		
		mediaOut.path = fileOut.getCanonicalPath();
		
		cmd.add(mediaOut.path);

		execFFMPEG(cmd, sc);
		
		return mediaOut;
	}
	
	public void extractAudio (Clip mdesc, String audioFormat, File audioOutPath,
							  ShellCallback sc) throws IOException, InterruptedException {
		ArrayList<String> cmd = new ArrayList<String>();

		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-i");
		cmd.add(new File(mdesc.path).getCanonicalPath());
		
		cmd.add("-vn");
		
		if (mdesc.startTime != null) {
			cmd.add("-ss");
			cmd.add(mdesc.startTime);
		}
		
		if (mdesc.duration != -1) {
			cmd.add("-t");
			cmd.add(String.format(Locale.US,"%f",mdesc.duration));
		}
					
		cmd.add("-f");
		cmd.add(audioFormat); //wav
		
		//everything to WAV!
		cmd.add(audioOutPath.getCanonicalPath());

		execFFMPEG(cmd, sc);
	}
	
	public int killVideoProcessor (boolean asRoot, boolean waitFor) throws IOException {
		int killDelayMs = 300;
		int result = -1;
		int procId = -1;

		while ((procId = ShellUtils.findProcessId(mFfmpegBin)) != -1) {
		//	Log.d(TAG, "Found PID=" + procId + " - killing now...");
			String[] cmd = { ShellUtils.SHELL_CMD_KILL+' '+procId };
			try {
				result = ShellUtils.doShellCommand(cmd, new ShellCallback() {
						@Override
						public void shellOut(String msg) {
						}

						@Override
						public void processComplete(int exitValue) {
						}
					}, asRoot, waitFor);
				Thread.sleep(killDelayMs);
			}
			catch (Exception e){}
		}

		return result;
	}

	public Clip trim (Clip mediaIn, boolean withSound, String outPath, ShellCallback sc)
					   throws Exception {
		Clip mediaOut = new Clip();
		String mediaPath = mediaIn.path;
		
		ArrayList<String> cmd = new ArrayList<String>();
		cmd = new ArrayList<String>();
		cmd.add(mFfmpegBin);
		cmd.add("-y");
				
		if (mediaIn.startTime != null){
			cmd.add(Argument.STARTTIME);
			cmd.add(mediaIn.startTime);
		}
		
		if (mediaIn.duration != -1){
			cmd.add("-t");
			cmd.add(String.format(Locale.US, "%f", mediaIn.duration));
		}

		cmd.add("-i");
		cmd.add(mediaPath);
		if (!withSound){
			cmd.add("-an");
		}
		cmd.add("-strict");
		cmd.add("-2");//experimental
		mediaOut.path = outPath;
		cmd.add(mediaOut.path);
		
		execFFMPEG(cmd, sc);
		return mediaOut;
	}
	
	public Clip getInfo (Clip in) throws IOException, InterruptedException {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd = new ArrayList<String>();
		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-i");
		cmd.add(new File(in.path).getCanonicalPath());
		
		InfoParser ip = new InfoParser(in);
		execFFMPEG(cmd, ip, null);

		try{
			Thread.sleep(200);
		}catch (Exception e){
		}

		return in;
	}

	public void rotate (String inVideoPath, String outVideoPath, ShellCallback sc)
			 			throws IOException, InterruptedException {
		//旋转视频并重编码
		//ffmpeg -i original.mp4 -vf "transpose=1, format=yuv420p" -codec:v libx264 -preset slow -crf 25 -codec:a copy flipped.mp4
		//只设置metadata rotate值
		//ffmpeg -i input.mp4 -c copy -metadata:s:v:0 rotate=90 output.mp4

		ArrayList<String> cmd = new ArrayList<String>();
		cmd = new ArrayList<String>();
		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-i");
		cmd.add(inVideoPath);
		cmd.add("-vf");
		cmd.add("transpose=1, format=yuv420p");
		cmd.add("-metadata:s:v");
		cmd.add("rotate=0");
		cmd.add("-c:v");
		cmd.add("libx264");
		cmd.add("-preset");
		cmd.add("ultrafast");
		cmd.add("-crf");
		cmd.add("25");
		cmd.add("-c:a");
		cmd.add("copy");
		cmd.add(outVideoPath);
		
		execFFMPEG(cmd, sc);
	}
	
	public void watermark(String inVideoPath, String outVideoPath, ShellCallback sc)
			 				throws IOException, InterruptedException {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-i");
		cmd.add(inVideoPath);
		cmd.add("-i");
		cmd.add(mVideoLogo);
		cmd.add("-filter_complex");
		cmd.add("overlay=(main_w-overlay_w-10):(main_h-overlay_h-10)");
		cmd.add("-c:v");
		cmd.add("libx264");
		cmd.add("-preset");
		cmd.add("ultrafast");
		cmd.add("-crf");
		cmd.add("25");
		cmd.add("-c:a");
		cmd.add("copy");
		cmd.add(outVideoPath);

		execFFMPEG(cmd, sc);
	}
	
	public void generationGif(String inVideoPath, String outGifPath,
							  String startTime, ShellCallback sc)
								throws IOException, InterruptedException {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-ss");
		cmd.add(startTime);
		cmd.add("-i");
		cmd.add(inVideoPath);
		cmd.add("-vf");
		cmd.add("fps=8,scale=160:-1:flags=lanczos");
		cmd.add("-vframes");
		cmd.add("20");
		cmd.add(outGifPath);

		execFFMPEG(cmd, sc);
	}
	
	public void generationPic(String inVideoPath, String outPicCatalogPath,
							  String startTime, ShellCallback sc)
								throws IOException, InterruptedException {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-ss");
		cmd.add(startTime);
		cmd.add("-i");
		cmd.add(inVideoPath);
		cmd.add("-vf");
		cmd.add("fps=3,scale=200:-1:flags=lanczos");
		cmd.add("-vframes");
		cmd.add("9");
		cmd.add("-f");
		cmd.add("image2");
		cmd.add(outPicCatalogPath+"/img-%d.jpg");

		execFFMPEG(cmd, sc);
	}
	
}


