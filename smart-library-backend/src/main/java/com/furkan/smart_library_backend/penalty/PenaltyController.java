package com.furkan.smart_library_backend.penalty;

import com.furkan.smart_library_backend.penalty.dto.PenaltyResponse;
import com.furkan.smart_library_backend.user.User;
import com.furkan.smart_library_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PenaltyController {

    private final PenaltyService penaltyService;
    private final UserRepository userRepository;

    @GetMapping("/my")
    public ResponseEntity<List<PenaltyResponse>> getMyPenalties(Principal principal) {
        return ResponseEntity.ok(penaltyService.getMyPenalties(principal.getName()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PenaltyResponse>> getAllPenalties() {
        try {
            return ResponseEntity.ok(penaltyService.getAllPenalties());
        } catch (Exception e) {
            log.error("Error while fetching all penalties", e);
            return ResponseEntity.status(500).build();
        }
    }



    @PostMapping("/{id}/pay")
    public ResponseEntity<PenaltyResponse> payPenalty(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAndDeletedFalse(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        return ResponseEntity.ok(penaltyService.payPenalty(id, user.getId()));
    }
}