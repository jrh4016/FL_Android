package com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook;

import java.io.Serializable;
import java.math.BigDecimal;

public class Cost implements Serializable {

    private String type = "";
    private String name = "";
    private BigDecimal price = new BigDecimal(0);

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

}
