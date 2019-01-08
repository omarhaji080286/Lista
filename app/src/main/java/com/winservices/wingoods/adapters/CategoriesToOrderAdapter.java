package com.winservices.wingoods.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.AmountsDataManager;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.DescriptionsDataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.models.Amount;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Description;
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
    private String amountValue = "";
    private String brandValue = "";

    public CategoriesToOrderAdapter(List<CategoryGroup> groups, Context context) {
        super(groups);
        this.context = context;
        this.groups = groups;
    }

    public List<CategoryGroup> getAdapterGroups() {
        return this.groups;
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
                amountValue = "";
                brandValue = "";
                showCompleteGoodDescDialog(holder.rlViewForeground, good, flatPosition, childIndex, group);
            }
        });
    }

    private void showCompleteGoodDescDialog(ViewGroup viewGroup, final Good good, final int flatPosition, final int childIndex, final ExpandableGroup group) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context)
                .inflate(R.layout.fragment_good_desc, viewGroup, false);

        TextView txtGoodName = mView.findViewById(R.id.txtGoodName);
        final TextView txtGoodDesc = mView.findViewById(R.id.txtGoodDesc);

        final MultiAutoCompleteTextView editBrand = mView.findViewById(R.id.macBrand);

        final LinearLayout llQuantity = mView.findViewById(R.id.llQuantity);
        final GridLayout gridAmounts = mView.findViewById(R.id.gridAmounts);

        final Button btnQuantity = mView.findViewById(R.id.btnQuantity);
        final Button btnGrammage = mView.findViewById(R.id.btnGrammage);
        final Button btnLitrage = mView.findViewById(R.id.btnLitrage);
        final Button btnDh = mView.findViewById(R.id.btnDh);

        ImageView imgPlus = mView.findViewById(R.id.imgPlus);
        ImageView imgMinus = mView.findViewById(R.id.imgMinus);
        final EditText editAmount = mView.findViewById(R.id.editAmount);

        Button btnUpdateGood = mView.findViewById(R.id.btn_update_good);
        Button btnCancel = mView.findViewById(R.id.btn_cancel);

        //Show the dialog
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //EditText
        txtGoodName.setText(good.getGoodName());
        txtGoodDesc.setText(good.getGoodDesc());

        //prepare Brand EditText
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        final Category category = categoriesDataProvider.getCategoryByGoodId(good.getGoodId());

        DescriptionsDataManager descriptionsDataManager = new DescriptionsDataManager(context);
        String[] descriptions = descriptionsDataManager.getDescriptions(category.getDCategoryId());

        ArrayAdapter<String> macBrandAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_expandable_list_item_1, descriptions);
        editBrand.setAdapter(macBrandAdapter);
        editBrand.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

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
                amountValue = charSequence.toString() + " " + context.getString(R.string.item_s);
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
                if (editAmount.getText().toString().isEmpty()) {
                    editAmount.setText(String.valueOf(0));
                    return;
                }
                Integer amount = Integer.valueOf(editAmount.getText().toString());
                if (amount == 0) return;
                if (amount == 1) {
                    editAmount.setText(String.valueOf(0));
                    amountValue = "";
                    String goodDesc = "( " + brandValue + " " + amountValue + ")";
                    txtGoodDesc.setText(goodDesc);
                    return;
                }
                editAmount.setText(String.valueOf(Math.max(amount - 1, 1)));
            }
        });
        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editAmount.getText().toString().isEmpty())
                    editAmount.setText(String.valueOf(0));
                Integer amount = Integer.valueOf(editAmount.getText().toString());
                editAmount.setText(String.valueOf(amount + 1));
            }
        });

        //Hide or show buttons depending on the category
        switch (category.getDCategoryId()) {
            case DefaultCategory.GROCERIES:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.VISIBLE);
                btnLitrage.setVisibility(View.VISIBLE);
                btnDh.setVisibility(View.VISIBLE);
                break;
            case DefaultCategory.FRUITS_AND_VEGETABLES:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.VISIBLE);
                btnLitrage.setVisibility(View.GONE);
                btnDh.setVisibility(View.VISIBLE);
                break;
            case DefaultCategory.MEATS:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.VISIBLE);
                btnLitrage.setVisibility(View.GONE);
                btnDh.setVisibility(View.GONE);
                break;
            case DefaultCategory.FISH:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.VISIBLE);
                btnLitrage.setVisibility(View.GONE);
                btnDh.setVisibility(View.GONE);
                break;
            case DefaultCategory.SPICES:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.VISIBLE);
                btnLitrage.setVisibility(View.GONE);
                btnDh.setVisibility(View.VISIBLE);
                break;
            case DefaultCategory.HYGIENE_PRODUCTS:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.VISIBLE);
                btnLitrage.setVisibility(View.VISIBLE);
                btnDh.setVisibility(View.VISIBLE);
                break;
            case DefaultCategory.DRUGS:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.GONE);
                btnLitrage.setVisibility(View.GONE);
                btnDh.setVisibility(View.GONE);
                break;
            case DefaultCategory.COSMETICS:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.GONE);
                btnLitrage.setVisibility(View.GONE);
                btnDh.setVisibility(View.GONE);
                break;
            case DefaultCategory.BREADS:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.VISIBLE);
                btnLitrage.setVisibility(View.GONE);
                btnDh.setVisibility(View.GONE);
                break;
            case DefaultCategory.HARDWARES:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.GONE);
                btnLitrage.setVisibility(View.GONE);
                btnDh.setVisibility(View.GONE);
                break;
            default:
                btnQuantity.setVisibility(View.VISIBLE);
                btnGrammage.setVisibility(View.VISIBLE);
                btnLitrage.setVisibility(View.VISIBLE);
                btnDh.setVisibility(View.VISIBLE);
        }


        //prepare Buttons
        btnQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, txtGoodDesc, editBrand, gridAmounts);
                changeButtonsBackGround(view, btnGrammage, btnLitrage, btnDh);
            }
        });

        btnGrammage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, txtGoodDesc, editBrand, gridAmounts);
                changeButtonsBackGround(view, btnQuantity, btnLitrage, btnDh);
            }
        });
        btnLitrage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, txtGoodDesc, editBrand, gridAmounts);
                changeButtonsBackGround(view, btnQuantity, btnGrammage, btnDh);
            }
        });
        btnDh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, txtGoodDesc, editBrand, gridAmounts);
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
                String finalGoodDesc = brandValue + " " + amountValue;
                if (inputsOk(editBrand)) {
                    updateGood(good.getGoodId(), finalGoodDesc);
                    ((Good) group.getItems().get(childIndex)).setGoodDesc(finalGoodDesc);
                    notifyItemChanged(flatPosition);
                    dialog.dismiss();
                    storeDescription(editBrand.getText().toString(), category.getDCategoryId());
                }
            }
        });
    }

    private void storeDescription(String descValue, int dCategoryId) {

        if (!descValue.isEmpty()) {
            Description description = new Description(Description.USER_DESCRIPTION, descValue.trim(), dCategoryId);
            DescriptionsDataManager.InsertDescription insertDescription = new DescriptionsDataManager.InsertDescription(context, description);
            Thread t = new Thread(insertDescription);
            t.start();
        }
    }

    private boolean inputsOk(MultiAutoCompleteTextView editBrand) {

        if (amountValue.isEmpty()) {
            Toast.makeText(context, R.string.select_amount, Toast.LENGTH_SHORT).show();
            return false;
        }

        /*if (editBrand.getVisibility() == View.VISIBLE) {
            if (editBrand.getText().toString().isEmpty()) {
                editBrand.setError("Description required");
                return false;
            }
        }*/
        return true;
    }

    private void updateGood(int goodId, String goodDesc) {
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
        DataManager dataManager = new DataManager(context);

        Good good = goodsDataProvider.getGoodById(goodId);
        good.setGoodDesc(goodDesc);
        dataManager.updateGood(good);

    }

    private void changeContent(final View view, LinearLayout llQuantity,
                               final TextView txtGoodDesc, final MultiAutoCompleteTextView editBrand, final GridLayout gridAmounts) {
        switch (view.getId()) {
            case R.id.btnQuantity:
                llQuantity.setVisibility(View.VISIBLE);
                gridAmounts.setVisibility(View.GONE);
                break;
            default:
                llQuantity.setVisibility(View.GONE);
                gridAmounts.removeAllViews();
                gridAmounts.setVisibility(View.VISIBLE);
                gridAmounts.post(new Runnable() {
                    @Override
                    public void run() {
                        setGridValues(view.getId(), gridAmounts, editBrand, txtGoodDesc);
                    }
                });
        }
    }

    private void setGridValues(final int viewId, final GridLayout gridAmounts, final MultiAutoCompleteTextView editBrand, final TextView txtGoodDesc) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int gWidth = gridAmounts.getWidth();
        int columnCount = gridAmounts.getColumnCount();
        int marginGridCell = (int) context.getResources().getDimension(R.dimen.margin_grid_cell);
        int w = gWidth / columnCount;
        int h = (int) (w * 0.60);
        final List<Amount> amounts = getUnitsValues(viewId);

        for (int i = 0; i < amounts.size(); i++) {
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setGravity(Gravity.CENTER);
            params.setMargins(0, 0, marginGridCell, marginGridCell);
            params.width = w;
            params.height = h;
            final View childAmount = inflater.inflate(R.layout.item_amount_in_grid, null);

            final Amount amount = amounts.get(i);
            final TextView tvTitle = childAmount.findViewById(R.id.txtAmount);
            tvTitle.setText(amount.getAmountValue());
            childAmount.setTag(amount.getAmountId());
            childAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvTitle.setBackgroundColor(context.getResources().getColor(R.color.colorGreen1));
                    refreshGoodDesc(gridAmounts, amount, amounts, editBrand, txtGoodDesc);
                }
            });
            gridAmounts.addView(childAmount, params);
        }
    }

    private void refreshGoodDesc(GridLayout gridAmounts, Amount amount, List<Amount> amounts,
                                 MultiAutoCompleteTextView editBrand, TextView txtGoodDesc) {

        for (int j = 0; j < amounts.size(); j++) {
            if (amounts.get(j).getAmountId() != amount.getAmountId())
                gridAmounts.getChildAt(j).findViewById(R.id.txtAmount).setBackgroundColor(context.getResources().getColor(R.color.colorBlue1));
        }

        amountValue = amount.getAmountValue();
        String goodDesc = "( " + editBrand.getText().toString() + " " + amountValue + " )";
        txtGoodDesc.setText(goodDesc);

    }

    private void changeButtonsBackGround(View view, Button btn1, Button btn2, Button btn3) {
        view.setBackground(context.getDrawable(R.drawable.btn_amount_selected));
        ((Button) view).setTextColor(context.getResources().getColor(R.color.white));
        btn1.setBackground(context.getDrawable(R.drawable.btn_amount));
        btn1.setTextColor(context.getResources().getColor(R.color.black));
        btn2.setBackground(context.getDrawable(R.drawable.btn_amount));
        btn2.setTextColor(context.getResources().getColor(R.color.black));
        btn3.setBackground(context.getDrawable(R.drawable.btn_amount));
        btn3.setTextColor(context.getResources().getColor(R.color.black));
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

    public int getGoodPosition(int goodId) {
        int position = -1;
        for (int i = 0; i < groups.size(); i++) {
            position = position + 1;
            for (int j = 0; j < groups.get(i).getItems().size(); j++) {
                position = position + 1;
                Good good = (Good) groups.get(i).getItems().get(j);
                if (good.getGoodId() == goodId) {
                    return position;
                }
            }
        }
        return position;
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
