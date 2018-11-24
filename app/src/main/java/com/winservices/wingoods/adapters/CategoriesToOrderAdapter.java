package com.winservices.wingoods.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.List;

public class CategoriesToOrderAdapter extends ExpandableRecyclerViewAdapter<CategoriesToOrderAdapter.CategoryInOrderVH,CategoriesToOrderAdapter.GoodInOrderVH> {

    private Context context;
    private List<CategoryGroup> groups;
    private int lastPosition = -1;


    public CategoriesToOrderAdapter(List<CategoryGroup> groups, Context context) {
        super(groups);
        this.context = context;
        this.groups = groups;
    }


    @Override
    public CategoryInOrderVH onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_in_order, parent, false);
        return new CategoryInOrderVH(view);
    }

    @Override
    public GoodInOrderVH onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_good_in_order, parent, false);
        return new GoodInOrderVH(view);
    }

    @Override
    public void onBindGroupViewHolder(CategoryInOrderVH holder, int flatPosition, ExpandableGroup group) {
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        Category category = categoriesDataProvider.getCategoryById(((CategoryGroup) group).getCategoryId());

        String imagePath = SharedPrefManager.getInstance(context).getImagePath(DefaultCategory.PREFIX_D_CATEGORY+category.getDCategoryId());
        Bitmap bitmap = UtilsFunctions.getOrientedBitmap(imagePath);

        holder.imgCategory.setImageBitmap(bitmap);
        holder.txtCategoryName.setText(category.getCategoryName());
        holder.llContainer.setClickable(false);

    }

    @Override
    public void onBindChildViewHolder(GoodInOrderVH holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Good good = (Good) group.getItems().get(childIndex);

        holder.txtGoodDesc.setText(good.getGoodDesc());
        holder.txtGoodName.setText(good.getGoodName());

    }



    /*@Override
    public int getItemCount() {
        return goodsToOrder.size();
    }


    public List<Good> getGoodsToOrder() {
        return goodsToOrder;
    }

    public void addAdditionalGoods(List<Good> additionalGoods){
        this.goodsToOrder.addAll(additionalGoods);
        notifyDataSetChanged();
    }

    public void remove(int position){
        this.goodsToOrder.remove(position);
        notifyDataSetChanged();
    }*/


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

     class CategoryInOrderVH extends GroupViewHolder {

        private TextView txtCategoryName;
        private ImageView imgCategory;
        private LinearLayout llContainer;

        CategoryInOrderVH(View itemView) {
            super(itemView);

            llContainer = itemView.findViewById(R.id.llContainer);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);

        }
    }


    class GoodInOrderVH extends ChildViewHolder {

        private TextView txtGoodName, txtGoodDesc;

        GoodInOrderVH(View itemView) {
            super(itemView);

            txtGoodName = itemView.findViewById(R.id.txtGoodName);
            txtGoodDesc = itemView.findViewById(R.id.txtGoodDesc);

        }
    }



}
