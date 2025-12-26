package com.furkan.smart_library_backend.penalty;

import com.furkan.smart_library_backend.penalty.dto.PenaltyResponse;
import com.furkan.smart_library_backend.user.User;
import com.furkan.smart_library_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/penalties")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;
    private final UserRepository userRepository; // Kullanıcıyı bulmak için eklendi

    @GetMapping("/my")
    public ResponseEntity<List<PenaltyResponse>> getMyPenalties(Principal principal) {
        // Principal null gelirse patlamaması için basit bir kontrol eklenebilir ama
        // Spring Security devredeyken buraya loginsiz girilmez.
        return ResponseEntity.ok(penaltyService.getMyPenalties(principal.getName()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PenaltyResponse>> getAllPenalties() {
        return ResponseEntity.ok(penaltyService.getAllPenalties());
    }

    @PostMapping("/{id}/pay")
    // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PenaltyResponse> payPenalty(
            @PathVariable UUID id, // DÜZELTME: Veritabanında UUID olduğu için Long yerine UUID
            @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Email üzerinden kullanıcıyı bul (userDetails.getUsername() emaili döner)
        User user = userRepository.findByEmailAndDeletedFalse(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // 2. Servise UUID tipindeki ID'leri gönder
        return ResponseEntity.ok(penaltyService.payPenalty(id, user.getId()));
    }
}