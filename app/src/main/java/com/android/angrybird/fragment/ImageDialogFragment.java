package com.android.angrybird.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.angrybird.R;
import com.android.angrybird.util.Utils;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by hp pc on 10-06-2017.
 */

public class ImageDialogFragment extends DialogFragment {

    public static final String TAG = ImageDialogFragment.class.getSimpleName();
    private static final String KEY_FILE_PATH = "KEY_FILE_PATH";
    private String outputFileName;

    public static ImageDialogFragment getInstance(String filePath) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_FILE_PATH, filePath);
        ImageDialogFragment fragment = new ImageDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.image_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String filePath = getArguments().getString(KEY_FILE_PATH);
        if (!TextUtils.isEmpty(filePath)) {
            new FileCopyAsyncTask().execute(filePath);

            PhotoView photoView = (PhotoView) view.findViewById(R.id.photo);
            view.findViewById(R.id.share_option).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.shareImage(getActivity(), outputFileName);
                }
            });
            Glide.with(this).load(filePath).placeholder(R.drawable.ic_account_circle_black_24dp).into(photoView);
        } else {
            dismiss();
        }
    }

    private class FileCopyAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            String filePath = strings[0];
            final String outputFileName = Environment.getExternalStorageDirectory() + "/" + FilenameUtils.getName(filePath);
            try {
                FileUtils.copyFileToDirectory(new File(filePath), Environment.getExternalStorageDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return outputFileName;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mDialog.isShowing())
                mDialog.cancel();
            outputFileName = s;
        }
    }
}
