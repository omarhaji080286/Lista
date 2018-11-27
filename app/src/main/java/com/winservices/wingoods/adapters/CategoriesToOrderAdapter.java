package com.winservices.wingoods.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.AmountsDataManager;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.models.Amount;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.ArrayList;
import java.util.List;

public class CategoriesToOrderAdapter
        extends ExpandableRecyclerViewAdapter<CategoriesToOrderAdapter.CategoryInOrderVH, CategoriesToOrderAdapter.GoodInOrderVH> {

    private Context context;
    private List<CategoryGroup> groups;
    private int lastPosition = -1;
    private String amountValue= "";
    private String brandValue = "";

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
    public void onBindChildViewHolder(final GoodInOrderVH holder, final int flatPosition, final ExpandableGroup group, final int childIndex) {
        final Good good = (Good) group.getItems().get(childIndex);

        holder.txtGoodDesc.setText(good.getGoodDesc());
        holder.txtGoodName.setText(good.getGoodName());

        holder.rlViewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCompleteGoodDescDialog(holder.rlViewForeground, good, flatPosition, childIndex, group);
            }
        });

    }

    private void showCompleteGoodDescDialog(ViewGroup viewGroup, final Good good, final int flatPosition, final int childIndex, final ExpandableGroup group ) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context)
                .inflate(R.layout.fragment_good_desc, viewGroup, false);

        TextView txtGoodName = mView.findViewById(R.id.txtGoodName);
        final TextView txtGoodDesc = mView.findViewById(R.id.txtGoodDesc);
        final EditText editBrand = mView.findViewById(R.id.editBrand);


        final LinearLayout llQuantity = mView.findViewById(R.id.llQuantity);
        final LinearLayout llUnitsValues = mView.findViewById(R.id.llUnitsValues);

        final Button btnQuantity = mView.findViewById(R.id.btnQuantity);
        final Button btnGrammage = mView.findViewById(R.id.btnGrammage);
        final Button btnLitrage = mView.findViewById(R.id.btnLitrage);
        final Button btnDh = mView.findViewById(R.id.btnDh);

        ImageView imgPlus = mView.findViewById(R.id.imgPlus);
        ImageView imgMinus = mView.findViewById(R.id.imgMinus);
        final EditText editAmount = mView.findViewById(R.id.editAmount);

        Button btnUpdateGood = mView.findViewById(R.id.btn_update_good);
        Button btnCancel = mView.findViewById(R.id.btn_cancel);

        //EditText
        txtGoodName.setText(good.getGoodName());

        //Show the dialog
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();


        //prepare Brand EditText
        editBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                brandValue = charSequence.toString();
                String goodDesc = "( " + brandValue + " " + amountValue + " )";
                txtGoodDesc.setText(goodDesc);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //prepare Edit Amount
        editAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                amountValue = charSequence.toString() + " pièce(s)";
                String goodDesc = "( " + brandValue + " " + amountValue + ")";
                txtGoodDesc.setText(goodDesc);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //prepare Images + and -
        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer amount = Integer.valueOf(editAmount.getText().toString());
                editAmount.setText(String.valueOf(Math.max(amount-1,0)));
            }
        });
        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer amount = Integer.valueOf(editAmount.getText().toString());
                editAmount.setText(String.valueOf(amount+1));
            }
        });

        //prepare Buttons
        btnQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, llUnitsValues, txtGoodDesc, editBrand);
                changeButtonsBackGround(view, btnGrammage, btnLitrage, btnDh);
            }
        });

        btnGrammage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, llUnitsValues, txtGoodDesc, editBrand);
                changeButtonsBackGround(view, btnQuantity, btnLitrage, btnDh);
            }
        });
        btnLitrage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, llUnitsValues, txtGoodDesc, editBrand);
                changeButtonsBackGround(view, btnQuantity, btnGrammage, btnDh);
            }
        });
        btnDh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, llUnitsValues, txtGoodDesc, editBrand);
                changeButtonsBackGround(view, btnQuantity, btnGrammage, btnLitrage);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnUpdateGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String finalGoodDesc = txtGoodDesc.getText().toString();
                updateGood(good.getGoodId(),finalGoodDesc);
                ((Good) group.getItems().get(childIndex)).setGoodDesc(finalGoodDesc);
                notifyItemChanged(flatPosition);
                dialog.dismiss();
            }
        });
    }

    private void updateGood(int goodId, String goodDesc){
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
        DataManager dataManager = new DataManager(context);

        Good good = goodsDataProvider.getGoodById(goodId);
        good.setGoodDesc(goodDesc);
        dataManager.updateGood(good);

    }

    private void changeContent(View view, LinearLayout llQuantity, LinearLayout llUnitsValues, final TextView txtGoodDesc, final EditText editBrand) {
        switch (view.getId()) {
            case R.id.btnQuantity:
                llQuantity.setVisibility(View.VISIBLE);
                llUnitsValues.setVisibility(View.GONE);
                break;
            default:
                llQuantity.setVisibility(View.GONE);
                llUnitsValues.setVisibility(View.VISIBLE);
                llUnitsValues.removeAllViews();
                RadioGroup rg = getRadioGroup(view.getId());
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                        RadioButton rb = radioGroup.findViewById(checkedId);
                        amountValue = rb.getText().toString();
                        String goodDesc = "( " + editBrand.getText().toString() + " " + amountValue + " )";
                        txtGoodDesc.setText(goodDesc);
                    }
                });
                llUnitsValues.addView(rg);
        }
    }

    private void changeButtonsBackGround(View view, Button btn1, Button btn2, Button btn3) {
        view.setBackground(context.getDrawable(R.drawable.red_button));
        btn1.setBackground(context.getDrawable(R.drawable.gray_button));
        btn2.setBackground(context.getDrawable(R.drawable.gray_button));
        btn3.setBackground(context.getDrawable(R.drawable.gray_button));
    }

    private RadioGroup getRadioGroup(int viewId) {
        RadioGroup rg = new RadioGroup(context);
        RadioGroup.LayoutParams rglp;

        rg.setOrientation(RadioGroup.VERTICAL);

        for (int i = 0; i < getUnitsValues(viewId).size(); i++) {
            RadioButton rb = new RadioButton(context);
            rb.setText(getUnitsValues(viewId).get(i).getAmountValue());
            rglp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            rg.addView(rb, rglp);
        }
        return rg;
    }

    private List<Amount> getUnitsValues(int viewId) {
        AmountsDataManager amountsDataManager = new AmountsDataManager(context);
        switch (viewId) {
            case R.id.btnGrammage:
                return amountsDataManager.getAmounts(Amount.WEIGHT);
            case R.id.btnLitrage:
                return amountsDataManager.getAmounts(Amount.VOLUME);
            case R.id.btnDh:
                return amountsDataManager.getAmounts(Amount.CURRENCY);
            default:
                return new ArrayList<>();
        }
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
