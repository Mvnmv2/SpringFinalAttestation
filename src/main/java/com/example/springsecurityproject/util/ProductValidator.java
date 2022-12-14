package com.example.springsecurityproject.util;

import com.example.springsecurityproject.models.Product;
import com.example.springsecurityproject.security.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class ProductValidator implements Validator {

    private final ProductService productService;

    public ProductValidator(ProductService productService) {
        this.productService = productService;
    }

    //В этом методе укзываем для какой модели предназначен
    //данный вылидатор
    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product) target;
        if(productService.getProductFindByTitle(product) != null){
            errors.rejectValue("title", "", "Данное наименование товара уже используется");
        };
    }
}
