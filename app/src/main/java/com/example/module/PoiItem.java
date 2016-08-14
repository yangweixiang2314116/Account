package com.example.module;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.baidu.mapapi.search.core.PoiInfo;

@Table(name = "PoiItems")
public class PoiItem extends Model {
    @Column(name = "name")
    public String name;
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
    }

    public PoiItem(String name, String uid, String address, String city, String phoneNum, Account account) {
        this.name = name;
        this.uid = uid;
        this.address = address;
        this.city = city;
        this.phoneNum = phoneNum;
        this.account = account;
    }

    public static PoiItem build(PoiInfo object, Account account) {
        return new PoiItem(object.name, object.uid, object.address, object.city, object.phoneNum, account);
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
