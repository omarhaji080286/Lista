package com.winservices.wingoods.utils;

import android.graphics.Canvas;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.winservices.wingoods.adapters.CategoriesToOrderAdapter;
import com.winservices.wingoods.viewholders.CategoryGroupViewHolder;
import com.winservices.wingoods.viewholders.GoodItemViewHolder;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (viewHolder != null) {
            View foregroundView = getCastedView(viewHolder);
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    private View getCastedView(RecyclerView.ViewHolder viewHolder){
        View foregroundView = null;
        if (viewHolder instanceof CategoriesToOrderAdapter.GoodInOrderVH){
            foregroundView = ((CategoriesToOrderAdapter.GoodInOrderVH) viewHolder).rlViewForeground;
        } else if (viewHolder instanceof GoodItemViewHolder){
            foregroundView = ((GoodItemViewHolder) viewHolder).viewForeground;
        }
        return foregroundView;
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = getCastedView(viewHolder);
        if (foregroundView!=null) {
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = getCastedView(viewHolder);
        if (foregroundView!=null) {
            getDefaultUIUtil().clearView(foregroundView);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = getCastedView(viewHolder);
        if (foregroundView!=null){
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof CategoryGroupViewHolder) {
            return 0;
        }
        //int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT; //| ItemTouchHelper.RIGHT;
        return makeMovementFlags(0, swipeFlags);
    }

}
