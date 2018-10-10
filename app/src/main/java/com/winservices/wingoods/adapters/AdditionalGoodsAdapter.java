package com.winservices.wingoods.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.viewholders.AdditionalGoodVH;
import com.winservices.wingoods.viewholders.CategoryGroupViewHolder;
import com.winservices.wingoods.viewholders.CategoryInOrderVH;
import com.winservices.wingoods.viewholders.GoodItemViewHolder;

import java.util.ArrayList;
import java.util.List;


public class AdditionalGoodsAdapter extends ExpandableRecyclerViewAdapter<CategoryInOrderVH,AdditionalGoodVH> {

    private Context context;
    private List<CategoryGroup> groups;
    private List<Good> selectedAdditionalGoods;

    public AdditionalGoodsAdapter(List<CategoryGroup> groups, Context context) {
        super(groups);
        this.context = context;
        this.groups = groups;
        selectedAdditionalGoods = new ArrayList<>();
    }

    @Override
    public CategoryInOrderVH onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_with_additional_goods, parent, false);
        return new CategoryInOrderVH(view);
    }

    @Override
    public AdditionalGoodVH onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_additional_good_to_order, parent, false);
        return new AdditionalGoodVH(view);
    }

    @Override
    public void onBindGroupViewHolder(CategoryInOrderVH holder, int flatPosition, ExpandableGroup group) {

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        final Category category = categoriesDataProvider.getCategoryById(((CategoryGroup) group).getCategoryId());
        categoriesDataProvider.closeDB();

        holder.container.setClickable(false);
        holder.icon.setImageResource(category.getIcon());
        holder.txtCategoryName.setText(category.getCategoryName());
    }

    @Override
    public void onBindChildViewHolder(final AdditionalGoodVH holder, int flatPosition, ExpandableGroup group, int childIndex) {

        final Good good = (Good) group.getItems().get(childIndex);

        holder.cbAdditionalGood.setText(good.getGoodName());
        holder.cbAdditionalGood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean isChecked = holder.cbAdditionalGood.isChecked();
                if (isChecked){
                    selectedAdditionalGoods.add(good);
                } else {
                    selectedAdditionalGoods.remove(good);
                }
            }
        });

    }

    public List<Good> getSelectedAdditionalGoods() {
        return selectedAdditionalGoods;
    }


    public void insertGood(Good goodToInsert) {

        for (int i = 0; i < groups.size(); i++) {
            CategoryGroup categoryGroup = groups.get(i);
            if (categoryGroup.getCategoryId()==goodToInsert.getCategoryId()){
                groups.get(i).insertItem(goodToInsert);
            }
        }
        notifyDataSetChanged();
    }
}
