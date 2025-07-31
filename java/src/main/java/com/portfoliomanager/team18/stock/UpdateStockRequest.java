package com.portfoliomanager.team18.stock;

import java.math.BigDecimal;

public class UpdateStockRequest {
    private Integer qty;
    private BigDecimal avgPrice;

    public UpdateStockRequest(Integer qty, BigDecimal avgPrice) {
        this.qty = qty;
        this.avgPrice = avgPrice;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    @Override
    public String toString() {
        return "UpdateStockRequest{" +
                "qty=" + qty +
                ", avgPrice=" + avgPrice +
                '}';
    }
}