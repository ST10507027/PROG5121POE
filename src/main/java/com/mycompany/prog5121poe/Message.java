/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prog5121poe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Message {

    // Messages returned by the checks
    public static final String RECIPIENT_SUCCESS = "Cell phone number successfully captured.";
    public static final String RECIPIENT_FAILURE = "Cell phone number is incorrectly formatted or does not "
            + "contain an international code. Please correct the number and try again.";
    public static final String LENGTH_SUCCESS = "Message ready to send.";

    // Details of one message
    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;

    // Number of messages sent so far
    private static int totalMessages = 0;

    // Lists that keep the messages while the program is running
    private static ArrayList<Message> sentMessages = new ArrayList<Message>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<Message>();
    private static ArrayList<String> usedIDs = new ArrayList<String>();

    private static Random random = new Random();

    // Name of the file where messages are stored
    public static String fileName = "messages.json";

    // Constructor
    public Message(String recipient, String messageText) {
        this.recipient = recipient;

        // Stops errors if the message is null
        if (messageText == null) {
            this.messageText = "";
        } else {
            this.messageText = messageText;
        }

        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
    }

    // Sends, disregards or stores a message
    // Choice 1 = Send, 2 = Disregard, 3 = Store
    public String sentMessage(int choice) {

        // Only check the message when it is being sent or stored
        String problem = "";
        if (choice == 1 || choice == 3) {
            problem = findProblem();
        }

        if (choice == 1) {
            if (!problem.equals("")) {
                return problem;
            }
            // The hash is made before the total goes up so the number is correct
            messageHash = createMessageHash();
            sentMessages.add(this);
            totalMessages++;
            return "Message successfully sent.";
        } else if (choice == 2) {
            // Keep the message until the user presses 0 to delete it
            if (!disregardedMessages.contains(this)) {
                disregardedMessages.add(this);
            }
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            if (!problem.equals("")) {
                return problem;
            }
            storeMessage();
            return "Message successfully stored.";
        } else {
            return "Invalid option.";
        }
    }

    // Checks the recipient and the length
    // Returns an empty text if the message is fine
    private String findProblem() {
        String recipientResult = checkRecipientCell();
        if (!recipientResult.equals(RECIPIENT_SUCCESS)) {
            return recipientResult;
        }

        String lengthResult = checkMessageLength();
        if (!lengthResult.equals(LENGTH_SUCCESS)) {
            return lengthResult;
        }

        return "";
    }

    // Deletes the message from the lists and from the JSON file
    // The total of sent messages stays the same so message numbers are not repeated
    public String deleteMessage() {
        boolean deleted = false;

        if (disregardedMessages.remove(this)) {
            deleted = true;
        }

        if (sentMessages.remove(this)) {
            deleted = true;
        }

        // Remove it from the file as well
        ArrayList<String> oldEntries = readStoredMessages();
        ArrayList<String> newEntries = removeEntry(oldEntries, messageID);

        if (newEntries.size() < oldEntries.size()) {
            writeStoredMessages(newEntries);
            deleted = true;
        }

        if (deleted) {
            return "Message deleted.";
        } else {
            return "Message not found.";
        }
    }

    // Stores the message in the JSON file
    public void storeMessage() {
        ArrayList<String> entries = readStoredMessages();

        // Remove the message first so it is never stored twice
        entries = removeEntry(entries, messageID);
        entries.add(toJson());

        writeStoredMessages(entries);
    }

    // Makes the JSON text for this message (one line)
    private String toJson() {
        return "{\"messageID\":\"" + escapeJSON(messageID) + "\","
                + "\"messageHash\":\"" + escapeJSON(messageHash) + "\","
                + "\"recipient\":\"" + escapeJSON(String.valueOf(recipient)) + "\","
                + "\"message\":\"" + escapeJSON(messageText) + "\"}";
    }

    // Reads the stored messages from the file (one message per line)
    private static ArrayList<String> readStoredMessages() {
        ArrayList<String> entries = new ArrayList<String>();
        File file = new File(fileName);

        if (!file.exists()) {
            return entries;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while (line != null) {
                line = line.trim();

                // Take off the comma at the end of the line
                if (line.endsWith(",")) {
                    line = line.substring(0, line.length() - 1);
                }

                // Only keep the lines that hold a message
                if (line.startsWith("{\"messageID\":") && line.endsWith("}")) {
                    entries.add(line);
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading messages: " + e.getMessage());
        }

        return entries;
    }

    // Writes the messages to the file as a JSON array
    private static void writeStoredMessages(ArrayList<String> entries) {
        try {
            FileWriter writer = new FileWriter(fileName);

            writer.write("[\n");
            for (int i = 0; i < entries.size(); i++) {
                writer.write(entries.get(i));
                if (i < entries.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            writer.write("]\n");

            writer.close();
        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    // Returns a new list without the message that has this ID
    private static ArrayList<String> removeEntry(ArrayList<String> entries, String id) {
        ArrayList<String> result = new ArrayList<String>();
        String key = "\"messageID\":\"" + id + "\"";

        for (int i = 0; i < entries.size(); i++) {
            if (!entries.get(i).contains(key)) {
                result.add(entries.get(i));
            }
        }

        return result;
    }

    // Changes special characters so the JSON is valid
    private String escapeJSON(String text) {
        text = text.replace("\\", "\\\\");
        text = text.replace("\"", "\\\"");
        text = text.replace("\n", "\\n");
        text = text.replace("\r", "\\r");
        text = text.replace("\t", "\\t");
        return text;
    }

    // Returns all the messages sent while the program is running
    public static String printMessages() {
        String output = "";

        for (int i = 0; i < sentMessages.size(); i++) {
            Message message = sentMessages.get(i);

            output = output + "Message ID: " + message.messageID
                    + "\nMessage Hash: " + message.messageHash
                    + "\nRecipient: " + message.recipient
                    + "\nMessage: " + message.messageText
                    + "\n\n";
        }

        return output;
    }

    // Looks for a sent message by its ID, returns null if it is not there
    public static Message findSentMessage(String id) {
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).messageID.equals(id)) {
                return sentMessages.get(i);
            }
        }
        return null;
    }

    // Returns the details of this message
    public String displayMessage() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    // Returns the total number of messages sent
    public static int returnTotalMessages() {
        return totalMessages;
    }

    // Returns how many sent messages are in the list
    public static int returnSentMessageCount() {
        return sentMessages.size();
    }

    // Returns how many disregarded messages are waiting to be deleted
    public static int returnDisregardedCount() {
        return disregardedMessages.size();
    }

    // Returns how many messages are stored in the file
    public static int returnStoredMessageCount() {
        return readStoredMessages().size();
    }

    // Makes a message ID of exactly ten digits that has not been used before
    private String generateMessageID() {
        String id = "";
        boolean unique = false;

        while (!unique) {
            long number = 1000000000L + (long) (random.nextDouble() * 9000000000L);
            id = String.valueOf(number);

            if (!usedIDs.contains(id)) {
                unique = true;
            }
        }

        usedIDs.add(id);
        return id;
    }

    // Returns true only if the ID is exactly ten digits
    public static boolean isValidMessageID(String id) {
        if (id == null) {
            return false;
        }
        return id.matches("\\d{10}");
    }

    // Checks that the message ID is exactly ten digits
    public boolean checkMessageID() {
        return isValidMessageID(messageID);
    }

    // Checks that the number is in international format
    // It starts with +, the country code does not start with 0
    // and it has between 8 and 15 digits (for example +27718693002)
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("\\+[1-9]\\d{7,14}")) {
            return RECIPIENT_SUCCESS;
        } else {
            return RECIPIENT_FAILURE;
        }
    }

    // Checks that the message is not longer than 250 characters
    public String checkMessageLength() {
        if (messageText.length() <= 250) {
            return LENGTH_SUCCESS;
        }

        int extra = messageText.length() - 250;
        return "Message exceeds 250 characters by " + extra
                + "; please reduce the size.";
    }

    // Makes the hash from the first two digits of the ID,
    // the message number and the first and last words
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");

        // Take out punctuation like ? or ,
        String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");

        String hash = messageID.substring(0, 2) + ":" + totalMessages
                + ":" + firstWord + lastWord;

        return hash.toUpperCase();
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
