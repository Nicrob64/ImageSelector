package com.yongchun.library.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.yongchun.library.model.LocalMedia;

import java.io.File;
import java.io.IOException;
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
		try {
			File externalDir = Environment.getExternalStoragePublicDirectory(env);
			File album = new File(externalDir, getApplicationName(context));
			album.mkdirs();
			File image = File.createTempFile(
				timeStamp,  /* prefix */
				postfix,  /* suffix */
				album      /* directory */
			);
		return image;
		}catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}

	public static String getApplicationName(Context context) {
		ApplicationInfo applicationInfo = context.getApplicationInfo();
		int stringId = applicationInfo.labelRes;
		return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
	}


	public static String getRealPathFromUri(Context context, Uri contentUri) {

		Cursor cursor = null;
		String filePath = null;
		if(contentUri == null){
			return null;
		}
		if(contentUri.getScheme().equals("content")) {
			String[] proj = {android.provider.MediaStore.Images.ImageColumns.DATA};
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			cursor.moveToFirst();
			filePath = cursor.getString(0);
			cursor.close();
		}else{
			filePath = contentUri.getPath();
		}

		return filePath;
	}

}
