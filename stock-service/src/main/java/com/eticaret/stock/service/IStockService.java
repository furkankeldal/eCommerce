package com.eticaret.stock.service;

import com.eticaret.stock.model.Stock;

import java.util.List;

public interface IStockService {
    Stock createStock(Stock stock);
    Stock getStockById(Long id);
    Stock getStockByProductId(String productId);
    List<Stock> getAllStocks();
    Stock updateStock(Long id, Stock stock);
    Stock reserveStock(Long id, Integer quantity);
    Stock releaseStock(Long id, Integer quantity);
    void deleteStock(Long id);
}

