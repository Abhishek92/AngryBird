package com.android.angrybird.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
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
    private String aliasNo;
    private String date;
    private String particular;
    private String debitAmount;
    private String creditAmount;
    private String debitWeight;
    private String crediWeight;
    private boolean status;
    private String createdDate;
    private String modifiedDate;

    @Generated(hash = 1817400804)
    public Item(Long itemId, Long userId, String aliasNo, String date,
                String particular, String debitAmount, String creditAmount,
                String debitWeight, String crediWeight, boolean status,
                String createdDate, String modifiedDate) {
        this.itemId = itemId;
        this.userId = userId;
        this.aliasNo = aliasNo;
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
        return this.itemId;
    }
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    public Long getUserId() {
        return this.userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAliasNo() {
        return this.aliasNo;
    }

    public void setAliasNo(String aliasNo) {
        this.aliasNo = aliasNo;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getParticular() {
        return this.particular;
    }
    public void setParticular(String particular) {
        this.particular = particular;
    }
    public String getDebitAmount() {
        return this.debitAmount;
    }
    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }
    public String getCreditAmount() {
        return this.creditAmount;
    }
    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }
    public String getDebitWeight() {
        return this.debitWeight;
    }
    public void setDebitWeight(String debitWeight) {
        this.debitWeight = debitWeight;
    }
    public String getCrediWeight() {
        return this.crediWeight;
    }
    public void setCrediWeight(String crediWeight) {
        this.crediWeight = crediWeight;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getCreatedDate() {
        return this.createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public String getModifiedDate() {
        return this.modifiedDate;
    }
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


}