public class MethodDemo {
    static void printStartMessage() {
        System.out.println("受注処理を開始します");
    }

    static int calcSubtotal(int quantity, int unitPrice) {
        return quantity * unitPrice;
    }

    static int calcBillingAmount(int quantity, int unitPrice, int shippingFee, int discount) {
        int subtotal = calcSubtotal(quantity, unitPrice);
        return subtotal + shippingFee - discount;
    }

    public static void main(String[] args) {
        printStartMessage();

        int billingAmount = calcBillingAmount(4, 1800, 800, 500);
        System.out.println("請求金額: " + billingAmount);
    }
}