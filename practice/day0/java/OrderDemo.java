public class OrderDemo {
    public static void main(String[] args) {
        Order order = new Order();
        order.productName = "Mouse";
        order.quantity = 3;
        order.price = 120000;

        int total = order.calcTotal();
        System.out.println(order.productName + " 合計: " + total);
    }
}