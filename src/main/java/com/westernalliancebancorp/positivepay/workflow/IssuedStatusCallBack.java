package com.westernalliancebancorp.positivepay.workflow;

import org.springframework.stereotype.Service;

/**
 * IssuedStatusCallBack is
 *
 * @author Giridhar Duggirala
 */
@Service
public class IssuedStatusCallBack implements PostActionCallback {
    @Override
    public boolean executePostActionCallback(CallbackContext callbackContext) {
        System.out.println("This is executed");
        return Boolean.TRUE;
    }
}
