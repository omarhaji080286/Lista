package com.winservices.wingoods.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.Controlers;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.ArrayList;
import java.util.List;

public class CategoriesToOrderAdapter extends ExpandableRecyclerViewAdapter<CategoriesToOrderAdapter.CategoryInOrderVH, CategoriesToOrderAdapter.GoodInOrderVH> {

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

        String imagePath = SharedPrefManager.getInstance(context).getImagePath(DefaultCategory.PREFIX_D_CATEGORY + category.getDCategoryId());
        Bitmap bitmap = UtilsFunctions.getOrientedBitmap(imagePath);

        holder.imgCategory.setImageBitmap(bitmap);
        holder.txtCategoryName.setText(category.getCategoryName());
        holder.llContainer.setClickable(false);

    }

    @Override
    public void onBindChildViewHolder(final GoodInOrderVH holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Good good = (Good) group.getItems().get(childIndex);

        holder.txtGoodDesc.setText(good.getGoodDesc());
        holder.txtGoodName.setText(good.getGoodName());

        holder.rlViewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCompleteGoodDescDialog(holder.rlViewForeground, good);
            }
        });

    }

    private void showCompleteGoodDescDialog(ViewGroup viewGroup, Good good) {


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context)
                .inflate(R.layout.fragment_good_desc, viewGroup, false);

        Button btnUpdateGood = mView.findViewById(R.id.btn_update_good);
        Button btnCancel = mView.findViewById(R.id.btn_cancel);
        Button btnQuantity = mView.findViewById(R.id.btnQuantity);
        Button btnGrammage = mView.findViewById(R.id.btnGrammage);
        Button btnLitrage = mView.findViewById(R.id.btnLitrage);
        final TextView txtGoodName = mView.findViewById(R.id.txtGoodName);
        final EditText editBrand = mView.findViewById(R.id.edit_good_desc);

        //prepare widgets

        //EditText
        txtGoodName.setText(good.getGoodName());

        //Show the dialog
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //Action Button "Cancel"
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //Action Button "Update"
        /*btnUpdateGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedName = editGoodName.getText().toString();
                if (Controlers.inputOk(updatedName)) {
                    int categoryId = (int) spinner.getSelectedItemId();

                    GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
                    Good updatedGood = goodsDataProvider.getGoodById(goodItem.getGoodId());

                    updatedGood.setGoodName(updatedName.trim());
                    updatedGood.setCategoryId(categoryId);
                    updatedGood.setQuantityLevelId(goodItem.getQuantityLevelId());
                    updatedGood.setGoodDesc(editGoodDesc.getText().toString().trim());
                    updatedGood.setSync(DataBaseHelper.SYNC_STATUS_FAILED);
                    if (!editGoodDesc.getText().toString().trim().isEmpty()) {
                        holder.goodDescription.setVisibility(View.VISIBLE);
                    }

                    DataManager dataManager = new DataManager(context);
                    int result = dataManager.updateGood(updatedGood);

                    switch (result) {
                        case Constants.SUCCESS:
                            Toast.makeText(context, context.getResources().getString(R.string.good_updated), Toast.LENGTH_SHORT).show();
                            refreshList();
                            dialog.dismiss();
                            if (mOnGoodUpdatedListener != null) {
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
        });*/

    }

    public void removeChildItem(int position) {
        ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
        CategoryGroup cg = (CategoryGroup) expandableList.getExpandableGroup(listPos);
        cg.remove(listPos.childPos);
        notifyItemRemoved(position);
    }

    public int getGoodsToOrderNumber() {
        int goodsToOrderNumber = 0;
        for (int i = 0; i < groups.size(); i++) {
            goodsToOrderNumber = goodsToOrderNumber + groups.get(i).getItems().size();
        }
        return goodsToOrderNumber;
    }

    public List<Good> getGoodsToOrder() {
        List<Good> goodsToOrder = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            for (int j = 0; j < groups.get(i).getItems().size(); j++) {
                goodsToOrder.add((Good) groups.get(i).getItems().get(j));
            }
        }
        return goodsToOrder;
    }

    public List<Good> getGoodsToComplete() {
        List<Good> goodsToOrder = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            for (int j = 0; j < groups.get(i).getItems().size(); j++) {
                Good good = (Good) groups.get(i).getItems().get(j);
                if (good.getGoodDesc().isEmpty()) {
                    goodsToOrder.add(good);
                }
            }
        }
        return goodsToOrder;
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

    public class GoodInOrderVH extends ChildViewHolder {

        public RelativeLayout rlViewForeground;
        private TextView txtGoodName, txtGoodDesc;

        GoodInOrderVH(View itemView) {
            super(itemView);

            txtGoodName = itemView.findViewById(R.id.txtGoodName);
            txtGoodDesc = itemView.findViewById(R.id.txtGoodDesc);
            rlViewForeground = itemView.findViewById(R.id.rlViewForeground);

        }
    }


}
