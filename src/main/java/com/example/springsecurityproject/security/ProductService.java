package com.example.springsecurityproject.security;

import com.example.springsecurityproject.models.Product;
import com.example.springsecurityproject.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Temporal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //Метод по получению всех продуктов
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    //Метод по получению всех продукта по id
    public Product getProductById(int id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }

    //Метод по добавлению продукта, который пришел с формы
    @Transactional
    public void saveProduct(Product newProduct){
        productRepository.save(newProduct);
    }

    //Метод по обновлению информации о продукте
    @Transactional
    public void updateProduct(int id, Product product){
        product.setId(id);
        productRepository.save(product);
    }

    //Метод по удалению продукта по id
    @Transactional
    public void deleteProduct(int id){
        productRepository.deleteById(id);
    }


    //Метод по получению товара по наименованию
    public Product getProductFindByTitle(Product product){
        Optional<Product> product_db = productRepository.findByTitle(product.getTitle());
        return product_db.orElse(null);
    }

}
