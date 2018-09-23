package com.winservices.wingoods.models;


import android.widget.ImageView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;


public class CategoryGroup extends ExpandableGroup {

    private int categoryId;

    public CategoryGroup(String title, List<Good> items) {
        super(title, items);
    }

    public void remove(int position) {
        getItems().remove(position);
    }

    public void insertItem(int position, Good newItem){
        this.getItems().add(position, newItem);
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
