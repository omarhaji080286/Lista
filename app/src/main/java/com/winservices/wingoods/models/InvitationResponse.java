package com.winservices.wingoods.models;

public class InvitationResponse {

    private int confirmationStatus;
    private int hasResponded ;

    public InvitationResponse() {
        this.confirmationStatus = CoUser.REJECTED;
        this.hasResponded = CoUser.HAS_NOT_RESPONDED;
    }

    public int getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(int confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
    }

    public int getHasResponded() {
        return hasResponded;
    }

    public void setHasResponded(int hasResponded) {
        this.hasResponded = hasResponded;
    }
}
