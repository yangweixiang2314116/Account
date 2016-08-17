package com.example.module;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import java.util.List;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.example.account.Constants;

@Table(name = "ImageItems")
public class ImageItem extends Model {
    @Column(name = "Path")
    public String Path;
    @Column(name = "ServerPath")
    public String ServerPath;
    @Column(name = "Account", onDelete = Column.ForeignKeyAction.CASCADE)
    public Account account;
    @Column(name = "CreateTime")
    public long CreateTime;

    public ImageItem() {
        CreateTime = System.currentTimeMillis();
        Path = "";
        ServerPath = "";
    }

    public static List<ImageItem> GetImageItems(Account account)
    {
        return new Select()
                .from(ImageItem.class)
                .where("Account   = ?",account.getId())
                .orderBy("CreateTime desc")
                .execute();
    }

    public static void deleteAll(Account account){
        new Delete().from(ImageItem.class)
                .where("Account   = ?", account.getId())
                .execute();
    }

    public static List<ImageItem> getAllImages(){
        return new Select()
                .from(ImageItem.class)
                .orderBy("CreateTime desc")
                .execute();
    }

}