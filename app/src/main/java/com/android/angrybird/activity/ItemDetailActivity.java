package com.android.angrybird.activity;

import com.android.angrybird.R;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.ItemAsset;
import com.android.angrybird.databinding.ActivityItemDetailBinding;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class ItemDetailActivity extends BaseActivity<ActivityItemDetailBinding> {

    public static final String KEY_ITEM_DATA = "KEY_ITEM_DATA";

    @Override
    protected void onCreateCustom(ActivityItemDetailBinding viewBinding) {
        Item item = Parcels.unwrap(getIntent().getParcelableExtra(KEY_ITEM_DATA));
        viewBinding.setData(item);
        ItemAsset itemAsset = DBManager.INSTANCE.getDaoSession().getItemAssetDao().load(item.getItemId());
        Glide.with(this).load(itemAsset.getImagePath()).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(viewBinding.itemImage);
    }

    @Override
    protected int getActivityContentView() {
        return R.layout.activity_item_detail;
    }

    @Override
    protected void onLoadImage(String filePath) {

    }
}
