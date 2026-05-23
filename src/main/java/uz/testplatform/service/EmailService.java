package uz.testplatform.service;

public interface EmailService {
    void sendEmailConfirmation(String email, String token, String userName);

}
