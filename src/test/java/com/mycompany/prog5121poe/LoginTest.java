/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.prog5121poe;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

// tests the methods in the login class
public class LoginTest {

    Login user = new Login("Kyle", "Smith");

    @Test
    public void testCheckUserNameCorrect() {assertTrue(user.checkUserName("kyl_1"));}

    @Test
    public void testCheckUserNameIncorrect() {assertFalse(user.checkUserName("kyle!!!!!!!"));}

    @Test
    public void testCheckPasswordCorrect() {assertTrue(user.checkPasswordComplexity("Ch&&sec@ke99!"));}

    @Test
    public void testCheckPasswordIncorrect() {assertFalse(user.checkPasswordComplexity("password"));}

    @Test
    public void testCheckCellPhoneCorrect() {assertTrue(user.checkCellPhoneNumber("+27838968976"));}

    @Test
    public void testCheckCellPhoneIncorrect() {assertFalse(user.checkCellPhoneNumber("08966553"));}
    
    @Test
public void testLoginStatusSuccessful() {

    user.registerUser("kyl_1","Ch&&sec@ke99!","+27838968976");

    boolean loginStatus = user.loginUser("kyl_1","Ch&&sec@ke99!");

    assertEquals("Welcome Kyle Smith, it is great to see you again.",user.returnLoginStatus(loginStatus));
}

@Test
public void testLoginStatusFailed() {

    user.registerUser("kyl_1","Ch&&sec@ke99!","+27838968976");

    boolean loginStatus = user.loginUser("wrong","wrongpassword");

    assertEquals("Username or password incorrect, please try again.",user.returnLoginStatus(loginStatus));
}
}

