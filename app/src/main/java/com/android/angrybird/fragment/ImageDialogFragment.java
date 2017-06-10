package com.android.angrybird.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.angrybird.R;
import com.android.angrybird.util.Utils;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by hp pc on 10-06-2017.
 */

public class ImageDialogFragment extends DialogFragment {

    public static final String TAG = ImageDialogFragment.class.getSimpleName();
    private static final String KEY_FILE_PATH = "KEY_FILE_PATH";

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
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo);
        view.findViewById(R.id.share_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.shareImage(getActivity(), filePath);
            }
        });
        Glide.with(this).load(filePath).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(photoView);
    }
}
