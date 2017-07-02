package com.android.angrybird.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.angrybird.R;
import com.android.angrybird.activity.AddEditItemActivity;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.ItemAsset;
import com.android.angrybird.databinding.ItemListLayoutBinding;
import com.android.angrybird.fragment.ItemDetailFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp pc on 05-05-2017.
 */

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private Context mContext;
    private List<Item> mItemList;
    private List<Item> mBackupItemList;
    private OnItemActionListener mListener;
    public ItemListAdapter(Context context, List<Item> itemList)
    {
        mContext = context;
        mItemList = itemList;
        mBackupItemList = new ArrayList<>(itemList);
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
        holder.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(null != mListener)
                    mListener.onItemSelected(item);
                return true;
            }
        });

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemDetailFragment itemDetailFragment = new ItemDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(ItemDetailFragment.KEY_ITEM_DATA, Parcels.wrap(item));
                itemDetailFragment.setArguments(bundle);
                itemDetailFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), ItemDetailFragment.TAG);
            }
        });

        long itemId = item.getAliasNo();
        holder.binding.sNoTxt.setText(itemId == 0 ? "" : String.valueOf(itemId));
        double debitAmt = TextUtils.isEmpty(item.getDebitAmount()) ? 0 : Double.parseDouble(item.getDebitAmount());
        double creditAmt = TextUtils.isEmpty(item.getCreditAmount()) ? 0 : Double.parseDouble(item.getCreditAmount());
        holder.binding.balanceTxt.setText(String.format("%.2f", debitAmt - creditAmt));
        ItemAsset itemAsset = DBManager.INSTANCE.getDaoSession().getItemAssetDao().load(item.getItemId());
        setEditAndDelete(holder, item, itemAsset);
    }

    private void setEditAndDelete(final ViewHolder holder, final Item items, final ItemAsset itemAsset) {
        holder.binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddEditItemActivity.class);
                intent.putExtra(AddEditItemActivity.KEY_ITEM_DATA, Parcels.wrap(items));
                intent.putExtra(AddEditItemActivity.KEY_ITEM_ASSET_DATA, Parcels.wrap(itemAsset));
                mContext.startActivity(intent);
            }
        });

        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext).setMessage("Do you want to delete this item?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DBManager.INSTANCE.getDaoSession().getItemDao().deleteByKey(items.getItemId());
                        DBManager.INSTANCE.getDaoSession().getItemAssetDao().deleteByKey(itemAsset.getItemAssetId());
                        if(null != mListener)
                            mListener.onItemDeleted();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void setFilter(String queryText) {
        mItemList = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (Item item : mBackupItemList) {
            String aliasNo = item.getAliasNo() == null ? String.valueOf("0") : String.valueOf(item.getAliasNo());
            if (aliasNo.toLowerCase().contains(queryText)
                    || item.getParticular().toLowerCase().contains(queryText))
                mItemList.add(item);
        }
        notifyDataSetChanged();
    }

    public void flushFilter() {
        mItemList = new ArrayList<>();
        mItemList.addAll(mBackupItemList);
        notifyDataSetChanged();
    }

    public interface OnItemActionListener {
        void onItemSelected(Object t);

        void onItemDeleted();
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
}
