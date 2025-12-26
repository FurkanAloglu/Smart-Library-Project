package com.furkan.smart_library_backend.penalty;

import com.furkan.smart_library_backend.penalty.dto.PenaltyResponse;
import com.furkan.smart_library_backend.user.User;
import com.furkan.smart_library_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final UserRepository userRepository;

    @Transactional
    public PenaltyResponse payPenalty(Long penaltyId, Long userId) {
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new RuntimeException("Ceza bulunamadı!"));

        // 1. Güvenlik Kontrolü: Başkasının cezasını ödemeye mi çalışıyor? (IDOR Koruması)
        // Eğer admin değilse ve user ID eşleşmiyorsa hata fırlat.
        if (!penalty.getBorrowing().getUser().getId().equals(userId)) {
            // Buraya log atılmalı: "Şüpheli işlem denemesi user: " + userId
            throw new RuntimeException("Bu işlem için yetkiniz yok.");
        }

        // 2. Mantık Kontrolü: Zaten ödenmiş mi?
        if (penalty.isPaid()) {
            throw new RuntimeException("Bu ceza zaten ödenmiş.");
        }

        // 3. Simüle Edilmiş Ödeme İşlemi (Burada normalde Iyzico/Stripe çağrılır)
        boolean paymentSuccess = true; // Bankadan OK döndüğünü varsayıyoruz.

        if (paymentSuccess) {
            penalty.setPaid(true);
            penalty.setPaymentDate(LocalDate.now()); // Şu anki tarihi at
            return penaltyMapper.toResponse(penaltyRepository.save(penalty)); // Mapper kullandığını varsayıyorum
        } else {
            throw new RuntimeException("Ödeme işlemi başarısız oldu.");
        }
    }

    public List<PenaltyResponse> getMyPenalties(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return penaltyRepository.findAllByUserId(user.getId()).stream()
                .map(PenaltyResponse::fromEntity)
                .toList();
    }

    public List<PenaltyResponse> getAllPenalties() {
        return penaltyRepository.findAll().stream()
                .map(PenaltyResponse::fromEntity)
                .toList();
    }
}