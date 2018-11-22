package com.winservices.wingoods.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.winservices.wingoods.R;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.List;

public class DefaultCategoriesAdapter extends RecyclerView.Adapter<DefaultCategoriesAdapter.DCategoryVH> {

    private List<DefaultCategory> dCategories;
    private Context context;

    public DefaultCategoriesAdapter(List<DefaultCategory> dCategories, Context context) {
        this.dCategories = dCategories;
        this.context = context;
    }

    public void setDCategories(List<DefaultCategory> dCategories) {
        this.dCategories = dCategories;
        notifyDataSetChanged();
    }

    @Override
    public DCategoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_d_category, parent, false);
        return new DCategoryVH(itemView);
    }

    @Override
    public void onBindViewHolder(DCategoryVH holder, int position) {

        DefaultCategory dCategory = dCategories.get(position);

        String imagePath = SharedPrefManager.getInstance(context).getImagePath(DefaultCategory.PREFIX_D_CATEGORY+dCategory.getDCategoryId());
        Bitmap bitmap = UtilsFunctions.getOrientedBitmap(imagePath);

        holder.imgDCategory.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return dCategories.size();
    }

    class DCategoryVH extends RecyclerView.ViewHolder {

        private ImageView imgDCategory;

        public DCategoryVH(View itemView) {
            super(itemView);

            imgDCategory = itemView.findViewById(R.id.img_d_category);
        }
    }

}
