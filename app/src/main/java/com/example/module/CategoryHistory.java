package com.example.module;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "CategoryHistorys")
public class CategoryHistory extends Model {
    @Column(name = "Content")
    public String Content;
    @Column(name = "LastUseTime")
    public long LastUseTime;

    public CategoryHistory() {
        LastUseTime = System.currentTimeMillis();
        Content = "";
    }

    public static List<CategoryHistory> GetHistoryItems()
    {
        return new Select()
                .from(CategoryHistory.class)
                .limit(20)
                .orderBy("LastUseTime desc")
                .execute();
    }

    public static List<CategoryHistory> GetHistoryItemsForSearch()
    {
        return new Select()
                .from(CategoryHistory.class)
                .limit(5)
                .orderBy("LastUseTime desc")
                .execute();
    }

    public static boolean IsExistCategoryContent(String  item)
    {
        return new Select().from(CategoryHistory.class).where("Content == ? ", item).executeSingle() != null;
    }

    public static CategoryHistory GetCategoryItemByContent(String value)
    {
        return new Select().from(CategoryHistory.class).where("Content == ? ", value).executeSingle() ;
    }

    public static void deleteAll(){
        new Delete().from(CategoryHistory.class).execute();
    }

}