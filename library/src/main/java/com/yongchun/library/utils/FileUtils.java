package com.yongchun.library.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by dee on 15/11/20.
 */
public class FileUtils {
    public static String POSTFIX = ".JPEG";
	public static String APP_NAME = "";
	public static String CAMERA_PATH = "/";

	public static void setAppName(String appName){
		APP_NAME = appName;
		CAMERA_PATH = "/" + APP_NAME + "/";
	}

    public static File createCameraFile(Context context) {
        return createMediaFile(context,CAMERA_PATH);
    }

	public static File createTempCropFile(Context context){
		return new File(context.getCacheDir(),  UUID.randomUUID().toString() + POSTFIX);
	}

    private static File createMediaFile(Context context, String parentPath){
        String state = Environment.getExternalStorageState();
        File rootDir = state.equals(Environment.MEDIA_MOUNTED)?Environment.getExternalStorageDirectory():context.getCacheDir();

        File folderDir = new File(rootDir.getAbsolutePath() + parentPath);
        if (!folderDir.exists() && folderDir.mkdirs()){

        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String fileName = APP_NAME + "_" + timeStamp + "";
        File tmpFile = new File(folderDir, fileName + POSTFIX);
        return tmpFile;
    }
}
