package com.yongchun.library.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;

import com.yongchun.library.model.LocalMedia;

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


	public static File createTempCropFile(Context context){
		return new File(context.getCacheDir(),  UUID.randomUUID().toString() + POSTFIX);
	}

	public static File createMediaFile(Context context, boolean picture, String postfix){

		String env = Environment.DIRECTORY_PICTURES;
		if(picture){
			env = Environment.DIRECTORY_PICTURES;
		}else{
			env = Environment.DIRECTORY_MOVIES;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
		File album = new File(Environment.getExternalStoragePublicDirectory(env), getApplicationName(context));
		File tmpFile = new File(album, timeStamp + postfix);
		return tmpFile;
	}

	public static String getApplicationName(Context context) {
		ApplicationInfo applicationInfo = context.getApplicationInfo();
		int stringId = applicationInfo.labelRes;
		return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
	}

}
