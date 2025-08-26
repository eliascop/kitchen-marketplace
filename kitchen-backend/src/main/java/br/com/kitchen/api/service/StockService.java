package br.com.kitchen.api.service;

import br.com.kitchen.api.model.ProductSku;
import br.com.kitchen.api.model.Stock;
import br.com.kitchen.api.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService extends GenericService<Stock,Long> {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        super(stockRepository, Stock.class);
        this.stockRepository = stockRepository;
    }

    private Stock getStockOrThrow(ProductSku productSku) {
        return stockRepository.findBySku_SkuAndSeller_Id(
                        productSku.getSku(),
                        productSku.getProduct().getSeller().getId()
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stock not found with sku " + productSku.getSku()
                ));
    }

    @Transactional
    public void reserveStock(ProductSku productSku, int quantity) {
        Stock stock = getStockOrThrow(productSku);

        if (stock.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock to reserve for product " + productSku.getSku());
        }

        stock.setReservedQuantity(stock.getReservedQuantity() + quantity);
        stockRepository.save(stock);
    }

    @Transactional
    public void confirmReservation(ProductSku productSku, int quantity) {
        Stock stock = getStockOrThrow(productSku);

        stock.setReservedQuantity(stock.getReservedQuantity() - quantity);
        stock.setTotalQuantity(stock.getTotalQuantity() - quantity);

        stockRepository.save(stock);
    }

    @Transactional
    public void releaseReservation(ProductSku productSku, int quantity) {
        Stock stock = getStockOrThrow(productSku);

        stock.setReservedQuantity(stock.getReservedQuantity() - quantity);
        stockRepository.save(stock);
    }

    public void validateSkuAvailability(ProductSku sku, int quantity) {
        Stock stock = getStockOrThrow(sku);

        if (stock.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock for product " + sku.getSku());
        }
    }
}
