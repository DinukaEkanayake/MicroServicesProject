package com.university.ProductService.service;

import com.university.ProductService.dto.ProductRequest;
import com.university.ProductService.dto.ProductResponse;
import com.university.ProductService.model.Product;
import com.university.ProductService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j //from lombok to add logs
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} Saved",product.getId());


    }

    public List<ProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream().map(product -> ProductResponse.builder()
                .id(product.getId())
                .build()).toList();

    }
}
