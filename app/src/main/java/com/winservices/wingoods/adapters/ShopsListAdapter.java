package com.winservices.wingoods.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.winservices.wingoods.R;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopsFilter;
import com.winservices.wingoods.viewholders.ShopInListViewHolder;

import java.util.ArrayList;

public class ShopsListAdapter extends RecyclerView.Adapter<ShopInListViewHolder> {

    private Context context;
    private ArrayList<Shop> shops;
    private int serverCategoryIdToOrder;

    public ShopsListAdapter(Context context, ArrayList<Shop> shops, int serverCategoryIdToOrder) {
        this.context = context;
        this.shops = shops;
        this.serverCategoryIdToOrder = serverCategoryIdToOrder;
    }

    @Override
    public ShopInListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop_in_list, parent, false);
        return new ShopInListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShopInListViewHolder holder, int position) {

        Shop shop = this.shops.get(position);

        holder.shopName.setText(shop.getShopName());
        holder.shopType.setText(shop.getShopType().getShopTypeName());
        holder.shopEmail.setText(shop.getShopEmail());
        holder.shopPhone.setText(shop.getShopPhone());
        holder.shopAdress.setText(shop.getShopAdress());

        if (serverCategoryIdToOrder != 0) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TO DO
                    //Process the order
                    Toast.makeText(context, "category to order = " + serverCategoryIdToOrder, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return shops.size();
    }


    public void setShopNameFilter(ArrayList<Shop> newList){
        shops = new ArrayList<>();
        shops.addAll(newList);
        notifyDataSetChanged();
    }

    public void setShopsFilter(ShopsFilter shopsFilter){
        ArrayList<Shop> newList = new ArrayList<>();
        for (int j = 0; j < shopsFilter.getSelectedCities().size(); j++) {
            City city = shopsFilter.getSelectedCities().get(j);
            for (int i = 0; i < shops.size() ; i++) {
                Shop shop = shops.get(i);
                if(shop.getCity().getServerCityId()==city.getServerCityId()){
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
    }
}
