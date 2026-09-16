/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.iie.prog5121;

/**
 *
 * @author Student
 */
public class Login {
    
     // Variables used to store the registered user's details
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;
    
    // Creates a Login object using the user's name.
    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    //Checks whether the username has an underscore and is no more than five characters long.
    public boolean checkUserName(String username) {
    return username.contains("_") && username.length() <= 5;
}
   
    // Checks whether the password has at least 8 characters, a capital letter, a number and a special character
    public boolean checkPasswordComplexity(String password) {

    // Password must contain at least eight characters
    if (password.length() < 8) {
        return false;
    }

    boolean hasCapital = false;
    boolean hasNumber = false;
    boolean hasSpecialCharacter = false;

    // Checks each character in the password
    for (int i = 0; i < password.length(); i++) {

    char character = password.charAt(i);

    if (Character.isUpperCase(character)) {
          hasCapital = true;
        }

    if (Character.isDigit(character)) {
          hasNumber = true;
        }

    if (!Character.isLetterOrDigit(character)) {
          hasSpecialCharacter = true;
        }
    }

    return hasCapital && hasNumber && hasSpecialCharacter;
}
    
    //Checks whether the cellphone number is in the South African international format
    //Reference: Oracle. 2024. String.matches() Method.Available at: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html#matches(java.lang.String) (Accessed: 16 September 2026).
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {

    // Regex checks for +27 followed by nine digits
    String cellPhoneRegex = "^\\+27\\d{9}$";

    return cellPhoneNumber.matches(cellPhoneRegex);
}
    
    // Registers the user after checking their username,password and cellphone
    public String registerUser(String username, String password,
        String cellPhoneNumber) {

    // Store the registration details
    this.username = username;
    this.password = password;
    this.cellPhoneNumber = cellPhoneNumber;

    // Check the username
    if (!checkUserName(username)) {
        return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    }

    // Check the password
    if (!checkPasswordComplexity(password)) {
        return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    }

    // Check the cellphone number
    if (!checkCellPhoneNumber(cellPhoneNumber)) {
        return "Cell phone number incorrectly formatted or does not contain international code.";
    }

    return "Registration successful.";
}
    
}
