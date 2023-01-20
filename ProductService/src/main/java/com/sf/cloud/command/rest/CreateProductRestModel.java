package com.sf.cloud.command.rest;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class CreateProductRestModel {
    @NotBlank(message="Product title is a required field")
    private String title;

    @Min(value=1, message="Product price cannot be less than one")
    private BigDecimal price;

    @Max(value=500, message="Product quantity cannot be more than 500")
    private Integer quantity;




}
