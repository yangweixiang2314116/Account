package com.example.module;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;


@Table(name = "OnlineHistorys")
public class OnlineHistory extends Model {
    @Column(name = "Content")
    public String Content;
    @Column(name = "LastUseTime")
    public long LastUseTime;

    public OnlineHistory() {
        LastUseTime = System.currentTimeMillis();
        Content = "";
    }

    public static List<OnlineHistory> GetHistoryItems()
    {
        return new Select()
                .from(OnlineHistory.class)
                .limit(20)
                .orderBy("LastUseTime desc")
                .execute();
    }

    public static List<OnlineHistory> GetHistoryItemsForSearch()
    {
        return new Select()
                .from(OnlineHistory.class)
                .limit(5)
                .orderBy("LastUseTime desc")
                .execute();
    }


    public static boolean IsExistOnlineContent(String  item)
    {
        return new Select().from(OnlineHistory.class).where("Content == ? ", item).executeSingle() != null;
    }

    public static OnlineHistory GetOnlineitemByContent(String value)
    {
        return new Select().from(OnlineHistory.class).where("Content == ? ", value).executeSingle() ;
    }

    public static void deleteAll(){
        new Delete().from(OnlineHistory.class).execute();
    }

}
