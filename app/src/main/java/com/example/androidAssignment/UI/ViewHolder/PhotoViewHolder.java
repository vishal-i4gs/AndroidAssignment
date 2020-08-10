package com.example.androidAssignment.UI.ViewHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.androidAssignment.Model.ListItem;
import com.example.androidAssignment.R;

public class PhotoViewHolder extends BaseViewHolder {

    private TextView photoTitleView;
    private ImageView photoImageView;

    public PhotoViewHolder(@NonNull View itemView,
                           @NonNull final PhotoClickListener photoClickListener) {
        super(itemView);
        photoTitleView = itemView.findViewById(R.id.photo_title);
        photoImageView = itemView.findViewById(R.id.photo_image_view);
        photoImageView.setOnClickListener(
                view -> photoClickListener.onClicked(PhotoAction.CLICK,
                getAdapterPosition()));
        ImageButton photoRemoveButton = itemView.findViewById(R.id.photo_remove_button);
        photoRemoveButton.setOnClickListener(
                view -> photoClickListener.onClicked(PhotoAction.REMOVE,
                getAdapterPosition()));
        photoImageView.setImageResource(0);
        photoTitleView.setText("");
    }

    public void setData(ListItem listItem) {
        photoImageView.setImageDrawable(null);
        photoTitleView.setText(listItem.title);
        if (listItem.dataMap.currentInput != null) {
            if (Uri.parse(listItem.dataMap.currentInput) != null) {
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(
                                listItem.dataMap.currentInput),
                        50, 50);
                photoImageView.setImageBitmap(thumbnail);
            }
        }
    }
}
