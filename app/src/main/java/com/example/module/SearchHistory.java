package com.example.module;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import java.util.List;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

@Table(name = "SearchHistorys")
public class SearchHistory extends Model {
    @Column(name = "Content")
    public String Content;
    @Column(name = "CreateTime")
    public long CreateTime;

    public SearchHistory() {
        CreateTime = System.currentTimeMillis();
        Content = "";
    }

    public static List<SearchHistory> GetHistoryItems()
    {
        return new Select()
                .from(SearchHistory.class)
                .limit(5)
                .orderBy("CreateTime desc")
                .execute();
    }

    public static boolean IsExistSearchContent(String  item)
    {
        return new Select().from(SearchHistory.class).where("Content == ? ", item).executeSingle() != null;
    }

    public static void deleteAll(){
        new Delete().from(SearchHistory.class).execute();
    }

}