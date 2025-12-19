package com.furkan.smart_library_backend.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendLateReturnNotification(String toEmail, String bookTitle, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("alogluwork@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Gecikmiş İade Bildirimi / Late Return Alert");
            message.setText("Sayın " + userName + ",\n\n" +
                    "'" + bookTitle + "' isimli kitabı iade etmeniz gereken tarih geçmiştir.\n" +
                    "Sisteme ceza tutarı yansıtılmıştır.\n\n" +
                    "Lütfen en kısa sürede kütüphaneye uğrayınız.\n\n" +
                    "- Kütüphane Yönetimi");

            mailSender.send(message);
            System.out.println("Mail sent successfully to: " + toEmail);

        } catch (Exception e) {
            System.err.println("MAIL GÖNDERİLEMEDİ: " + e.getMessage());
        }
    }
}