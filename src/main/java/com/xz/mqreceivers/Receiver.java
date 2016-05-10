package com.xz.mqreceivers;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public abstract class Receiver {

    @Autowired
    ReceiverManager receiverManager;

    @PostConstruct
    public void init() {
        this.receiverManager.registerReceiver(this);
    }

    public abstract void taskReceived(AggrTask aggrTask);
}
