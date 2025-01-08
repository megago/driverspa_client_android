package com.driverspa.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Yerzhan Tanatov on 17/03/16.
 */
public class NotificationId {

        private final static AtomicInteger c = new AtomicInteger(0);
        public static int getID() {
            return c.incrementAndGet();
        }

}
