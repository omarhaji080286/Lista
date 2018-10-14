package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.ReceivedInvitation;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class InvitationsDataManager {

    private final static String TAG = "InvitationsDataManager";
    private DataBaseHelper db;

    public InvitationsDataManager(Context context) {
        this.db = new DataBaseHelper(context);
        //this.db = DataBaseHelper.getInstance(context);
        Log.d(TAG, Constants.TAG_LISTA+"DB opened");

    }

    public void closDB(){
        db.close();
        Log.d(TAG, Constants.TAG_LISTA+"DB closed");

    }

    public int addReceivedInvitation( ReceivedInvitation invitation) {
        int result;
        if (db.invitationExists(invitation.getInvitationEmail())) {
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

    public ReceivedInvitation getReceivedInvitation( String senderEmail) {
        Log.d(TAG, Constants.TAG_LISTA+"getReceivedInvitation called");
        return db.getReceivedInvitation(senderEmail);
        }

    List<ReceivedInvitation> getNotSyncResponses(){
        List<ReceivedInvitation> list = new ArrayList<>();
        Cursor cursor = db.getNotSyncResponses();
        while (cursor.moveToNext()) {
            int receivedInvitationId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String invitationEmail = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SENDER_EMAIL));
            int invitationResponse = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_INVITATION_RESPONSE));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            int serverCoUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CO_USER_ID));
            int serverGroupId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));


            ReceivedInvitation invitation = new ReceivedInvitation(receivedInvitationId,  invitationEmail,  invitationResponse,  userId,  serverCoUserId );
            invitation.setServerGroupeId(serverGroupId);
            list.add(invitation);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getNotSyncResponses called");
        return list;
    }

}
