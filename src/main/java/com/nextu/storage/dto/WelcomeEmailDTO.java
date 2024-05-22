package com.nextu.storage.dto;

public class WelcomeEmailDTO {
    private String userEmail;
    private String firstName;

    public WelcomeEmailDTO(String userEmail, String firstName) {
        this.userEmail = userEmail;
        this.firstName = firstName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
