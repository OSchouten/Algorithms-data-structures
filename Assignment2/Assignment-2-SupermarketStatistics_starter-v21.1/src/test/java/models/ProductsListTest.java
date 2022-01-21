package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ProductsListTest {

    OrderedList<Product> products;
    Product product1, product2, product2a, product3a, product3b, product6, product3c;

    @BeforeEach
    private void setup() {
        products = new OrderedArrayList<Product>(Comparator.comparing(Product::getBarcode));
        PurchaseTracker.importItemsFromFile(products,
                ProductsListTest.class.getResource("/products12.txt").getPath(),
                Product::fromLine);
        product1 = products.get(0);
        product2 = products.get(1);
        product6 = products.get(5);
        product2a = new Product(222222222222222L, "", 0.0);
        product3a = new Product(333333333333334L, "geraspte wortelen", 1.00);
        product3b = new Product(333333333333335L, "snoeptomaatjes", 2.50);

    }

    @Test
    public void importItemsLoadsAllProductsUnsorted() {
        assertEquals(12, products.size());
        assertEquals(111111111111111L, products.get(0).getBarcode());
        assertEquals(222222222222222L, products.get(1).getBarcode());
        assertEquals(333333333333333L, products.get(2).getBarcode());
    }

    @Test
    public void sortOrdersByBarcode() {
        products.sort();
        assertEquals(12, products.size());
        assertEquals(111111111111110L, products.get(0).getBarcode());
        assertEquals(111111111111111L, products.get(1).getBarcode());
        assertEquals(222222222222220L, products.get(2).getBarcode());
    }

    @Test
    public void indexOfFindsByComparator() {
        products.sort();
        for (int index = 0; index < products.size(); index++) {
            assertEquals(index, products.indexOf(products.get(index)));
        }

        products.add(product3a);
        assertSame(product3a, products.get(products.size() - 1));
        assertEquals(products.size() - 1, products.indexOf(product3a));
    }

    @Test
    public void aggregatePricesAccumulatesAll() {
        assertEquals(34.14, products.aggregate(Product::getPrice), 0.000001);
    }

    @Test
    public void doubleThePricesByMerge() {
        products.sort();
        for (int index = 0; index < products.size(); index++) {

            products.merge(products.get(index), (p1, p2) -> {
                p1.setPrice(p1.getPrice() + p2.getPrice());
                return p1;
            });
        }
        products.merge(product3a, (p1, p2) -> p1);

        assertEquals(69.28, products.aggregate(Product::getPrice), 0.000001);
    }

    @Test
    public void insertSustainsRepresentationInvariant() {
        products.sort();
        products.add(products.size() - 1, product3a);
        assertEquals(products.size() - 2, products.indexOf(product3a));
    }

    @Test
    public void newObjectSameValue() {
        products.add(product3a);
        products.sort();

       Product productNew = new Product(product3a.getBarcode(), product3a.getTitle(), product3a.getPrice());

        System.out.println(product3a == productNew);
        assertEquals(products.indexOfByBinarySearch(productNew), products.indexOfByBinarySearch(product3a));
    }

    @Test
    public void removeSustainsRepresentationInvariant() {
        products.sort();
        products.add(product3a);

        assertEquals(products.size() - 1, products.indexOf(product3a));
        products.remove(5);
        assertEquals(products.size() - 1, products.indexOf(product3a));
        products.remove(product1);
        assertEquals(products.size() - 1, products.indexOf(product3a));
        products.remove(products.size() - 1);

        for (int index = 0; index < products.size(); index++) {
            int j = products.indexOf(products.get(index));
            assertEquals(index, j);
        }

    }

    @Test
    public void somethingTest() {
        // Add products to array list
        products.add(product3a);
        products.add(product3a);
        products.add(product3a);
        products.add(product3a);
        products.add(product3a);
        products.add(product3a);
        products.add(product3a);
        products.add(product3a);

        // Re-sort array list
        // As unsorted array part is large
        products.sort();


        OrderedArrayList newProducts = new OrderedArrayList<Product>(Comparator.comparing(Product::getBarcode));
        newProducts.add(product3a);
        newProducts.add(product3a);
        newProducts.add(product3a);
        newProducts.add(product3a);
        newProducts.add(product3a);
        newProducts.add(product3a);

        products.addAll(newProducts);


    }

    @Test
    void CanAccessBinarySearchesWithoutLinear() {
        products.add(product1);
        products.add(product2);
        products.add(product2a);
        products.sort();

        OrderedArrayList orderedArrayList = (OrderedArrayList) products;
        OrderedArrayList<Product> productsCopy = (OrderedArrayList<Product>) products;

        int testIterative = orderedArrayList.indexOfByIterativeBinarySearch(product2);
        int testRecursive = productsCopy.indexOfByRecursiveBinarySearch(product2);

        assertEquals(testIterative, testRecursive);
    }
}
