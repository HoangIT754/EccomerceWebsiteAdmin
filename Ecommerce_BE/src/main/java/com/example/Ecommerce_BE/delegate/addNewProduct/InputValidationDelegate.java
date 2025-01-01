package com.example.Ecommerce_BE.delegate.addNewProduct;

import com.example.Ecommerce_BE.service.ProductsService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("inputValidationDelegate")
public class InputValidationDelegate implements JavaDelegate {

    private final ProductsService productsService;

    public InputValidationDelegate(ProductsService productsService) {
        this.productsService = productsService;
    }

    private static final Logger logger = LoggerFactory.getLogger(InputValidationDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String productName = (String) execution.getVariable("productName");
        Double price = (Double) execution.getVariable("price");
        Integer stock = (Integer) execution.getVariable("stock");

        logger.info("Executing InputValidationDelegate with productName: {}, price: {}, stock: {}", productName, price, stock);

        if (productName == null || productName.isEmpty()) {
            execution.setVariable("validInput", false);
            logger.error("Product name is required.");
            throw new IllegalArgumentException("Product name is required.");
        }

        if (productsService.isProductDuplicated(productName)) {
            execution.setVariable("validInput", false);
            logger.error("Product name '{}' is already in use.", productName);
            throw new IllegalArgumentException("Product name is already in use.");
        }

        if (price == null || price <= 0) {
            execution.setVariable("validInput", false);
            logger.error("Price must be greater than zero.");
            throw new IllegalArgumentException("Price must be greater than zero.");
        }

        if (stock == null || stock <= 0) {
            execution.setVariable("validInput", false);
            logger.error("Stock must be greater than zero.");
            throw new IllegalArgumentException("Stock must be greater than zero.");
        }

        execution.setVariable("validInput", true);
        logger.info("Input validation passed. Valid Input is: {}", execution.getVariable("validInput"));
    }
}
