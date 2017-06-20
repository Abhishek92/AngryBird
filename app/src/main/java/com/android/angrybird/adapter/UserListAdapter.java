package com.android.angrybird.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.angrybird.R;
import com.android.angrybird.activity.AddEditUserActivity;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.User;
import com.android.angrybird.databinding.HeaderLayoutViewBinding;
import com.android.angrybird.databinding.UserListItemLayoutBinding;
import com.android.angrybird.fragment.ImageDialogFragment;
import com.android.angrybird.pdf.PdfCreator;
import com.android.angrybird.util.Utils;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hp pc on 05-05-2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUserList;
    private List<User> mBackupList;
    private OnItemActionListener mListener;


    public UserListAdapter(Context context, List<User> userList)
    {
        mContext = context;
        mUserList = userList;
        mBackupList = new ArrayList<>(userList);
    }



    public void setOnItemClickListener(OnItemActionListener listener)
    {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.header_layout_view) {
            HeaderLayoutViewBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.header_layout_view, parent, false);
            return new ViewHolder(binding);
        } else {
            UserListItemLayoutBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.user_list_item_layout, parent, false);
            return new ViewHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User user = mUserList.get(position);
        if (user.getGender() == null) {
            holder.headerLayoutViewBinding.container.setText(user.getFirstName());
        } else {
            holder.bindData(user);
            holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mListener)
                        mListener.onItemSelected(user);
                }
            });
            Glide.with(mContext).load(user.getUserImagePath()).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.binding.avatarImg);
            holder.binding.avatarImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(user.getUserImagePath())) {
                        ImageDialogFragment imageDialogFragment = ImageDialogFragment.getInstance(user.getUserImagePath());
                        imageDialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), ImageDialogFragment.TAG);
                    }
                }
            });
            holder.binding.title.setText(String.format("%s. %s %s %s", user.getAliasNo(), user.getFirstName(), user.getMiddleName(), user.getLastName()));
            holder.binding.contact.setText("Contact no: ".concat(user.getContactOne()));
            holder.binding.address.setText("Address: ".concat(user.getAddress()));
            setUpPopupMenu(holder, user);
        }
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
                                new AlertDialog.Builder(mContext).setMessage("Do you want to delete this item?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DBManager.INSTANCE.getDaoSession().getUserDao().deleteByKey(user.getUserId());
                                        if(null != mListener)
                                            mListener.onItemDeleted();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).show();

                                return true;
                            case R.id.share:
                                PdfCreator pdfCreator = PdfCreator.createPdf(String.valueOf(user.getUserId()), user, mContext);
                                pdfCreator.createAndSharePdf();
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
    public int getItemViewType(int position) {
        return mUserList.get(position).getGender() == null ? R.layout.header_layout_view : R.layout.user_list_item_layout;
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void flushFilter() {
        mUserList = new ArrayList<>();
        mUserList.addAll(mBackupList);
        notifyDataSetChanged();
    }

    private List<User> getListWithHeader(List<User> list) {
        List<User> userList = new ArrayList<>(list);
        if (Utils.listNotNull(userList)) {
            Collections.sort(userList);
            User headr = new User();
            headr.setFirstName(String.valueOf(userList.get(0).getFirstName().charAt(0)));
            userList.add(0, headr);
            for (int i = 1; i < userList.size(); i++) {
                User user = userList.get(i - 1);
                User nextUser = userList.get(i);
                if (user.getFirstName().charAt(0) != nextUser.getFirstName().charAt(0)) {
                    User headerVal = new User();
                    headerVal.setFirstName(String.valueOf(nextUser.getFirstName().charAt(0)));
                    userList.add(i, headerVal);
                }
            }
        }

        return userList;
    }

    public void setFilter(String queryText) {

        mUserList = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (User item : mBackupList) {
            String contactOne = TextUtils.isEmpty(item.getContactOne()) ? "" : item.getContactOne();
            String contactTwo = TextUtils.isEmpty(item.getContactTwo()) ? "" : item.getContactTwo();
            String aliasNo = item.getAliasNo() == null ? String.valueOf("0") : String.valueOf(item.getAliasNo());
            if (item.getFirstName().toLowerCase().contains(queryText) || contactOne.toLowerCase().contains(queryText)
                    || contactTwo.toLowerCase().contains(queryText) || aliasNo.toLowerCase().contains(queryText)
                    && item.getGender() != null)
                mUserList.add(item);
        }
        mUserList = getListWithHeader(mUserList);
        notifyDataSetChanged();
    }

    public interface OnItemActionListener {
        void onItemSelected(Object t);

        void onItemDeleted();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private UserListItemLayoutBinding binding;
        private HeaderLayoutViewBinding headerLayoutViewBinding;
        public ViewHolder(UserListItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = DataBindingUtil.getBinding(binding.getRoot());

        }

        public ViewHolder(HeaderLayoutViewBinding binding) {
            super(binding.getRoot());
            this.headerLayoutViewBinding = DataBindingUtil.getBinding(binding.getRoot());
        }

        public void bindData(User organisation) {
            if (null != binding) {
                binding.setData(organisation);
                binding.executePendingBindings();
            }
        }
    }
}
