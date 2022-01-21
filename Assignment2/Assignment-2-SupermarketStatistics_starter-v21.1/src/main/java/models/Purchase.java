package models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class Purchase {
    private final Product product;
    private int count;

    public Purchase(Product product, int count) {
        this.product = product;
        this.count = count;
    }

    /**
     * parses purchase summary information from a textLine with format: barcode, amount
     *
     * @param textLine
     * @param products a list of products ordered and searchable by barcode
     *                 (i.e. the comparator of the ordered list shall consider only the barcode when comparing products)
     * @return a new Purchase instance with the provided information
     * or null if the textLine is corrupt or incomplete
     */
    public static Purchase fromLine(String textLine, List<Product> products) {

        //check for null/non values
        if (textLine.isBlank() || textLine.isEmpty()) return null;

        //split up in parts into array
        String[] parts = textLine.split(",");
        //retrieve values from array and store in variables
        long barcode = Long.parseLong(parts[0].trim());
        int counter = Integer.parseInt(parts[1].trim());

        int index = products.indexOf(new Product(barcode));
        if (index != -1) {
            return new Purchase(products.get(index), counter);
        }
        return null;
    }

    /**
     * add a delta amount to the count of the purchase summary instance
     *
     * @param delta
     */
    public void addCount(int delta) {
        this.count += delta;
    }

    public long getBarcode() {
        return this.product.getBarcode();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Product getProduct() {
        return product;
    }

    public String getRevenue() {
        float rev = (float) (getProduct().getPrice() * this.count);

        return String.format("%.02f", rev);
    }

    public double getRevenueDouble() {
        return getProduct().getPrice() * this.count;
    }

    @Override
    public String toString() {
        return String.valueOf(getProduct().getBarcode()) +
                '/' + getProduct().getTitle() +
                '/' + this.count +
                '/' + getRevenue();
    }
}
