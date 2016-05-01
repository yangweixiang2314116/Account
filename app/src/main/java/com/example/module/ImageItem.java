package com.example.module;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ImageItems")
public class ImageItem extends Model {
    @Column(name = "Path")
    public String Path;
    @Column(name = "ServerPath")
    public String ServerPath;
    @Column(name = "Account", onDelete = Column.ForeignKeyAction.CASCADE)
    public Account account;
}