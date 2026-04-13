public class Order {
    String productName;
    int quantity;
    int price;

    int calcTotal() {
        return quantity * price;
    }
}