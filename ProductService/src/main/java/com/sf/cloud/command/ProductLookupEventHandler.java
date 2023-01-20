package com.sf.cloud.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;
import com.sf.cloud.core.data.ProductLookupEntity;
import com.sf.cloud.core.data.ProductLookupRepository;
import com.sf.cloud.core.events.ProductCreateEvent;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventHandler {
    private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventHandler(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @EventHandler
    public void on(ProductCreateEvent event){
        ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.getProductId(), event.getTitle());

        productLookupRepository.save(productLookupEntity);
    }

    @ResetHandler
    public void reset(){
        productLookupRepository.deleteAll();
    }
}
