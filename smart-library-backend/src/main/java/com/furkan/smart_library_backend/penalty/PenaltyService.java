package com.furkan.smart_library_backend.penalty;

import com.furkan.smart_library_backend.penalty.dto.PenaltyResponse;
import com.furkan.smart_library_backend.user.User;
import com.furkan.smart_library_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional; // DİKKAT: Doğru import (Spring transaction)
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final UserRepository userRepository;

    @Transactional
    public PenaltyResponse payPenalty(UUID penaltyId, UUID userId) {
        try {
            Penalty penalty = penaltyRepository.findById(penaltyId)
                    .orElseThrow(() -> new RuntimeException("Ceza bulunamadı!"));

            if (!penalty.getBorrowing().getUser().getId().equals(userId)) {
                throw new RuntimeException("Bu işlem için yetkiniz yok.");
            }

            if (penalty.isPaid()) {
                throw new RuntimeException("Bu ceza zaten ödenmiş.");
            }

            penalty.setPaid(true);
            penalty.setPaymentDate(LocalDateTime.now());

            Penalty savedPenalty = penaltyRepository.save(penalty);
            return PenaltyResponse.fromEntity(savedPenalty);
        } catch (Exception e) {
            log.error("Error in PenaltyService.payPenalty penaltyId={}, userId={}", penaltyId, userId, e);
            throw new RuntimeException("Failed to pay penalty", e);
        }
    }

    public List<PenaltyResponse> getMyPenalties(String email) {
        try {
            User user = userRepository.findByEmailAndDeletedFalse(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            return penaltyRepository.findAllByUserId(user.getId()).stream()
                    .map(PenaltyResponse::fromEntity)
                    .toList();
        } catch (Exception e) {
            log.error("Error in PenaltyService.getMyPenalties email={}", email, e);
            throw new RuntimeException("Failed to get penalties for user", e);
        }
    }

    public List<PenaltyResponse> getAllPenalties() {
        try {
            return penaltyRepository.findAll().stream()
                    .map(PenaltyResponse::fromEntity)
                    .toList();
        } catch (Exception e) {
            log.error("Error in PenaltyService.getAllPenalties", e);
            throw new RuntimeException("Failed to get all penalties", e);
        }
    }

    public void triggerManualPenaltyCalculation(UUID borrowingId) {
        try {
            BigDecimal dailyFee = new BigDecimal("0.50");
            penaltyRepository.callCalculatePenalty(borrowingId, dailyFee);
        } catch (Exception e) {
            log.error("Error in PenaltyService.triggerManualPenaltyCalculation borrowingId={}", borrowingId, e);
            throw new RuntimeException("Failed to trigger penalty calculation", e);
        }
    }
}