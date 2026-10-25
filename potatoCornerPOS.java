import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

public class potatoCornerPOS {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        ArrayList<OrderItem> items = new ArrayList<>();
        boolean running = true;

        while (running) {

            clearScreen();
            System.out.println("================================");
            System.out.println("  POTATO CORNER ORDERING SYSTEM ");
            System.out.println("================================");
            System.out.println("1. Order");
            System.out.println("2. View Cart");
            System.out.println("3. Checkout");
            System.out.println("4. Exit System");
            System.out.println("================================");
            System.out.print("Enter your choice: ");

            int choice = inputValidation(input);

            switch (choice) {

                case 1:
                    addItemToCart(input, items);
                    break;

                case 2:
                    input.nextLine();
                    viewCartCheckout render = new viewCartCheckout();
                    render.viewCartLogic(items, input);
                    break;

                case 3:
                    viewCartCheckout newRender = new viewCartCheckout();
                    newRender.receiptGenerator(items, input);
                    break;

                case 4:
                    running = false;
                    break;
                
            }
            
        }
        input.close();
    }

    // basically a method for input validation so para ma catch pag letter or symbol yung input at hindi mag crash
    public static int inputValidation(Scanner input){
        while(!input.hasNextInt()){
            System.out.print("letters are not allowed! enter a number: ");
            input.next();
        }
        return input.nextInt();
    }

    // it clears text
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    // nextline method so it wont immediately goes to menu unless user press something to continue
    public static void pauseUser(String message){
        System.out.print(message);
        Scanner input = new Scanner(System.in); 
        input.nextLine();
    }
    public static class OrderItem{
        private String size;
        private List<String> flavors;
        private int quantity;
        private double unitPrice;
        private double itemTotal;

        // constructor
        public OrderItem(String size, List<String> flavors, int quantity){
            this.size = size;
            this.flavors = flavors;
            this.quantity = quantity;
            this.unitPrice = calculateUnitPrice(size);
            this.itemTotal = this.unitPrice * quantity;
        }

        // size formatter and prices
        private double calculateUnitPrice(String size){
            
            switch(size.toUpperCase()){
                case "REGULAR":
                    return 41.0;
                case "LARGE":
                    return 67.0;
                case "JUMBO":
                    return 97.0;
                case "MEGA":
                    return 127.0;
                case "GIGA":
                    return 198.0;
                case "TERA":
                    return 228.0;
                default:
                    System.out.println("UNKNOWN SIZE!");
            }

            return 0.0;
        }

        // this 3 methods (getItemtotal, getSize, getQuantity) will be used for view cart :)
        public double getItemTotal(){
            return itemTotal;
        }

        public String getSize(){
            return this.size;
        }

        public List<String> getFlavors(){
            return this.flavors;
        }

        public int getQuantity(){
            return this.quantity;
        }
    }
    // this an acutal ordering logic, from choosing size, flavors and quantity, this is where it all happens.
    public static void addItemToCart(Scanner input, ArrayList<OrderItem> items){
        int flavorLimit = 1;
        
        String[] sizes = {"regular", "large", "jumbo", "mega", "giga", "tera"};
        String[] flavors = {"cheese","chili cheese", "barbecue", "sour cream", "chili BBQ", "golden sweet corn", "white cheedar", "wasabi", "truffle"};
        String[] sizePrices = {"41.0", "67.0", "97.0", "127.0", "198.0", "228.0"};

        clearScreen();
        System.out.println("\n====CHOOSE SIZES====" + "\n");
        for(int i = 0; i < sizes.length; i++){
            System.out.println((1 + i) + ". " + sizes[i] + " - P" + sizePrices[i]);
        }
        System.out.println("\n====================");
        System.out.print("\nSelect sizes: ");
        int Choice = inputValidation(input);

        if(Choice < 1 || Choice > sizes.length){
            System.out.println("invalid size selection! returning to menu.");
            return;
        }
        String sizeChoice = sizes[Choice - 1];
        
        String upperSize = sizeChoice.toUpperCase();
        if(upperSize.equals ("JUMBO") || upperSize.equals("MEGA")){
            flavorLimit = 2;
        }
        else if(upperSize.equals("GIGA") || upperSize.equals("TERA")){
            flavorLimit = 3;
        }
        System.out.println("\n-> You choose " + Choice + " . you can pick up to " + flavorLimit + " flavors.");

        List<String> selectedFlavors = new ArrayList<>();
        for(int f = 1; f <= flavorLimit; f++){
            System.out.println("\n--- CHOOSE FLAVOR " + f + " of " + flavorLimit + " ---");
            for(int i = 0; i < flavors.length; i++){
                System.out.println((i + 1) + ". " + flavors[i]);
            }
            System.out.print("Select flavor number: ");
            int flavorSelected = inputValidation(input);

            if(flavorSelected >= 1 && flavorSelected <= flavors.length){
                selectedFlavors.add(flavors[flavorSelected - 1]);
            }
            else{
                System.out.println("invalid flavor!");
                f--;
            }
        }

        System.out.print("Enter how many: ");
        int quantity = inputValidation(input);

        if(quantity <= 0){
            System.out.println("there must be atleast 1 order. setting quantity to 1");
            quantity = 1;
        }
        OrderItem newItem = new OrderItem(sizeChoice, selectedFlavors, quantity);
        items.add(newItem);

        System.out.println("\n Order Success! added to cart: " + quantity + "pcs " + sizeChoice + " fries, " + selectedFlavors);
    }

    public static class viewCartCheckout{
        
        // logic to view your current order
        public void viewCartLogic(ArrayList <OrderItem> print, Scanner input){
            clearScreen();
            
            System.out.println("\n=========== VIEW CART ==========");
            if (print.isEmpty()){
                pauseUser("\nCart is empty!\npress any key to continue");
                return;
            }
            
            
            double grandTotal = 0;

            for (int i = 0; i < print.size(); i++){
                OrderItem item = print.get(i);

                System.out.println("\nOrder #" + (i + 1));
                System.out.println("Size: " + item.getSize());
                System.out.println("Flavors: " + item.getFlavors());
                System.out.println("Quantity: " + item.getQuantity());

                System.out.printf("Subtotal: P%.2f%n",item.getItemTotal());

                grandTotal += item.getItemTotal();

            }

            System.out.println("GRAND TOTAL: " + grandTotal);
            System.out.println("\nPress any key to return or type CLEAR to empty your cart! ");
            System.out.print("Action: ");
            String choice = input.nextLine().trim();
            
            // logic for clearing your orders currently 
            if(choice.equalsIgnoreCase("CLEAR")){
                print.clear();
                clearScreen();
                pauseUser("Cart cleared succesfully! \nEnter any key to return");
            }
            else{
                clearScreen();
                pauseUser("Returning to menu.....");
            }
        }

        public void receiptGenerator(ArrayList<OrderItem> print, Scanner input){
            
            String folderName = "Receipts"; 
            String filename = folderName + "PotatoCornerReceipt.txt";

            if(print.isEmpty()){
                clearScreen();
                System.out.println("=========== CHECKOUT ==========");
                pauseUser("\nEmpty cart! cannot proceed\npress any key to continue: ");
            }

            double grandTotal = 0.0;
            for(int i = 0; i < print.size(); i++){
                grandTotal += print.get(i).getItemTotal();
            }

            //paying logic
            System.out.println("\n=========== CHECKOUT ==========");
            System.out.printf("Grand total to pay: " + grandTotal);

            double cashPaid = 0.0;
            while(cashPaid < grandTotal){
                System.out.print("\nEnter cash amount: ");
                if(input.hasNextDouble()){
                    cashPaid = input.nextDouble();   
                    if(cashPaid < grandTotal){
                        System.out.printf("Insufficent amount! need P%.2f\n", (grandTotal - cashPaid));
                    }
                }
                else{
                    pauseUser("Invalid input! \npress any key to continue: ");
                }
            }

            double change = cashPaid - grandTotal;
            System.out.printf("Total change P%.2f\n", change);

            //this create folder automatically if the folder doesnt exit!
            File folder = new File(folderName);
            if(! folder.exists()){
                folder.mkdir();
            }

            try (PrintWriter save = new PrintWriter(new FileOutputStream(filename))) {
                
                save.println("================================");
                save.println("      POTATO CORNER RECEIPT     ");
                save.println("================================");

                for(int i = 0; i < print.size(); i++){
                    OrderItem order = print.get(i);
                    save.println("ORDER " +(1 + i));
                    save.printf("%dx %s Fries\n", order.getQuantity(), order.getSize());
                    save.println("Flavors: " + String.join(", ", order.getFlavors()));
                    save.printf("Subtotal:  P%.2f\n", order.getItemTotal());
                    save.println("-------------------------------");
                }
                save.println("================================");
                save.printf("GRAND TOTAL :  P%.2f\n", grandTotal);
                save.printf("CASH PAID :  P%.2f\n", cashPaid);
                save.printf("CHANGE :  P%.2f\n", change);
                save.println("================================");
                save.println("     THANK YOU FOR PURCHASE!    ");
                save.println("================================");

                pauseUser("\n receipt save successfully to " + filename +"\n press any key to continue: ");
                print.clear();


            } catch (IOException e) {
                pauseUser("\nerror writing a save \npress any key: " + e.getMessage());
            }
        }

    }
}
