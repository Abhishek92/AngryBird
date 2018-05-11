package com.android.angrybird.util;

import android.app.IntentService;
import android.content.Intent;

import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;

import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class UpdateEmptyItemDateIntentService extends IntentService {

    public UpdateEmptyItemDateIntentService() {
        super("UpdateEmptyItemDateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            List<Item> itemList = DBManager.INSTANCE.getDaoSession().getItemDao().queryRaw("WHERE DATE = ?", "");
            for (Item item : itemList) {
                item.setDate(Utils.DEFAULT_DATE);
                DBManager.INSTANCE.getDaoSession().getItemDao().update(item);
            }
        }
    }
}
