package com.sf.cloud.query;

import com.sf.cloud.query.rest.ProductRestModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import com.sf.cloud.core.data.ProductEntity;
import com.sf.cloud.core.data.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductsQueryHandler {

    private final ProductRepository productRepository;

    public ProductsQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProductsb(FindProductsQuery query){
        List<ProductRestModel> productsRest = new ArrayList<>();
        List<ProductEntity> storedProducts = productRepository.findAll();

        for(ProductEntity productEntity : storedProducts){
            ProductRestModel productRestModel = new ProductRestModel();
            BeanUtils.copyProperties(productEntity, productRestModel);
            productsRest.add(productRestModel);
        }
        return  productsRest;
    }


    @QueryHandler
    public List<ProductRestModel> findProductsa(FindProductsQuery query){
        List<ProductRestModel> productsRest1 = new ArrayList<>();

        List<ProductEntity> storedProducts = productRepository.findAll();

        for(ProductEntity productEntity : storedProducts){
            ProductRestModel productRestModel = new ProductRestModel();
            BeanUtils.copyProperties(productEntity, productRestModel);
            productsRest1.add(productRestModel);
        }
        return  productsRest1;
    }

}
