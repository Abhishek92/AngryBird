package com.android.angrybird.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by hp pc on 04-05-2017.
 */

@Entity
public class Admin {
    @Id(autoincrement = true)
    private Long id;
    private String pin;

    @Generated(hash = 971474828)
    public Admin(Long id, String pin) {
        this.id = id;
        this.pin = pin;
    }

    @Generated(hash = 1708792177)
    public Admin() {
    }

    public Long getId() {
        return id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
