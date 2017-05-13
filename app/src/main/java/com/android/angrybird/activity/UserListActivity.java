package com.android.angrybird.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
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

import org.parceler.Parcels;

import java.util.List;

public class UserListActivity extends BaseActivity<ActivityUserListBinding> implements UserListAdapter.OnItemActionListener {

    private static final int REQUEST_PERMISSION_STORAGE = 2001;
    private ActivityUserListBinding viewBinding;
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.copy_db)
        {
            copyDbToExternalStorage();
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
        new AsyncTask<Void,Void,Void>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(UserListActivity.this, "Copy started", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                FileUtils.copyDatabaseToExternalStorage();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(UserListActivity.this, "Copy finished", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @Override
    public void onItemDeleted() {
        new GetAllUserList().execute();
    }

    private class GetAllUserList extends AsyncTask<Void, Void, List<User>>
    {

        @Override
        protected List<User> doInBackground(Void... voids) {
            return DBManager.INSTANCE.getDaoSession().getUserDao().loadAll();
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            UserListAdapter adapter = new UserListAdapter(UserListActivity.this, users);
            adapter.setOnItemClickListener(UserListActivity.this);
            viewBinding.userListRv.setAdapter(adapter);
        }
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
}
