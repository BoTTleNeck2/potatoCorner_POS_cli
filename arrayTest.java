import java.util.Scanner;

public class arrayTest{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        
        System.out.print("Enter member amount: ");
        int members = input.nextInt();

        input.nextLine();

        String[] mems = new String[members];
        for(int i = 0; i < mems.length; i++){
            System.out.print("Enter member name " + (i + 1) + ": ");
            mems[i] = input.nextLine();
        }

        for(int i = 0;i < mems.length; i++){
            System.out.println("Member " + (1 + i) + ": " + mems[i]);
        }
        input.close();
    }
}