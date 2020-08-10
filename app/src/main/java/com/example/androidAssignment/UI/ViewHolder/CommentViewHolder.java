package com.example.androidAssignment.UI.ViewHolder;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.androidAssignment.Model.ListItem;
import com.example.androidAssignment.R;

public class CommentViewHolder extends BaseViewHolder {

    private ListItem listItem;

    private TextView commentTitleView;
    private EditText commentEntry;
    private TextWatcher textWatcher;
    private Switch commentSwitch;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        commentTitleView = itemView.findViewById(R.id.comment_title);
        commentSwitch = itemView.findViewById(R.id.comment_switch);
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(
                    CharSequence charSequence, int i, int i1, int i2) {
                listItem.dataMap.currentInput = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        commentSwitch.setOnCheckedChangeListener(
                (compoundButton, b) -> {
                    listItem.dataMap.expanded = b;
                    if (b) {
                        commentEntry.setVisibility(View.VISIBLE);
                    } else {
                        commentEntry.setVisibility(View.GONE);
                    }
                });
        commentEntry = itemView.findViewById(R.id.comment_entry);
    }

    public void setData(final ListItem listItem) {
        this.listItem = listItem;
        commentTitleView.setText(listItem.title);
        commentEntry.removeTextChangedListener(textWatcher);
        commentEntry.setText("");
        if (listItem.dataMap.currentInput != null) {
            commentEntry.setText(listItem.dataMap.currentInput);
        }
        commentSwitch.setChecked(listItem.dataMap.expanded);
        if (listItem.dataMap.expanded) {
            commentEntry.setVisibility(View.VISIBLE);
        } else {
            commentEntry.setVisibility(View.GONE);
        }
        commentEntry.addTextChangedListener(textWatcher);
    }

}

