/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prog5121poe;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MessageTest {

    // Test data from the brief
    private static final String NUMBER_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String NUMBER_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    @Test
    public void testMessageIDCreated() {
        Message message = new Message(NUMBER_1, TEXT_1);

        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
        assertTrue(message.getMessageID().matches("\\d{10}"));
    }

    @Test
    public void testMessageLengthSuccess() {
        Message message = new Message(NUMBER_1, TEXT_1);

        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        Message message = new Message(NUMBER_1, "A".repeat(260));

        assertEquals(
                "Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength());
    }

    @Test
    public void testRecipientSuccess() {
        Message message = new Message(NUMBER_1, TEXT_1);

        assertEquals("Cell phone number successfully captured.",
                message.checkRecipientCell());
    }

    @Test
    public void testRecipientFailure() {
        Message message = new Message(NUMBER_2, TEXT_2);

        assertEquals(
                "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the number and try again.",
                message.checkRecipientCell());
    }

    // The ID prefix is random, so the expected hash is built from the
    // message's own ID and the current message count.
    @Test
    public void testMessageHashTestCase1() {
        Message message = new Message(NUMBER_1, TEXT_1);

        String expected = message.getMessageID().substring(0, 2)
                + ":" + Message.returnTotalMessages() + ":HITONIGHT";

        assertEquals(expected, message.createMessageHash());
    }

    // Tests the remaining message hashes in a loop.
    @Test
    public void testMessageHashesInLoop() {
        String[][] testData = {
            {NUMBER_1, TEXT_1, "HITONIGHT"},
            {NUMBER_2, TEXT_2, "HIPAYMENT"}
        };

        for (String[] row : testData) {
            Message message = new Message(row[0], row[1]);

            String expected = message.getMessageID().substring(0, 2)
                    + ":" + Message.returnTotalMessages() + ":" + row[2];

            assertEquals(expected, message.createMessageHash());
        }
    }

    @Test
    public void testSendMessage() {
        Message message = new Message(NUMBER_1, TEXT_1);
        int totalBefore = Message.returnTotalMessages();

        assertEquals("Message successfully sent.", message.sentMessage(1));
        assertEquals(totalBefore + 1, Message.returnTotalMessages());
        assertTrue(message.printMessages().contains(message.getMessageID()));
    }

    @Test
    public void testDisregardMessage() {
        Message message = new Message(NUMBER_2, TEXT_2);
        int totalBefore = Message.returnTotalMessages();

        assertEquals("Press 0 to delete the message.", message.sentMessage(2));
        assertEquals(totalBefore, Message.returnTotalMessages());
    }

    @Test
    public void testStoreMessage() {
        Message message = new Message(NUMBER_1, TEXT_1);

        assertEquals("Message successfully stored.", message.sentMessage(3));
    }
}
