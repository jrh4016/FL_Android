package com.skeds.android.phone.business.Utilities.General;

import com.google.analytics.tracking.android.Log;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

//this class partially copied from javax.faces.convert.NumberConverter
public class NumberFormatTool {

    private String currencyCode = null;
    private String currencySymbol = null;
    private Boolean groupingUsed = true;
    private Boolean integerOnly = false;
    private Integer maxFractionDigits;
    private Integer maxIntegerDigits;
    private Integer minFractionDigits;
    private Integer minIntegerDigits;
    private Locale locale = null;
    private String pattern = null;
    private String type = "number";
    private static final String NBSP = "\u00a0";

    /**
     * <p>Return the ISO 4217 currency code used by <code>getAsString()</code>
     * with a <code>type</code> of <code>currency</code>.  If not set,
     * the value used will be based on the formatting <code>Locale</code>.</p>
     */
    public String getCurrencyCode() {

        return (this.currencyCode);

    }


    /**
     * <p>Set the ISO 4217 currency code used by <code>getAsString()</code>
     * with a <code>type</code> of <code>currency</code>.</p>
     *
     * @param currencyCode The new currency code
     */
    public void setCurrencyCode(String currencyCode) {

        clearInitialState();
        this.currencyCode = currencyCode;

    }


    /**
     * <p>Return the currency symbol used by <code>getAsString()</code>
     * with a <code>type</code> of <code>currency</code>.  If not set,
     * the value used will be based on the formatting <code>Locale</code>.</p>
     */
    public String getCurrencySymbol() {

        return (this.currencySymbol);

    }


    /**
     * <p>Set the currency symbol used by <code>getAsString()</code>
     * with a <code>type</code> of <code>currency</code>.</p>
     *
     * @param currencySymbol The new currency symbol
     */
    public void setCurrencySymbol(String currencySymbol) {

        clearInitialState();
        this.currencySymbol = currencySymbol;

    }


    /**
     * <p>Return <code>true</code> if <code>getAsString</code> should include
     * grouping separators if necessary.  If not modified, the default value
     * is <code>true</code>.</p>
     */
    public boolean isGroupingUsed() {

        return (this.groupingUsed != null ? this.groupingUsed : true);

    }


    /**
     * <p>Set the flag indicating whether <code>getAsString()</code> should
     * include grouping separators if necessary.</p>
     *
     * @param groupingUsed The new grouping used flag
     */
    public void setGroupingUsed(boolean groupingUsed) {

        clearInitialState();
        this.groupingUsed = groupingUsed;

    }


    /**
     * <p>Return <code>true</code> if only the integer portion of the given
     * value should be returned from <code>getAsObject()</code>.  If not
     * modified, the default value is <code>false</code>.</p>
     */
    public boolean isIntegerOnly() {

        return (this.integerOnly != null ? this.integerOnly : false);

    }


    /**
     * <p>Set to <code>true</code> if only the integer portion of the given
     * value should be returned from <code>getAsObject()</code>.</p>
     *
     * @param integerOnly The new integer-only flag
     */
    public void setIntegerOnly(boolean integerOnly) {

        clearInitialState();
        this.integerOnly = integerOnly;

    }


    /**
     * <p>Return the maximum number of digits <code>getAsString()</code> should
     * render in the fraction portion of the result.</p>
     */
    public int getMaxFractionDigits() {

        return (this.maxFractionDigits != null ? this.maxFractionDigits : 0);

    }


    /**
     * <p>Set the maximum number of digits <code>getAsString()</code> should
     * render in the fraction portion of the result.  If not set, the number of
     * digits depends on the value being converted.</p>
     *
     * @param maxFractionDigits The new limit
     */
    public void setMaxFractionDigits(int maxFractionDigits) {

        clearInitialState();
        this.maxFractionDigits = maxFractionDigits;

    }


    /**
     * <p>Return the maximum number of digits <code>getAsString()</code> should
     * render in the integer portion of the result.</p>
     */
    public int getMaxIntegerDigits() {

        return (this.maxIntegerDigits != null ? this.maxIntegerDigits : 0);

    }


    /**
     * <p>Set the maximum number of digits <code>getAsString()</code> should
     * render in the integer portion of the result.  If not set, the number of
     * digits depends on the value being converted.</p>
     *
     * @param maxIntegerDigits The new limit
     */
    public void setMaxIntegerDigits(int maxIntegerDigits) {

        clearInitialState();
        this.maxIntegerDigits = maxIntegerDigits;

    }


    /**
     * <p>Return the minimum number of digits <code>getAsString()</code> should
     * render in the fraction portion of the result.</p>
     */
    public int getMinFractionDigits() {

        return (this.minFractionDigits != null ? this.minFractionDigits : 0);

    }


    /**
     * <p>Set the minimum number of digits <code>getAsString()</code> should
     * render in the fraction portion of the result.  If not set, the number of
     * digits depends on the value being converted.</p>
     *
     * @param minFractionDigits The new limit
     */
    public void setMinFractionDigits(int minFractionDigits) {

        clearInitialState();
        this.minFractionDigits = minFractionDigits;

    }


    /**
     * <p>Return the minimum number of digits <code>getAsString()</code> should
     * render in the integer portion of the result.</p>
     */
    public int getMinIntegerDigits() {

        return (this.minIntegerDigits != null ? this.minIntegerDigits : 0);

    }


    /**
     * <p>Set the minimum number of digits <code>getAsString()</code> should
     * render in the integer portion of the result.  If not set, the number of
     * digits depends on the value being converted.</p>
     *
     * @param minIntegerDigits The new limit
     */
    public void setMinIntegerDigits(int minIntegerDigits) {

        clearInitialState();
        this.minIntegerDigits = minIntegerDigits;

    }

    /**
     * <p>Set the <code>Locale</code> to be used when parsing numbers.
     * If set to <code>null</code>, the <code>Locale</code> stored in the
     * {@link javax.faces.component.UIViewRoot} for the current request
     * will be utilized.</p>
     *
     * @param locale The new <code>Locale</code> (or <code>null</code>)
     */
    public void setLocale(Locale locale) {

        clearInitialState();
        this.locale = locale;

    }

    public void setLocale(String locale) {

        clearInitialState();
        this.locale = getLocaleFromString(locale);

    }

    /**
     * <p>Return the format pattern to be used when formatting and
     * parsing numbers.</p>
     */
    public String getPattern() {

        return (this.pattern);

    }


    /**
     * <p>Set the format pattern to be used when formatting and parsing
     * numbers.  Valid values are those supported by
     * <code>java.text.DecimalFormat</code>.
     * An invalid value will cause a {@link javax.faces.convert.ConverterException} when
     * <code>getAsObject()</code> or <code>getAsString()</code> is called.</p>
     *
     * @param pattern The new format pattern
     */
    public void setPattern(String pattern) {

        clearInitialState();
        this.pattern = pattern;

    }


    /**
     * <p>Return the number type to be used when formatting and parsing numbers.
     * If not modified, the default type is <code>number</code>.</p>
     */
    public String getType() {

        return (this.type);

    }


    /**
     * <p>Set the number type to be used when formatting and parsing numbers.
     * Valid values are <code>currency</code>, <code>number</code>, or
     * <code>percent</code>.
     * An invalid value will cause a {@link javax.faces.convert.ConverterException} when
     * <code>getAsObject()</code> or <code>getAsString()</code> is called.</p>
     *
     * @param type The new number style
     */
    public void setType(String type) {

        clearInitialState();
        this.type = type;

    }

    public String format(Object value) {
        try {

            // If the specified value is null, return a zero-length String
            if (value == null) {
                return "";
            }

            // If the incoming value is still a string, play nice
            // and return the value unmodified
            if (value instanceof String) {
                return (String) value;
            }

            if (locale == null) {
                locale = Locale.US;
            }

            // Create and configure the formatter to be used
            NumberFormat formatter =
                    getNumberFormat(locale);
            if (((pattern != null) && pattern.length() != 0)
                    || "currency".equals(type)) {
                configureCurrency(formatter);
            }
            configureFormatter(formatter);

            // Perform the requested formatting
            return (formatter.format(value));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @throws ConverterException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public Object parse(String value, boolean toBigDecimal) {
        Object returnValue = null;
        NumberFormat parser = null;

        try {

            // If the specified value is null or zero-length, return null
            if (value == null) {
                return (null);
            }
            value = value.trim();
            if (value.length() < 1) {
                return (null);
            }

            // Identify the Locale to use for parsing
            if (locale == null) {
                locale = Locale.US;
            }

            // Create and configure the parser to be used
            parser = getNumberFormat(locale);
            if (((pattern != null) && pattern.length() != 0)
                    || "currency".equals(type)) {
                configureCurrency(parser);
            }
            parser.setParseIntegerOnly(isIntegerOnly());
            boolean groupSepChanged = false;
            // BEGIN HACK 4510618
            // This lovely bit of code is for a workaround in some
            // oddities in the JDK's parsing code.
            // See:  http://bugs.sun.com/view_bug.do?bug_id=4510618
            if (parser instanceof DecimalFormat) {
                DecimalFormat dParser = (DecimalFormat) parser;

                // Take a small hit in performance to avoid a loss in
                // precision due to DecimalFormat.parse() returning Double
                if (toBigDecimal) {
                    dParser.setParseBigDecimal(true);
                }
                DecimalFormatSymbols symbols =
                        dParser.getDecimalFormatSymbols();
                if (symbols.getGroupingSeparator() == '\u00a0') {
                    groupSepChanged = true;
                    String tValue;
                    if (value.contains(NBSP)) {
                        tValue = value.replace('\u00a0', ' ');
                    } else {
                        tValue = value;
                    }
                    symbols.setGroupingSeparator(' ');
                    dParser.setDecimalFormatSymbols(symbols);
                    try {
                        return dParser.parse(tValue);
                    } catch (ParseException pe) {
                        if (groupSepChanged) {
                            symbols.setGroupingSeparator('\u00a0');
                            dParser.setDecimalFormatSymbols(symbols);
                        }
                    }
                }
            }
            // END HACK 4510618

            // Perform the requested parsing
            returnValue = parser.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return returnValue;
    }

    private static Class currencyClass;

    static {
        try {
            currencyClass = Class.forName("java.util.Currency");
            // container's runtime is J2SE 1.4 or greater
        } catch (Exception ignored) {
        }
    }

    private static final Class[] GET_INSTANCE_PARAM_TYPES =
            new Class[]{String.class};


    /**
     * <p/>
     * Override the formatting locale's default currency symbol with the
     * specified currency code (specified via the "currencyCode" attribute) or
     * currency symbol (specified via the "currencySymbol" attribute).</p>
     * <p/>
     * <p>If both "currencyCode" and "currencySymbol" are present,
     * "currencyCode" takes precedence over "currencySymbol" if the
     * java.util.Currency class is defined in the container's runtime (that
     * is, if the container's runtime is J2SE 1.4 or greater), and
     * "currencySymbol" takes precendence over "currencyCode" otherwise.</p>
     * <p/>
     * <p>If only "currencyCode" is given, it is used as a currency symbol if
     * java.util.Currency is not defined.</p>
     * <pre>
     * Example:
     *
     * JDK    "currencyCode" "currencySymbol" Currency symbol being displayed
     * -----------------------------------------------------------------------
     * all         ---            ---         Locale's default currency symbol
     *
     * <1.4        EUR            ---         EUR
     * >=1.4       EUR            ---         Locale's currency symbol for Euro
     *
     * all         ---           \u20AC       \u20AC
     *
     * <1.4        EUR           \u20AC       \u20AC
     * >=1.4       EUR           \u20AC       Locale's currency symbol for Euro
     * </pre>
     *
     * @param formatter The <code>NumberFormatter</code> to be configured
     */
    private void configureCurrency(NumberFormat formatter) throws Exception {

        // Implementation copied from JSTL's FormatNumberSupport.setCurrency()

        String code = null;
        String symbol = null;

        if ((currencyCode == null) && (currencySymbol == null)) {
            return;
        }

        if ((currencyCode != null) && (currencySymbol != null)) {
            if (currencyClass != null)
                code = currencyCode;
            else
                symbol = currencySymbol;
        } else if (currencyCode == null) {
            symbol = currencySymbol;
        } else {
            if (currencyClass != null)
                code = currencyCode;
            else
                symbol = currencyCode;
        }

        if (code != null) {
            Object[] methodArgs = new Object[1];

            /*
            * java.util.Currency.getInstance()
            */
            Method m = currencyClass.getMethod("getInstance",
                    GET_INSTANCE_PARAM_TYPES);
            methodArgs[0] = code;
            Object currency = m.invoke(null, methodArgs);

            /*
            * java.text.NumberFormat.setCurrency()
            */
            Class[] paramTypes = new Class[1];
            paramTypes[0] = currencyClass;
            Class numberFormatClass = Class.forName("java.text.NumberFormat");
            m = numberFormatClass.getMethod("setCurrency", paramTypes);
            methodArgs[0] = currency;
            m.invoke(formatter, methodArgs);
        } else {
            /*
            * Let potential ClassCastException propagate up (will almost
            * never happen)
            */
            DecimalFormat df = (DecimalFormat) formatter;
            DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            dfs.setCurrencySymbol(symbol);
            df.setDecimalFormatSymbols(dfs);
        }

    }


    /**
     * <p>Configure the specified <code>NumberFormat</code> based on the
     * formatting properties that have been set.</p>
     *
     * @param formatter The <code>NumberFormat</code> instance to configure
     */
    private void configureFormatter(NumberFormat formatter) {

        formatter.setGroupingUsed(groupingUsed);
        if (isMaxIntegerDigitsSet()) {
            formatter.setMaximumIntegerDigits(maxIntegerDigits);
        }
        if (isMinIntegerDigitsSet()) {
            formatter.setMinimumIntegerDigits(minIntegerDigits);
        }
        if (isMaxFractionDigitsSet()) {
            formatter.setMaximumFractionDigits(maxFractionDigits);
        }
        if (isMinFractionDigitsSet()) {
            formatter.setMinimumFractionDigits(minFractionDigits);
        }

    }


    private boolean isMaxIntegerDigitsSet() {

        return (maxIntegerDigits != null);

    }


    private boolean isMinIntegerDigitsSet() {

        return (minIntegerDigits != null);

    }


    private boolean isMaxFractionDigitsSet() {

        return (maxFractionDigits != null);

    }


    private boolean isMinFractionDigitsSet() {

        return (minFractionDigits != null);

    }

    /**
     * <p>Return a <code>NumberFormat</code> instance
     *
     * @param locale The <code>Locale</code> used to select formatting
     *               and parsing conventions
     */
    private NumberFormat getNumberFormat(Locale locale) {

        if (pattern == null && type == null) {
            throw new IllegalArgumentException("Either pattern or type must" +
                    " be specified.");
        }

        // PENDING(craigmcc) - Implement pooling if needed for performance?

        // If pattern is specified, type is ignored
        if (pattern != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
            return (new DecimalFormat(pattern, symbols));
        }

        // Create an instance based on the specified type
        else if (type.equals("currency")) {
            return (NumberFormat.getCurrencyInstance(locale));
        } else if (type.equals("number")) {
            return (NumberFormat.getNumberInstance(locale));
        } else if (type.equals("percent")) {
            return (NumberFormat.getPercentInstance(locale));
        } else {
            // PENDING(craigmcc) - i18n
            throw new RuntimeException
                    (new IllegalArgumentException(type));
        }


    }

    //copy from javax.faces.component.UIViewRoot
    private static Locale getLocaleFromString(String localeStr)
            throws IllegalArgumentException {
        // length must be at least 2.
        if (null == localeStr || localeStr.length() < 2) {
            throw new IllegalArgumentException("Illegal locale String: " +
                    localeStr);
        }

        Locale result = null;
        String lang = null;
        String country = null;
        String variant = null;
        char[] seps = {
                '-',
                '_'
        };
        int inputLength = localeStr.length();
        int i = 0;
        int j = 0;

        // to have a language, the length must be >= 2
        if ((inputLength >= 2) &&
                ((i = indexOfSet(localeStr, seps, 0)) == -1)) {
            // we have only Language, no country or variant
            if (2 != localeStr.length()) {
                throw new
                        IllegalArgumentException("Illegal locale String: " +
                        localeStr);
            }
            lang = localeStr.toLowerCase();
        }

        // we have a separator, it must be either '-' or '_'
        if (i != -1) {
            lang = localeStr.substring(0, i);
            // look for the country sep.
            // to have a country, the length must be >= 5
            if ((inputLength >= 5) &&
                    (-1 == (j = indexOfSet(localeStr, seps, i + 1)))) {
                // no further separators, length must be 5
                if (inputLength != 5) {
                    throw new
                            IllegalArgumentException("Illegal locale String: " +
                            localeStr);
                }
                country = localeStr.substring(i + 1);
            }
            if (j != -1) {
                country = localeStr.substring(i + 1, j);
                // if we have enough separators for language, locale,
                // and variant, the length must be >= 8.
                if (inputLength >= 8) {
                    variant = localeStr.substring(j + 1);
                } else {
                    throw new
                            IllegalArgumentException("Illegal locale String: " +
                            localeStr);
                }
            }
        }
        if (variant != null && country != null && lang != null) {
            result = new Locale(lang, country, variant);
        } else if (lang != null && country != null) {
            result = new Locale(lang, country);
        } else if (lang != null) {
            result = new Locale(lang, "");
        }
        return result;
    }

    /**
     * @param str       local string
     * @param set       the substring
     * @param fromIndex starting index
     * @return starting at <code>fromIndex</code>, the index of the
     * first occurrence of any substring from <code>set</code> in
     * <code>toSearch</code>, or -1 if no such match is found
     */
    //copy from javax.faces.component.UIViewRoot
    private static int indexOfSet(String str, char[] set, int fromIndex) {
        int result = -1;
        for (int i = fromIndex, len = str.length(); i < len; i++) {
            for (int j = 0, innerLen = set.length; j < innerLen; j++) {
                if (str.charAt(i) == set[j]) {
                    result = i;
                    break;
                }
            }
            if (-1 != result) {
                break;
            }
        }
        return result;
    }

    private boolean transientFlag = false;


    public boolean isTransient() {
        return (transientFlag);
    }


    public void setTransient(boolean transientFlag) {
        this.transientFlag = transientFlag;
    }


    private boolean initialState;

    public void markInitialState() {
        initialState = true;
    }

    public boolean initialStateMarked() {
        return initialState;
    }

    public void clearInitialState() {
        initialState = false;
    }

    public static NumberFormatTool getCurrencyFormat() {
        NumberFormatTool currencyFormatTool = new NumberFormatTool();
        currencyFormatTool.setType("currency");
        currencyFormatTool.setCurrencySymbol(
                UserUtilitiesSingleton.getInstance().user.getCountryInfo().getCurrencySymbol());
        try {
            currencyFormatTool.setLocale(UserUtilitiesSingleton.getInstance().user.getCountryInfo().getLocalCode());
        } catch (IllegalArgumentException e) {
            Log.e(e.getMessage());
        }
        return currencyFormatTool;
    }

    public static NumberFormatTool getPercentFormat() {
        NumberFormatTool percentFormatTool = new NumberFormatTool();
        percentFormatTool.setType("percent");
        percentFormatTool.setMaxFractionDigits(7);
        try {
            percentFormatTool.setLocale(UserUtilitiesSingleton.getInstance().user.getCountryInfo().getLocalCode());
        } catch (IllegalArgumentException e) {
            Log.e(e.getMessage());
        }

        return percentFormatTool;
    }

}

