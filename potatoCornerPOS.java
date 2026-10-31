import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class potatoCornerPOS {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        ArrayList<OrderItem> items = new ArrayList<>();
        boolean running = true;

        while (running) {

            clearScreen();
            System.out.println("==================================================");
            System.out.println("            POTATO CORN ORDERING SYSTEM           ");
            System.out.println("==================================================");
            System.out.println("  [1] Order / Add Item");
            System.out.println("  [2] View Cart");
            System.out.println("  [3] Checkout");
            System.out.println("  [4] Exit System");
            System.out.println("==================================================");
            System.out.print("  Enter your choice: ");

            int choice = inputValidation(input);

            switch (choice) {

                case 1:
                    systemLogic render = new systemLogic();
                    render.addItemToCart(input, items);
                    break;

                case 2:
                    input.nextLine();
                    systemLogic newRender = new systemLogic();
                    newRender.viewCartLogic(items, input);
                    break;

                case 3:
                    systemLogic neuRender = new systemLogic();
                    neuRender.receiptGenerator(items, input);
                    break;

                case 4:
                    running = false;
                    break;
                default:
                    input.nextLine();
                    System.out.println("\nInvalid choice! Please select 1-4.");
                    pauseUser(input, "Press ENTER to continue...");
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
    public static void pauseUser(Scanner input,String message){
        System.out.print(message); 
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
    
    public static class systemLogic{
        
        // this an acutal ordering logic, from choosing size, flavors and quantity, this is where it all happens.
        public void addItemToCart(Scanner input, ArrayList<OrderItem> items){
    
        String[] sizes = {"regular", "large", "jumbo", "mega", "giga", "tera"};
        String[] flavors = {"cheese","chili cheese", "barbecue", "sour cream", "chili BBQ", "golden sweet corn", "white cheedar", "wasabi", "truffle"};
        double[] sizePrices = {41.0, 67.0, 97.0, 127.0, 198.0, 228.0};

        clearScreen();
        System.out.println("==================================================");
        System.out.println("                ORDER POTATO FRIES                ");
        System.out.println("==================================================");
        System.out.printf("%-5s %-20s %12s%n","NO.", "SIZE", "PRICE");
        System.out.println("--------------------------------------------------");
        for(int i = 0; i < sizes.length; i++){
            System.out.printf("%-5d %-20s PHP %8.2f%n",i + 1, sizes[i], sizePrices[i]);
        }
        System.out.println("==================================================");
        System.out.print("Select sizes: ");
        int Choice = inputValidation(input);
        input.nextLine();
        if(Choice < 1 || Choice > sizes.length){
            pauseUser(input, "\nInvalid size!\nPress any key to return...");
            return;
        }
        String sizeChoice = sizes[Choice - 1];
        
        int flavorLimit = 1;
        String upperSize = sizeChoice.toUpperCase();
        if(upperSize.equals ("JUMBO") || upperSize.equals("MEGA")){
            flavorLimit = 2;
        }
        else if(upperSize.equals("GIGA") || upperSize.equals("TERA")){
            flavorLimit = 3;
        }
        clearScreen();
        System.out.println("Selected Size : " + Choice);
        System.out.printf("Price         : PHP %,.2f%n", sizePrices[Choice - 1]);
        System.out.println("Flavor Limit  : " + flavorLimit);

        List<String> selectedFlavors = new ArrayList<>();
        for(int f = 1; f <= flavorLimit; f++){
            System.out.println("--------------------------------------------------");
            System.out.println(" CHOOSE FLAVOR " + f + " OF " + flavorLimit           );
            System.out.println("--------------------------------------------------");
            for(int i = 0; i < flavors.length; i++){
                System.out.printf("  [%d] %s%n", i + 1, flavors[i]);
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
            pauseUser(input, "there must be atleast 1 order. setting quantity to 1");
            quantity = 1;
        }
        OrderItem newItem = new OrderItem(sizeChoice, selectedFlavors, quantity);
        items.add(newItem);

        System.out.println("==================================================");
        System.out.println("                  ITEM ADDED!");
        System.out.println("==================================================");
        System.out.printf("  %-12s : %s%n", "Size", Choice);
        System.out.printf("  %-12s : %s%n","Flavors", String.join(", ", selectedFlavors));
        System.out.printf("  %-12s : %d%n", "Quantity", quantity);
        System.out.println("--------------------------------------------------");
        System.out.printf("  %-12s : PHP %,.2f%n","SUBTOTAL", newItem.getItemTotal());
        System.out.println("==================================================");
        pauseUser(input, "\nPress ENTER to return to menu...");
    }
        
        
        // logic to view your current order
        public void viewCartLogic(ArrayList <OrderItem> items, Scanner input){
            clearScreen();
            System.out.println("=========================================================\n");
            if (items.isEmpty()){
                System.out.println("                  YOUR CART IS EMPTY");
                System.out.println("           Add some delicious fries first!\n");
                System.out.println("=========================================================");
                pauseUser(input, "press any key to continue: ");
                return;
            }
        
            double grandTotal = 0.0;
            int totalQuantity = 0;
            clearScreen();
            System.out.println("================================================================");
            System.out.println("                      YOUR CURRENT ORDERS");

            for (int i = 0; i < items.size(); i++){
                OrderItem item = items.get(i);
                System.out.println("  -----------------------------------------------------------");
                System.out.printf("  ORDER #%02d%n", (i + 1));
                System.out.printf("  %-16s : %s%n","Size", item.getSize());
                System.out.printf("  %-16s : %d pack(s)%n","Quantity", item.getQuantity());
                System.out.printf("  %-16s : %s%n","Flavors", String.join(", ", item.getFlavors()));
                System.out.printf("  %-16s : PHP %,.2f%n","Subtotal", item.getItemTotal());
                grandTotal += item.getItemTotal();
                totalQuantity += item.getQuantity();

            }

            System.out.println("================================================================");
            System.out.println("                         CART SUMMARY\n");
            System.out.printf("  Total Order Entries                  : %d%n",items.size());
            System.out.printf("  Total Packs                          : %d%n",totalQuantity);
            System.out.println("----------------------------------------------------------------");
            System.out.printf("  GRAND TOTAL                          : PHP %,.2f%n",grandTotal);
            System.out.println("================================================================");
            System.out.println();
            System.out.println("  CART OPTIONS");
            System.out.println("  press ANY KEY Return to Main Menu");
            System.out.println("  press Y Remove All Items from Cart");
            System.out.println("================================================================");
            System.out.print("  Enter action: ");
            String choice = input.nextLine().trim();
            
            // logic for clearing your orders currently 
            if(choice.equalsIgnoreCase("Y")){
                clearScreen();
                System.out.println("Are you sure you want to clear the cart? Y/N: ");
                String confirm = input.nextLine();

                if(confirm.equalsIgnoreCase("Y")){
                    items.clear();
                    clearScreen();
                    System.out.println("  All items have been removed from your cart.");
                }
                else{
                    clearScreen();
                    System.out.println("\nCancelled");
                }
                pauseUser(input, "press any key to continue: ");
            }
            else{
                return;
            }
        }
        
        public void receiptGenerator(ArrayList<OrderItem> items, Scanner input){
            
            //ito yung gumagawa ng order number
            Random orderTicket = new Random(); 
            int randomNumber = orderTicket.nextInt(999);
            
            input.nextLine();
            clearScreen();
            String filename = "PotatoCornerReceipt.txt";
            System.out.println("==================================================");
            System.out.println("                   CHECKOUT                       ");
            System.out.println("==================================================\n");
            if(items.isEmpty()){
                
                pauseUser(input, "your cart is empty!\nPlease add items before checkout.");
                return;
            }
            
            System.out.print("Enter customers name: ");
            String name = input.nextLine();
            
            System.out.print("Enter customers number: ");
            int number = input.nextInt();
            
            double grandTotal = 0.0;
            for(int i = 0; i < items.size(); i++){
                grandTotal += items.get(i).getItemTotal();
            }

            //paying logic
            System.out.printf("Grand total to pay: " + grandTotal);

            double cashPaid = 0.0;
            while(cashPaid < grandTotal){
                System.out.print("\nEnter cash amount: ");
                if(input.hasNextDouble()){
                    cashPaid = input.nextDouble();   
                    if(cashPaid < grandTotal){
                        System.out.printf( "Insufficent amount! need P%.2f", (grandTotal - cashPaid));
                    }
                }
                else{
                    pauseUser(input, "invalid input!\npress any key to continue: ");
                }
                
            }
            double change = cashPaid - grandTotal;
            
            //itong format mismo prini print into text file
            try (PrintWriter save = new PrintWriter(new FileOutputStream(filename))) {
                
                save.println("==================================================");
                save.println("               ORDER NUMBER #" + randomNumber );
                save.println("==================================================");
                save.println("CUSTOMER NAME : " + name);
                save.println("CUSTOMER NUMBER : " + number);
                save.println( "--------------------------------------------------");
                save.println("            PLEASE GIVE THIS RECEIPT");
                save.println("       TO A NEARBY CASHIER FOR YOUR ORDER");

                for(int i = 0; i < items.size(); i++){
                    OrderItem order = items.get(i);
                    save.println();
                    save.println("ORDER #" + (i + 1));
                    save.printf("%d x %s Fries%n",order.getQuantity(), order.getSize());
                    save.println("Flavors: "+ String.join(", ", order.getFlavors()));
                    save.printf("Subtotal: PHP %,.2f%n", order.getItemTotal());
                    save.println( "--------------------------------------------------");
                }
                save.printf("GRAND TOTAL : PHP %,.2f%n", grandTotal);
                save.printf("CASH PAID   : PHP %,.2f%n", cashPaid);
                save.printf("CHANGE      : PHP %,.2f%n", change);

                save.println("==================================================");
                save.println("          THANK YOU FOR YOUR PURCHASE!");
                save.println("==================================================");
                
                clearScreen();
                input.nextLine();
                System.out.println("==================================================");
                System.out.println("                PAYMENT SUCCESSFUL");
                System.out.println("==================================================");
                System.out.printf("  Grand Total : PHP %,.2f%n", grandTotal);
                System.out.printf("  Cash Paid   : PHP %,.2f%n", cashPaid);
                System.out.printf("  Change      : PHP %,.2f%n", change);
                System.out.println("==================================================");
                pauseUser(input, "\n receipt save successfully to " + filename + "\n Press any key to continue: ");
                items.clear();


            } catch (IOException e) {
                pauseUser(input, "\nerror writing a save \npress any key: " + e.getMessage());
            }
        }

    }
}
