package com.furkan.smart_library_backend.penalty;

import com.furkan.smart_library_backend.penalty.dto.PenaltyResponse;
import com.furkan.smart_library_backend.user.User;
import com.furkan.smart_library_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional; // DİKKAT: Doğru import (Spring transaction)
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final UserRepository userRepository;

    @Transactional
    public PenaltyResponse payPenalty(UUID penaltyId, UUID userId) { // ID tipi UUID yapıldı
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new RuntimeException("Ceza bulunamadı!"));

        // 1. Güvenlik: Başkasının cezasını ödemeye çalışma kontrolü
        if (!penalty.getBorrowing().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu işlem için yetkiniz yok.");
        }

        // 2. Mantık: Zaten ödenmiş mi?
        if (penalty.isPaid()) {
            throw new RuntimeException("Bu ceza zaten ödenmiş.");
        }

        // 3. Ödeme İşlemi
        penalty.setPaid(true);
        penalty.setPaymentDate(LocalDateTime.now());

        Penalty savedPenalty = penaltyRepository.save(penalty);
        return PenaltyResponse.fromEntity(savedPenalty);
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

    public void triggerManualPenaltyCalculation(UUID borrowingId) { //ceza tutarını değiştirmek için
        BigDecimal dailyFee = new BigDecimal("0.50");
        penaltyRepository.callCalculatePenalty(borrowingId, dailyFee);
    }
}