package com.android.angrybird.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Created by hp pc on 31-05-2017.
 */

public class SaveBitmapTask extends AsyncTask<String, Void, String> {

    private Context context;
    private FileSaveListener imagePathListener;
    private ProgressDialog mProgressDialog;

    public SaveBitmapTask(Context context) {
        this.context = context;
        imagePathListener = (FileSaveListener) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        return FileUtils.storeImage(context, BitmapFactory.decodeFile(strings[0]));
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mProgressDialog.isShowing())
            mProgressDialog.cancel();
        if (null != imagePathListener)
            imagePathListener.onFileSaved(s);
    }

    public interface FileSaveListener {
        void onFileSaved(String path);
    }
}
