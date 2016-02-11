package com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook;

import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The Value for {@link TaxRate}
 *
 * @author daliashkevich
 */
public class TaxValue implements Serializable {
    private TaxRate taxRate;
    private BigDecimal value;

    public TaxRate getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(TaxRate taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
