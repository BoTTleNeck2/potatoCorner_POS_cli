import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class potatoCornerPOS {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        ArrayList<OrderItem> items = new ArrayList<>();
        boolean running = true;

        while (running) {

            System.out.println("================================");
            System.out.println("  POTATO CORNER ORDERING SYSTEM ");
            System.out.println("================================");
            System.out.println("1. Order/Add Item");
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
                    viewCartCheckout render = new viewCartCheckout();
                    render.viewCartLogic(items);
                    break;

                case 3:
                   System.out.println("Under Construction");
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

        System.out.println("\n====CHOOSE SIZES====");
        for(int i = 0; i < sizes.length; i++){
            System.out.println((1 + i) + ". " + sizes[i]);
        }
        System.out.print("Select sizes: ");
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
        
        public void viewCartLogic(ArrayList <OrderItem> print){
            System.out.println("\n=========== VIEW CART ==========");
            if (print.isEmpty()){
                System.out.println("Your cart is empty.");
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
        }
    }
}
