package com.android.angrybird.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.angrybird.R;
import com.android.angrybird.adapter.UserListAdapter;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.User;
import com.android.angrybird.databinding.ActivityUserListBinding;
import com.android.angrybird.util.FileUtils;
import com.android.angrybird.util.Utils;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class UserListActivity extends BaseActivity<ActivityUserListBinding> implements UserListAdapter.OnItemActionListener {

    private static final int REQUEST_PERMISSION_STORAGE = 2001;
    private ActivityUserListBinding viewBinding;
    private UserListAdapter mAdapter;

    @Override
    protected void onCreateCustom(ActivityUserListBinding viewBinding) {
        this.viewBinding = viewBinding;
        viewBinding.addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserListActivity.this, AddEditUserActivity.class));
            }
        });
        setUpRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetAllUserList().execute();
    }

    @Override
    protected int getActivityContentView() {
        return R.layout.activity_user_list;
    }

    @Override
    protected void onLoadImage(String filePath) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getResources().getString(R.string.query_hint));

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
        if(item.getItemId() == R.id.copy_db)
        {
            copyDbToExternalStorage();
        } else if (item.getItemId() == R.id.change_password) {
            MainActivity.startActivity(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyDbToExternalStorage() {
        boolean writePermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean readPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!(writePermission && readPermission)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_STORAGE);
        }
        else
        {
            copyDataBase();
        }

    }

    /**
     * Set up recycler view
     */
    private void setUpRecyclerView()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewBinding.userListRv.setLayoutManager(linearLayoutManager);
        viewBinding.userListRv.setNestedScrollingEnabled(true);

    }

    @Override
    public void onItemSelected(Object t) {
        User user = (User) t;
        Intent intent = new Intent(this, ItemListActivity.class);
        intent.putExtra(ItemListActivity.KEY_USER_DATA, Parcels.wrap(user));
        startActivity(intent);
    }

    private void copyDataBase()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(UserListActivity.this, "Copy started", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    File dest = FileUtils.creatImagesFolderExternalStorage();
                    org.apache.commons.io.FileUtils.copyDirectory(FileUtils.getImageDir(getApplicationContext()), dest);
                    FileUtils.copyDatabaseToExternalStorage();
                    return dest.getPath();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Toast.makeText(UserListActivity.this, "Copy finished", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(result)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserListActivity.this);
                    builder.setMessage("Backup data at location: " + result)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }.execute();
    }

    @Override
    public void onItemDeleted() {
        new GetAllUserList().execute();
    }

    private List<User> getListWithHeader(List<User> userList)
    {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                   copyDataBase();

                } else
                {
                    Toast.makeText(this, "Please consider granting it all permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class GetAllUserList extends AsyncTask<Void, Void, List<User>> {

        @Override
        protected List<User> doInBackground(Void... voids) {
            return DBManager.INSTANCE.getDaoSession().getUserDao().loadAll();
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            mAdapter = new UserListAdapter(UserListActivity.this, getListWithHeader(users));
            mAdapter.setOnItemClickListener(UserListActivity.this);
            viewBinding.userListRv.setAdapter(mAdapter);
        }
    }
}
