package com.android.angrybird.activity;

import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.angrybird.R;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.ItemAsset;
import com.android.angrybird.databinding.ActivityAddEditItemBinding;
import com.android.angrybird.fragment.DatePickerFragment;
import com.android.angrybird.util.DateTimeUtil;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class AddEditItemActivity extends BaseActivity<ActivityAddEditItemBinding> implements DatePickerFragment.IDateSetListener {

    public static final String KEY_ITEM_DATA = "KEY_ITEM_DATA";
    public static final String KEY_ITEM_ASSET_DATA = "KEY_ITEM_ASSET_DATA";
    public static final String KEY_USER_ID = "KEY_USER_ID";
    private ActivityAddEditItemBinding viewBinding;

    private String mDate;
    private String mParticular;
    private String mCreditAmt;
    private String mDebitAmt;
    private String mCreditWgt;
    private String mDebitWgt;
    private String mImageFilePath;
    private Long userId;
    private Item item;
    private ItemAsset itemAsset;
    private ActionBar mActionBar;

    @Override
    protected void onCreateCustom(ActivityAddEditItemBinding viewBinding) {
        this.viewBinding = viewBinding;
        userId = getIntent().getLongExtra(KEY_USER_ID, 0);
        item = Parcels.unwrap(getIntent().getParcelableExtra(KEY_ITEM_DATA));
        itemAsset = Parcels.unwrap(getIntent().getParcelableExtra(KEY_ITEM_ASSET_DATA));
        mActionBar = getSupportActionBar();
        viewBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(null != item && null != itemAsset)
                    updateItemToDatabase();
                else
                    addItemToDatabase();
            }
        });

        viewBinding.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker();
            }
        });

        viewBinding.dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.setDateListener(AddEditItemActivity.this);
                fragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
            }
        });
        setItemDataForEdit();
    }

    private void setItemDataForEdit()
    {
        if(null != itemAsset && null != item)
        {
            if(null != mActionBar)
                mActionBar.setTitle("Edit Item");
            viewBinding.dateEt.setText(item.getDate());
            viewBinding.particularEt.setText(item.getParticular());
            viewBinding.creditAmtEt.setText(item.getCreditAmount());
            viewBinding.debitAmtEt.setText(item.getDebitAmount());
            viewBinding.creditWeightEt.setText(item.getCrediWeight());
            viewBinding.debitWeightEt.setText(item.getDebitWeight());
            Glide.with(this).load(itemAsset.getImagePath()).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(viewBinding.itemImage);
        }
        else {
            if(null != mActionBar)
                mActionBar.setTitle("Add Item");
        }
    }

    private void updateItemToDatabase()
    {
        if (validate()) {
            item.setModifiedDate(DateTimeUtil.getCurrentDateTime());
            item.setDate(mDate);
            item.setParticular(mParticular);
            item.setStatus(false);
            item.setCreditAmount(mCreditAmt);
            item.setDebitAmount(mDebitAmt);
            item.setCrediWeight(mCreditWgt);
            item.setDebitWeight(mDebitWgt);
            itemAsset.setImagePath(mImageFilePath);
            DBManager.INSTANCE.getDaoSession().getItemDao().update(item);
            DBManager.INSTANCE.getDaoSession().getItemAssetDao().update(itemAsset);
            finish();
        }
    }

    private void addItemToDatabase() {
        if (validate()) {
            Item item = new Item();
            item.setUserId(userId);
            item.setCreatedDate(DateTimeUtil.getCurrentDateTime());
            item.setModifiedDate("");
            item.setDate(mDate);
            item.setParticular(mParticular);
            item.setStatus(false);
            item.setCreditAmount(mCreditAmt);
            item.setDebitAmount(mDebitAmt);
            item.setCrediWeight(mCreditWgt);
            item.setDebitWeight(mDebitWgt);

            Long id = DBManager.INSTANCE.getDaoSession().getItemDao().insert(item);
            ItemAsset itemAsset = new ItemAsset();
            itemAsset.setItemId(id);
            itemAsset.setImagePath(mImageFilePath);
            DBManager.INSTANCE.getDaoSession().getItemAssetDao().insert(itemAsset);
            finish();
        }
    }

    private boolean validate() {
        mDate = viewBinding.dateEt.getText().toString();
        mParticular = viewBinding.particularEt.getText().toString();
        mCreditAmt = viewBinding.creditAmtEt.getText().toString();
        mDebitAmt = viewBinding.debitAmtEt.getText().toString();
        mCreditWgt = viewBinding.creditWeightEt.getText().toString();
        mDebitWgt = viewBinding.debitWeightEt.getText().toString();

        if (TextUtils.isEmpty(mDate)) {
            Toast.makeText(this, "Date is empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mParticular)) {
            Toast.makeText(this, "Particular is empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mCreditAmt)) {
            Toast.makeText(this, "Credit Amount is empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mDebitAmt)) {
            Toast.makeText(this, "Debit Amount is empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mCreditWgt)) {
            Toast.makeText(this, "Credit weight is empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mDebitWgt)) {
            Toast.makeText(this, "Debit weight is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected int getActivityContentView() {
        return R.layout.activity_add_edit_item;
    }

    @Override
    protected void onLoadImage(String filePath) {
        mImageFilePath = filePath;
        Glide.with(this).load(filePath).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(viewBinding.itemImage);
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        mDate = DateTimeUtil.getFormattedDate(year, month, day);
        viewBinding.dateEt.setText(mDate);

    }


}
