package com.android.angrybird.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.angrybird.fragment.ImageDialogFragment;
import com.android.angrybird.receiver.ScreenTrackReceiver;
import com.android.angrybird.util.FileUtils;
import com.android.angrybird.util.SaveBitmapTask;

/**
 * Created by hp pc on 04-05-2017.
 */

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements SaveBitmapTask.FileSaveListener {

    protected static final int CAMERA_REQUEST = 1001;
    protected static final int GALLERY_REQUEST = 1002;
    protected static final int GALLERY_KITKAT_INTENT_CALLED = 1003;
    private static final int REQUEST_PERMISSION = 1005;
    private final CharSequence[] items = {"Take Photo", "Choose from Library",
            "Cancel"};
    protected Uri mCapturedImageURI;
    private ScreenTrackReceiver mReceiver;
    private String mImageFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        T t = DataBindingUtil.setContentView(this, getActivityContentView());
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenTrackReceiver();
        registerReceiver(mReceiver, filter);
        onCreateCustom(t);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if(AngryBirdApp.getAppInstance().wasInBackground())
        {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
            AngryBirdApp.getAppInstance().setWasInBackground(false);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(ScreenTrackReceiver.isScreenOff)
        {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
        }*/

    }

    /*@Override
        protected void onPause() {

            if(ScreenTrackReceiver.isScreenOff)
            {
                Intent intent = new Intent(this, AuthenticationActivity.class);
                startActivity(intent);
            }

            super.onPause();

        }
    */
    abstract protected void onCreateCustom(T viewBinding);

    abstract protected int getActivityContentView();

    abstract protected void onLoadImage(String filePath);


    protected void showPicker() {
        boolean writePermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean readPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean camerPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!(writePermission && readPermission && camerPermission)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_PERMISSION);
        }
        else
        {
            showImagePicker();
        }
    }

    protected void saveImageToFile(String filePath) {
        new SaveBitmapTask(this).execute(filePath);
    }


    protected void showImage(String filePath) {
        ImageDialogFragment imageDialogFragment = ImageDialogFragment.getInstance(filePath);
        imageDialogFragment.show(getSupportFragmentManager(), ImageDialogFragment.TAG);
    }

    private void showImagePicker()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Your Option!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraClick();
                } else if (items[item].equals("Choose from Library")) {
                    galleryClick();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraClick() {
        // dialog.dismiss();
        String fileName = System.currentTimeMillis()+".png";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        mCapturedImageURI = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        intent.putExtra("return-data", false);

        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void galleryClick() {
        // dialog.dismiss();
        if (Build.VERSION.SDK_INT < 19) {
            try {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, GALLERY_REQUEST);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
               if ((requestCode == GALLERY_REQUEST || requestCode == GALLERY_KITKAT_INTENT_CALLED)
                       && resultCode == Activity.RESULT_OK && null != data) {
                   if (requestCode == GALLERY_REQUEST) {
                       mCapturedImageURI = data.getData();
                       String[] column = {MediaStore.Images.Media.DATA};
                       Cursor c = getContentResolver().query(mCapturedImageURI,
                               column, null, null, null);
                       c.moveToFirst();
                       String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                       path = FileUtils.getPath(this, mCapturedImageURI);
                       onLoadImage(path);
                       c.close();
                       Log.d("Uri Got Path", "" + path);

                   } else if (requestCode == GALLERY_KITKAT_INTENT_CALLED) {
                       mCapturedImageURI = data.getData();
                       String path = FileUtils.getPath(this, mCapturedImageURI);
                       onLoadImage(path);

                   }
               } else if (requestCode == CAMERA_REQUEST
                       && resultCode == Activity.RESULT_OK) {

                   String[] projection = {
                           MediaStore.Images.Thumbnails._ID,  // The columns we want
                           MediaStore.Images.Thumbnails.IMAGE_ID,
                           MediaStore.Images.Thumbnails.KIND,
                           MediaStore.Images.Thumbnails.DATA};
                   String selection = MediaStore.Images.Thumbnails.KIND + "=" + // Select only mini's
                           MediaStore.Images.Thumbnails.MINI_KIND;

                   String sort = MediaStore.Images.Thumbnails._ID + " DESC";

                   Cursor myCursor = this.managedQuery(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, selection, null, sort);

                   long imageId = 0l;
                   long thumbnailImageId = 0l;
                   String thumbnailPath = "";

                   try {
                       myCursor.moveToFirst();
                       imageId = myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID));
                       thumbnailImageId = myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID));
                       thumbnailPath = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
                   } finally {
                       myCursor.close();
                   }


                   String[] largeFileProjection = {
                           MediaStore.Images.ImageColumns._ID,
                           MediaStore.Images.ImageColumns.DATA
                   };

                   String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
                   myCursor = this.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, largeFileProjection, null, null, largeFileSort);
                   String largeImagePath = "";

                   try {
                       myCursor.moveToFirst();
                       largeImagePath = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                   } finally {
                       myCursor.close();
                   }
                   // These are the two URI's you'll be interested in. They give you a handle to the actual images
                   Uri uriLargeImage = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imageId));
                   Uri uriThumbnailImage = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, String.valueOf(thumbnailImageId));

                   onLoadImage(largeImagePath);

                  /* String[] projection = {MediaStore.Images.Media.DATA};

                   Cursor cursor = getContentResolver().query(mCapturedImageURI,
                           projection, null, null, null);
                   int column_index_data = cursor
                           .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                   cursor.moveToFirst();
                   String path = cursor.getString(column_index_data);
                   onLoadImage(path);
                   cursor.close();*/

               }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                    showImagePicker();
                } else
                {
                    Toast.makeText(this, "Please consider granting it all permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected String getImageFilePath() {
        return mImageFilePath;
    }

    @Override
    public void onFileSaved(String path) {
        mImageFilePath = path;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
