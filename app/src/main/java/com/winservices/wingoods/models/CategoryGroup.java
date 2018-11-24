package com.winservices.wingoods.models;


import android.widget.ImageView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;


public class CategoryGroup extends ExpandableGroup {

    private int categoryId;
    private int serverCategoryId;
    private List<Good> items;
    private Category category;

    public CategoryGroup(String title, List<Good> items) {
        super(title, items);
        this.items = items;
    }



    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void remove(int position) {
        getItems().remove(position);
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getServerCategoryId() {
        return serverCategoryId;
    }

    public void setServerCategoryId(int serverCategoryId) {
        this.serverCategoryId = serverCategoryId;
    }

    public void removeItem(Good item){
        this.items.remove(item);
    }

    public void insertItem(Good good){
        this.items.add(good);
    }

    public void clear() {
        this.items.clear();
    }
}
