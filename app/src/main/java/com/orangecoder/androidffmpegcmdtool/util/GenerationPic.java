package com.orangecoder.androidffmpegcmdtool.util;

import android.content.Context;

import com.orangecoder.androidffmpegcmdtool.core.ShellCallback;

import java.io.File;
import java.io.IOException;

import com.orangecoder.androidffmpegcmdtool.core.FfmpegController;


public class GenerationPic {
	
	public static String excute(Context context, String inVideoPath,
								String startTime, final GPFfmpegHandleListener listener)
								throws IOException, InterruptedException {
        File outPicCatalogFile = new File(new File(inVideoPath).getParentFile(), "PIC");
		final String outPicCatalogPath = outPicCatalogFile.getCanonicalPath();

		deleteCatalog(outPicCatalogFile);
		outPicCatalogFile.mkdirs();

		FfmpegController.getInstance(context).generationPic(inVideoPath,
				outPicCatalogPath, startTime, new ShellCallback() {

					@Override
					public void shellOut(String shellLine) {
//						System.out.println("generationPIC>" + shellLine);
					}

					@Override
					public void processComplete(int exitValue) {
//						System.out.println("generationPIC>" + exitValue);
						if(listener != null) {
							listener.finish(exitValue, outPicCatalogPath);
						}
					}
				});
		return outPicCatalogPath;
	}
	
	public static void deleteCatalog(File file) throws IOException {  
        if (file.isFile()) {  
            file.delete();  
            return;  
        }  
  
        if(file.isDirectory()){  
            File[] childFiles = file.listFiles();  
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
      
            for (int i = 0; i < childFiles.length; i++) {  
            	deleteCatalog(childFiles[i]);  
            }  
            file.delete();
        }  
    } 
}
