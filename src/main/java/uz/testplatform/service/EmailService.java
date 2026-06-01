package uz.testplatform.service;

public interface EmailService {
    void sendEmailChangeConfirmation (String email, String token, String userName);

}
