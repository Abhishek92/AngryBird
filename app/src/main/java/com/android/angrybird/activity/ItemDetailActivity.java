package com.android.angrybird.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.angrybird.R;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.ItemAsset;
import com.android.angrybird.databinding.ActivityItemDetailBinding;
import com.android.angrybird.util.Utils;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailActivity extends BaseActivity<ActivityItemDetailBinding> {

    public static final String KEY_ITEM_DATA = "KEY_ITEM_DATA";
    private List<ItemAsset> mItemAssetList = new ArrayList<>();
    private List<String> mImageList = new ArrayList<>();

    @Override
    protected void onCreateCustom(ActivityItemDetailBinding viewBinding) {
        Item item = Parcels.unwrap(getIntent().getParcelableExtra(KEY_ITEM_DATA));
        viewBinding.setData(item);
        if(null != item) {
            mItemAssetList = DBManager.INSTANCE.getDaoSession().getItemAssetDao().queryRaw("WHERE ITEM_ID = ?", String.valueOf(item.getItemId()));
            for (int i = 0; i < mItemAssetList.size(); i++) {
                mImageList.add(mItemAssetList.get(i).getImagePath());
            }

            addMultipleImages(viewBinding);
        }

    }

    @Override
    protected int getActivityContentView() {
        return R.layout.activity_item_detail;
    }

    @Override
    protected void onLoadImage(String filePath) {

    }

    private void addMultipleImages(ActivityItemDetailBinding viewBinding)
    {
        if(Utils.listNotNull(mImageList)) {
            viewBinding.horizontalSv.setVisibility(View.VISIBLE);
            viewBinding.imgContainer.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(8,0,8,0);
            for (int i = 0; i < mImageList.size(); i++) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(lp);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImage(view.getTag().toString());
                    }
                });
                viewBinding.imgContainer.addView(imageView);
                Glide.with(this).load(mImageList.get(i)).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(imageView);
                imageView.setTag(mImageList.get(i));
            }

        }
    }


}
