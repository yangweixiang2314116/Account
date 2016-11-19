package com.example.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;


@Table(name = "OfflineHistorys")
public class OfflineHistory extends Model implements Parcelable {
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
    @Column(name = "lastUseTime")
    public long lastUseTime;

    public OfflineHistory() {
        name = "";
        uid = "";
        address = "";
        city = "";
        phoneNum = "";
        latitude = 0.0;
        longitude = 0.0;
        latitudeE6 = 0.0;
        longitudeE6 = 0.0;
        lastUseTime = System.currentTimeMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.name);
        dest.writeString(this.uid);
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeString(this.phoneNum);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitudeE6);
        dest.writeDouble(this.longitudeE6);
        dest.writeLong(this.lastUseTime);
    }

    private OfflineHistory(Parcel in) {
        this.name = in.readString();
        this.uid = in.readString();
        this.address = in.readString();
        this.city = in.readString();
        this.phoneNum = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.latitudeE6 = in.readDouble();
        this.longitudeE6 = in.readDouble();
        this.lastUseTime = in.readLong();
    }

    public static final Creator<OfflineHistory> CREATOR = new Creator<OfflineHistory>() {
        @Override
        public OfflineHistory createFromParcel(Parcel parcel) {
            return new OfflineHistory(parcel);
        }

        @Override
        public OfflineHistory[] newArray(int size) {
            return new OfflineHistory[size];
        }
    };

    public static List<OfflineHistory> GetHistoryItems()
    {
        return new Select()
                .from(OfflineHistory.class)
                .limit(20)
                .orderBy("lastUseTime desc")
                .execute();
    }

    public static List<OfflineHistory> GetHistoryItemsForSearch()
    {
        return new Select()
                .from(OfflineHistory.class)
                .limit(5)
                .orderBy("LastUseTime desc")
                .execute();
    }

    public static OfflineHistory Build(PoiInfo  poi)
    {
        OfflineHistory item = new OfflineHistory();
        item.address = poi.address;
        item.city = poi.city;
        item.latitude = poi.location.latitude;
        item.longitude = poi.location.longitude;
        item.latitudeE6 = poi.location.latitudeE6;
        item.longitudeE6 = poi.location.longitudeE6;
        item.name = poi.name;
        item.uid = poi.uid;
        item.city = poi.city;
        item.phoneNum = poi.phoneNum;
        item.lastUseTime = System.currentTimeMillis();
        return item;
    }

    public static boolean IsExistOfflineContent(PoiInfo  poi)
    {
        return new Select().from(OfflineHistory.class).where(" latitude == ?  and longitude == ?",
                poi.location.latitude, poi.location.longitude).executeSingle() != null;
    }

    public static OfflineHistory GetOfflineitemByContent(PoiInfo  poi)
    {
        return new Select().from(OfflineHistory.class).where(" latitude == ?  and longitude == ?",
                poi.location.latitude, poi.location.longitude).executeSingle() ;
    }

    public static void deleteAll(){
        new Delete().from(OfflineHistory.class).execute();
    }
}

