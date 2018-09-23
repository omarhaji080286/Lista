package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.ReceivedInvitation;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class InvitationsDataManager {

    private DataBaseHelper db;

    public InvitationsDataManager(Context context) {
        this.db = new DataBaseHelper(context);
    }

    public void closDB(){
        db.close();
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
        return result;
    }

    public boolean updateReceivedInvitation( ReceivedInvitation invitation){
        return db.updateReceivedInvitation(invitation);
    }

    public ReceivedInvitation getReceivedInvitation( String senderEmail) {
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
        return list;
    }

}
