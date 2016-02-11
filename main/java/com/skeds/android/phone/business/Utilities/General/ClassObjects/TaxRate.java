package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.math.BigDecimal;

public class TaxRate implements Serializable {

    private long id = 0;

    private String name = "";

    private String rateType = "";

    private BigDecimal rateValue = new BigDecimal(0);

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        rateType = type;
    }

    public void setValue(BigDecimal val) {
        rateValue = val;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return rateType;
    }

    public BigDecimal getValue() {
        return rateValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaxRate other = (TaxRate) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
