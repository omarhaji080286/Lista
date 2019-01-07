package com.winservices.wingoods.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.CategoriesInMyGoodsAdapter;
import com.winservices.wingoods.adapters.MyGoodsAdapter;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.RecyclerItemTouchHelper;
import com.winservices.wingoods.viewholders.GoodItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MyGoods extends android.support.v4.app.Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public static final String TAG = MyGoods.class.getSimpleName();

    private EditText searchView;
    private RecyclerView mRecyclerView;
    public MyGoodsAdapter mAdapter;
    private List<CategoryGroup> categories, initialMainList;
    private LinearLayout fragMyGoods;
    private ImageView imgHighlightOff;
    private RecyclerView categoriesToChooseRecyclerView;
    private TextView txtChooseCategory;
    public CategoriesInMyGoodsAdapter categoriesToChooseAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_goods, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.my_goods));

        searchView = view.findViewById(R.id.search_good);
        mRecyclerView = view.findViewById(R.id.my_goods_recyclerview);
        fragMyGoods = view.findViewById(R.id.frag_my_goods);
        imgHighlightOff = view.findViewById(R.id.img_highlight_off);
        categoriesToChooseRecyclerView = view.findViewById(R.id.list_categories);
        txtChooseCategory = view.findViewById(R.id.txt_choose_a_category);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        SyncHelper.sync(getContext());
        searchView.setSelected(false);

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(getContext());
        categories = categoriesDataProvider.getMainGoodsList("");
        initialMainList = new ArrayList<>();
        initialMainList.addAll(categories);

        //Adapter for Goods expandable recycler list view
        mAdapter = new MyGoodsAdapter(categories, getContext());

        final int GRID_COLUMN_NUMBER = 3;
        GridLayoutManager glm1 = new GridLayoutManager(getContext(), GRID_COLUMN_NUMBER);
        glm1.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case 2:
                        return GRID_COLUMN_NUMBER;
                    default:
                        return 1;
                }
            }
        });

        mRecyclerView.setLayoutManager(glm1);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnGoodUpdatedListener(new MyGoodsAdapter.OnGoodUpdatedListener() {
            @Override
            public void onGoodUpdated() {
                reloadMainList();
            }
        });

        //Adapter for categories to choose
        CategoriesDataProvider categoriesDataProvider2 = new CategoriesDataProvider(getContext());
        List<Category> categoriesToChoose = categoriesDataProvider2.getAllCategories();

        categoriesToChooseAdapter = new CategoriesInMyGoodsAdapter(getContext(), categoriesToChoose);

        GridLayoutManager glm2 = new GridLayoutManager(getContext(), 3);
        categoriesToChooseRecyclerView.setLayoutManager(glm2);
        categoriesToChooseRecyclerView.setAdapter(categoriesToChooseAdapter);
        categoriesToChooseRecyclerView.setHasFixedSize(true);

        categoriesToChooseAdapter.setOnGoodAddedListener(new CategoriesInMyGoodsAdapter.OnGoodAddedListener() {
            @Override
            public void onGoodAdded() {
                searchView.setText("");
                searchView.setFocusable(true);
                imgHighlightOff.setVisibility(View.INVISIBLE);
                reloadMainList();
                categoriesToChooseRecyclerView.setVisibility(View.GONE);
                txtChooseCategory.setVisibility(View.GONE);
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                imgHighlightOff.setVisibility(View.VISIBLE);

                //set the new list to the adapter
                List<CategoryGroup> newMainList;

                if (!s.toString().equals("")){
                    newMainList = getFilteredMainList(s.toString());
                    mAdapter.setNewList(newMainList);
                    expandAll();
                } else {
                    mAdapter.refreshList();
                    collapseAll();
                }

                if (mAdapter.getGroups().size()==0){
                    categoriesToChooseAdapter.setGoodName(s.toString());
                    txtChooseCategory.setVisibility(View.VISIBLE);
                    categoriesToChooseRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    txtChooseCategory.setVisibility(View.GONE);
                    categoriesToChooseRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        imgHighlightOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setText("");
                searchView.setFocusable(true);
                imgHighlightOff.setVisibility(View.INVISIBLE);
            }
        });

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
    }

    private void expandAll(){
        for (int i = mAdapter.getGroups().size()-1; i >=0 ; i--) {
            expandGroup(i);
        }
    }

    private void collapseAll(){
        for (int i = mAdapter.getGroups().size()-1; i >=0 ; i--) {
            collapseGroup(i);
        }
    }

    public void collapseGroup (int gPos){
        if(!mAdapter.isGroupExpanded(gPos)){
            return;
        }
        mAdapter.toggleGroup(gPos);
    }

    public void expandGroup (int gPos){
        if(mAdapter.isGroupExpanded(gPos)){
            return;
        }
        mAdapter.toggleGroup(gPos);
    }


    public void reloadMainList(){

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(getContext());
        initialMainList = categoriesDataProvider.getMainGoodsList("");

        List<CategoryGroup> increasedMainList = new ArrayList<>();
        increasedMainList.addAll(getFilteredMainList(searchView.getText().toString()));

        mAdapter.setNewList(increasedMainList);
    }

    private List<CategoryGroup> getFilteredMainList(String goodName){
        List<CategoryGroup> newMainList = new ArrayList<>();
        for (int i = 0; i < initialMainList.size() ; i++) {
            CategoryGroup categoryGroup = initialMainList.get(i);
            List<Good> newGoods = new ArrayList<>();
            for (int j = 0; j < categoryGroup.getItems().size(); j++) {
                Good good = (Good) categoryGroup.getItems().get(j);
                if (good.getGoodName().toLowerCase().contains(goodName.toLowerCase())){
                    newGoods.add(good);
                }
            }
            if (newGoods.size()>0){
                CategoryGroup newCategoryGroup = new CategoryGroup(categoryGroup.getTitle(), newGoods );
                newCategoryGroup.setCategoryId(categoryGroup.getCategoryId());
                CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(getContext());
                Category category = categoriesDataProvider.getCategoryById(categoryGroup.getCategoryId());
                newCategoryGroup.setCategory(category);
                newMainList.add(newCategoryGroup);
            }
        }
        return newMainList;
    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof GoodItemViewHolder) {

            GoodItemViewHolder item = (GoodItemViewHolder) viewHolder;

            // get the removed item name to display it in snack bar
            String name = item.goodName.getText().toString();

            // backup of removed item for undo purpose
            final Good deletedItem = new Good(item.getGoodId()
                    , item.goodName.getText().toString()
                    , item.getCategoryId()
                    , item.getLevel()
                    , item.isToBuy());

            deletedItem.setGoodDesc(item.goodDescription.getText().toString());

            final int deletedIndex = viewHolder.getAdapterPosition();

            //remove item
            mAdapter.removeChildItem(position, item.getGoodId());
            reloadMainList();

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(fragMyGoods, "'" + name + "' " + getResources().getString(R.string.deleted), Snackbar.LENGTH_LONG);
            snackbar.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem/*, deletedIndex - 1*/);
                }
            });
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorGrrenFluo));
            snackbar.show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SyncHelper.sync(getContext());
    }
}
