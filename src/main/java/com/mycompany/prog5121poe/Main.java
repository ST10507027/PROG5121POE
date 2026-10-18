/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prog5121poe;

import java.util.Scanner;

/**
 * 
 * @author Yaqeen Patelia
 */
// Main class runs the registration, login and QuickChat system
public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("*REGISTRATION SYSTEM*");

        // Get the users name
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
        String registrationMessage = user.registerUser(username, password, cellPhoneNumber);
        System.out.println();
        System.out.println(registrationMessage);

        // Stop if registration was not successful
        boolean registrationValid = user.checkUserName(username)
                && user.checkPasswordComplexity(password)
                && user.checkCellPhoneNumber(cellPhoneNumber);

        if (!registrationValid) {
            System.out.println();
            System.out.println("Please correct your registration details and try again.");
            input.close();
            return;
        }

        System.out.println("*LOGIN*");

        System.out.print("Enter your username: ");
        String loginUsername = input.nextLine();

        System.out.print("Enter your password: ");
        String loginPassword = input.nextLine();

        // Check the login details
        boolean loginStatus = user.loginUser(loginUsername, loginPassword);
        System.out.println();
        System.out.println(user.returnLoginStatus(loginStatus));

        // Only continue to QuickChat if login was successful
        if (loginStatus) {
            runQuickChat(input);
        }

        input.close();
    }

    // Runs the QuickChat menu until the user quits.
    private static void runQuickChat(Scanner input) {
        System.out.println("\nWelcome to QuickChat.");

        System.out.print("How many messages would you like to enter? ");
        int messageLimit = readPositiveNumber(input);
        int messagesCreated = 0;

        while (true) {
            System.out.println("\n=== QuickChat Menu ===");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");

            String choice = input.nextLine();

            switch (choice) {
                case "1":
                    if (messagesCreated >= messageLimit) {
                        System.out.println("You have reached your message limit.");
                    } else if (sendMessage(input)) {
                        messagesCreated++;
                    }
                    break;

                case "2":
                    System.out.println("Coming Soon.");
                    break;

                case "3":
                    System.out.println("\nTotal messages sent: "
                            + Message.returnTotalMessages());
                    System.out.println("Thank you for using QuickChat.");
                    return;

                default:
                    System.out.println("Invalid option. Please choose 1, 2, or 3.");
            }
        }
    }

    // Captures one message. Returns true if it counts towards the limit.
    private static boolean sendMessage(Scanner input) {
        System.out.print("Enter recipient cellphone number: ");
        String recipient = input.nextLine();

        System.out.print("Enter your message: ");
        String messageText = input.nextLine();

        Message message = new Message(recipient, messageText);

        String recipientResult = message.checkRecipientCell();
        if (!recipientResult.equals("Cell phone number successfully captured.")) {
            System.out.println(recipientResult);
            return false;
        }

        String lengthResult = message.checkMessageLength();
        if (!lengthResult.equals("Message ready to send.")) {
            System.out.println(lengthResult);
            return false;
        }

        System.out.println("Message ID generated: " + message.getMessageID());

        System.out.println("\nChoose what to do with the message:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Choose an option: ");

        int messageChoice = readMenuNumber(input);
        System.out.println(message.sentMessage(messageChoice));

        if (messageChoice == 1) {
            System.out.println("\nMessage details:");
            System.out.println(message.displayMessage());
        }

        return true;
    }

    // Reads a positive whole number.
    private static int readPositiveNumber(Scanner input) {
        while (true) {
            System.out.print("Enter a number greater than zero: ");
            String value = input.nextLine();

            try {
                int number = Integer.parseInt(value);
                if (number > 0) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // Ask again if the input is not a number.
            }

            System.out.println("Please enter a valid positive whole number.");
        }
    }

    // Reads the numeric message action (1, 2 or 3).
    private static int readMenuNumber(Scanner input) {
        while (true) {
            String value = input.nextLine();

            try {
                int number = Integer.parseInt(value);
                if (number >= 1 && number <= 3) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // Ask again if the input is not a number.
            }

            System.out.println("Enter 1, 2, or 3.");
        }
    }
}