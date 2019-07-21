package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.winservices.wingoods.models.ReceivedInvitation;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class InvitationsDataManager {

    private final static String TAG = "InvitationsDataManager";
    private DataBaseHelper db;

    public InvitationsDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
    }

    public boolean isInvitationPending(){
        return db.isInvitationPending();
    }

    public int addReceivedInvitation( ReceivedInvitation invitation) {
        int result;
        if (db.invitationExists(invitation.getInvitationPhone())) {
            result = Constants.DATAEXISTS;
        } else {
            if (db.addReceivedInvitation(invitation)) {
                result = Constants.SUCCESS;
            } else {
                result = Constants.ERROR;
            }
        }
        Log.d(TAG, Constants.TAG_LISTA+"addReceivedInvitation called");

        return result;
    }

    public boolean updateReceivedInvitation( ReceivedInvitation invitation){
        Log.d(TAG, Constants.TAG_LISTA+"updateReceivedInvitation called");

        return db.updateReceivedInvitation(invitation);
    }

    public ReceivedInvitation getReceivedInvitation( String senderPhone) {
        Log.d(TAG, Constants.TAG_LISTA+"getReceivedInvitation called");
        return db.getReceivedInvitation(senderPhone);
        }


    public ReceivedInvitation getReceivedInvitationById(int receivedInvitationId) {
        Cursor cursor = db.getReceivedInvitationById(receivedInvitationId);

        cursor.moveToNext();

        //int receivedInvitationId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_RECEIVED_INVITATION_ID));
        String invitationPhone = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SENDER_PHONE));
        String invitationCategories ="";
        int response = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_INVITATION_RESPONSE));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
        int serverCoUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CO_USER_ID));
        int serverGroupId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));

        ReceivedInvitation invitation = new ReceivedInvitation(invitationPhone, invitationCategories, response);
        invitation.setUserId(userId);
        invitation.setServerCoUserId(serverCoUserId);
        invitation.setServerGroupId(serverGroupId);
        invitation.setReceivedInvitationId(receivedInvitationId);

        return invitation;
    }

    public List<ReceivedInvitation> getNotSyncReceivedInvitations(){
        List<ReceivedInvitation> list = new ArrayList<>();
        Cursor cursor = db.getNotSyncReceivedInvitations();
        while (cursor.moveToNext()) {
            int receivedInvitationId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String invitationPhone = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SENDER_PHONE));
            int invitationResponse = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_INVITATION_RESPONSE));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            int serverCoUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CO_USER_ID));
            int serverGroupId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));


            ReceivedInvitation invitation = new ReceivedInvitation(receivedInvitationId,  invitationPhone,  invitationResponse,  userId,  serverCoUserId );
            invitation.setServerGroupId(serverGroupId);
            list.add(invitation);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getNotSyncResponses called");
        return list;
    }

}
