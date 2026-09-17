/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.iie.prog5121;
import java.util.Scanner;

/**
 *
 * @author Yaqeen Patelia
 */
// Main class runs the registration and login system
public class Main {
     public static void main(String[] args) {Scanner input = new Scanner(System.in);

      System.out.println("*REGISTRATION SYSTEM*");
      
    //Get the users name
         System.out.print("Enter your first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

    // Create a Login object
        Login user = new Login(firstName, lastName);

    // Get registration details
        System.out.print("Enter a username: ");
        String username = input.nextLine();

        System.out.print("Enter a password: ");
        String password = input.nextLine();

        System.out.print("Enter your South African cell phone number: ");
        String cellPhoneNumber = input.nextLine();

    // Register the user
        String registrationMessage = user.registerUser(username,password,cellPhoneNumber);
        System.out.println();
        System.out.println(registrationMessage);

    // Only continue to login if registration was successful
        if (user.checkUserName(username)
        && user.checkPasswordComplexity(password)
        && user.checkCellPhoneNumber(cellPhoneNumber)) {

        System.out.println("*LOGIN*");

        System.out.print("Enter your username: ");
        String loginUsername = input.nextLine();

        System.out.print("Enter your password: ");
        String loginPassword = input.nextLine();

    // Check the login details
        boolean loginStatus = user.loginUser(loginUsername,loginPassword);
        System.out.println();
        System.out.println(user.returnLoginStatus(loginStatus));
        } 
        
        else 
        {
         System.out.println();
         System.out.println("Please correct your registration details and try again.");
        }
        input.close();
    }
}
