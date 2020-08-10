package com.example.androidAssignment.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidAssignment.Model.ListItem;
import com.example.androidAssignment.R;
import com.example.androidAssignment.UI.ViewHolder.BaseViewHolder;
import com.example.androidAssignment.UI.ViewHolder.CommentViewHolder;
import com.example.androidAssignment.UI.ViewHolder.PhotoAction;
import com.example.androidAssignment.UI.ViewHolder.PhotoClickListener;
import com.example.androidAssignment.UI.ViewHolder.PhotoViewHolder;
import com.example.androidAssignment.UI.ViewHolder.SingleChoiceViewHolder;
import com.example.androidAssignment.ViewModels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> implements PhotoClickListener {

    private static final int PHOTO_ITEM_VIEW_TYPE = 0;
    private static final int SINGLE_CHOICE_ITEM_VIEW_TYPE = 1;
    private static final int COMMENT_ITEM_VIEW_TYPE = 2;

    private MainActivityViewModel mainActivityViewModel;
    private List<ListItem> list = new ArrayList<>();

    ListAdapter(MainActivityViewModel mainActivityViewModel) {
        this.mainActivityViewModel = mainActivityViewModel;
    }

    void setList(List<ListItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    void reloadPosition(int position) {
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PHOTO_ITEM_VIEW_TYPE:
                View photoItemView = LayoutInflater
                        .from(parent.getContext()).inflate(
                                R.layout.photo_view_holder,
                                parent, false);
                return new PhotoViewHolder(photoItemView,
                        this);
            case SINGLE_CHOICE_ITEM_VIEW_TYPE:
                View singleChoiceItemView = LayoutInflater
                        .from(parent.getContext()).inflate(
                                R.layout.single_choice_view_holder,
                                parent, false);
                return new SingleChoiceViewHolder(singleChoiceItemView);
            case COMMENT_ITEM_VIEW_TYPE:
                View commentItemView = LayoutInflater
                        .from(parent.getContext()).inflate(
                                R.layout.comment_view_holder,
                                parent, false);
                return new CommentViewHolder(commentItemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BaseViewHolder viewHolder = (BaseViewHolder) holder;
        viewHolder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list == null) {
            return -1;
        }
        ListItem listItem = list.get(position);
        switch (listItem.type) {
            case PHOTO:
                return PHOTO_ITEM_VIEW_TYPE;
            case SINGLE_CHOICE:
                return SINGLE_CHOICE_ITEM_VIEW_TYPE;
            case COMMENT:
                return COMMENT_ITEM_VIEW_TYPE;
            default:
                return -1;
        }
    }

    @Override
    public void onClicked(PhotoAction photoAction, int position) {
        mainActivityViewModel.handlePhotoListItemAction(
                photoAction, position);
    }
}
