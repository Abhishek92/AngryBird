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
import com.android.angrybird.activity.AddEditUserActivity;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.User;
import com.android.angrybird.databinding.UserListItemLayoutBinding;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by hp pc on 05-05-2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUserList;
    private OnItemActionListener mListener;

    public UserListAdapter(Context context, List<User> userList)
    {
        mContext = context;
        mUserList = userList;
    }

    public void setOnItemClickListener(OnItemActionListener listener)
    {
        mListener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserListItemLayoutBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.user_list_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User user = mUserList.get(position);
        holder.bindData(user);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != mListener)
                    mListener.onItemSelected(user);
            }
        });
        Glide.with(mContext).load(user.getUserImagePath()).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.binding.avatarImg);
        holder.binding.title.setText(String.format("%s %s %s", user.getFirstName(), user.getMiddleName(), user.getLastName()));
        holder.binding.contact.setText("Contact no: ".concat(user.getContactOne()));
        setUpPopupMenu(holder, user);
    }

    private void setUpPopupMenu(final ViewHolder holder, final User user) {
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
                                Intent intent = new Intent(mContext, AddEditUserActivity.class);
                                intent.putExtra(AddEditUserActivity.KEY_USER_DATA, Parcels.wrap(user));
                                mContext.startActivity(intent);
                                return true;
                            case R.id.delete:
                                DBManager.INSTANCE.getDaoSession().getUserDao().deleteByKey(user.getUserId());
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
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private UserListItemLayoutBinding binding;
        public ViewHolder(UserListItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = DataBindingUtil.getBinding(binding.getRoot());
        }

        public void bindData(User organisation) {
            if (null != binding) {
                binding.setData(organisation);
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
