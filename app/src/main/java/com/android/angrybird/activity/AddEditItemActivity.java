package com.android.angrybird.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.angrybird.R;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.ItemAsset;
import com.android.angrybird.databinding.ActivityAddEditItemBinding;
import com.android.angrybird.fragment.DatePickerFragment;
import com.android.angrybird.util.DateTimeUtil;
import com.android.angrybird.util.FileUtils;
import com.android.angrybird.util.Utils;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class AddEditItemActivity extends BaseActivity<ActivityAddEditItemBinding> implements DatePickerFragment.IDateSetListener {

    public static final String KEY_ITEM_DATA = "KEY_ITEM_DATA";
    public static final String KEY_ITEM_ASSET_DATA = "KEY_ITEM_ASSET_DATA";
    public static final String KEY_USER_ID = "KEY_USER_ID";
    public static final String KEY_USER_NAME = "KEY_USER_NAME";
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
    private ActionBar mActionBar;
    private List<ItemAsset> mItemAssetList = new ArrayList<>();
    private List<String> mImageList = new ArrayList<>();
    private String userName;


    @Override
    protected void onCreateCustom(ActivityAddEditItemBinding viewBinding) {
        this.viewBinding = viewBinding;
        userId = getIntent().getLongExtra(KEY_USER_ID, 0);
        userName = getIntent().getStringExtra(KEY_USER_NAME);
        item = Parcels.unwrap(getIntent().getParcelableExtra(KEY_ITEM_DATA));
        if(null != item) {
            mItemAssetList = DBManager.INSTANCE.getDaoSession().getItemAssetDao().queryRaw("WHERE ITEM_ID = ?", String.valueOf(item.getItemId()));
        }
        viewBinding.descText.setText("Adding New Item for: ".concat(userName));
        mActionBar = getSupportActionBar();
        if (null != mActionBar)
            mActionBar.setDisplayHomeAsUpEnabled(true);

        viewBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(null != item && Utils.listNotNull(mItemAssetList))
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void setItemDataForEdit()
    {
        if(Utils.listNotNull(mItemAssetList) && null != item)
        {
            if(null != mActionBar)
                mActionBar.setTitle("Edit Item");
            viewBinding.dateEt.setText(item.getDate());
            viewBinding.particularEt.setText(item.getParticular());
            viewBinding.creditAmtEt.setText(item.getCreditAmount());
            viewBinding.debitAmtEt.setText(item.getDebitAmount());
            viewBinding.creditWeightEt.setText(item.getCrediWeight());
            viewBinding.debitWeightEt.setText(item.getDebitWeight());
            setImagesForEdit();
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
            DBManager.INSTANCE.getDaoSession().getItemDao().update(item);
            updateImages(item.getItemId());
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
            insertImages(id);
            showAddMoreDialog();
        }
    }

    private void showAddMoreDialog() {
        new AlertDialog.Builder(this).setMessage("Do you want to Add more item?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clearAllViews();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).show();
    }

    private void clearAllViews() {
        viewBinding.particularEt.setText("");
        viewBinding.dateEt.setText("");
        viewBinding.debitWeightEt.setText("");
        viewBinding.creditWeightEt.setText("");
        viewBinding.debitAmtEt.setText("");
        viewBinding.creditAmtEt.setText("");
        mImageList.clear();

        viewBinding.imgContainer.removeAllViews();
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.add_more_image_view, null);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker();
            }
        });
        viewBinding.imgContainer.addView(linearLayout);
    }

    private void updateImages(Long id)
    {
        if (mImageList.size() > mItemAssetList.size()) {
            int diff = mImageList.size() - mItemAssetList.size();
            List<String> imageList = mImageList.subList(mItemAssetList.size(), mImageList.size());
            List<ItemAsset> itemAssetList = new ArrayList<>();
            for (int i = 0; i < diff; i++) {
                ItemAsset itemAsset = new ItemAsset();
                itemAsset.setItemId(id);
                itemAsset.setImagePath(imageList.get(i));
                itemAssetList.add(itemAsset);
            }
            DBManager.INSTANCE.getDaoSession().getItemAssetDao().insertInTx(itemAssetList);
        }
    }

    private void insertImages(Long id)
    {
        List<ItemAsset> itemAssets = new ArrayList<>();
        for (int i = 0; i < mImageList.size(); i++) {
            ItemAsset itemAsset = new ItemAsset();
            itemAsset.setItemId(id);
            itemAsset.setImagePath(mImageList.get(i));
            itemAssets.add(itemAsset);
        }

        DBManager.INSTANCE.getDaoSession().getItemAssetDao().insertInTx(itemAssets);
    }

    private boolean validate() {
        mDate = viewBinding.dateEt.getText().toString();
        mParticular = viewBinding.particularEt.getText().toString();
        mCreditAmt = viewBinding.creditAmtEt.getText().toString();
        mDebitAmt = viewBinding.debitAmtEt.getText().toString();
        mCreditWgt = viewBinding.creditWeightEt.getText().toString();
        mDebitWgt = viewBinding.debitWeightEt.getText().toString();

        if (TextUtils.isEmpty(mParticular)) {
            Toast.makeText(this, "Particular is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected int getActivityContentView() {
        return R.layout.activity_add_edit_item;
    }

    @Override
    protected void onLoadImage(final String filePath) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                mImageFilePath = FileUtils.storeImage(AddEditItemActivity.this, bitmap);
                return mImageFilePath;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mImageList.add(mImageFilePath);
                addMultipleImages();
            }
        }.execute();
    }

    private void setImagesForEdit()
    {
        if(Utils.listNotNull(mItemAssetList))
        {
            for (int i = 0; i < mItemAssetList.size(); i++) {
                mImageList.add(mItemAssetList.get(i).getImagePath());
            }
        }
        addMultipleImages();
    }

    private void addMultipleImages()
    {
        if(Utils.listNotNull(mImageList)) {
            viewBinding.horizontalSv.setVisibility(View.VISIBLE);
            viewBinding.imgContainer.removeAllViews();
            for (int i = 0; i < mImageList.size(); i++) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.image_item_layout, null);
                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.img);
                viewBinding.imgContainer.addView(linearLayout);
                Glide.with(this).load(mImageList.get(i)).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(imageView);
                imageView.setTag(mImageList.get(i));
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImage(view.getTag().toString());
                    }
                });
            }

            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.add_more_image_view, null);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPicker();
                }
            });
            viewBinding.imgContainer.addView(linearLayout);
        }
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        mDate = DateTimeUtil.getFormattedDate(year, month, day);
        viewBinding.dateEt.setText(mDate);

    }
}
