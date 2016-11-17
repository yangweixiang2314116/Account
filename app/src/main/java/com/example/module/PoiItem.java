package com.example.module;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.account.Constants;

@Table(name = "PoiItems")
public class PoiItem extends Model {
    @Column(name = "name")
    public String name;
    @Column(name = "latitude")
    public Double  latitude;
    @Column(name = "longitude")
    public Double  longitude;
    @Column(name = "latitudeE6")
    public Double  latitudeE6;
    @Column(name = "longitudeE6")
    public Double  longitudeE6;
    @Column(name = "uid")
    public String uid;
    @Column(name = "address")
    public String address;
    @Column(name = "city")
    public String city;
    @Column(name = "phoneNum")
    public String phoneNum;
    @Column(name = "Account", onDelete = Column.ForeignKeyAction.CASCADE)
    public Account account;

    public PoiItem() {
        name = "";
        uid = "";
        address = "";
        city = "";
        phoneNum = "";
        latitude = 0.0;
        longitude = 0.0;
        latitudeE6 = 0.0;
        longitudeE6 = 0.0;
    }

    public PoiItem(String name, String uid, String address, String city, String phoneNum,
                   double latitude, double longitude, double latitudeE6, double longitudeE6, Account account) {
        this.name = name;
        this.uid = uid;
        this.address = address;
        this.city = city;
        this.phoneNum = phoneNum;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitudeE6 = latitudeE6;
        this.longitudeE6 = longitudeE6;
        this.account = account;
    }


    public static PoiItem build(PoiInfo object, Account account) {
        Log.i(Constants.TAG, "--PoiInfo---");
        return new PoiItem(object.name, object.uid, object.address, object.city, object.phoneNum,
                object.location.latitude, object.location.longitude, object.location.latitudeE6, object.location.longitudeE6,account);
    }


    public static PoiItem build(OfflineHistory object, Account account) {
        Log.i(Constants.TAG, "--buildFromOffline---");
        return new PoiItem(object.name, object.uid, object.address, object.city, object.phoneNum,
                object.latitude, object.longitude, object.latitudeE6, object.longitudeE6,account);
    }

    public static PoiItem GetPoiItem(Account account)
    {
        return new Select()
                .from(PoiItem.class)
                .where("Account   = ?",account.getId())
                .executeSingle();
    }

    public static void delete(Account account){
        new Delete().from(PoiItem.class)
                .where("Account   = ?", account.getId())
                .execute();
    }
}
