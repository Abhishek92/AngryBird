package com.android.angrybird.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.angrybird.R;
import com.android.angrybird.adapter.ItemListAdapter;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.ItemAsset;
import com.android.angrybird.database.User;
import com.android.angrybird.databinding.ActivityItemListBinding;
import com.android.angrybird.util.Utils;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemListActivity extends BaseActivity<ActivityItemListBinding> implements ItemListAdapter.OnItemActionListener {

    public static final String KEY_USER_DATA = "KEY_USER_DATA";
    private ActivityItemListBinding viewBinding;
    private User user;
    private ItemListAdapter mAdapter;
    private List<Item> mItemList = new ArrayList<>();

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
        Glide.with(this).load(user.getUserImagePath()).placeholder(R.drawable.ic_account_circle_black_24dp).into(viewBinding.personHeaderView.userImg);
        viewBinding.personHeaderView.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage(user.getUserImagePath());
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item_list, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getResources().getString(R.string.query_hint_item));

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter != null && !TextUtils.isEmpty(newText))
                    mAdapter.setFilter(newText);
                else if (mAdapter != null && TextUtils.isEmpty(newText))
                    mAdapter.flushFilter();
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mAdapter != null && !TextUtils.isEmpty(query))
                    mAdapter.setFilter(query);
                else if (mAdapter != null && TextUtils.isEmpty(query))
                    mAdapter.flushFilter();
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        else if (item.getItemId() == R.id.sort_list) {
            sortItemList();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortItemList() {
        if (Utils.listNotNull(mItemList)) {
            sortUserItems();
            mAdapter.sortList(mItemList);
        }
    }

    private void sortUserItems() {
        Collections.sort(mItemList, new Comparator<Item>() {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            @Override
            public int compare(Item o1, Item o2) {
                try {
                    return dateFormat.parse(o1.getDate()).compareTo(dateFormat.parse(o2.getDate()));
                } catch (ParseException e) {
                    return 0;
                }
            }
        });
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
        double totalDebitAmt = 0;
        double totalCreditAmt = 0;
        double totalDebitWeight = 0;
        double totalCeditWeight = 0;
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            double debitAmt = TextUtils.isEmpty(item.getDebitAmount()) ? 0 : Double.parseDouble(item.getDebitAmount());
            double creditAmt = TextUtils.isEmpty(item.getCreditAmount()) ? 0 : Double.parseDouble(item.getCreditAmount());

            double debitWgt = TextUtils.isEmpty(item.getDebitWeight()) ? 0 : Double.parseDouble(item.getDebitWeight());
            double creditWgt = TextUtils.isEmpty(item.getCrediWeight()) ? 0 : Double.parseDouble(item.getCrediWeight());

            totalDebitAmt += debitAmt;
            totalCreditAmt += creditAmt;

            totalDebitWeight += debitWgt;
            totalCeditWeight += creditWgt;
        }
        double balance = totalDebitAmt - totalCreditAmt;
        double wgtbalance = totalDebitWeight - totalCeditWeight;

        viewBinding.personHeaderView.debitAmt.setText(String.format("Debit Amount:  %.2f", totalDebitAmt));
        viewBinding.personHeaderView.creditAmt.setText(String.format("Credit Amount:  %.2f", totalCreditAmt));
        viewBinding.personHeaderView.balance.setText(String.format("Amount Balance:  %.2f", balance));

        viewBinding.personHeaderView.debitWgt.setText(String.format("Debit Weight:  %.2f", totalDebitWeight));
        viewBinding.personHeaderView.creditWgt.setText(String.format("Credit Weight:  %.2f", totalCeditWeight));
        viewBinding.personHeaderView.wgtbalance.setText(String.format("Weight Balance:  %.2f", wgtbalance));
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
            mItemList = itemList;
            sortUserItems();
            mAdapter = new ItemListAdapter(ItemListActivity.this, itemList);
            mAdapter.setOnItemClickListener(ItemListActivity.this);
            viewBinding.itemListRv.setAdapter(mAdapter);
            viewBinding.itemListRv.scrollToPosition(mItemList.size() - 1);

        }
    }
}
