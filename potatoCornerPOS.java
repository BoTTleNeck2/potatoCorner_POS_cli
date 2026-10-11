import java.util.Scanner;

public class potatoCornerPOS {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        int choice = 0;

        while (choice != 4) {

            System.out.println("================================");
            System.out.println("  POTATO CORNER ORDERING SYSTEM ");
            System.out.println("================================");
            System.out.println("1. Order/Add Item");
            System.out.println("2. View Cart");
            System.out.println("3. Checkout");
            System.out.println("4. Exit System");
            System.out.println("================================");
            System.out.print("Enter your choice: ");

            choice = input.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("Under Construction");
                    break;

                case 2:
                    System.out.println("Under Construction");
                    break;

                case 3:
                   System.out.println("Under Construction");
                    break;

                case 4:
                    System.exit(0);
                    break;
                
            }
            
        }

        input.close();
    }
}
