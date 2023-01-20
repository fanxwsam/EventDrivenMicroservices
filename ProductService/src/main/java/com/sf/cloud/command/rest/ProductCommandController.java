package com.sf.cloud.command.rest;

import com.sf.cloud.command.CreateProductCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductCommandController {

    private final Environment env;
    private final CommandGateway commandGateway;

    @Autowired
    public ProductCommandController(Environment env, CommandGateway commandGateway) {
        this.env = env;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel){
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .price(createProductRestModel.getPrice())
                .quantity(createProductRestModel.getQuantity())
                .title(createProductRestModel.getTitle())
                .productId(UUID.randomUUID().toString())
                .build();

        String returnValue;
        returnValue= commandGateway.sendAndWait(createProductCommand);
//        try {
//            returnValue= commandGateway.sendAndWait(createProductCommand);
//        }catch (Exception ex){
//            returnValue = ex.getMessage();
//        }

        return returnValue;
    }

//    @GetMapping
//    public String getProducts(){
//        System.out.println("===========================  getProducts ===   port:  " + env.getProperty("local.server.port"));
//        return "HTTP get handled. port:  " + env.getProperty("local.server.port");
//    }
//
//
//
//    @PutMapping
//    public String updateProduct(){
//        return "HTTP put handled";
//    }
//
//    @DeleteMapping
//    public String deleteProduct(){
//        return "HTTP delete handled";
//    }
}
