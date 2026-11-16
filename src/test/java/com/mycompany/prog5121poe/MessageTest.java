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

public class MessageTest {

    // Test data from the brief
    private static final String NUMBER_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String NUMBER_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    private static final String RECIPIENT_FAILURE = "Cell phone number is incorrectly formatted or does not "
            + "contain an international code. Please correct the number and try again.";

    private String oldFileName;

    // Use a test file so the real messages.json is not changed
    @BeforeEach
    public void setUp() {
        oldFileName = Message.fileName;
        Message.fileName = "test_messages.json";
        new File("test_messages.json").delete();
    }

    // Delete the test file and put the real file name back
    @AfterEach
    public void tearDown() {
        new File("test_messages.json").delete();
        Message.fileName = oldFileName;
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

    // ---------- Message ID ----------

    @Test
    public void testMessageIDCreated() {
        Message message = new Message(NUMBER_1, TEXT_1);

        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
        assertTrue(message.getMessageID().matches("\\d{10}"));
    }

    @Test
    public void testMessageIDMustBeExactlyTenDigits() {
        assertTrue(Message.isValidMessageID("1234567890"));

        assertFalse(Message.isValidMessageID("123456789"));    // 9 digits
        assertFalse(Message.isValidMessageID("12345678901"));  // 11 digits
        assertFalse(Message.isValidMessageID("12345abcde"));   // has letters
        assertFalse(Message.isValidMessageID("+123456789"));   // has a symbol
        assertFalse(Message.isValidMessageID("1234 56789"));   // has a space
        assertFalse(Message.isValidMessageID(""));
        assertFalse(Message.isValidMessageID(null));
    }

    @Test
    public void testMessageIDsAreUnique() {
        String[] ids = new String[200];

        for (int i = 0; i < ids.length; i++) {
            ids[i] = new Message(NUMBER_1, TEXT_1).getMessageID();
        }

        // Compare every ID with the ones after it
        for (int i = 0; i < ids.length; i++) {
            for (int j = i + 1; j < ids.length; j++) {
                assertFalse(ids[i].equals(ids[j]));
            }
        }
    }

    // ---------- Message length ----------

    @Test
    public void testMessageLengthSuccess() {
        Message message = new Message(NUMBER_1, TEXT_1);

        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        Message message = new Message(NUMBER_1, "A".repeat(260));

        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength());
    }

    // ---------- Recipient number ----------

    // Test case 1: valid international number
    @Test
    public void testRecipientSuccessTestCase1() {
        Message message = new Message(NUMBER_1, TEXT_1);

        assertEquals("Cell phone number successfully captured.",
                message.checkRecipientCell());
    }

    // Test case 2: number without an international code
    @Test
    public void testRecipientFailureTestCase2() {
        Message message = new Message(NUMBER_2, TEXT_2);

        assertEquals(RECIPIENT_FAILURE, message.checkRecipientCell());
    }

    @Test
    public void testRecipientAcceptsInternationalNumbers() {
        String[] validNumbers = {"+27718693002", "+14155552671", "+447911123456"};

        for (int i = 0; i < validNumbers.length; i++) {
            Message message = new Message(validNumbers[i], TEXT_1);
            assertEquals(Message.RECIPIENT_SUCCESS, message.checkRecipientCell());
        }
    }

    @Test
    public void testRecipientRejectsBadFormats() {
        String[] badNumbers = {
            "08575975889",        // no international code
            "27718693002",        // no +
            "+0718693002",        // country code starts with 0
            "+27",                // too short
            "+27 718693002",      // has a space
            "+27718693002x",      // has a letter
            "+2771869300212345",  // too long
            ""                    // empty
        };

        for (int i = 0; i < badNumbers.length; i++) {
            Message message = new Message(badNumbers[i], TEXT_1);
            assertEquals(RECIPIENT_FAILURE, message.checkRecipientCell());
        }

        // A null number should also fail
        Message nullMessage = new Message(null, TEXT_1);
        assertEquals(RECIPIENT_FAILURE, nullMessage.checkRecipientCell());
    }

    // ---------- Message hash ----------

    // The first two digits of the ID are random, so the expected hash
    // is built from the ID of the message itself
    @Test
    public void testMessageHashTestCase1() {
        Message message = new Message(NUMBER_1, TEXT_1);

        String expected = message.getMessageID().substring(0, 2)
                + ":" + Message.returnTotalMessages() + ":HITONIGHT";

        assertEquals(expected, message.createMessageHash());
    }

    // Tests the other message hashes in a loop
    @Test
    public void testMessageHashesInLoop() {
        String[][] testData = {
            {NUMBER_1, TEXT_1, "HITONIGHT"},
            {NUMBER_2, TEXT_2, "HIPAYMENT"}
        };

        for (int i = 0; i < testData.length; i++) {
            Message message = new Message(testData[i][0], testData[i][1]);

            String expected = message.getMessageID().substring(0, 2)
                    + ":" + Message.returnTotalMessages() + ":" + testData[i][2];

            assertEquals(expected, message.createMessageHash());
        }
    }

    // ---------- Send ----------

    // Test case 1: send the message
    @Test
    public void testSendMessage() {
        Message message = new Message(NUMBER_1, TEXT_1);
        int totalBefore = Message.returnTotalMessages();

        assertEquals("Message successfully sent.", message.sentMessage(1));
        assertEquals(totalBefore + 1, Message.returnTotalMessages());
        assertSame(message, Message.findSentMessage(message.getMessageID()));
    }

    @Test
    public void testSendRejectedForInvalidRecipient() {
        Message message = new Message(NUMBER_2, TEXT_2);
        int totalBefore = Message.returnTotalMessages();

        assertEquals(RECIPIENT_FAILURE, message.sentMessage(1));
        assertEquals(totalBefore, Message.returnTotalMessages());
        assertNull(Message.findSentMessage(message.getMessageID()));
    }

    @Test
    public void testSendRejectedForLongMessage() {
        Message message = new Message(NUMBER_1, "A".repeat(260));
        int totalBefore = Message.returnTotalMessages();

        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.sentMessage(1));
        assertEquals(totalBefore, Message.returnTotalMessages());
    }

    // ---------- Recently sent messages ----------

    @Test
    public void testRecentMessagesAreListedInOrder() {
        Message first = new Message(NUMBER_1, TEXT_1);
        Message second = new Message("+27831234567", "Hello Sam, see you soon.");
        int countBefore = Message.returnSentMessageCount();

        first.sentMessage(1);
        second.sentMessage(1);

        String list = Message.printMessages();

        assertEquals(countBefore + 2, Message.returnSentMessageCount());
        assertTrue(list.contains("Message ID: " + first.getMessageID()));
        assertTrue(list.contains("Message ID: " + second.getMessageID()));
        assertTrue(list.contains("Recipient: +27831234567"));
        assertTrue(list.contains("Message: Hello Sam, see you soon."));
        assertTrue(list.indexOf(first.getMessageID()) < list.indexOf(second.getMessageID()));
    }

    // ---------- Disregard and delete ----------

    // Test case 2: the number has no international code and Discard is selected
    @Test
    public void testDisregardMessageTestCase2() {
        Message message = new Message(NUMBER_2, TEXT_2);
        int totalBefore = Message.returnTotalMessages();
        int disregardedBefore = Message.returnDisregardedCount();

        assertEquals(RECIPIENT_FAILURE, message.checkRecipientCell());
        assertEquals("Press 0 to delete the message.", message.sentMessage(2));

        assertEquals(totalBefore, Message.returnTotalMessages());
        assertEquals(disregardedBefore + 1, Message.returnDisregardedCount());
    }

    @Test
    public void testPressZeroDeletesDisregardedMessage() {
        Message message = new Message(NUMBER_2, TEXT_2);
        int disregardedBefore = Message.returnDisregardedCount();

        message.sentMessage(2);
        assertEquals(disregardedBefore + 1, Message.returnDisregardedCount());

        assertEquals("Message deleted.", message.deleteMessage());
        assertEquals(disregardedBefore, Message.returnDisregardedCount());

        // Deleting it a second time should not find it
        assertEquals("Message not found.", message.deleteMessage());
    }

    @Test
    public void testDeleteSentMessage() {
        Message message = new Message(NUMBER_1, TEXT_1);
        message.sentMessage(1);
        assertNotNull(Message.findSentMessage(message.getMessageID()));

        assertEquals("Message deleted.", message.deleteMessage());

        assertNull(Message.findSentMessage(message.getMessageID()));
        assertFalse(Message.printMessages().contains(message.getMessageID()));
    }

    // ---------- Store ----------

    @Test
    public void testStoreMessage() throws Exception {
        Message message = new Message(NUMBER_1, TEXT_1);

        assertEquals("Message successfully stored.", message.sentMessage(3));
        assertEquals(1, Message.returnStoredMessageCount());

        String file = readTestFile().trim();

        assertTrue(file.startsWith("["));
        assertTrue(file.endsWith("]"));
        assertTrue(file.contains("\"messageID\":\"" + message.getMessageID() + "\""));
        assertTrue(file.contains("\"recipient\":\"" + NUMBER_1 + "\""));
    }

    @Test
    public void testStoreRejectedForInvalidRecipient() {
        Message message = new Message(NUMBER_2, TEXT_2);

        assertEquals(RECIPIENT_FAILURE, message.sentMessage(3));
        assertEquals(0, Message.returnStoredMessageCount());
    }

    @Test
    public void testDeleteStoredMessage() throws Exception {
        Message keep = new Message(NUMBER_1, TEXT_1);
        Message remove = new Message("+27831234567", "Hello Sam, see you soon.");

        keep.sentMessage(3);
        remove.sentMessage(3);
        assertEquals(2, Message.returnStoredMessageCount());

        assertEquals("Message deleted.", remove.deleteMessage());

        String file = readTestFile();

        assertEquals(1, Message.returnStoredMessageCount());
        assertTrue(file.contains(keep.getMessageID()));
        assertFalse(file.contains(remove.getMessageID()));
    }

    // ---------- Invalid option ----------

    @Test
    public void testInvalidOption() {
        Message message = new Message(NUMBER_1, TEXT_1);

        assertEquals("Invalid option.", message.sentMessage(9));
    }
}
