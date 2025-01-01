package com.example.Ecommerce_BE.delegate.addNewProduct;

import com.example.Ecommerce_BE.dto.request.product.ProductCreationRequest;
import com.example.Ecommerce_BE.entity.Products;
import com.example.Ecommerce_BE.service.ProductsService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component("addToDatabase")
public class AddToDatabaseDelegate implements JavaDelegate {

    @Autowired
    private ProductsService productsService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String productName = (String) execution.getVariable("productName");
        BigDecimal price = new BigDecimal(execution.getVariable("price").toString());
        String description = execution.getVariable("description").toString();
        String brandId = (String) execution.getVariable("brandId");
        Integer stock = (Integer) execution.getVariable("stock");
        List<String> subCategoryIds = (List<String>) execution.getVariable("subCategoryIds");
        String imgUrl = (String) execution.getVariable("imgUrl");

        ProductCreationRequest request = new ProductCreationRequest();
        request.setName(productName);
        request.setPrice(price);
        request.setDescription(description);
        request.setStock(stock);
        request.setBrand(UUID.fromString(brandId));
        request.setSubCategories(
                subCategoryIds.stream().map(UUID::fromString).toList()
        );
        request.setImgURL(imgUrl);

        productsService.createProduct(request);

        Products addedProduct = productsService.getProductByName(productName);
        if (addedProduct == null) {
            throw new RuntimeException("Product was not added to the database: " + productName);
        }

        System.out.println("Product added to database successfully: " + productName);
    }
}
