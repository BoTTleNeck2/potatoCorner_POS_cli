import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class potatoCornerPOS {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        usefulMethods use = new usefulMethods();
        ArrayList<OrderItem> items = new ArrayList<>();
        systemLogic render = new systemLogic();
        boolean running = true;

        while (running) {

            use.clearScreen();
            System.out.println("==================================================");
            System.out.println("            POTATO CORNER ORDERING SYSTEM           ");
            System.out.println("==================================================");
            System.out.println("  [1] Order / Add Item");
            System.out.println("  [2] View Cart");
            System.out.println("  [3] Checkout");
            System.out.println("  [4] Exit System");
            System.out.println("==================================================");
            System.out.print("  Enter your choice: ");

            int choice = use.inputValidation(input);

            switch (choice) {

                case 1:
                    render.addItemToCart(input, items);
                    break;

                case 2:
                    input.nextLine();
                    render.viewCartLogic(items, input);
                    break;

                case 3:
                    render.receiptGenerator(items, input);
                    break;

                case 4:
                    running = false;
                    break;
                default:
                    use.clearScreen();
                    input.nextLine();
                    System.out.println("\nInvalid choice! Please select 1-4.");
                    use.pauseUser(input, "Press ENTER to continue...");
                    break;
                
            }
            
        }
        input.close();
    }
    
    public static class usefulMethods{    
        //discount logic
        private double[] applyDiscount(double grandTotal, Scanner input){
            double discountAmount = 0.0;
            double discountPercent = 50.0;
            
            System.out.print("are senior citizen/PWD? (Y/N): ");
            String yesiWantDiscount = input.nextLine();
            
            if(yesiWantDiscount.equalsIgnoreCase("Y")){
                discountAmount = grandTotal * (discountPercent/ 100.0);
                grandTotal -= discountAmount;
                System.out.println("\n50% Discount successfully applied!");
            }
            else{
                discountPercent = 0.0;
                System.out.println("\nno discount applied!");
            }
            System.out.println("--------------------------------------");
            return new double[] { grandTotal, discountAmount , discountPercent };
        }
        
        //payment logic in checkout
        private double paymentProcess(double grandTotal, Scanner input){
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
                    input.nextLine();
                    System.out.println("\ninvalid input! \nplease enter your payment");
                }
                
            }
            return cashPaid;
        }
        //name validator, it prevents empty or symbol when inputting name
        private String usernameValidator(Scanner input){
            String name = "";
            while (true) { 
                System.out.print("Enter customers name: ");
                name = input.nextLine().trim();
                
                if(name.matches("^[a-zA-Z\\s]+$") && !name.isEmpty()){
                    break;
                }
                else{
                    System.out.println("invalid name! please enter only letters!");
                }
            }
            return name;
        }
        
        //catches usernumbers input and validate whether it is 12 digit or not (will be also used in checkout)
        private String numberLimitValidator(Scanner input){
            String PhoneNumber = "";
            while(true){
                System.out.print("Enter contact number (12 digits only): ");
                PhoneNumber = input.nextLine().trim();
    
                if(PhoneNumber.matches("^\\d{12}$")){
                    break;
                }
                else{
                    System.out.println("invalid input! please enter 12 digits number!");
                }
            }
            return PhoneNumber;
        }
        
        // basically a method for input validation so para ma catch pag letter or symbol yung input at hindi mag crash
        public int inputValidation(Scanner input){
            while(!input.hasNextInt()){
                System.out.print("letters are not allowed! enter a number: ");
                input.nextLine();
            }
            return input.nextInt();
        }
        
        // it clears text
        public void clearScreen() {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
        
        // nextline method so it wont immediately goes to menu unless user press something to continue
        public void pauseUser(Scanner input,String message){
            System.out.print(message); 
            input.nextLine();
        }
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
        
        // this 3 methods (getItemtotal, getSize, getQuantity) will be used for systemLogic class :)
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
        
        usefulMethods use = new usefulMethods();

        // this an acutal ordering logic, from choosing size, flavors and quantity, this is where it all happens.
        public void addItemToCart(Scanner input, ArrayList<OrderItem> items){
            
            //arrays for menu
            String[] sizes = {"regular", "large", "jumbo", "mega", "giga", "tera"};
            String[] flavors = {"cheese","chili cheese", "barbecue", "sour cream", "chili BBQ", "golden sweet corn", "white cheedar", "wasabi", "truffle"};
            double[] sizePrices = {41.0, 67.0, 97.0, 127.0, 198.0, 228.0};

            use.clearScreen();
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
            int Choice = use.inputValidation(input);
            input.nextLine();
            if(Choice < 1 || Choice > sizes.length){
                use.pauseUser(input, "\nInvalid size!\nPress any key to return...");
                return;
            }
            String sizeChoice = sizes[Choice - 1];
            
            //sets flavor limit, if regular, large only 1 flavor, 2 flavors for jumbo and mega, 3 flavors for giga and tera
            int flavorLimit = 1;
            String upperSize = sizeChoice.toUpperCase();
            if(upperSize.equals ("JUMBO") || upperSize.equals("MEGA")){
                flavorLimit = 2;
            }
            else if(upperSize.equals("GIGA") || upperSize.equals("TERA")){
                flavorLimit = 3;
            }
            use.clearScreen();
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
                int flavorSelected = use.inputValidation(input);

                if(flavorSelected >= 1 && flavorSelected <= flavors.length){
                    selectedFlavors.add(flavors[flavorSelected - 1]);
                }
                else{
                    System.out.println("invalid flavor!");
                    f--;
                }
            }

            System.out.print("Enter quantity: ");
            int quantity = use.inputValidation(input);

            if(quantity <= 0){
                use.pauseUser(input, "there must be atleast 1 order. setting quantity to 1");
                quantity = 1;
            }
            OrderItem newItem = new OrderItem(sizeChoice, selectedFlavors, quantity);
            items.add(newItem);
            
            use.clearScreen();
            input.nextLine();
            System.out.println("==================================================");
            System.out.println("                  ITEM ADDED!");
            System.out.println("==================================================");
            System.out.printf("  %-12s : %s%n", "Size", Choice);
            System.out.printf("  %-12s : %s%n","Flavors", String.join(", ", selectedFlavors));
            System.out.printf("  %-12s : %d%n", "Quantity", quantity);
            System.out.println("--------------------------------------------------");
            System.out.printf("  %-12s : PHP %,.2f%n","SUBTOTAL", newItem.getItemTotal());
            System.out.println("==================================================");
            use.pauseUser(input, "\nPress any KEY to return to menu...");
        }
        // logic to view your current order
        public void viewCartLogic(ArrayList <OrderItem> items, Scanner input){
            use.clearScreen();
            System.out.println("=========================================================\n");
            if (items.isEmpty()){
                System.out.println("                  YOUR CART IS EMPTY");
                System.out.println("           Add some delicious fries first!\n");
                System.out.println("=========================================================");
                use.pauseUser(input, "press any key to continue: ");
                return;
            }
        
            double grandTotal = 0.0;
            int totalQuantity = 0;
            use.clearScreen();
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
             
            // logic for clearing your current orders 
            if(choice.equalsIgnoreCase("Y")){
                System.out.print("Are you sure you want to clear the cart? Y/N: ");
                String confirm = input.nextLine();

                if(confirm.equalsIgnoreCase("Y")){
                    items.clear();
                    use.clearScreen();
                    System.out.println("All items have been removed from your cart.");
                }
                else{
                    use.clearScreen();
                    System.out.println("\nCancelled");
                }
                use.pauseUser(input, "\npress any key to continue: ");
            }
            else{
                return;
            }
        }
        //loads saved text receipt after purchasing
        public void loadFile(String filename,Scanner input){
            use.clearScreen();
            File folder = new File("output");

            //checks if theres a file or not
            if (!folder.exists() || folder.listFiles() == null || folder.listFiles().length == 0) {
                use.clearScreen();
                use.pauseUser(input, "No saved receipts found yet!\nPress ENTER to return...");
                return;
            }

            // Read and display it directly on the terminal
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {
                String line;
                use.clearScreen();
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                use.pauseUser(input, "\nReceipt printed successfully!\nPress any KEY to return to menu...");

            }catch (IOException e) {
                use.pauseUser(input, "\nError reading file: " + e.getMessage() + "\nPress ENTER to return...");
            }
        }
        //logic to generate receipt
        public void receiptGenerator(ArrayList<OrderItem> items, Scanner input){
            
            //ito yung gumagawa ng order number para sa receipt
            Random orderTicket = new Random(); 
            int randomNumber = orderTicket.nextInt(999);
            
            //eto naman is for date and time ng inorder sa receipt
            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
            String currentTime = time.format(formatter);
            
            input.nextLine();
            use.clearScreen();
            File folder = new File("output");

            // Check if the folder exists, and create it if it doesn't
            if (!folder.exists()) {
                folder.mkdirs(); 
            }
            String filename = "output/PotatoCornerReceipt.txt";

            System.out.println("==================================================");
            System.out.println("                   CHECKOUT                       ");
            System.out.println("==================================================\n");
            if(items.isEmpty()){
                
                use.pauseUser(input, "your cart is empty!\nPlease add items before checkout.");
                return;
            }
            
            //calls the validator methods from helpful method class
            String userName = use.usernameValidator(input);
            String number = use.numberLimitValidator(input);
            
            //sets a starting number for grandTotal and to calculate for receipt
            double grandTotal = 0.0;
            for(int i = 0; i < items.size(); i++){
                grandTotal += items.get(i).getItemTotal();
            }
            
            //calls the discount logic from helpful method class
            double[] discount = use.applyDiscount(grandTotal, input);
            grandTotal = discount[0];
            double discountAmount = discount[1];
            double discountPercent = discount[2];

            System.out.printf("\nGrand total to pay: " + grandTotal);
            double cashPaid = use.paymentProcess(grandTotal, input);
            double change = cashPaid - grandTotal;
            
            //itong format mismo prini print into text file
            try (PrintWriter save = new PrintWriter(new FileOutputStream(filename))) {
                
                save.println("==================================================");
                save.println("                 POTATO CORNER ");
                save.println("        WORLD'S BEST FLAVORED FRENCH FRIES\n");
                save.println("               ORDER NUMBER #" + randomNumber );
                save.println("==================================================");
                save.println("            "+currentTime);
                save.println("CUSTOMER NAME : " + userName);
                save.println("CUSTOMER NUMBER : " + number);
                save.println( "--------------------------------------------------");
                save.println("            PLEASE GIVE THIS RECEIPT");
                save.println("          TO A NEARBY VENDOR FOR ORDER");

                for(int i = 0; i < items.size(); i++){
                    OrderItem order = items.get(i);
                    save.println();
                    save.println("ORDER #" + (i + 1));
                    save.printf("%d x %s Fries%n",order.getQuantity(), order.getSize());
                    save.println("Flavors: "+ String.join(", ", order.getFlavors()));
                    save.printf("Subtotal: PHP %,.2f%n", order.getItemTotal());
                    save.println( "--------------------------------------------------");
                }
                if(discountAmount > 0){
                    save.printf("DISCOUNT (%.0f%%) : -PHP %,.2f%n", discountPercent, discountAmount);
                }
                save.printf("GRAND TOTAL : PHP %,.2f%n", grandTotal);
                save.printf("CASH PAID   : PHP %,.2f%n", cashPaid);
                save.printf("CHANGE      : PHP %,.2f%n", change);

                save.println("==================================================");
                save.println("          THANK YOU FOR YOUR PURCHASE!");
                save.println("==================================================");
                
                use.clearScreen();
                input.nextLine();
                System.out.println("==================================================");
                System.out.println("                PAYMENT SUCCESSFUL");
                System.out.println("==================================================");
                System.out.printf("  Grand Total : PHP %,.2f%n", grandTotal);
                System.out.printf("  Cash Paid   : PHP %,.2f%n", cashPaid);
                System.out.printf("  Change      : PHP %,.2f%n", change);
                System.out.println("==================================================");
                use.pauseUser(input, "\n receipt save successfully to " + filename + "\n Press any key to continue: ");
            } catch (IOException e) {
                use.pauseUser(input, "\nerror writing a save \npress any key: " + e.getMessage());
            }
            loadFile(filename,input);
            // it clears ordered items after payment and printing the receipt so user order new
            items.clear();
        }
    }
}
