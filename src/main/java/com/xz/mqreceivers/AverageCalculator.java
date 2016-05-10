package com.xz.mqreceivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Component
@ReceiverInfo(taskType = "average")
public class AverageCalculator extends Receiver {

    static final Logger LOG = LoggerFactory.getLogger(AverageCalculator.class);

    @Override
    public void taskReceived(AggrTask aggrTask) {
        LOG.info("command received");
    }
}
