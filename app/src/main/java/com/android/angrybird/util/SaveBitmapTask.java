package com.android.angrybird.util;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by hp pc on 31-05-2017.
 */

public class SaveBitmapTask extends AsyncTask<String, Void, String> {

    private Context context;
    private FileSaveListener imagePathListener;

    public SaveBitmapTask(Context context) {
        this.context = context;
        imagePathListener = (FileSaveListener) context;
    }

    @Override
    protected String doInBackground(String... strings) {
        return ImageCompressionUtil.getInstance(context).compressImage(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (null != imagePathListener)
            imagePathListener.onFileSaved(s);
    }

    public interface FileSaveListener {
        void onFileSaved(String path);
    }
}
