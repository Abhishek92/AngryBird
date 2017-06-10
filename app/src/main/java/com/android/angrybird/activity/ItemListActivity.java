package com.android.angrybird.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.android.angrybird.R;
import com.android.angrybird.adapter.ItemListAdapter;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.ItemAsset;
import com.android.angrybird.database.User;
import com.android.angrybird.databinding.ActivityItemListBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.parceler.Parcels;

import java.util.List;

public class ItemListActivity extends BaseActivity<ActivityItemListBinding> implements ItemListAdapter.OnItemActionListener {

    public static final String KEY_USER_DATA = "KEY_USER_DATA";
    private ActivityItemListBinding viewBinding;
    private User user;

    @Override
    protected void onCreateCustom(ActivityItemListBinding viewBinding) {
        this.viewBinding = viewBinding;
        user = Parcels.unwrap(getIntent().getParcelableExtra(KEY_USER_DATA));
        viewBinding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemListActivity.this, AddEditItemActivity.class);
                intent.putExtra(AddEditItemActivity.KEY_USER_ID, user.getUserId());
                intent.putExtra(AddEditItemActivity.KEY_USER_NAME, user.getFirstName().concat(" ").concat(user.getLastName()));
                startActivity(intent);
            }
        });
        setUpHeaderView();
        setUpRecyclerView();
    }

    private void setUpHeaderView() {
        Glide.with(this).load(user.getUserImagePath()).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).placeholder(R.drawable.ic_account_circle_black_24dp).into(viewBinding.personHeaderView.userImg);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(user.getFirstName().concat(" ").concat(user.getLastName()));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected int getActivityContentView() {
        return R.layout.activity_item_list;
    }

    @Override
    protected void onLoadImage(String filePath) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetAllItemList().execute();
    }

    /**
     * Set up recycler view
     */
    private void setUpRecyclerView()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewBinding.itemListRv.setLayoutManager(linearLayoutManager);
        viewBinding.itemListRv.setNestedScrollingEnabled(true);

    }

    @Override
    public void onItemSelected(Object t) {
        final Item item = (Item) t;
        final ItemAsset itemAsset = DBManager.INSTANCE.getDaoSession().getItemAssetDao().load(item.getItemId());
        new AlertDialog.Builder(this).setMessage("What you want to do with this item?").setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(ItemListActivity.this, AddEditItemActivity.class);
                intent.putExtra(AddEditItemActivity.KEY_ITEM_DATA, Parcels.wrap(item));
                intent.putExtra(AddEditItemActivity.KEY_ITEM_ASSET_DATA, Parcels.wrap(itemAsset));
                intent.putExtra(AddEditItemActivity.KEY_USER_NAME, user.getFirstName().concat(" ").concat(user.getLastName()));
                startActivity(intent);
            }
        }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DBManager.INSTANCE.getDaoSession().getItemDao().deleteByKey(item.getItemId());
                if (null != itemAsset)
                    DBManager.INSTANCE.getDaoSession().getItemAssetDao().deleteByKey(itemAsset.getItemAssetId());
                new GetAllItemList().execute();
                dialogInterface.cancel();
            }
        }).show();
    }

    @Override
    public void onItemDeleted() {
        new GetAllItemList().execute();
    }

    private void setHeaderAmountValue(List<Item> itemList) {
        int totalDebitAmt = 0;
        int totalCreditAmt = 0;
        int totalDebitWeight = 0;
        int totalCeditWeight = 0;
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            int debitAmt = TextUtils.isEmpty(item.getDebitAmount()) ? 0 : Integer.parseInt(item.getDebitAmount());
            int creditAmt = TextUtils.isEmpty(item.getCreditAmount()) ? 0 : Integer.parseInt(item.getCreditAmount());

            int debitWgt = TextUtils.isEmpty(item.getDebitWeight()) ? 0 : Integer.parseInt(item.getDebitWeight());
            int creditWgt = TextUtils.isEmpty(item.getCrediWeight()) ? 0 : Integer.parseInt(item.getCrediWeight());

            totalDebitAmt += debitAmt;
            totalCreditAmt += creditAmt;

            totalDebitWeight += debitWgt;
            totalCeditWeight += creditWgt;
        }
        int balance = totalDebitAmt - totalCreditAmt;
        int wgtbalance = totalDebitWeight - totalCeditWeight;

        viewBinding.personHeaderView.debitAmt.setText(String.format("Debit Amount:  %d", totalDebitAmt));
        viewBinding.personHeaderView.creditAmt.setText(String.format("Credit Amount:  %d", totalCreditAmt));
        viewBinding.personHeaderView.balance.setText(String.format("Amount Balance:  %d", balance));

        viewBinding.personHeaderView.debitWgt.setText(String.format("Debit Weight:  %d", totalDebitWeight));
        viewBinding.personHeaderView.creditWgt.setText(String.format("Credit Weight:  %d", totalCeditWeight));
        viewBinding.personHeaderView.wgtbalance.setText(String.format("Weight Balance:  %d", wgtbalance));
    }

    private class GetAllItemList extends AsyncTask<Void, Void, List<Item>>
    {

        @Override
        protected List<Item> doInBackground(Void... voids) {
            return DBManager.INSTANCE.getDaoSession().getItemDao().queryRaw("WHERE USER_ID = ?", String.valueOf(user.getUserId()));
        }

        @Override
        protected void onPostExecute(List<Item> itemList) {
            super.onPostExecute(itemList);
            setHeaderAmountValue(itemList);
            ItemListAdapter adapter = new ItemListAdapter(ItemListActivity.this, itemList);
            adapter.setOnItemClickListener(ItemListActivity.this);
            viewBinding.itemListRv.setAdapter(adapter);
        }
    }
}
