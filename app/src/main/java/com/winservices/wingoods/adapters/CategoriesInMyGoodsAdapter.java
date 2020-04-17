package com.winservices.wingoods.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;
import com.winservices.wingoods.viewholders.CategoryItemInMyGoods;

import java.util.List;


public class CategoriesInMyGoodsAdapter extends RecyclerView.Adapter<CategoryItemInMyGoods> {

    private Context context;
    private List<Category> categories;
    private String goodName;

    public CategoriesInMyGoodsAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    @Override
    public CategoryItemInMyGoods onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_in_my_goods, parent, false);
        return new CategoryItemInMyGoods(view);
    }

    @Override
    public void onBindViewHolder(CategoryItemInMyGoods holder, int position) {

        final Category category = categories.get(position);
        holder.categoryName.setText(category.getCategoryName());

        String imgPath = SharedPrefManager.getInstance(context).getImagePath(DefaultCategory.PREFIX_D_CATEGORY + category.getDCategoryId());
        if (imgPath != null) {
            Bitmap bitmap = UtilsFunctions.getPNG(imgPath);
            holder.categoryIcon.setImageBitmap(bitmap);
        } else {
            holder.categoryIcon.setImageResource(R.drawable.others);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsersDataManager usersDataManager = new UsersDataManager(context);
                User currentUser = usersDataManager.getCurrentUser();
                Good good = new Good(goodName,category.getCategoryId(), Constants.LEVEL_1_EMPTY_INT,
                        true, DataBaseHelper.SYNC_STATUS_FAILED, currentUser.getEmail());
                good.setGoodDesc("");
                good.setIsOrdered(0);

                DataManager dataManager = new DataManager(context);
                int res = dataManager.addGood(good);
                if (res == Constants.SUCCESS) {
                    Toast.makeText(context, R.string.good_add_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                }

                if(mOnGoodAddedListener != null){
                    mOnGoodAddedListener.onGoodAdded();
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnGoodAddedListener{
        public void onGoodAdded();
    }

    OnGoodAddedListener mOnGoodAddedListener;
    public void setOnGoodAddedListener(OnGoodAddedListener onGoodAddedListener) {
        this.mOnGoodAddedListener = onGoodAddedListener;
    }

    public void refreshList(){
        categories.clear();
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        List<Category> categoriesToChoose = categoriesDataProvider.getAllCategories();
        categories.addAll(categoriesToChoose);
        notifyDataSetChanged();
    }



}
