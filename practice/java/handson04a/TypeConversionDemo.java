public class TypeConversionDemo {
    public static void main(String[] args) {
        String priceText = "2500";
        int price = Integer.parseInt(priceText); // String -> int

        double taxRate = 0.125;
        double taxedPrice = price * (1 + taxRate); // int と double の演算

        int billingAmount = (int) taxedPrice; // double -> int
        String billingText = String.valueOf(billingAmount); // int -> String

        System.out.println("変換前の価格: " + priceText);
        System.out.println("税込金額(double): " + taxedPrice);
        System.out.println("請求金額(int): " + billingAmount);
        System.out.println("請求金額(String): " + billingText);
    }
}