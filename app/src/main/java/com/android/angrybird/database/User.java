package com.android.angrybird.database;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.parceler.Parcel;

/**
 * Created by hp pc on 05-05-2017.
 */
@Parcel
@Entity
public class User implements Comparable<User> {

    @Id(autoincrement = true)
    private Long userId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String contactOne;
    private String contactTwo;
    private String gender;
    private String dateOfBirth;
    private String address;
    private String userImagePath;
    private String createdDate;
    private String modifiedDate;
    private boolean status;

    @Generated(hash = 1720186769)
    public User(Long userId, String firstName, String lastName, String middleName,
            String contactOne, String contactTwo, String gender, String dateOfBirth,
            String address, String userImagePath, String createdDate,
            String modifiedDate, boolean status) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.contactOne = contactOne;
        this.contactTwo = contactTwo;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.userImagePath = userImagePath;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.status = status;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getContactOne() {
        return contactOne;
    }

    public void setContactOne(String contactOne) {
        this.contactOne = contactOne;
    }

    public String getContactTwo() {
        return contactTwo;
    }

    public void setContactTwo(String contactTwo) {
        this.contactTwo = contactTwo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
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

    public boolean isStatus() {
        return status;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return firstName;
    }

    @Override
    public int compareTo(@NonNull User user) {
        return this.getFirstName().compareTo(user.getFirstName());
    }
}
