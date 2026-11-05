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
        //payment logic in checkout
        private Object[] paymentProcess(double grandTotal, Scanner input){
            double cashPaid = 0.0;
            String paymentMethod = "";
            while(true){
                System.out.println(" SELECT PAYMENT METHOD     ");
                System.out.println("------------------------------------");
                System.out.println("  [1] Cash");
                System.out.println("  [2] QR ph");
                System.out.println("  [3] Credit / Debit Card");
                System.out.println("====================================");
                System.out.print("  Enter payment choice: ");
                
                int payChoice = inputValidation(input);
                input.nextLine();
                
                if(payChoice == 1){
                    paymentMethod = "Cash";
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
                    input.nextLine();
                    break;
                }
            
                else if(payChoice == 2){
                    paymentMethod = "QR Ph";
                    System.out.println("\n------------------------------------");
                    System.out.println("            QR PH PAYMENT           ");
                    System.out.println("------------------------------------");
                    // ASCII Art Mockup for a QR Code Terminal Display
                    System.out.println("      ╔══════════════════════╗      ");
                    System.out.println("      ║  ███████  ▄█  ███████║      ");
                    System.out.println("      ║  █ ▄▄▄ █  █▀█  █ ▄▄▄ █║      ");
                    System.out.println("      ║  █ █▄█ █  ▀▀▄  █ █▄█ █║      ");
                    System.out.println("      ║  ▀▀▀▀▀▀▀  ▀ ▀  ▀▀▀▀▀▀▀║      ");
                    System.out.println("      ║    [!] SCAN ME !      ║      ");
                    System.out.println("      ╚══════════════════════╝      ");
                    System.out.println("------------------------------------");
                    System.out.println("Supported: Gcash, Maya, InstaPay, etc.");
                    
                    // Validate 10-digit transaction reference number from their banking app
                    String qrRef = customValidator(input, "Enter 10-digit Transfer Ref No: ", "^\\d{10}$", "Invalid Reference! Must be a 10-digit transaction code.");
                    
                    processPaymentAnimation("Validating QR Ph transfer...");
                    
                    cashPaid = grandTotal; 
                    clearScreen();
                    System.out.println("====================================");
                    System.out.println("        QR PH PAYMENT SUMMARY       ");
                    System.out.println("====================================");
                    System.out.printf("  Amount    : PHP %,.2f%n", grandTotal);
                    System.out.printf("  Ref Code  : QRPH-%s%n", qrRef);
                    System.out.println("====================================");
                    pauseUser(input,"\nPress any key to continue to receipt...");
                    break;
                }
                else if(payChoice == 3){
                    paymentMethod = "Credit/Debit Card";
                    System.out.println("\n------------------------------------");
                    System.out.println("         CARD TERMINAL (POS)        ");
                    System.out.println("------------------------------------");
                    System.out.println("[!] Insert, Swipe, or Tap Card...");
                    
                    // Validate simulated 16-digit card number (or last 4 digits for simplicity, e.g., 4 digits)
                    String cardLast4 = customValidator(input, "Enter Last 4 Digits of Card: ", "^\\d{4}$", "Invalid! Must enter exactly the last 4 digits.");
                    
                    // Validate 4-digit Card PIN / Authorization
                    String cardPin = customValidator(input, "Enter 4-digit Card PIN: ", "^\\d{4}$", "Invalid PIN! Must be 4 digits.");
                    
                    processPaymentAnimation("Connecting to bank network & authorizing...");
                    
                    cashPaid = grandTotal; 
                    clearScreen();
                    System.out.println("====================================");
                    System.out.println("        CARD PAYMENT SUMMARY        ");
                    System.out.println("====================================");
                    System.out.printf("  Card Used : ****-****-****-%s%n", cardLast4);
                    System.out.printf("  Amount    : PHP %,.2f%n", grandTotal);
                    System.out.println("  Approval  : APPR-" + (int)(Math.random() * 900000 + 100000));
                    System.out.println("====================================");
                    pauseUser(input,"\nPress any key to continue to receipt...");
                    break;
                }
                else {
                    System.out.println("Invalid payment option! Please choose 1 to 4.");
                }
            }
            return new Object[] { cashPaid, paymentMethod };
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
        //flexible number limit validator
        private String customValidator(Scanner input, String prompt, String regex, String errorMessage){
            String result = "";
            while(true){
                System.out.print(prompt);
                result = input.nextLine().trim();
                if(result.matches(regex)){
                    break;
                } else {
                    System.out.println(errorMessage);
                }
            }
            return result;
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
        //animation for payment process
        private void processPaymentAnimation(String message) {
            System.out.println("\n" + message);
            try {
                Thread.sleep(1500); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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
                    return 45.0;
                case "LARGE":
                    return 70.0;
                case "JUMBO":
                    return 99.0;
                case "MEGA":
                    return 130.0;
                case "GIGA":
                    return 205.0;
                case "TERA":
                    return 235.0;
                    
                // Loopys
                case "LOOPYS LARGE":
                    return 75.0;
                case "LOOPYS MEGA":
                    return 135.0;
                    
                // Crunchy Chicken Pops
                case "CHICKEN POPS REGULAR":
                    return 79.0;
                case "CHICKEN POPS LARGE":
                    return 99.0;
                case "CHICKEN POPS MEGA":
                    return 199.0;
                    
                // Chicken Fries
                case "CHICKEN FRIES LARGE":
                    return 99.0;
                case "CHICKEN FRIES MEGA":
                    return 199.0;
                    
                // Mix & Max Combos
                case "LARGE MIX":
                    return 99.0;
                case "MEGA MIX":
                    return 149.0;
                case "TERA MIX":
                    return 259.0;
                //loaded fries
                case "LOADED FRIES(UMAMI BBQ)":
                case "LOADED FRIES(CHICKEN HAM O' CHEESE)":
                case "LOADED FRIES(TRUFFLE PARMESAN)":
                    return 159.0;
                
                //friescream
                case "FRIESCREAM(REGULAR)":
                    return 89.0;
                case "FRIESCREAM(LARGE)":
                    return 159.0;
                    
                // Drinks & Coolers
                case "BOTTLED WATER":
                    return 25.0;
                case "SODA - COKE":
                case "SODA - SPRITE":
                    return 55.0;
                case "POP COOLERS":
                    return 65.0;
                    
                // Add-ons & Extras
                case "EXTRA FLAVOR POWDER":
                    return 15.0;
                case "CHEESE DIP":
                    return 25.0;
                case "EXTRA SAUCE":
                    return 10.0;
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
    
            // 1. Category Selection
            use.clearScreen();
            System.out.println("==================================================");
            System.out.println("                  SELECT CATEGORY                 ");
            System.out.println("==================================================");
            System.out.println("  [1] Flavored Fries");
            System.out.println("  [2] Loopys");
            System.out.println("  [3] Crunchy Chicken Pops");
            System.out.println("  [4] Chicken Fries");
            System.out.println("  [5] Mix & Max Combos");
            System.out.println("  [6] Loaded Fries");
            System.out.println("  [7] Friescream");
            System.out.println("  [8] Drinks");
            System.out.println("  [9] Add-ons & Extras");
            System.out.println("  [0] Return to Menu");
            System.out.println("==================================================");
            System.out.print("  Enter category choice: ");
            int category = use.inputValidation(input);
            input.nextLine(); // Clear buffer

            // Common Flavors array used across food items
            String[] flavors = {"Cheese", "Chili Cheese", "Barbecue", "Sour Cream", "Chili BBQ", "Golden Sweet Corn", "White Cheddar", "Wasabi", "Truffle"};
            String[] itemsList = null;
            double[] prices = null;
            String categoryName = "";
            String selectedItemName = "";
            double selectedPrice = 0.0;
            boolean requiresFlavors = true;

            //setup items
            switch(category) {
                case 1:
                    categoryName = "Flavored Fries";
                    itemsList = new String[]{"Regular", "Large", "Jumbo", "Mega", "Giga", "Tera"};
                    prices = new double[]{45.0, 70.0, 99.0, 130.0, 205.0, 235.0};
                    break;
                case 2:
                    categoryName = "Loopys";
                    itemsList = new String[]{"Loopys Large", "Loopys Mega"};
                    prices = new double[]{75.0, 135.0};
                    break;
                case 3:
                    categoryName = "Crunchy Chicken Pops";
                    itemsList = new String[]{"Chicken Pops Regular", "Chicken Pops Large", "Chicken Pops Mega"};
                    prices = new double[]{79.0, 99.0, 199.0};
                    break;
                case 4:
                    categoryName = "Chicken Fries / free korean soy sauce";
                    itemsList = new String[]{"Chicken Fries Large", "Chicken Fries Mega"};
                    prices = new double[]{99.0, 199.0};
                    break;
                case 5:
                    categoryName = "Mix & Max Combos";
                    itemsList = new String[]{"Large Mix", "Mega Mix", "Tera Mix"};
                    prices = new double[]{99.0, 149.0, 259.0};
                    break;
                case 6:
                    categoryName = "LOADED FRIES FLAVOR";
                    itemsList = new String[]{"LOADED FRIES(UMAMI BBQ)","LOADED FRIES(CHICKEN HAM O' CHEESE)", "LOADED FRIES(TRUFFLE PARMESAN)" };
                    prices = new double[]{159.0, 159.0, 159.0};
                    requiresFlavors = false;
                    break;
                case 7:
                    categoryName = "Friescream Sizes";
                    itemsList = new String[]{"Friescream(Regular)", "Friescream(Large)"};
                    prices = new double[]{89.0, 159.0};
                    requiresFlavors = false;
                    break;
                case 8:
                    categoryName = "Drinks & Coolers";
                    use.clearScreen();
                    System.out.println("==================================================");
                    System.out.println("                 DRINKS & COOLERS                 ");
                    System.out.println("==================================================");
                    System.out.println("  [1] Soda - Coke");
                    System.out.println("  [2] Soda - Sprite");
                    System.out.println("  [3] Pop Coolers");
                    System.out.println("  [4] Bottled Water");
                    System.out.println("==================================================");
                    System.out.print("  Select drink number: ");
                    int drinkChoice = use.inputValidation(input);
                    input.nextLine();

                    if (drinkChoice == 1) {
                        itemsList = new String[]{"Soda - Coke"};
                        prices = new double[]{55.0};
                    } 
                    else if (drinkChoice == 2) {
                        itemsList = new String[]{"Soda - Sprite"};
                        prices = new double[]{55.0};
                    } 
                    else if (drinkChoice == 3 || drinkChoice == 3) { // handles Pop Coolers or direct choice
                        itemsList = new String[]{"Pop Coolers"};
                        prices = new double[]{65.0};
                    } 
                    else if (drinkChoice == 4) {
                        itemsList = new String[]{"Bottled Water"};
                        prices = new double[]{25.0};
                    } 
                    else {
                        use.pauseUser(input, "\nInvalid selection!\nPress any key to return...");
                        return;
                    }
                    requiresFlavors = false; // Drinks dont need flavor loops
                    break;
                case 9:
                    categoryName = "Add-ons & Extras";
                    itemsList = new String[]{"Extra Flavor Powder", "Cheese Dip", "Extra Sauce"};
                    prices = new double[]{15.0, 25.0, 10.0};
                    requiresFlavors = false; // Add-ons don't need flavor loops
                    break;
                case 0:
                    return;
                default:
                    use.pauseUser(input, "\nInvalid category!\nPress any key to return...");
                    return;
            }

            //DISPLAY MENU FOR CHOSEN CATEGORY
            use.clearScreen();
            System.out.println("============================================================");
            System.out.println("               >> " + categoryName.toUpperCase() + " <<               ");
            System.out.println("============================================================");
            System.out.printf("%-5s %-35s %15s%n", "NO.", "ITEM / SIZE", "PRICE");
            System.out.println("------------------------------------------------------------");
            for(int i = 0; i < itemsList.length; i++){
                System.out.printf("%-5d %-35s PHP %10.2f%n", i + 1, itemsList[i], prices[i]);
            }
            System.out.println("============================================================");
            System.out.println("Type 0 to go back menu");
            System.out.print("Select item number: ");
            int choice = use.inputValidation(input);
            input.nextLine();
            
            if(choice == 0){
                return;
            }
            if(choice < 1 || choice > itemsList.length){
                use.pauseUser(input, "\nInvalid selection!\nPress any key to return...");
                return;
            }
            selectedItemName = itemsList[choice - 1];
            selectedPrice = prices[choice - 1];

            // DETERMINE FLAVOR LIMIT
            int flavorLimit = 1;
            String upperName = selectedItemName.toUpperCase();
            
            // Set limits based on size name (Mega/Giga/Mega Mix = 2 flavors, Tera/Tera Mix = 3 flavors)
            if(upperName.contains("MEGA") || upperName.contains("GIGA") || upperName.contains("MEGA MIX")){
                flavorLimit = 2;
            }
            else if(upperName.contains("TERA") || upperName.contains("TERA MIX")){
                flavorLimit = 3;
            }
            // Drinks override
            if(!requiresFlavors){
                flavorLimit = 0;
            }

            // --- GET QUANTITY FIRST ---
            System.out.print("Enter quantity: ");
            int quantity = use.inputValidation(input);
            input.nextLine();

            if(quantity <= 0){
                use.pauseUser(input, "\nOrder cancelled! Returning to menu...\nPress any key to continue...");
                return; // Exits the method and safely returns to the main menu
            }

            double totalAddedPrice = 0.0;

            // LOOP THROUGH EACH PACK FOR INDEPENDENT FLAVOR CONFIGURATION
            for(int i = 1; i <= quantity; i++){
                List<String> selectedFlavors = new ArrayList<>();

                if(requiresFlavors){
                    for (int f = 1; f <= flavorLimit; f++) {
                        use.clearScreen();
                        System.out.println("==================================================");
                        System.out.println("   CONFIGURING " + selectedItemName + " (Pack " + i + "/" + quantity + ")   ");
                        System.out.println("==================================================");
                        System.out.println("Flavor Limit : Choose " + flavorLimit + " flavor(s) total");
                        System.out.println("--------------------------------------------------");
                        System.out.println(" CHOOSE FLAVOR " + f + " OF " + flavorLimit          );
                        System.out.println("--------------------------------------------------");
                        for(int j = 0; j < flavors.length; j++){
                            System.out.printf("  [%d] %s%n", j + 1, flavors[j]);
                        }
                        System.out.print("Select flavor number: ");
                        int flavorSelected = use.inputValidation(input);
                        input.nextLine();
                        
                        if(flavorSelected >= 1 && flavorSelected <= flavors.length){
                            selectedFlavors.add(flavors[flavorSelected - 1]);
                        }
                        else{
                            System.out.println("Invalid flavor!");
                            f--; // Retry this flavor slot
                        }
                    }
                } else {
                    selectedFlavors.add("N/A");
                }

                // Create independent item with quantity = 1 for this individual pack
                OrderItem newItem = new OrderItem(selectedItemName, selectedFlavors, 1);
                items.add(newItem);
                totalAddedPrice += newItem.getItemTotal();
            }
            use.clearScreen();
            System.out.println("==================================================");
            System.out.println("                    ITEM ADDED!                   ");
            System.out.println("==================================================");
            System.out.printf("  %-12s : %s%n", "Item", selectedItemName);
            System.out.printf("  %-12s : %d%n", "Quantity", quantity);
            System.out.println("--------------------------------------------------");
            System.out.printf("  %-12s : PHP %,.2f%n","TOTAL ADDED", totalAddedPrice);
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
            
            Random orderTicket = new Random(); 
            int randomNumber = orderTicket.nextInt(999);
            
            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
            String currentTime = time.format(formatter);
            
            input.nextLine();
            use.clearScreen();
            File folder = new File("output");

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
            
            String userName = use.usernameValidator(input);
            String number = use.customValidator(input, "Enter contact number (11 digits): ", "^\\d{11}$", "Invalid input! Please enter 11 digits.");
            
            // PWD Check
            boolean isPWD = false;
            String pwdIdNumber = "";
            
            while(true){
                System.out.print("Are you a PWD? (Y/N): ");
                String pwdInput = input.nextLine().trim();
                
                if(pwdInput.equalsIgnoreCase("Y")){
                    isPWD = true;
                    // Enforces DOH PSGC 17-digit layout format: RR-PPMM-BBB-NNNNNNN (e.g., 03-5402-013-0000001)
                    pwdIdNumber = use.customValidator(input, 
                        "Enter DOH PSGC PWD ID (Format XX-XXXX-XXX-XXXXXXX): ", 
                        "^\\d{2}-\\d{4}-\\d{3}-\\d{7}$", 
                        "Invalid format! Must follow the 17-digit DOH PSGC layout (e.g., 03-5402-013-0000001).");
                    
                    use.clearScreen();
                    System.out.println("\n20% PWD Discount applied successfully!");
                    break;
                } 
                else if(pwdInput.equalsIgnoreCase("N")){
                    isPWD = false;
                    use.clearScreen();
                    System.out.println("\nNo discount applied.");
                    break;
                } 
                else {
                    System.out.println("Invalid input! Please enter only 'Y' or `N`.");
                }
            }
            System.out.println("--------------------------------------");

            // Calculate totals with per-item discount and VAT
            double subtotalSum = 0.0;
            double totalDiscountAmount = 0.0;
            
            // Temporary storage to hold discounted item totals for the receipt
            double[] itemFinalTotals = new double[items.size()];
            double[] itemDiscounts = new double[items.size()];

            for(int i = 0; i < items.size(); i++){
                OrderItem order = items.get(i);
                double originalItemTotal = order.getItemTotal();
                subtotalSum += originalItemTotal;
                
                double itemDiscount = 0.0;
                if(isPWD){
                    itemDiscount = originalItemTotal * 0.20; // 20% discount per order item
                }
                
                itemDiscounts[i] = itemDiscount;
                itemFinalTotals[i] = originalItemTotal - itemDiscount;
                totalDiscountAmount += itemDiscount;
            }

            double grandTotal = subtotalSum - totalDiscountAmount;
            
            //12% VAT computation (PWDs are legally VAT-exempt under RA 10754)
            double vatAmount = isPWD ? 0.0 : (grandTotal * 0.12);
            System.out.println("====================================");
            System.out.println("          CHECKOUT SUMMARY          ");
            System.out.println("====================================");
            System.out.printf("  Subtotal    : PHP %,.2f%n", subtotalSum);
            if(totalDiscountAmount > 0) {
                System.out.printf("  PWD Disc    : -PHP %,.2f%n", totalDiscountAmount);
            }
            if (isPWD) {
                System.out.println("  VAT (12%)   : PHP 0.00 (VAT EXEMPT)");
            } else {
                System.out.printf("  VAT (12%%)   : PHP %,.2f%n", vatAmount);
            }
            System.out.println("------------------------------------");
            System.out.printf("  GRAND TOTAL : PHP %,.2f%n", grandTotal);
            System.out.println("====================================");
            
            Object[] paymentResult = use.paymentProcess(grandTotal, input);
            double cashPaid = (double) paymentResult[0];
            String paymentMethod = (String) paymentResult[1];
            double change = cashPaid - grandTotal;
            
            // Print into text file
            try (PrintWriter save = new PrintWriter(new FileOutputStream(filename))) {
                
                save.println("==================================================");
                save.println("                 POTATO CORNER ");
                save.println("        WORLD'S BEST FLAVORED FRENCH FRIES\n");
                save.println("               ORDER NUMBER #" + randomNumber );
                save.println("==================================================");
                save.println("            "+currentTime);
                save.println("CUSTOMER NAME : " + userName);
                save.println("CUSTOMER NUMBER : " + number);
                if (isPWD) {
                    save.println("PWD ID NUMBER : " + pwdIdNumber); // <-- Prints PWD ID on receipt if applicable
                }
                save.println( "--------------------------------------------------");
                save.println("            PLEASE GIVE THIS RECEIPT");
                save.println("          TO A NEARBY VENDOR FOR ORDER");

                for(int i = 0; i < items.size(); i++){
                    OrderItem order = items.get(i);
                    save.println();
                    save.println("ORDER #" + (i + 1));
                    save.printf("%d x %s Fries%n", order.getQuantity(), order.getSize());
                    save.println("Flavors: "+ String.join(", ", order.getFlavors()));
                    if(isPWD && itemDiscounts[i] > 0){
                        save.printf("Original Subtotal : PHP %,.2f%n", order.getItemTotal());
                        save.printf("PWD Discount (20%%): -PHP %,.2f%n", itemDiscounts[i]);
                        save.printf("Item Total        : PHP %,.2f%n", itemFinalTotals[i]);
                    } else {
                        save.printf("Subtotal: PHP %,.2f%n", order.getItemTotal());
                    }
                    save.println( "--------------------------------------------------");
                }
                
                save.printf("SUBTOTAL        : PHP %,.2f%n", subtotalSum);
                if(totalDiscountAmount > 0){
                    save.printf("TOTAL PWD DISC  : -PHP %,.2f%n", totalDiscountAmount);
                }
                if (isPWD) {
                    save.println("VAT (12%)       : PHP 0.00 (VAT EXEMPT)");
                } else {
                    save.printf("VAT (12%%)       : PHP %,.2f%n", vatAmount);
                }
                save.printf("GRAND TOTAL     : PHP %,.2f%n", grandTotal);
                save.printf("PAYMENT TYPE    : %s%n", paymentMethod);
                save.printf("CASH PAID       : PHP %,.2f%n", cashPaid);
                save.printf("CHANGE          : PHP %,.2f%n", change);

                save.println("==================================================");
                save.println("          THANK YOU FOR YOUR PURCHASE!");
                save.println("==================================================");
                
                use.clearScreen();
                System.out.println("==================================================");
                System.out.println("                PAYMENT SUCCESSFUL");
                System.out.println("==================================================");
                System.out.printf("  Grand Total : PHP %,.2f%n", grandTotal);
                if (isPWD) {
                    System.out.println("  VAT (12%)   : PHP 0.00 (VAT EXEMPT)");
                } else {
                    System.out.printf("  VAT (12%%)   : PHP %,.2f%n", vatAmount);
                }
                System.out.printf("  Cash Paid   : PHP %,.2f%n", cashPaid);
                System.out.printf("  Change      : PHP %,.2f%n", change);
                System.out.println("==================================================");
                use.pauseUser(input, "\n receipt save successfully to " + filename + "\n Press any key to continue: ");
            } catch (IOException e) {
                use.pauseUser(input, "\nerror writing a save \npress any key: " + e.getMessage());
            }
            loadFile(filename,input);
            items.clear();
        }
    }
}