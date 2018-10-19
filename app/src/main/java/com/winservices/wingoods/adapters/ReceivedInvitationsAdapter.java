package com.winservices.wingoods.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.InvitationsDataManager;
import com.winservices.wingoods.dbhelpers.Synchronizer;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.ReceivedInvitation;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.viewholders.InvitationViewHolder;

import java.util.List;


public class ReceivedInvitationsAdapter extends RecyclerView.Adapter<InvitationViewHolder> {

    private Context context;
    private List<ReceivedInvitation> receivedInvitations;

    public ReceivedInvitationsAdapter(Context context, List<ReceivedInvitation> receivedInvitations) {
        this.context = context;
        this.receivedInvitations = receivedInvitations;
    }

    @Override
    public InvitationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invitation, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InvitationViewHolder holder, int position) {

        final ReceivedInvitation receivedInvitation = receivedInvitations.get(position);

        holder.senderEmail.setText(receivedInvitation.getInvitationEmail());
        holder.categories.setText(receivedInvitation.getInvitationCategories());


        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                respondToInvitation(CoUser.ACCEPTED, receivedInvitation.getInvitationEmail());
                holder.accept.setClickable(false);
                holder.accept.setTextColor(context.getResources().getColor(R.color.colorGray));
                holder.decline.setClickable(false);
                holder.decline.setTextColor(context.getResources().getColor(R.color.colorGray));

            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                respondToInvitation(CoUser.REJECTED, receivedInvitation.getInvitationEmail());
                holder.accept.setClickable(false);
                holder.accept.setTextColor(context.getResources().getColor(R.color.colorGray));
                holder.decline.setClickable(false);
                holder.decline.setTextColor(context.getResources().getColor(R.color.colorGray));
            }
        });

    }

    @Override
    public int getItemCount() {
        return receivedInvitations.size();
    }


    private void respondToInvitation(int response, String senderEmail){

        if (NetworkMonitor.checkNetworkConnection(context)) {

            InvitationsDataManager invitationsDataManager = new InvitationsDataManager(context);
            ReceivedInvitation invitation = invitationsDataManager.getReceivedInvitation(senderEmail);
            invitation.setResponse(response);
            invitationsDataManager.updateReceivedInvitation( invitation);

            if (invitation.getResponse() == CoUser.ACCEPTED) {
                UsersDataManager usersDataManager = new UsersDataManager(context);
                User user = usersDataManager.getCurrentUser();
                user.setServerGroupId(invitation.getServerGroupId());
                usersDataManager.updateUser(user);
                Toast.makeText(context, R.string.invitation_accepted, Toast.LENGTH_SHORT).show();

                //Delete all local Categories and goods
                DataManager dataManager = new DataManager(context);
                dataManager.deleteAllUserCategoriesAndGoods();

                Synchronizer sync = new Synchronizer(context);
                sync.deleteAllUserDataOnServerAndSyncGroup(context, user, invitation);
            } else {
                Toast.makeText(context, R.string.invitation_declined, Toast.LENGTH_SHORT).show();
            }

            Synchronizer sync = new Synchronizer(context);
            sync.synchronizeAll();

        } else {
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }


}
