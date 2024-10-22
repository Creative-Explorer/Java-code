package spring_security.JWT_Token.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring_security.JWT_Token.dto.*;
import spring_security.JWT_Token.service.PaymentService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    @CrossOrigin(origins = "http://localhost:4200")
    public CompletableFuture<ResponseEntity<PaymentResponseDTO>> processPayment(
            @RequestBody PaymentRequestDTO paymentRequestDTO) {
        return paymentService.processPayment(paymentRequestDTO)
                .thenApply(ResponseEntity::ok).exceptionally(ex -> {
            if (ex.getCause() instanceof PaymentService.ResourceNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (ex.getCause() instanceof PaymentService.PaymentProcessingException) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        });
    }

    @GetMapping("/{paymentId}/status")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<PaymentStatusDTO> getPaymentStatus(@PathVariable Long paymentId) {
        PaymentStatusDTO statusDTO = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(statusDTO);
    }

    @PostMapping("/refund")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<RefundResponseDTO> processRefund(
            @RequestBody RefundRequestDTO refundRequestDTO) {
        RefundResponseDTO refundResponse = paymentService.processRefund(refundRequestDTO);
        return ResponseEntity.ok(refundResponse);
    }

    @GetMapping("/history")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<PaymentDTO>> getPaymentHistory(
            @RequestParam Long userId) {
        List<PaymentDTO> paymentHistory = paymentService.getPaymentHistory(userId);
        return ResponseEntity.ok(paymentHistory);
    }
}
