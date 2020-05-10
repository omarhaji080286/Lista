package com.winservices.wingoods.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.OrderActivity;
import com.winservices.wingoods.activities.ShopsActivity;
import com.winservices.wingoods.adapters.CategoriesToOrderAdapter;

import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.RecyclerItemTouchHelper;

import java.util.List;
import java.util.Objects;

public class OrderFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public static final String TAG = "OrderFragment";

    private RecyclerView rvCategoriesToOrder;
    private CategoriesToOrderAdapter categoriesToOrderAdapter;
    private Shop shop;
    private GridLayoutManager glm;

    public OrderFragment(CategoriesToOrderAdapter categoriesToOrderAdapter, Shop shop) {
        this.categoriesToOrderAdapter = categoriesToOrderAdapter;
        this.shop = shop;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.order_frag_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        OrderActivity orderActivity = (OrderActivity) Objects.requireNonNull(getActivity());
        switch (item.getItemId()) {
            case R.id.completeOrderData:
                sendOrder(orderActivity);
                break;
            case android.R.id.home :
                Intent intent = new Intent(orderActivity, ShopsActivity.class);
                intent.putExtra(Constants.ORDER_INITIATED, true);
                startActivity(intent);
                orderActivity.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategoriesToOrder = view.findViewById(R.id.rvCategoriesToOrder);
        TextView txtShopName = view.findViewById(R.id.txt_shop_name);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvCategoriesToOrder);

        txtShopName.setText(shop.getShopName());

        loadCategoriesToOrder();

    }

    private void loadCategoriesToOrder() {

        final int GRID_COLUMN_NUMBER = 3;
        glm = new GridLayoutManager(getContext(), GRID_COLUMN_NUMBER);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoriesToOrderAdapter.getItemViewType(position) == 2) {
                    return GRID_COLUMN_NUMBER;
                }
                return 1;
            }
        });

        rvCategoriesToOrder.setLayoutManager(glm);
        rvCategoriesToOrder.setAdapter(categoriesToOrderAdapter);
        expandRecyclerView();
    }

    private void expandRecyclerView() {
        for (int i = categoriesToOrderAdapter.getGroups().size() - 1; i >= 0; i--) {
            if (categoriesToOrderAdapter.isGroupExpanded(i)) {
                return;
            }
            categoriesToOrderAdapter.toggleGroup(i);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CategoriesToOrderAdapter.GoodInOrderVH) {
            categoriesToOrderAdapter.removeChildItem(position);
        }
    }

    private void sendOrder(OrderActivity orderActivity) {
        if (categoriesToOrderAdapter.getGoodsToOrderNumber() > 0) {
            if (categoriesToOrderAdapter.getGoodsToComplete().size() == 0) {
                orderActivity.setTitle(getString(R.string.validate_order_form));
                orderActivity.displayFragment(new CompleteOrderFragment(shop), CompleteOrderFragment.TAG);
            } else {
                Toast.makeText(getContext(), R.string.set_descriptions, Toast.LENGTH_SHORT).show();
                List<Good> goodsToComplete = categoriesToOrderAdapter.getGoodsToComplete();
                for (int i = 0; i < goodsToComplete.size(); i++) {
                    Good good = goodsToComplete.get(i);
                    int position = categoriesToOrderAdapter.getGoodPosition(good.getGoodId());
                    animateItem(position);
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.empty_order, Toast.LENGTH_SHORT).show();
        }
    }

    private void animateItem(final int position) {
        new Handler().postDelayed(() -> {

            View v = glm.findViewByPosition(position);
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
            if (v != null) {
                v.startAnimation(anim);
            }

        }, 50);
    }

}
