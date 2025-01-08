package com.driverspa.util.otto;

import com.driverspa.model.FareRequest;

/**
 * Created by Yerzhan Tanatov on 15/01/18.
 */
public class AcceptFareRequestBid {
    FareRequest.BidObject bidObject;

    public AcceptFareRequestBid(FareRequest.BidObject bidObject) {
        this.bidObject = bidObject;
    }

    public FareRequest.BidObject getBidObject() {
        return bidObject;
    }

    public void setBidObject(FareRequest.BidObject bidObject) {
        this.bidObject = bidObject;
    }
}
