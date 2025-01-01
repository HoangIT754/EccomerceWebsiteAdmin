package com.example.Ecommerce_BE.delegate.addNewProduct;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("notifyInputErrorDelegate")
public class NotifyInputErrorDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String errorMessage = (String) execution.getVariable("errorMessage");
        notifyError(errorMessage);
    }

    private void notifyError(String errorMessage) {
        System.out.println("Input Error: " + errorMessage);
    }
}
