/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package za.ac.iie.prog5121;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

// tests the methods in the login class
public class LoginTest {

    Login user = new Login("Kyle", "Smith");

    @Test
    public void testCheckUserNameCorrect() {
        assertTrue(user.checkUserName("kyl_1"));
    }

    @Test
    public void testCheckUserNameIncorrect() {
        assertFalse(user.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testCheckPasswordCorrect() {
        assertTrue(user.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testCheckPasswordIncorrect() {
        assertFalse(user.checkPasswordComplexity("password"));
    }

    @Test
    public void testCheckCellPhoneCorrect() {
        assertTrue(user.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCheckCellPhoneIncorrect() {
        assertFalse(user.checkCellPhoneNumber("08966553"));
    }
}

