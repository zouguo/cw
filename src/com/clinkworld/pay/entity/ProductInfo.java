package com.clinkworld.pay.entity;

import java.io.Serializable;

/**
 * Created by srh on 2015/10/20.
 * <p/>
 * 商品信息
 */
public class ProductInfo implements Serializable {

    /**
     * 商品编码
     */
    private String productBarCode;

    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品单价
     */
    private float price;
    /**
     * 商品数量
     */
    private int number = 1;

    public String getProductBarCode() {
        return productBarCode;
    }

    public void setProductBarCode(String productBarCode) {
        this.productBarCode = productBarCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
