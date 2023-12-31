package com.example.application.data;

import java.io.Serializable;
import java.time.LocalDate;

public class OrderItem implements Serializable {
    private String orderId;
    private String itemName;
    private LocalDate orderDate;
    private String orderStatus;
    private Double orderCost;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Double getOrderTotal() {
        return orderCost;
    }

    public void setOrderTotal(Double orderCost) {
        this.orderCost = orderCost;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
