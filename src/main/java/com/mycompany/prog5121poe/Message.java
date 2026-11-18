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
    private String sender;
    private String recipient;
    private String messageText;
    private String messageHash;

    // Number of messages sent so far
    private static int totalMessages = 0;

    // The arrays that keep the messages while the program is running
    private static ArrayList<Message> sentMessages = new ArrayList<Message>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<Message>();
    private static ArrayList<Message> storedMessages = new ArrayList<Message>();
    private static ArrayList<String> messageHashes = new ArrayList<String>();
    private static ArrayList<String> messageIDs = new ArrayList<String>();

    // Every ID that was made, so IDs are never repeated
    private static ArrayList<String> usedIDs = new ArrayList<String>();

    private static Random random = new Random();

    // The name of the person who is logged in (the sender of the messages)
    private static String currentSender = "Developer";

    // Name of the file where messages are stored
    public static String fileName = "messages.json";

    // Constructor used when a new message is made
    public Message(String recipient, String messageText) {
        this.sender = currentSender;
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

    // Constructor used when a message is read back from the JSON file
    private Message(String id, String hash, String sender, String recipient, String messageText) {
        this.messageID = id;
        this.messageHash = hash;
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;

        if (!usedIDs.contains(id)) {
            usedIDs.add(id);
        }
    }

    // Sets the name of the person who is logged in
    public static void setSender(String name) {
        currentSender = name;
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
            markAsSent();
            return "Message successfully sent.";
        } else if (choice == 2) {
            markAsDisregarded();
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            if (!problem.equals("")) {
                return problem;
            }
            markAsStored();
            return "Message successfully stored.";
        } else {
            return "Invalid option.";
        }
    }

    // Adds the message to the sent array
    private void markAsSent() {
        // The hash is made before the total goes up so the number is correct
        messageHash = createMessageHash();
        sentMessages.add(this);
        totalMessages++;
        addToIdAndHashArrays();
    }

    // Adds the message to the disregarded array
    private void markAsDisregarded() {
        if (!disregardedMessages.contains(this)) {
            disregardedMessages.add(this);
        }
        addToIdAndHashArrays();
    }

    // Stores the message in the JSON file and reads the file into the stored array
    private void markAsStored() {
        storeMessage();
        addToIdAndHashArrays();
    }

    // Used by the developer (and the unit tests) to put test data into the arrays
    // without using the menu. The flag is Sent, Stored or Disregard.
    // This skips the recipient check because test message 4 has a number
    // without an international code but must still be sent.
    public static Message addDeveloperEntry(String recipient, String messageText, String flag) {
        Message message = new Message(recipient, messageText);

        if (flag.equalsIgnoreCase("Sent")) {
            message.markAsSent();
        } else if (flag.equalsIgnoreCase("Stored")) {
            message.markAsStored();
        } else if (flag.equalsIgnoreCase("Disregard")) {
            message.markAsDisregarded();
        }

        return message;
    }

    // Empties all the arrays (used by the unit tests so each test starts clean)
    public static void resetArrays() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        usedIDs.clear();
        totalMessages = 0;
        currentSender = "Developer";
    }

    // Adds the ID and the hash to their arrays (the two arrays line up)
    private void addToIdAndHashArrays() {
        if (!messageIDs.contains(messageID)) {
            messageIDs.add(messageID);
            messageHashes.add(messageHash);
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

    // Deletes the message from all the arrays and from the JSON file
    // The total of sent messages stays the same so message numbers are not repeated
    public String deleteMessage() {
        boolean deleted = false;

        if (removeFromList(disregardedMessages, messageID)) {
            deleted = true;
        }

        if (removeFromList(sentMessages, messageID)) {
            deleted = true;
        }

        // Remove it from the file as well
        ArrayList<String> oldEntries = readStoredEntries();
        ArrayList<String> newEntries = removeEntry(oldEntries, messageID);

        if (newEntries.size() < oldEntries.size()) {
            writeStoredEntries(newEntries);
            deleted = true;
        }

        // Read the file again so the stored array matches the file
        loadStoredMessages();

        // Take the ID and hash out of their arrays
        int index = messageIDs.indexOf(messageID);
        if (index >= 0) {
            messageIDs.remove(index);
            messageHashes.remove(index);
        }

        if (deleted) {
            return "Message deleted.";
        } else {
            return "Message not found.";
        }
    }

    // Removes the message with this ID from a list, returns true if it was found
    private static boolean removeFromList(ArrayList<Message> list, String id) {
        boolean removed = false;

        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).messageID.equals(id)) {
                list.remove(i);
                removed = true;
            }
        }

        return removed;
    }

    // Stores the message in the JSON file
    public void storeMessage() {
        ArrayList<String> entries = readStoredEntries();

        // Remove the message first so it is never stored twice
        entries = removeEntry(entries, messageID);
        entries.add(toJson());

        writeStoredEntries(entries);

        // Read the file again so the stored array matches the file
        loadStoredMessages();
    }

    // Makes the JSON text for this message (one line)
    private String toJson() {
        return "{\"messageID\":\"" + escapeJSON(messageID) + "\","
                + "\"messageHash\":\"" + escapeJSON(messageHash) + "\","
                + "\"sender\":\"" + escapeJSON(String.valueOf(sender)) + "\","
                + "\"recipient\":\"" + escapeJSON(String.valueOf(recipient)) + "\","
                + "\"message\":\"" + escapeJSON(messageText) + "\"}";
    }

    // Reads the JSON file into the stored messages array
    public static void loadStoredMessages() {
        storedMessages.clear();

        ArrayList<String> lines = readStoredEntries();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            String id = getJsonValue(line, "messageID");
            String hash = getJsonValue(line, "messageHash");
            String sender = getJsonValue(line, "sender");
            String recipient = getJsonValue(line, "recipient");
            String text = getJsonValue(line, "message");

            // Older files did not save the sender
            if (sender.equals("")) {
                sender = "Unknown";
            }

            Message message = new Message(id, hash, sender, recipient, text);
            storedMessages.add(message);
            message.addToIdAndHashArrays();
        }
    }

    // Finds the value of a key in one line of JSON, for example "recipient":"+2783"
    private static String getJsonValue(String line, String key) {
        String search = "\"" + key + "\":\"";
        int start = line.indexOf(search);

        if (start == -1) {
            return "";
        }

        start = start + search.length();
        String value = "";
        int i = start;

        while (i < line.length()) {
            char letter = line.charAt(i);

            if (letter == '\\' && i + 1 < line.length()) {
                // Change the escaped characters back to normal
                char next = line.charAt(i + 1);
                if (next == 'n') {
                    value = value + "\n";
                } else if (next == 'r') {
                    value = value + "\r";
                } else if (next == 't') {
                    value = value + "\t";
                } else {
                    value = value + next;
                }
                i = i + 2;
            } else if (letter == '"') {
                // This is the end of the value
                i = line.length();
            } else {
                value = value + letter;
                i++;
            }
        }

        return value;
    }

    // Reads the lines from the file that hold a message (one message per line)
    private static ArrayList<String> readStoredEntries() {
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

    // Writes the lines to the file as a JSON array
    private static void writeStoredEntries(ArrayList<String> entries) {
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

    // ---------- Stored Messages menu features ----------

    // a. Shows the sender and recipient of all the stored messages
    public static String displaySendersAndRecipients() {
        if (storedMessages.size() == 0) {
            return "No stored messages.";
        }

        String output = "";

        for (int i = 0; i < storedMessages.size(); i++) {
            Message message = storedMessages.get(i);
            output = output + "Sender: " + message.sender
                    + " | Recipient: " + message.recipient + "\n";
        }

        return output;
    }

    // b. Returns the longest stored message
    public static String longestStoredMessage() {
        if (storedMessages.size() == 0) {
            return "No stored messages.";
        }

        String longest = storedMessages.get(0).messageText;

        for (int i = 1; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).messageText.length() > longest.length()) {
                longest = storedMessages.get(i).messageText;
            }
        }

        return longest;
    }

    // c. Searches for a message ID and shows the recipient and the message
    public static String searchByMessageID(String id) {
        if (!messageIDs.contains(id)) {
            return "Message ID not found.";
        }

        Message found = findMessage(sentMessages, id);

        if (found == null) {
            found = findMessage(storedMessages, id);
        }

        if (found == null) {
            found = findMessage(disregardedMessages, id);
        }

        if (found == null) {
            return "Message ID not found.";
        }

        return "Recipient: " + found.recipient + "\nMessage: " + found.messageText;
    }

    // Looks for a sent message by its ID, returns null if it is not there
    public static Message findSentMessage(String id) {
        return findMessage(sentMessages, id);
    }

    // Looks for a message with this ID in a list, returns null if it is not there
    private static Message findMessage(ArrayList<Message> list, String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).messageID.equals(id)) {
                return list.get(i);
            }
        }
        return null;
    }

    // d. Searches the sent and stored messages for one recipient
    public static String searchByRecipient(String recipient) {
        String output = "";

        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).recipient.equals(recipient)) {
                output = output + "Message (Sent): " + sentMessages.get(i).messageText + "\n";
            }
        }

        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).recipient.equals(recipient)) {
                output = output + "Message (Stored): " + storedMessages.get(i).messageText + "\n";
            }
        }

        if (output.equals("")) {
            return "No messages found for this recipient.";
        }

        return output;
    }

    // e. Deletes a stored message using its hash
    public static String deleteMessageByHash(String hash) {
        Message found = null;

        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).messageHash.equalsIgnoreCase(hash)) {
                found = storedMessages.get(i);
            }
        }

        if (found == null) {
            return "Message hash not found.";
        }

        String text = found.messageText;
        found.deleteMessage();

        return "Message: \"" + text + "\" successfully deleted.";
    }

    // f. Report with the full details of all the stored messages
    public static String displayStoredReport() {
        if (storedMessages.size() == 0) {
            return "No stored messages.";
        }

        String report = "STORED MESSAGES REPORT\n\n";

        for (int i = 0; i < storedMessages.size(); i++) {
            Message message = storedMessages.get(i);

            report = report + "Message ID: " + message.messageID
                    + "\nMessage Hash: " + message.messageHash
                    + "\nSender: " + message.sender
                    + "\nRecipient: " + message.recipient
                    + "\nMessage: " + message.messageText
                    + "\n\n";
        }

        return report;
    }

    // Report that shows all the sent messages (hash, recipient and message)
    public static String displaySentReport() {
        if (sentMessages.size() == 0) {
            return "No messages have been sent yet.";
        }

        return "SENT MESSAGES REPORT\n\n" + printMessages();
    }

    // ---------- Returning the arrays ----------

    public static ArrayList<Message> returnSentMessages() {
        return sentMessages;
    }

    public static ArrayList<Message> returnDisregardedMessages() {
        return disregardedMessages;
    }

    public static ArrayList<Message> returnStoredMessages() {
        return storedMessages;
    }

    public static ArrayList<String> returnMessageHashes() {
        return messageHashes;
    }

    public static ArrayList<String> returnMessageIDs() {
        return messageIDs;
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

    // Returns how many sent messages are in the array
    public static int returnSentMessageCount() {
        return sentMessages.size();
    }

    // Returns how many disregarded messages are in the array
    public static int returnDisregardedCount() {
        return disregardedMessages.size();
    }

    // Returns how many messages are in the stored array
    public static int returnStoredMessageCount() {
        return storedMessages.size();
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

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }
}
