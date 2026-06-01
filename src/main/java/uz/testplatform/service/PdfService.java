package uz.testplatform.service;

public interface PdfService {

    byte[] generateResultPdf(String resultCode, String userEmail);

}
