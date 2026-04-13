import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMapDemo {
    public static void main(String[] args) {
        List<String> products = new ArrayList<>();
        products.add("Laptop");
        products.add("Mouse");
        products.add("Keyboard");

        for (String p : products) {
            System.out.println("商品: " + p);
        }

        Map<String, Integer> counts = new HashMap<>();
        counts.put("Laptop", 2);
        counts.put("Mouse", 5);

        System.out.println("Mouse数量: " + counts.get("Mouse"));
    }
}