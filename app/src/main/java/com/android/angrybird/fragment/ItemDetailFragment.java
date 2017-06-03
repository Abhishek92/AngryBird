package com.android.angrybird.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.angrybird.R;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.ItemAsset;
import com.android.angrybird.databinding.FragmentItemDetailBinding;
import com.android.angrybird.util.Utils;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDetailFragment extends DialogFragment {


    public static final String TAG = ItemDetailFragment.class.getSimpleName();
    public static final String KEY_ITEM_DATA = "KEY_ITEM_DATA";
    private FragmentItemDetailBinding mBinding;
    private List<ItemAsset> mItemAssetList = new ArrayList<>();
    private List<String> mImageList = new ArrayList<>();

    public ItemDetailFragment() {
        // Required empty public constructor
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = DataBindingUtil.bind(view);
        Item item = Parcels.unwrap(getArguments().getParcelable(KEY_ITEM_DATA));
        mBinding.setData(item);
        if (null != item) {
            mItemAssetList = DBManager.INSTANCE.getDaoSession().getItemAssetDao().queryRaw("WHERE ITEM_ID = ?", String.valueOf(item.getItemId()));
            for (int i = 0; i < mItemAssetList.size(); i++) {
                mImageList.add(mItemAssetList.get(i).getImagePath());
            }

            addMultipleImages();
        }

    }

    private void addMultipleImages() {
        if (Utils.listNotNull(mImageList)) {
            mBinding.horizontalSv.setVisibility(View.VISIBLE);
            mBinding.imgContainer.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(8, 0, 8, 0);
            for (int i = 0; i < mImageList.size(); i++) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(lp);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImage(view.getTag().toString());
                    }
                });
                mBinding.imgContainer.addView(imageView);
                Glide.with(this).load(mImageList.get(i)).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(imageView);
                imageView.setTag(mImageList.get(i));
            }

        }
    }

    protected void showImage(String filePath) {
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });
        builder.setContentView(R.layout.img_layout);
        ImageView imageView = (ImageView) builder.findViewById(R.id.img);
        Glide.with(this).load(filePath).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(imageView);
        builder.show();
    }
}
