package com.android.angrybird.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.parceler.Parcel;

/**
 * Created by hp pc on 05-05-2017.
 */

@Parcel
@Entity
public class ItemAsset {

    @Id(autoincrement = true)
    private Long itemAssetId;
    private Long itemId;
    private String imagePath;

    @Generated(hash = 1996742540)
    public ItemAsset(Long itemAssetId, Long itemId, String imagePath) {
        this.itemAssetId = itemAssetId;
        this.itemId = itemId;
        this.imagePath = imagePath;
    }

    @Generated(hash = 1223351337)
    public ItemAsset() {
    }

    public Long getItemAssetId() {
        return itemAssetId;
    }

    public void setItemAssetId(Long itemAssetId) {
        this.itemAssetId = itemAssetId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}
