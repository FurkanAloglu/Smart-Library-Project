package com.furkan.smart_library_backend.borrowing;

import com.furkan.smart_library_backend.borrowing.dto.BorrowingRequest;
import com.furkan.smart_library_backend.borrowing.dto.BorrowingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    @GetMapping("/my")
    public ResponseEntity<List<BorrowingResponse>> getMyBorrowings(Principal principal) {
        return ResponseEntity.ok(borrowingService.getMyBorrowings(principal.getName()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BorrowingResponse>> getAllBorrowings() {
        return ResponseEntity.ok(borrowingService.getAllBorrowings());
    }

    @PostMapping
    public ResponseEntity<BorrowingResponse> borrowBook(
            @Valid @RequestBody BorrowingRequest request,
            Principal principal
    ) {
        return ResponseEntity.ok(borrowingService.borrowBook(request, principal.getName()));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Void> returnBook(
            @PathVariable UUID id,
            Principal principal
    ) {
        borrowingService.returnBook(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}