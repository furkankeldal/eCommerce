package com.eticaret.stock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.io.Serializable;

@Document(collection = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    
    @NotBlank
    private String productId;
    
    @NotNull
    @Min(0)
    private Integer quantity;
    
    @NotNull
    @Min(0)
    private Integer reservedQuantity;
    
    private String location;

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
}

