import java.util.Scanner;

public class Main {
    
    public static int menu(Scanner inputObj)
    {
        int option;

        System.out.println("Longaga");
        
        do
        {
            System.out.println("Select an option");
            System.out.println("1. New game");
            System.out.println("2. Load game");

            option = inputObj.nextInt();

        } while (option < 1 || option > 2);

        return option;
    }

    public static void main(String[] args)
    {

        Scanner inputObj = new Scanner(System.in);
        int choice = menu(inputObj);
        inputObj.nextLine(); // consume the leftover newline before using nextLine()
        //if choice is 1, it will start an entirely new tournament
        Tournament gameTournament = new Tournament();


        gameTournament.startTournament(choice);

        inputObj.close(); // close once at the very end

    }
}