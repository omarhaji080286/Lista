package com.winservices.wingoods.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.winservices.wingoods.R;


public class InvitationViewHolder extends GroupViewHolder {

    public TextView senderPhoneAndName;
    public TextView categories;
    public Button decline, accept;

    public InvitationViewHolder(View itemView) {
        super(itemView);

        senderPhoneAndName = itemView.findViewById(R.id.txt_phone_and_name);
        categories = itemView.findViewById(R.id.txt_invit_categories);
        decline = itemView.findViewById(R.id.btn_decline);
        accept = itemView.findViewById(R.id.btn_accept);

    }
}
