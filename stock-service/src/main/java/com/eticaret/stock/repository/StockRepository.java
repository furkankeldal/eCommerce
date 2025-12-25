package com.eticaret.stock.repository;

import com.eticaret.stock.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface StockRepository extends MongoRepository<Stock, Long> {
    Optional<Stock> findByProductId(String productId);
}

