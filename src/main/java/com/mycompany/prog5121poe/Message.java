/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prog5121poe;

import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Message {

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;

    private static int totalMessages = 0;
    
    private static final List<Message> sentMessages = new ArrayList<>();

    public Message(String recipient, String messageText) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }
    
    // Sends, disregards, or stores a message.
    // Choice 1 = Send, 2 = Disregard, 3 = Store.
    public String sentMessage(int choice) {

        switch (choice) {

            case 1:
                // Hash is created before the counter is incremented
                // so the message number is correct.
                messageHash = createMessageHash();
                sentMessages.add(this);
                totalMessages++;
                return "Message successfully sent.";

            case 2:
                return "Press 0 to delete the message.";

            case 3:
                storeMessage();
                return "Message successfully stored.";

            default:
                return "Invalid option.";
        }
    }

    // Stores the message details in JSON format.
    public void storeMessage() {

        try (FileWriter writer =
                     new FileWriter("messages.json", true)) {

            writer.write("{\n");
            writer.write("  \"messageID\": \""
                    + escapeJSON(messageID) + "\",\n");
            writer.write("  \"messageHash\": \""
                    + escapeJSON(messageHash) + "\",\n");
            writer.write("  \"recipient\": \""
                    + escapeJSON(recipient) + "\",\n");
            writer.write("  \"message\": \""
                    + escapeJSON(messageText) + "\"\n");
            writer.write("},\n");

        } catch (IOException e) {
            System.out.println(
                    "Error storing message: " + e.getMessage());
        }
    }

    // Escapes special characters before writing JSON.
    private String escapeJSON(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }

    // Displays the details of a sent message.
    public String printMessages() {

        StringBuilder output = new StringBuilder();

        for (Message message : sentMessages) {
        output.append("Message ID: ")
              .append(message.messageID)
              .append("\nMessage Hash: ")
              .append(message.messageHash)
              .append("\nRecipient: ")
              .append(message.recipient)
              .append("\nMessage: ")
              .append(message.messageText)
              .append("\n\n");
        }
        
       return output.toString();
    }
    
        // Returns the details of this message.
    public String displayMessage() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    public static int returnTotalMessages() {
        return totalMessages;
    }
    
    // Generates a message ID containing no more than 10 characters.
    private String generateMessageID() {
        Random random = new Random();
        long number = 1000000000L
                + (long) (random.nextDouble() * 9000000000L);

        return String.valueOf(number);
    }

    // Checks that the message ID is not longer than 10 characters.
    public boolean checkMessageID() {
        return messageID != null
                && messageID.length() <= 10;
    }

    // Checks the recipient number has an international code
    // (+ followed by 2 to 13 digits).
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("\\+\\d{2,13}")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the number and try again.";
    }

        // Checks the message does not exceed 250 characters.
    public String checkMessageLength() {
        if (messageText.length() <= 250) {
            return "Message ready to send.";
        }
        int excess = messageText.length() - 250;
        return "Message exceeds 250 characters by " + excess
                + "; please reduce the size.";
    }
    
    // Creates a hash using the first two ID characters,
    // the message number, and the first and last words.
    public String createMessageHash() {

        String[] words = messageText.trim().split("\\s+");

        String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");

        return (messageID.substring(0, 2)
                + ":" + totalMessages
                + ":" + firstWord
                + lastWord).toUpperCase();
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }
}
