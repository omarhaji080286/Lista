package com.winservices.wingoods.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.ShopsActivity;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.Controlers;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.UtilsFunctions;
import com.winservices.wingoods.viewholders.CategoryGroupViewHolder;
import com.winservices.wingoods.viewholders.GoodItemViewHolder;

import java.util.List;

public class MyGoodsAdapter extends ExpandableRecyclerViewAdapter<CategoryGroupViewHolder, GoodItemViewHolder> {

    private Context context;
    private List<CategoryGroup> groups;

    public MyGoodsAdapter(List<CategoryGroup> groups, Context context) {
        super(groups);
        this.context = context;
        this.groups = groups;
    }

    @Override
    public CategoryGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_group, parent, false);
        return new CategoryGroupViewHolder(view);
    }

    @Override
    public GoodItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_good, parent, false);
        return new GoodItemViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(final GoodItemViewHolder holder, final int flatPosition, ExpandableGroup group, final int childIndex) {

        final Good goodItem = (Good) group.getItems().get(childIndex);
        holder.setGoodId(goodItem.getGoodId());
        holder.setGoodName(goodItem.getGoodName());
        holder.setCategoryId(goodItem.getCategoryId());
        holder.setLevel(goodItem.getQuantityLevelId());
        holder.goodDescription.setText(goodItem.getGoodDesc());

        if (goodItem.isToBuy()) {
            holder.viewForeground.setBackground(ContextCompat.getDrawable(context, R.drawable.red_button));
        } else {
            holder.viewForeground.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_button));
        }

        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
                Good good = goodsDataProvider.getGoodById(goodItem.getGoodId());
                goodsDataProvider.closeDB();

                good.setToBuy(!goodItem.isToBuy());
                goodItem.setToBuy(!goodItem.isToBuy());
                good.setSync(DataBaseHelper.SYNC_STATUS_FAILED);

                DataManager dataManager = new DataManager(context);
                dataManager.updateGood(good);
                dataManager.closeDB();

                if (good.isToBuy()){
                    holder.viewForeground.setBackground(ContextCompat.getDrawable(context, R.drawable.red_button));
                } else {
                    holder.viewForeground.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_button));
                }
                notifyDataSetChanged();
            }
        });

        holder.viewForeground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View mView = LayoutInflater.from(context)
                        .inflate(R.layout.fragment_update_good, holder.viewForeground, false);

                Button btnUpdateGood = mView.findViewById(R.id.btn_update_good);
                Button btnCancel = mView.findViewById(R.id.btn_cancel);
                final EditText editGoodName = mView.findViewById(R.id.edit_good);
                final EditText editGoodDesc = mView.findViewById(R.id.edit_good_desc);
                final Spinner spinner = mView.findViewById(R.id.spinnerCategories);
                //final ToggleButton btnCaddy = mView.findViewById(R.id.btn_caddy);

                //prepare widgets

                //EditText
                editGoodName.setText(goodItem.getGoodName());
                editGoodDesc.setText(goodItem.getGoodDesc());

                //Spinner (categories)
                SimpleCursorAdapter adapter = UtilsFunctions.getSpinnerAdapter(context);
                spinner.setAdapter(adapter);
                Cursor categories = adapter.getCursor();
                int categoryPositionInSpinner = 0;
                while (categories.moveToNext()) {
                    if (categories.getInt(categories.getColumnIndex(DataBaseHelper._ID)) == goodItem.getCategoryId()) {
                        categoryPositionInSpinner = categories.getPosition();
                    }
                }

                //Show the dialog
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                spinner.setSelection(categoryPositionInSpinner);

                //Action Button "Cancel"
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //Action Button "Update"
                btnUpdateGood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String updatedName = editGoodName.getText().toString();
                        if (Controlers.inputOk(updatedName)) {
                            int categoryId = (int) spinner.getSelectedItemId();

                            GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
                            Good updatedGood = goodsDataProvider.getGoodById(goodItem.getGoodId());
                            goodsDataProvider.closeDB();

                            updatedGood.setGoodName(updatedName.trim());
                            updatedGood.setCategoryId(categoryId);
                            updatedGood.setQuantityLevelId(goodItem.getQuantityLevelId());
                            //updatedGood.setToBuy(true);
                            updatedGood.setGoodDesc(editGoodDesc.getText().toString().trim());
                            updatedGood.setSync(DataBaseHelper.SYNC_STATUS_FAILED);
                            if (!editGoodDesc.getText().toString().trim().isEmpty()){
                                holder.goodDescription.setVisibility(View.VISIBLE);
                            }

                            DataManager dataManager = new DataManager(context);
                            int result = dataManager.updateGood(updatedGood);
                            dataManager.closeDB();

                            switch (result) {
                                case Constants.SUCCESS:
                                    Toast.makeText(context, context.getResources().getString(R.string.good_updated), Toast.LENGTH_SHORT).show();
                                    refreshList();
                                    dialog.dismiss();
                                    if(mOnGoodUpdatedListener != null){
                                        mOnGoodUpdatedListener.onGoodUpdated();
                                    }
                                    break;
                                case Constants.DATAEXISTS:
                                    Toast.makeText(context, context.getResources().getString(R.string.good_exists), Toast.LENGTH_SHORT).show();
                                    break;
                                case Constants.ERROR:
                                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            UtilsFunctions.hideKeyboard(context, view);
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.input_txt_not_valid), Toast.LENGTH_SHORT).show();
                        }



                    }
                });
                return false;
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(final CategoryGroupViewHolder holder, int flatPosition, ExpandableGroup group) {

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        final Category category = categoriesDataProvider.getCategoryById(((CategoryGroup) group).getCategoryId());
        categoriesDataProvider.closeDB();

        holder.setCategoryName(group.getTitle());

        holder.categoryIcon.setImageResource(category.getIcon());

        int goodsToBuyNumber = category.getGoodsToBuyNumber(context);
        if (goodsToBuyNumber!=0) {
            holder.cartContainer.setVisibility(View.VISIBLE);
            holder.goodsToBuyNumber.setText(String.valueOf(goodsToBuyNumber));
        } else {
            holder.cartContainer.setVisibility(View.GONE);
        }

        holder.cartContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(NetworkMonitor.checkNetworkConnection(context)){
                Intent intent = new Intent(context, ShopsActivity.class);
                intent.putExtra(Constants.CATEGORY_TO_ORDER, category.getServerCategoryId());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
            }

            }
        });

    }


    public void refreshList() {
        this.groups.clear();
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        List<CategoryGroup> categories = categoriesDataProvider.getMainGoodsList("");
        categoriesDataProvider.closeDB();
        this.groups.addAll(categories);
        notifyDataSetChanged();
    }

    public void setNewList(List<CategoryGroup> newMainList){
        this.groups.clear();
        this.groups.addAll(newMainList);
        notifyDataSetChanged();
    }

    public void initMainList(List<CategoryGroup> mainList){
        this.groups.clear();
        this.groups.addAll(mainList);
    }


    public void removeChildItem(int position, int goodId) {
        DataManager dataManager = new DataManager(context);
        Boolean res = dataManager.deleteGoodById(goodId);
        dataManager.closeDB();
        if (res) {
            ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
            CategoryGroup cg = (CategoryGroup) expandableList.getExpandableGroup(listPos);
            cg.remove(listPos.childPos);
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, context.getResources().getText(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    public void restoreItem(Good deletedItem) {
        DataManager dataManager = new DataManager(context);
        int res = dataManager.restoreGood(deletedItem);
        dataManager.closeDB();
        switch (res) {
            case Constants.SUCCESS:
                refreshList();
                break;
            default:
                Toast.makeText(context, context.getResources().getText(R.string.error), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public interface OnGoodUpdatedListener{
        public void onGoodUpdated();
    }

    OnGoodUpdatedListener mOnGoodUpdatedListener;
    public void setOnGoodUpdatedListener(OnGoodUpdatedListener onGoodUpdatedListener) {
        this.mOnGoodUpdatedListener = onGoodUpdatedListener;
    }


}
