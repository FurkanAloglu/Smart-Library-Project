package com.furkan.smart_library_backend.penalty;

import com.furkan.smart_library_backend.penalty.dto.PenaltyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/penalties")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    @GetMapping("/my")
    public ResponseEntity<List<PenaltyResponse>> getMyPenalties(Principal principal) {
        return ResponseEntity.ok(penaltyService.getMyPenalties(principal.getName()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PenaltyResponse>> getAllPenalties() {
        return ResponseEntity.ok(penaltyService.getAllPenalties());
    }

    @PostMapping("/{id}/pay")
// @PreAuthorize("hasRole('USER')") // Security config'ine göre bunu aç
    public ResponseEntity<PenaltyResponse> payPenalty(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) { // Token'dan user'ı al

        // UserDetails'den ID'yi çekme mantığın nasılsa (CustomUserDetailsService) oradan ID'yi al.
        // Örnek: Long userId = ((CustomUserDetails) userDetails).getId();
        // Şimdilik servise ID'yi parametre geçiyoruz, sen kendi auth yapına göre düzenle.

        return ResponseEntity.ok(penaltyService.payPenalty(id, userId));
    }
}