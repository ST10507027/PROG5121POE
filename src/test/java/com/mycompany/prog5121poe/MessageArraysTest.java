/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prog5121poe;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageArraysTest {

    // Test data from the brief
    private static final String RECIPIENT_1 = "+27834557896";
    private static final String TEXT_1 = "Did you get the cake?";
    private static final String RECIPIENT_2 = "+27838884567";
    private static final String TEXT_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String RECIPIENT_3 = "+27834484567";
    private static final String TEXT_3 = "Yohoooo, I am at your gate.";
    private static final String RECIPIENT_4 = "0838884567";
    private static final String TEXT_4 = "It is dinner time!";
    private static final String RECIPIENT_5 = "+27838884567";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    private String oldFileName;

    // The five test messages
    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    private Message message5;

    // Use a test file so the real messages.json is not changed
    @BeforeEach
    public void setUp() {
        oldFileName = Message.fileName;
        Message.fileName = "test_messages.json";
        new File("test_messages.json").delete();
        Message.resetArrays();
    }

    // Delete the test file and put the real file name back
    @AfterEach
    public void tearDown() {
        new File("test_messages.json").delete();
        Message.fileName = oldFileName;
    }

    // Developer entry: puts the five test messages into the arrays
    private void addTestData() {
        message1 = Message.addDeveloperEntry(RECIPIENT_1, TEXT_1, "Sent");
        message2 = Message.addDeveloperEntry(RECIPIENT_2, TEXT_2, "Stored");
        message3 = Message.addDeveloperEntry(RECIPIENT_3, TEXT_3, "Disregard");
        message4 = Message.addDeveloperEntry(RECIPIENT_4, TEXT_4, "Sent");
        message5 = Message.addDeveloperEntry(RECIPIENT_5, TEXT_5, "Stored");
    }

    // Reads the whole test file into one String
    private String readTestFile() throws Exception {
        String text = "";
        BufferedReader reader = new BufferedReader(new FileReader("test_messages.json"));
        String line = reader.readLine();

        while (line != null) {
            text = text + line + "\n";
            line = reader.readLine();
        }

        reader.close();
        return text;
    }

    // ---------- The arrays ----------

    // Sent Messages array contains message 1 and message 4
    @Test
    public void testSentMessagesArrayPopulated() {
        addTestData();

        String[] expected = {"Did you get the cake?", "It is dinner time!"};

        assertEquals(2, Message.returnSentMessages().size());

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], Message.returnSentMessages().get(i).getMessageText());
        }
    }

    // Disregarded Messages array contains message 3
    @Test
    public void testDisregardedMessagesArrayPopulated() {
        addTestData();

        assertEquals(1, Message.returnDisregardedMessages().size());
        assertEquals(TEXT_3, Message.returnDisregardedMessages().get(0).getMessageText());
    }

    // Stored Messages array is read from the JSON file
    @Test
    public void testStoredMessagesArrayReadFromFile() {
        addTestData();

        // Empty the arrays, the file stays, then read the file again
        Message.resetArrays();
        assertEquals(0, Message.returnStoredMessageCount());

        Message.loadStoredMessages();

        assertEquals(2, Message.returnStoredMessages().size());
        assertEquals(message2.getMessageID(), Message.returnStoredMessages().get(0).getMessageID());
        assertEquals(RECIPIENT_2, Message.returnStoredMessages().get(0).getRecipient());
        assertEquals(TEXT_2, Message.returnStoredMessages().get(0).getMessageText());
        assertEquals(TEXT_5, Message.returnStoredMessages().get(1).getMessageText());
        assertEquals("Developer", Message.returnStoredMessages().get(1).getSender());
    }

    // Message Hash and Message ID arrays contain every message
    @Test
    public void testMessageHashAndIDArraysPopulated() {
        addTestData();

        Message[] all = {message1, message2, message3, message4, message5};

        assertEquals(5, Message.returnMessageHashes().size());
        assertEquals(5, Message.returnMessageIDs().size());

        for (int i = 0; i < all.length; i++) {
            assertTrue(Message.returnMessageIDs().contains(all[i].getMessageID()));
            assertTrue(Message.returnMessageHashes().contains(all[i].getMessageHash()));
            assertTrue(all[i].getMessageID().matches("\\d{10}"));
        }
    }

    // ---------- Stored Messages menu features ----------

    // a. Sender and recipient of all stored messages
    @Test
    public void testDisplaySendersAndRecipients() {
        addTestData();

        String result = Message.displaySendersAndRecipients();

        assertEquals("Sender: Developer | Recipient: +27838884567\n"
                + "Sender: Developer | Recipient: +27838884567\n", result);
    }

    // b. Longest message
    @Test
    public void testLongestStoredMessage() {
        addTestData();

        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.longestStoredMessage());
    }

    // c. Search for a message ID (message 4)
    @Test
    public void testSearchByMessageID() {
        addTestData();

        String result = Message.searchByMessageID(message4.getMessageID());

        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!", result);
    }

    @Test
    public void testSearchByMessageIDNotFound() {
        addTestData();

        assertEquals("Message ID not found.", Message.searchByMessageID("0000000000"));
    }

    // d. Search for all the messages for one recipient
    @Test
    public void testSearchByRecipient() {
        addTestData();

        String result = Message.searchByRecipient("+27838884567");

        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("Ok, I am leaving without you."));
        assertFalse(result.contains("Did you get the cake?"));
        assertFalse(result.contains("It is dinner time!"));
    }

    @Test
    public void testSearchByRecipientNotFound() {
        addTestData();

        assertEquals("No messages found for this recipient.",
                Message.searchByRecipient("+27000000000"));
    }

    // e. Delete a message using its hash (test message 2)
    @Test
    public void testDeleteMessageByHash() throws Exception {
        addTestData();

        String result = Message.deleteMessageByHash(message2.getMessageHash());

        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\""
                + " successfully deleted.", result);

        // It is gone from the array, the file and the ID and hash arrays
        assertEquals(1, Message.returnStoredMessageCount());
        assertEquals(TEXT_5, Message.returnStoredMessages().get(0).getMessageText());
        assertFalse(readTestFile().contains(message2.getMessageID()));
        assertFalse(Message.returnMessageIDs().contains(message2.getMessageID()));

        // Deleting it again should not find it
        assertEquals("Message hash not found.",
                Message.deleteMessageByHash(message2.getMessageHash()));
    }

    // f. Report of all the stored messages
    @Test
    public void testStoredMessagesReport() {
        addTestData();

        String report = Message.displayStoredReport();

        assertTrue(report.contains("STORED MESSAGES REPORT"));
        assertTrue(report.contains("Message ID: " + message2.getMessageID()));
        assertTrue(report.contains("Message Hash: " + message2.getMessageHash()));
        assertTrue(report.contains("Sender: Developer"));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains("Message: " + TEXT_2));
        assertTrue(report.contains("Message: " + TEXT_5));
        assertFalse(report.contains(TEXT_1));
    }

    // Display report: all the sent messages with hash, recipient and message
    @Test
    public void testSentMessagesReport() {
        addTestData();

        String report = Message.displaySentReport();

        assertTrue(report.contains("Message Hash: " + message1.getMessageHash()));
        assertTrue(report.contains("Recipient: " + RECIPIENT_1));
        assertTrue(report.contains("Message: " + TEXT_1));
        assertTrue(report.contains("Message Hash: " + message4.getMessageHash()));
        assertTrue(report.contains("Recipient: " + RECIPIENT_4));
        assertTrue(report.contains("Message: " + TEXT_4));
        assertFalse(report.contains(TEXT_2));
        assertFalse(report.contains(TEXT_3));
    }

    // ---------- Nothing stored ----------

    @Test
    public void testNoStoredMessages() {
        assertEquals("No stored messages.", Message.displaySendersAndRecipients());
        assertEquals("No stored messages.", Message.longestStoredMessage());
        assertEquals("No stored messages.", Message.displayStoredReport());
        assertEquals("No messages have been sent yet.", Message.displaySentReport());
    }
}
