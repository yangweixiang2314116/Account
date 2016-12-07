package com.ywxzhuangxiula.module;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "BrandHistorys")
public class BrandHistory extends Model {
    @Column(name = "Content")
    public String Content;
    @Column(name = "LastUseTime")
    public long LastUseTime;

    public BrandHistory() {
        LastUseTime = System.currentTimeMillis();
        Content = "";
    }

    public static List<BrandHistory> GetHistoryItems()
    {
        return new Select()
                .from(BrandHistory.class)
                .limit(20)
                .orderBy("LastUseTime desc")
                .execute();
    }

    public static List<BrandHistory> GetHistoryItemsForSearch()
    {
        return new Select()
                .from(BrandHistory.class)
                .limit(5)
                .orderBy("LastUseTime desc")
                .execute();
    }

    public static boolean IsExistBrandContent(String  item)
    {
        return new Select().from(BrandHistory.class).where("Content == ? ", item).executeSingle() != null;
    }

    public static BrandHistory GetBrandItemByContent(String value)
    {
        return new Select().from(BrandHistory.class).where("Content == ? ", value).executeSingle() ;
    }

    public static void deleteAll(){
        new Delete().from(BrandHistory.class).execute();
    }

}
