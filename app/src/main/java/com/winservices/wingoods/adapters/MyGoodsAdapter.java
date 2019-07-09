package com.winservices.wingoods.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.AmountsDataManager;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.DescriptionsDataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.models.Amount;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Description;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;
import com.winservices.wingoods.viewholders.CategoryGroupViewHolder;
import com.winservices.wingoods.viewholders.GoodItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MyGoodsAdapter extends ExpandableRecyclerViewAdapter<CategoryGroupViewHolder, GoodItemViewHolder> {

    private final static String TAG = MyGoodsAdapter.class.getSimpleName();
    private Context context;
    private List<CategoryGroup> groups;
    private String amountValue = "";
    private String brandValue = "";
    private OnGoodUpdatedListener mOnGoodUpdatedListener;

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
    public void onBindChildViewHolder(final GoodItemViewHolder holder, final int flatPosition, final ExpandableGroup group, final int childIndex) {

        final Good goodItem = (Good) group.getItems().get(childIndex);
        holder.setGoodId(goodItem.getGoodId());
        holder.setGoodName(goodItem.getGoodName());
        holder.setCategoryId(goodItem.getCategoryId());
        holder.setLevel(goodItem.getQuantityLevelId());
        holder.goodDescription.setText(goodItem.getGoodDesc());

        if (goodItem.isToBuy()) {
            holder.viewForeground.setBackground(ContextCompat.getDrawable(context, R.drawable.good_to_buy_color));
            if (goodItem.getIsOrdered() == 1) {
                holder.cart.setVisibility(View.VISIBLE);
            } else {
                holder.cart.setVisibility(View.GONE);
            }
        } else {
            holder.viewForeground.setBackground(ContextCompat.getDrawable(context, R.drawable.good_default_color));
            holder.cart.setVisibility(View.GONE);
        }

        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
        final Good good = goodsDataProvider.getGoodById(goodItem.getGoodId());

        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!good.isToBuy()) good.setUsesNumber(good.getUsesNumber() + 1);

                good.setToBuy(!goodItem.isToBuy());
                goodItem.setToBuy(!goodItem.isToBuy());
                good.setSync(DataBaseHelper.SYNC_STATUS_FAILED);

                DataManager dataManager = new DataManager(context);
                dataManager.updateGood(good);

                int goodsToBuyNumber = ((CategoryGroup) group).getCategory().getGoodsToBuyNumber();

                if (good.isToBuy()) {
                    holder.viewForeground.setBackground(ContextCompat.getDrawable(context, R.drawable.good_to_buy_color));
                    ((CategoryGroup) group).getCategory().setGoodsToBuyNumber(goodsToBuyNumber + 1);
                } else {
                    holder.viewForeground.setBackground(ContextCompat.getDrawable(context, R.drawable.good_default_color));
                    ((CategoryGroup) group).getCategory().setGoodsToBuyNumber(goodsToBuyNumber - 1);
                }

                int groupflatPosition = flatPosition - childIndex - 1;
                notifyItemChanged(flatPosition);
                notifyItemChanged(groupflatPosition);


            }
        });

        holder.viewForeground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                amountValue = "";
                brandValue = "";
                showCompleteGoodDescDialog(holder.viewForeground, good, flatPosition, childIndex, group);

                return false;
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(final CategoryGroupViewHolder holder, int flatPosition, ExpandableGroup group) {

        Category category = ((CategoryGroup) group).getCategory();

        holder.setCategoryName(group.getTitle());

        String imgPath = SharedPrefManager.getInstance(context).getImagePath(DefaultCategory.PREFIX_D_CATEGORY + category.getDCategoryId());
        if (imgPath != null) {
            Bitmap bitmap = UtilsFunctions.getPNG(imgPath);
            holder.categoryIcon.setImageBitmap(bitmap);
        } else {
            holder.categoryIcon.setImageResource(R.drawable.others);
        }

        int goodsToBuyNumber = category.getGoodsToBuyNumber();
        int orderedGoodsNumber = category.getOrderedGoodsNumber();
        if (goodsToBuyNumber != 0) {
            holder.cartContainer.setVisibility(View.VISIBLE);
            //String orderedOfToBuy = String.valueOf(orderedGoodsNumber)+" / "+String.valueOf(goodsToBuyNumber);
            String orderedOfToBuy = String.valueOf(goodsToBuyNumber);
            holder.goodsToBuyNumber.setText(orderedOfToBuy);

            if (orderedGoodsNumber > 0) {
                holder.caddy.setImageResource(R.drawable.ic_cart_full_black);
            } else {
                holder.caddy.setImageResource(R.drawable.ic_cart_empty);
            }

        } else {
            holder.cartContainer.setVisibility(View.GONE);
        }


    }

    public void refreshList() {
        this.groups.clear();
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        List<CategoryGroup> categories = categoriesDataProvider.getMainGoodsList("");
        this.groups.addAll(categories);
        notifyDataSetChanged();
    }

    public void setNewList(List<CategoryGroup> newMainList) {
        this.groups.clear();
        this.groups.addAll(newMainList);
        notifyDataSetChanged();
    }

    public void removeChildItem(int position, int goodId) {
        DataManager dataManager = new DataManager(context);
        Boolean res = dataManager.deleteGoodById(goodId);
        if (res) {
            ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
            CategoryGroup cg = (CategoryGroup) expandableList.getExpandableGroup(listPos);
            cg.remove(listPos.childPos);
            notifyItemRemoved(position);
        } else {
            Toast.makeText(context, context.getResources().getText(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    public void restoreItem(Good deletedItem) {
        DataManager dataManager = new DataManager(context);
        int res = dataManager.restoreGood(deletedItem);
        switch (res) {
            case Constants.SUCCESS:
                refreshList();
                break;
            default:
                Toast.makeText(context, context.getResources().getText(R.string.error), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setOnGoodUpdatedListener(OnGoodUpdatedListener onGoodUpdatedListener) {
        this.mOnGoodUpdatedListener = onGoodUpdatedListener;
    }

    private void showCompleteGoodDescDialog(ViewGroup viewGroup, final Good good, final int flatPosition, final int childIndex, final ExpandableGroup group) {

        View mView = LayoutInflater.from(context)
                .inflate(R.layout.fragment_update_good, viewGroup, false);

        TextView txtGoodName = mView.findViewById(R.id.txtGoodName);
        final TextView txtGoodDesc = mView.findViewById(R.id.txtGoodDesc);

        final MultiAutoCompleteTextView editBrand = mView.findViewById(R.id.macBrand);

        final LinearLayout llQuantity = mView.findViewById(R.id.llQuantity);

        final Button btnQuantity = mView.findViewById(R.id.btnQuantity);
        final Button btnGrammage = mView.findViewById(R.id.btnGrammage);
        final Button btnLitrage = mView.findViewById(R.id.btnLitrage);
        final Button btnDh = mView.findViewById(R.id.btnDh);

        ImageView imgPlus = mView.findViewById(R.id.imgPlus);
        ImageView imgMinus = mView.findViewById(R.id.imgMinus);
        final EditText editAmount = mView.findViewById(R.id.editAmount);

        Button btnUpdateGood = mView.findViewById(R.id.btn_update_good);
        Button btnCancel = mView.findViewById(R.id.btn_cancel);

        final Spinner spinner = mView.findViewById(R.id.spinnerCategories);

        final NumberPicker pickerAmounts = mView.findViewById(R.id.pickerAmounts);

        //Show the dialog
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //EditText
        txtGoodName.setText(good.getGoodName());
        txtGoodDesc.setText(good.getGoodDesc());

        //Spinner (categories)
        SimpleCursorAdapter adapter = UtilsFunctions.getSpinnerAdapter(context);
        spinner.setAdapter(adapter);
        Cursor categories = adapter.getCursor();
        int categoryPositionInSpinner = 0;
        while (categories.moveToNext()) {
            if (categories.getInt(categories.getColumnIndex(DataBaseHelper._ID)) == good.getCategoryId()) {
                categoryPositionInSpinner = categories.getPosition();
            }
        }
        spinner.setSelection(categoryPositionInSpinner);

        //prepare Brand EditText
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        final Category category = categoriesDataProvider.getCategoryByGoodId(good.getGoodId());


        DescriptionsDataManager descriptionsDataManager = new DescriptionsDataManager(context);
        String[] descriptions = descriptionsDataManager.getDescriptions(category.getDCategoryId());

        ArrayAdapter<String> macBrandAdapter = new ArrayAdapter<>(context,
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
                changeContent(view, llQuantity, txtGoodDesc, editBrand, pickerAmounts);
                changeButtonsBackGround(view, btnGrammage, btnLitrage, btnDh);
            }
        });

        btnGrammage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, txtGoodDesc, editBrand, pickerAmounts);
                changeButtonsBackGround(view, btnQuantity, btnLitrage, btnDh);
            }
        });
        btnLitrage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, txtGoodDesc, editBrand, pickerAmounts);
                changeButtonsBackGround(view, btnQuantity, btnGrammage, btnDh);
            }
        });
        btnDh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContent(view, llQuantity, txtGoodDesc, editBrand, pickerAmounts);
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
                final String finalGoodDesc = brandValue + " " + amountValue;
                if (inputsOk(editBrand)) {
                    int categoryId = (int) spinner.getSelectedItemId();
                    updateGood(good.getGoodId(), finalGoodDesc, categoryId, flatPosition);
                    refreshList();
                    dialog.dismiss();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            storeDescription(editBrand.getText().toString(), category.getDCategoryId());
                        }
                    });
                }
            }
        });

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

    private void updateGood(int goodId, String goodDesc, int categoryId, int flatPosition) {
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
        DataManager dataManager = new DataManager(context);

        Good good = goodsDataProvider.getGoodById(goodId);
        good.setGoodDesc(goodDesc);
        good.setSync(DataBaseHelper.SYNC_STATUS_FAILED);
        good.setCategoryId(categoryId);
        good.setToBuy(true);

        dataManager.updateGood(good);

    }

    private void refreshGoodDesc(String amountStr, MultiAutoCompleteTextView editBrand, TextView txtGoodDesc) {

        amountValue = amountStr;
        String goodDesc = "( " + editBrand.getText().toString() + " " + amountValue + " )";
        txtGoodDesc.setText(goodDesc);

    }

    private void storeDescription(String descValue, int dCategoryId) {

        if (!descValue.isEmpty()) {
            Description description = new Description(Description.USER_DESCRIPTION, descValue.trim(), dCategoryId);
            DescriptionsDataManager.InsertDescription insertDescription = new DescriptionsDataManager.InsertDescription(context, description);
            Thread t = new Thread(insertDescription);
            t.start();
        }
    }

    private void changeContent(final View view, LinearLayout llQuantity,
                               final TextView txtGoodDesc, final MultiAutoCompleteTextView editBrand, final NumberPicker pickerAmounts) {
        switch (view.getId()) {
            case R.id.btnQuantity:
                llQuantity.setVisibility(View.VISIBLE);
                pickerAmounts.setVisibility(View.GONE);
                break;
            default:
                llQuantity.setVisibility(View.GONE);
                pickerAmounts.setVisibility(View.VISIBLE);
                pickerAmounts.post(new Runnable() {
                    @Override
                    public void run() {
                        setPickerValues(view.getId(), pickerAmounts, txtGoodDesc, editBrand);
                    }
                });
        }
    }

    private void setPickerValues(int viewId, final NumberPicker pickerAmounts, final TextView txtGoodDesc, final MultiAutoCompleteTextView editBrand) {
        List<Amount> amounts = getUnitsValues(viewId);
        final String[] amountsStr = amountsToString(amounts);
        pickerAmounts.setDisplayedValues(null);
        pickerAmounts.setMaxValue(amountsStr.length - 1);
        pickerAmounts.setDisplayedValues(amountsStr);

        pickerAmounts.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                refreshGoodDesc(amountsStr[newVal], editBrand, txtGoodDesc);
            }
        });
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

    private String[] amountsToString(List<Amount> amounts) {
        String[] amountsStr = new String[amounts.size()];
        for (int i = 0; i < amounts.size(); i++) {
            amountsStr[i] = amounts.get(i).getAmountValue();
        }
        return amountsStr;
    }

    public interface OnGoodUpdatedListener {
        void onGoodUpdated();
    }

}
