package com.android.angrybird.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.android.angrybird.R;
import com.android.angrybird.adapter.ItemListAdapter;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.User;
import com.android.angrybird.databinding.ActivityItemListBinding;

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
                startActivity(intent);
            }
        });
        setUpRecyclerView();
    }

    @Override
    protected int getActivityContentView() {
        return R.layout.activity_item_list;
    }

    @Override
    protected void onLoadImage(String filePath) {

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
        Item item = (Item) t;
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra(ItemDetailActivity.KEY_ITEM_DATA, Parcels.wrap(item));
        startActivity(intent);
    }

    @Override
    public void onItemDeleted() {
        new GetAllItemList().execute();
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
            ItemListAdapter adapter = new ItemListAdapter(ItemListActivity.this, itemList);
            adapter.setOnItemClickListener(ItemListActivity.this);
            viewBinding.itemListRv.setAdapter(adapter);
        }
    }
}
