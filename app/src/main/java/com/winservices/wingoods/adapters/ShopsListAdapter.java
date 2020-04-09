package com.winservices.wingoods.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.OrderActivity;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopsFilter;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.PermissionUtil;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;
import com.winservices.wingoods.viewholders.ShopInListViewHolder;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static java.security.AccessController.getContext;

public class ShopsListAdapter extends RecyclerView.Adapter<ShopInListViewHolder> {

    private Context context;
    private ArrayList<Shop> shops;
    private boolean orderInitiated;

    public ShopsListAdapter(Context context, ArrayList<Shop> shops, boolean orderInitiated) {
        this.context = context;
        this.shops = shops;
        this.orderInitiated = orderInitiated;
    }

    @NonNull
    @Override
    public ShopInListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop_in_list, parent, false);
        return new ShopInListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShopInListViewHolder holder, int position) {

        final Shop shop = this.shops.get(position);
        holder.shopName.setText(shop.getShopName());
        holder.shopType.setText(shop.getShopType().getShopTypeName());
        holder.shopPhone.setText(shop.getShopPhone());
        holder.city.setText(shop.getCity().getCityName());

        String imagePath = SharedPrefManager.getInstance(context).getShopImagePath(shop.getServerShopId());
        if (imagePath != null) {
            Bitmap bitmap = UtilsFunctions.getOrientedBitmap(imagePath);
            holder.imgShopIcon.setImageBitmap(bitmap);
        } else {
            holder.imgShopIcon.setImageResource(R.drawable.default_shop_image);
        }

        holder.rvDCategories.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        DefaultCategoriesAdapter dCategoriesAdapter = new DefaultCategoriesAdapter(shop.getDefaultCategories(), context);
        holder.rvDCategories.setAdapter(dCategoriesAdapter);

        holder.btnOrder.setVisibility(View.GONE);


        if (canGetOrder(shop) && orderInitiated) {
            holder.btnOrder.setVisibility(View.VISIBLE);
            holder.btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.putExtra(Constants.ORDER_INITIATED, orderInitiated);
                    intent.putExtra(Constants.SELECTED_SHOP_ID, shop.getServerShopId());
                    intent.putExtra(Constants.SHOP, shop);

                    if(UtilsFunctions.isGPSEnabled(context)){
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    } else {
                        UtilsFunctions.enableGPS(((Activity) context), intent);
                    }

                }
            });
        } else {
            holder.btnOrder.setVisibility(View.GONE);
        }

    }

    private boolean canGetOrder(Shop shop) {

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        return (categoriesDataProvider.getCategoriesForOrder(shop).size() > 0);

    }

    @Override
    public int getItemCount() {
        return shops.size();
    }


    public void setShopNameFilter(ArrayList<Shop> newList) {
        shops = new ArrayList<>();
        shops.addAll(newList);
        notifyDataSetChanged();
    }

    public void setShopsFilter(ShopsFilter shopsFilter) {
        ArrayList<Shop> newList = new ArrayList<>();
        for (int j = 0; j < shopsFilter.getSelectedCities().size(); j++) {
            City city = shopsFilter.getSelectedCities().get(j);
            for (int i = 0; i < shops.size(); i++) {
                Shop shop = shops.get(i);
                if (shop.getCity().getServerCityId() == city.getServerCityId()) {
                    newList.add(shop);
                }
            }
        }
        shops.clear();
        shops.addAll(newList);
        notifyDataSetChanged();
    }

    public void setShops(ArrayList<Shop> shops) {
        this.shops.clear();
        this.shops.addAll(shops);
        notifyDataSetChanged();
    }
}
