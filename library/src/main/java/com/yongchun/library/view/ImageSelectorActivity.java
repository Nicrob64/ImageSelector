package com.yongchun.library.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yongchun.library.R;
import com.yongchun.library.adapter.ImageFolderAdapter;
import com.yongchun.library.adapter.ImageListAdapter;
import com.yongchun.library.model.LocalMedia;
import com.yongchun.library.model.LocalMediaFolder;
import com.yongchun.library.utils.FileUtils;
import com.yongchun.library.utils.GridSpacingItemDecoration;
import com.yongchun.library.utils.LocalMediaLoader;
import com.yongchun.library.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dee on 15/11/19.
 */
public class ImageSelectorActivity extends AppCompatActivity {
    public final static int REQUEST_IMAGE = 66;
    public final static int REQUEST_CAMERA = 67;
	public final static int REQUEST_VIDEO = 68;

    public final static String BUNDLE_CAMERA_PATH = "CameraPath";

    public final static String REQUEST_OUTPUT = "outputList";

    public final static String EXTRA_SELECT_MODE = "SelectMode";
    public final static String EXTRA_SHOW_CAMERA = "ShowCamera";
    public final static String EXTRA_ENABLE_PREVIEW = "EnablePreview";
    public final static String EXTRA_ENABLE_CROP = "EnableCrop";
    public final static String EXTRA_MAX_SELECT_NUM = "MaxSelectNum";
	public static final String EXTRA_MEDIA_TYPE = "MediaType";

	public static final int TYPE_VIDEO = LocalMediaLoader.TYPE_VIDEO;
	public static final int TYPE_IMAGE = LocalMediaLoader.TYPE_IMAGE;

    public final static int MODE_MULTIPLE = 1;
    public final static int MODE_SINGLE = 2;

    private int maxSelectNum = 9;
    private int selectMode = MODE_MULTIPLE;
    private boolean showCamera = true;
    private boolean enablePreview = true;
    private boolean enableCrop = false;
	private int mediaType = TYPE_IMAGE;

    private int spanCount = 3;

    private Toolbar toolbar;
    private TextView doneText;

    private TextView previewText;

    private RecyclerView recyclerView;
    private ImageListAdapter imageAdapter;

    private LinearLayout folderLayout;
    private TextView folderName;
    private FolderWindow folderWindow;

    private String cameraPath;

    private int thumnailSize = 256;


    public static void start(Activity activity, int maxSelectNum, int mode, boolean isShow, boolean enablePreview, boolean enableCrop) {
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        intent.putExtra(EXTRA_SELECT_MODE, mode);
        intent.putExtra(EXTRA_SHOW_CAMERA, isShow);
        intent.putExtra(EXTRA_ENABLE_PREVIEW, enablePreview);
        intent.putExtra(EXTRA_ENABLE_CROP, enableCrop);
        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageselector);

		thumnailSize = ScreenUtils.getScreenWidth(this)/spanCount;

        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        selectMode = getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_MULTIPLE);
        showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        enablePreview = getIntent().getBooleanExtra(EXTRA_ENABLE_PREVIEW, true);
        enableCrop = getIntent().getBooleanExtra(EXTRA_ENABLE_CROP, false);
		mediaType = getIntent().getIntExtra(EXTRA_MEDIA_TYPE, TYPE_IMAGE);

        if (selectMode == MODE_MULTIPLE) {
            enableCrop = false;
        } else {
            enablePreview = false;
        }
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH);
        }
        initView();
        registerListener();

        new LocalMediaLoader(this, mediaType).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                folderWindow.bindFolder(folders);
                imageAdapter.bindImages(folders.get(0).getImages());
            }
        });
    }

    public void initView() {
        folderWindow = new FolderWindow(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mediaType == TYPE_IMAGE ? R.string.picture : R.string.video);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);

        doneText = (TextView) findViewById(R.id.done_text);
        doneText.setVisibility(selectMode == MODE_MULTIPLE ? View.VISIBLE : View.GONE);

        previewText = (TextView) findViewById(R.id.preview_text);
        previewText.setVisibility(enablePreview ? View.VISIBLE : View.GONE);

        folderLayout = (LinearLayout) findViewById(R.id.folder_layout);
        folderName = (TextView) findViewById(R.id.folder_name);
		folderName.setText(mediaType == TYPE_IMAGE ? R.string.all_image : R.string.all_videos);

        recyclerView = (RecyclerView) findViewById(R.id.folder_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, ScreenUtils.dip2px(this, 2), false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        imageAdapter = new ImageListAdapter(this, maxSelectNum, selectMode, showCamera,enablePreview, mediaType, thumnailSize);
        recyclerView.setAdapter(imageAdapter);

    }

    public void registerListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        folderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderWindow.isShowing()) {
                    folderWindow.dismiss();
                } else {
                    folderWindow.showAsDropDown(toolbar);
                }
            }
        });
        imageAdapter.setOnImageSelectChangedListener(new ImageListAdapter.OnImageSelectChangedListener() {
            @Override
            public void onChange(List<LocalMedia> selectImages) {
                boolean enable = selectImages.size() != 0;
                doneText.setEnabled(enable ? true : false);
                previewText.setEnabled(enable ? true : false);
                if (enable) {
                    doneText.setText(getString(R.string.done_num, selectImages.size(), maxSelectNum));
                    previewText.setText(getString(R.string.preview_num, selectImages.size()));
                } else {
                    doneText.setText(R.string.done);
                    previewText.setText(R.string.preview);
                }
            }

            @Override
            public void onTakePhoto() {
                startCamera();
            }

            @Override
            public void onPictureClick(LocalMedia media, int position) {
                if (enablePreview) {
                    startPreview(imageAdapter.getImages(), position);
                } else if (enableCrop) {
                    startCrop(Uri.fromFile(new File(media.getPath())));
                } else {
                    onSelectDone(media.getPath());
                }
            }
        });
        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDone(imageAdapter.getSelectedImages());
            }
        });
        folderWindow.setOnItemClickListener(new ImageFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name, List<LocalMedia> images) {
                folderWindow.dismiss();
                imageAdapter.bindImages(images);
                folderName.setText(name);
            }
        });
        previewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreview(imageAdapter.getSelectedImages(), 0);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // on take photo success
            if (requestCode == REQUEST_CAMERA) {
				Uri uri = Uri.parse(cameraPath);
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                if (enableCrop) {
                    startCrop(uri);
                } else {
					if(uri != null) {
						onSelectDone(uri.toString());
					}else{
						onSelectDone(new ArrayList<LocalMedia>());
					}
                }
            }
            //on preview select change
            else if (requestCode == ImagePreviewActivity.REQUEST_PREVIEW) {
                boolean isDone = data.getBooleanExtra(ImagePreviewActivity.OUTPUT_ISDONE, false);
                List<LocalMedia> images = (List<LocalMedia>) data.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST);
                if (isDone) {
                    onSelectDone(images);
                }else{
                    imageAdapter.bindSelectImages(images);
                }
            }
            // on crop success
            else if (requestCode == ImageCropActivity.REQUEST_CROP) {
                String path = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);
                onSelectDone(path);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath);
    }

    /**
     * start to camera、preview、crop
     */
    public void startCamera() {
		switch (mediaType){
			case LocalMediaLoader.TYPE_IMAGE:
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (cameraIntent.resolveActivity(getPackageManager()) != null) {
					File cameraFile = FileUtils.createMediaFile(this, true, ".jpg");
					Uri photoUri = FileProvider.getUriForFile(this, getPackageName()+".provider", cameraFile);
					cameraPath = photoUri.toString();
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
					startActivityForResult(cameraIntent, REQUEST_CAMERA);
				}
				break;
			case LocalMediaLoader.TYPE_VIDEO:
				Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				if (videoIntent.resolveActivity(getPackageManager()) != null) {
					File cameraFile = FileUtils.createMediaFile(this, false, ".mp4");
					Uri videoUri = FileProvider.getUriForFile(this, getPackageName()+".provider", cameraFile);
					cameraPath = videoUri.toString();
					videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
					startActivityForResult(videoIntent, REQUEST_CAMERA);
				}
				break;
		}

    }

    public void startPreview(List<LocalMedia> previewImages, int position) {
        ImagePreviewActivity.startPreview(this, previewImages, imageAdapter.getSelectedImages(), maxSelectNum, position);
    }

    public void startCrop(Uri path) {
        ImageCropActivity.startCrop(this, path);
    }

    /**
     * on select done
     *
     * @param medias
     */
    public void onSelectDone(List<LocalMedia> medias) {
        ArrayList<String> images = new ArrayList<>();
        for (LocalMedia media : medias) {
            images.add(media.getPath());
        }
        onResult(images);
    }

    public void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        onResult(images);
    }

    public void onResult(ArrayList<String> images) {
        setResult(RESULT_OK, new Intent().putStringArrayListExtra(REQUEST_OUTPUT, images));
        finish();
    }
}
