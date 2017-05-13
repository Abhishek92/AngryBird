package com.android.angrybird.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.angrybird.R;
import com.android.angrybird.activity.AddEditItemActivity;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.ItemAsset;
import com.android.angrybird.databinding.ItemListLayoutBinding;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by hp pc on 05-05-2017.
 */

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private Context mContext;
    private List<Item> mItemList;
    private OnItemActionListener mListener;
    public ItemListAdapter(Context context, List<Item> itemList)
    {
        mContext = context;
        mItemList = itemList;
    }

    public void setOnItemClickListener(OnItemActionListener listener)
    {
        mListener = listener;
    }
    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemListLayoutBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_list_layout, parent, false);
        return new ItemListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ItemListAdapter.ViewHolder holder, int position) {
        final Item item = mItemList.get(position);
        holder.bindData(item);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != mListener)
                    mListener.onItemSelected(item);
            }
        });
        ItemAsset itemAsset = DBManager.INSTANCE.getDaoSession().getItemAssetDao().load(item.getItemId());
        Glide.with(mContext).load(itemAsset.getImagePath()).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.binding.avatarImg);

        setPopupMenu(holder, item, itemAsset);
    }

    private void setPopupMenu(final ViewHolder holder, final Item items, final ItemAsset itemAsset) {
        holder.binding.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(mContext, holder.binding.popupMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.edit:
                                Intent intent = new Intent(mContext, AddEditItemActivity.class);
                                intent.putExtra(AddEditItemActivity.KEY_ITEM_DATA, Parcels.wrap(items));
                                intent.putExtra(AddEditItemActivity.KEY_ITEM_ASSET_DATA, Parcels.wrap(itemAsset));
                                mContext.startActivity(intent);
                                return true;
                            case R.id.delete:
                                DBManager.INSTANCE.getDaoSession().getItemDao().deleteByKey(items.getItemId());
                                DBManager.INSTANCE.getDaoSession().getItemAssetDao().deleteByKey(itemAsset.getItemAssetId());
                                if(null != mListener)
                                    mListener.onItemDeleted();
                                return true;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemListLayoutBinding binding;
        public ViewHolder(ItemListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = DataBindingUtil.getBinding(binding.getRoot());
        }

        public void bindData(Item item) {
            if (null != binding) {
                binding.setData(item);
                binding.executePendingBindings();
            }
        }
    }

    public interface OnItemActionListener
    {
        public void onItemSelected(Object t);
        public void onItemDeleted();
    }
}
