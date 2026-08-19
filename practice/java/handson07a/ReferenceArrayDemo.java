import java.util.Arrays;

public class ReferenceArrayDemo {
    public static void main(String[] args) {
        int[] quantitiesA = {3, 5, 2};
        int[] quantitiesB = quantitiesA;
        quantitiesB[0] = 99;

        String quantitiesAText = Arrays.toString(quantitiesA); // 配列Aを表示用文字列へ変換
        String quantitiesBText = Arrays.toString(quantitiesB); // 配列Bを表示用文字列へ変換
        System.out.println("A: " + quantitiesAText);
        System.out.println("B: " + quantitiesBText);

        int[][] seats = {
                {101, 102, 103},
                {201, 202, 203},
                {301, 302, 303}
        };

        for (int row = 0; row < seats.length; row++) { // seats.lengthは行数
            for (int col = 0; col < seats[row].length; col++) { // seats[row]は現在の行、lengthはその要素数
                System.out.println("row=" + row + ", col=" + col + ", seatNo=" + seats[row][col]);
            }
        }
    }
}