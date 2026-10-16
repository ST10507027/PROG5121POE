/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prog5121poe;

import java.util.Random;

public class Message {

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;

    private static int totalMessages = 0;

    public Message(String recipient, String messageText) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
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

    // Checks the recipient number against the stated length
    // and international-code requirements.
    public boolean checkRecipientCell() {
        return recipient != null
                && recipient.length() <= 10
                && recipient.startsWith("+")
                && recipient.length() > 1
                && recipient.substring(1).matches("\\d+");
    }

    // Creates a hash using the first two ID characters,
    // the message number, and the first and last words.
    public String createMessageHash() {

        String[] words = messageText.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        return (messageID.substring(0, 2)
                + ":" + (totalMessages + 1)
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
