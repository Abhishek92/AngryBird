package com.android.angrybird.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
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
        holder.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(null != mListener)
                    mListener.onItemSelected(item);
                return true;
            }
        });

        holder.binding.sNoTxt.setText(String.valueOf(item.getItemId()));
        int debitAmt = TextUtils.isEmpty(item.getDebitAmount()) ? 0 : Integer.parseInt(item.getDebitAmount());
        int creditAmt = TextUtils.isEmpty(item.getCreditAmount()) ? 0 : Integer.parseInt(item.getCreditAmount());
        String balance = String.valueOf(debitAmt - creditAmt);
        holder.binding.balanceTxt.setText(balance);
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
