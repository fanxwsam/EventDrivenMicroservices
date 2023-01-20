package com.sf.cloud.command.interceptors;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.sf.cloud.command.CreateProductCommand;
import com.sf.cloud.core.data.ProductLookupEntity;
import com.sf.cloud.core.data.ProductLookupRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
    private static Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);
    private final ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> list) {
        return

                (index, command) -> {
                    LOGGER.info("Interceptor command: " + command.getPayload());
                    if (CreateProductCommand.class.equals(command.getPayloadType())) {
                        CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                        ProductLookupEntity productLookupEntity = productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());

                        if(productLookupEntity != null){
                            throw  new IllegalStateException(
                                    String.format("Product with productId %s or title %s already exist", createProductCommand.getProductId(), createProductCommand.getTitle())
                            );
                        }
                    }
                    return command;
                };
    }
}
