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
public class Item {
    @Id(autoincrement = true)
    private Long itemId;
    private Long userId;
    private String date;
    private String particular;
    private String debitAmount;
    private String creditAmount;
    private String debitWeight;
    private String crediWeight;
    private boolean status;
    private String createdDate;
    private String modifiedDate;

    @Generated(hash = 1209699489)
    public Item(Long itemId, Long userId, String date, String particular,
            String debitAmount, String creditAmount, String debitWeight,
            String crediWeight, boolean status, String createdDate,
            String modifiedDate) {
        this.itemId = itemId;
        this.userId = userId;
        this.date = date;
        this.particular = particular;
        this.debitAmount = debitAmount;
        this.creditAmount = creditAmount;
        this.debitWeight = debitWeight;
        this.crediWeight = crediWeight;
        this.status = status;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    @Generated(hash = 1470900980)
    public Item() {
    }

    public Long getItemId() {
        return itemId;
    }



    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParticular() {
        return particular;
    }

    public void setParticular(String particular) {
        this.particular = particular;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getDebitWeight() {
        return debitWeight;
    }

    public void setDebitWeight(String debitWeight) {
        this.debitWeight = debitWeight;
    }

    public String getCrediWeight() {
        return crediWeight;
    }

    public void setCrediWeight(String crediWeight) {
        this.crediWeight = crediWeight;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}