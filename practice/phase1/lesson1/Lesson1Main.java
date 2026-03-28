class User {
    String employeeCode;
    String name;
    String department;

    String displayName() {
        return "[" + employeeCode + "]" + " " + name + " - " + department;
    }
}

public class Lesson1Main {
    public static void main(String[] args) {
        User user = new User();
        user.employeeCode = "U001";
        user.name = "Yamada";
        user.department = "Sales";

        String text = user.displayName();
        System.out.println(text);

        User user2 = new User();
        user2.employeeCode = "U002";
        user2.name = "Suzuki";
        user2.department = "HR";

        String text2 = user2.displayName();
        System.out.println(text2);
    }
}