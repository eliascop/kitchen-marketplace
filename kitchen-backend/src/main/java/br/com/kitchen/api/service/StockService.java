package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.StockDTO;
import br.com.kitchen.api.mapper.StockMapper;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.CartItems;
import br.com.kitchen.api.model.ProductSku;
import br.com.kitchen.api.model.Stock;
import br.com.kitchen.api.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService extends GenericService<Stock,Long> {

    private final StockRepository stockRepository;
    private final OutboxService outboxService;

    public StockService(StockRepository stockRepository,
                        OutboxService outboxService) {
        super(stockRepository, Stock.class);
        this.stockRepository = stockRepository;
        this.outboxService = outboxService;
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
        stock.setStockAction("STOCK_RESERVED");
        save(stock);
    }

    @Transactional
    public void confirmReservation(ProductSku productSku, int quantity) {
        Stock stock = getStockOrThrow(productSku);

        stock.setReservedQuantity(stock.getReservedQuantity() - quantity);
        stock.setSoldQuantity(stock.getSoldQuantity() + quantity);
        stock.setStockAction("STOCK_CONFIRMED");
        save(stock);
    }

    @Transactional
    public void releaseReservation(ProductSku productSku, int quantity) {
        Stock stock = getStockOrThrow(productSku);

        stock.setReservedQuantity(stock.getReservedQuantity() - quantity);
        stock.setStockAction("STOCK_RELEASED");
        save(stock);
    }

    public void validateSkuAvailability(ProductSku sku, int quantity) {
        Stock stock = getStockOrThrow(sku);

        if (stock.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock for product " + sku.getSku());
        }
    }

    private void save(Stock stock){
        Stock stockSaved = stockRepository.save(stock);
        StockDTO dto = StockMapper.toDTO(stockSaved);
        outboxService.createStockEvent(dto);
    }

    @Transactional
    public void reserveStockFromCart(Cart cart) {
        for (CartItems item: cart.getCartItems()) {
            reserveStock(item.getProductSku(), item.getQuantity());
        }
    }

    @Transactional
    public void confirmStockFromCart(Cart cart) {
        for (CartItems item: cart.getCartItems()) {
            confirmReservation(item.getProductSku(), item.getQuantity());
        }
    }

    @Transactional
    public void releaseStockFromCart(Cart cart) {
        for (CartItems item: cart.getCartItems()) {
            releaseReservation(item.getProductSku(), item.getQuantity());
        }
    }
}
