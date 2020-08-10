package com.example.androidAssignment.UI.ViewHolder;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.androidAssignment.Model.ListItem;
import com.example.androidAssignment.R;

public class SingleChoiceViewHolder extends BaseViewHolder {

    private TextView singleChoiceTitleView;
    private RadioGroup singleChoiceRadioGroup;

    public SingleChoiceViewHolder(@NonNull View itemView) {
        super(itemView);
        singleChoiceTitleView = itemView.findViewById(R.id.single_choice_title);
        singleChoiceRadioGroup = itemView.findViewById(R.id.single_choice_radio_group);
    }

    public void setData(final ListItem listItem) {
        singleChoiceTitleView.setText(listItem.title);
        singleChoiceRadioGroup.removeAllViews();
        if (listItem.dataMap.options == null) {
            return;
        }
        for (int i = 0; i < listItem.dataMap.options.size(); i++) {
            final RadioButton radioButton = new RadioButton(itemView.getContext());
            radioButton.setText(listItem.dataMap.options.get(i));
            singleChoiceRadioGroup.addView(radioButton);
            final int index = i;
            radioButton.setOnClickListener(v -> {
                if (radioButton.isChecked()) {
                    listItem.dataMap.currentInput = String.valueOf(index);
                }
            });
        }
        if (listItem.dataMap.currentInput != null) {
            int singleChoiceSelectedIndex = Integer.parseInt(
                    listItem.dataMap.currentInput);
            singleChoiceRadioGroup.check(
                    singleChoiceRadioGroup.getChildAt(
                            singleChoiceSelectedIndex).getId());
        }
    }

}

