package com.driverspa.util.otto.ws;

import com.driverspa.model.CarItem;

/**
 * Created by Yerzhan Tanatov on 10/11/15.
 */
public class CarDefinitionItemRequestEvent {
    CarItem car;

    public CarDefinitionItemRequestEvent(CarItem car) {
        this.car = car;
    }

    public CarItem getCar() {
        return car;
    }

    public void setCar(CarItem car) {
        this.car = car;
    }
}
