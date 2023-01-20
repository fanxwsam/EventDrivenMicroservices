package com.sf.cloud.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sf.cloud.core.events.ProductReservationCancelledEvent;
import com.sf.cloud.core.events.ProductReservedEvent;
import com.sf.cloud.core.data.ProductEntity;
import com.sf.cloud.core.data.ProductRepository;
import com.sf.cloud.core.events.ProductCreateEvent;



@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {

    private final ProductRepository productRepository;

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductEventHandler.class);

    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception{
        throw exception;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception){
        // Log error message
    }


    @EventHandler
    public void on(ProductCreateEvent event) throws Exception{
        System.out.println("How is EventHandler running ? $$$$$$$$$$$$$$$$$$$$$$$$$");
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        try {
            productRepository.save(productEntity);
        } catch (Exception exception){
            exception.printStackTrace();
        }


//        if(true){
//            throw new Exception("Testing:   Forcing exception in the Event Handler class");
//        }

    }

    @EventHandler
    public void on(ProductReservedEvent event) throws InterruptedException {
        ProductEntity entity = productRepository.findByProductId(event.getProductId());
        LOGGER.info("##### ProductReservedEvent. productID: " + entity.getProductId()
                + ", existing product quantity " + entity.getQuantity());

        entity.setQuantity(entity.getQuantity() - event.getQuantity());

        productRepository.save(entity);
        LOGGER.info("$$$$ ProductReservedEvent. productID: " + entity.getProductId()
                + ", new product quantity " + entity.getQuantity());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent event) throws InterruptedException {
        ProductEntity entity = productRepository.findByProductId(event.getProductId());
        LOGGER.info("##### ProductCancelledEvent. productID: " + entity.getProductId()
                + ", existing product quantity " + entity.getQuantity());
        entity.setQuantity(entity.getQuantity() + event.getQuantity());
        productRepository.save(entity);
        LOGGER.info("$$$$ ProductCancelledEvent. productID: " + entity.getProductId()
                + ", new product quantity " + entity.getQuantity());
    }

    @ResetHandler
    public void reset(){
        productRepository.deleteAll();
    }
}
