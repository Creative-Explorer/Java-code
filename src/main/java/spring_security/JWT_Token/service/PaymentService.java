//package spring_security.JWT_Token.service;
//
//import com.paypal.base.rest.APIContext;
//import com.paypal.base.rest.PayPalRESTException;
//import com.paypal.sdk.exceptions.PayPalException;
//import com.paypal.sdk.payments.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import spring_security.JWT_Token.EnumConstants;
//import spring_security.JWT_Token.dto.PaymentRequestDTO;
//import spring_security.JWT_Token.dto.PaymentResponseDTO;
//import spring_security.JWT_Token.entity.Payment;
//import spring_security.JWT_Token.entity.ProductEntity;
//import spring_security.JWT_Token.entity.UserInfoEntity;
//import spring_security.JWT_Token.repository.PaymentRepository;
//import spring_security.JWT_Token.repository.ProductRepository;
//import spring_security.JWT_Token.repository.UserInfoRepository;
//
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class PaymentService {
//
//    @Autowired
//    private UserInfoRepository userInfoRepository;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Autowired
//    private APIContext apiContext;
//
//    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequestDTO) throws PayPalRESTException {
//        // Retrieve user
//        UserInfoEntity user = userInfoRepository.findById(paymentRequestDTO.getUserId())
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        // Retrieve products
//        List<ProductEntity> products = paymentRequestDTO.getProductIds().stream()
//                .map(productId -> productRepository.findById(productId)
//                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId)))
//                .collect(Collectors.toList());
//
//        // Create a PayPal payment
//        Amount amount = new Amount();
//        amount.setCurrency("USD");
//        amount.setTotal(String.format("%.2f", paymentRequestDTO.getAmount()));
//
//        Transaction transaction = new Transaction();
//        transaction.setAmount(amount);
//        transaction.setDescription("Payment description");
//
//        ItemList itemList = new ItemList();
//        itemList.setItems(products.stream()
//                .map(product -> {
//                    Item item = new Item();
//                    item.setName(product.getName());
//                    item.setPrice(String.format("%.2f", product.getPrice()));
//                    item.setCurrency("USD");
//                    item.setQuantity("1");
//                    return item;
//                })
//                .collect(Collectors.toList()));
//        transaction.setItemList(itemList);
//
//        List<Transaction> transactions = List.of(transaction);
//
//        Payer payer = new Payer();
//        payer.setPaymentMethod("paypal");
//
//        Payment payment = new Payment();
//        payment.setIntent("sale");
//        payment.setPayer(payer);
//        payment.setTransactions(transactions);
//
//        RedirectUrls redirectUrls = new RedirectUrls();
//        redirectUrls.setCancelUrl("http://localhost:8080/cancel");
//        redirectUrls.setReturnUrl("http://localhost:8080/success");
//        payment.setRedirectUrls(redirectUrls);
//
//        try {
//            Payment createdPayment = payment.create(apiContext);
//            String approvalUrl = createdPayment.getLinks().stream()
//                    .filter(link -> "approval_url".equals(link.getRel()))
//                    .map(Link::getHref)
//                    .findFirst()
//                    .orElseThrow(() -> new PayPalException("Approval URL not found"));
//
//            PaymentResponseDTO responseDTO = new PaymentResponseDTO();
//            responseDTO.setAmount(paymentRequestDTO.getAmount());
//            responseDTO.setPaymentMethod(paymentRequestDTO.getPaymentMethod());
//            responseDTO.setPaymentDate(new Date());
//            responseDTO.setStatus("PENDING");
//            responseDTO.setApprovalUrl(approvalUrl);
//
//            return responseDTO;
//        } catch (PayPalRESTException e) {
//            throw new PayPalException("Error occurred while creating payment", e);
//        }
//    }
//
//    public PaymentResponseDTO executePayment(String paymentId, String payerId) throws PayPalRESTException {
//        Payment payment = new Payment();
//        payment.setId(paymentId);
//
//        PaymentExecution paymentExecution = new PaymentExecution();
//        paymentExecution.setPayerId(payerId);
//
//        try {
//            Payment executedPayment = payment.execute(apiContext, paymentExecution);
//
//            PaymentResponseDTO responseDTO = new PaymentResponseDTO();
//            responseDTO.setAmount(executedPayment.getTransactions().get(0).getAmount().getTotal());
//            responseDTO.setPaymentMethod("paypal");
//            responseDTO.setPaymentDate(new Date());
//            responseDTO.setStatus(executedPayment.getState());
//
//            // Update payment status in your database
//            Payment dbPayment = paymentRepository.findById(paymentId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
//            dbPayment.setStatus(EnumConstants.SUCCESS);
//            paymentRepository.save(dbPayment);
//
//            return responseDTO;
//        } catch (PayPalRESTException e) {
//            throw new PayPalException("Error occurred while executing payment", e);
//        }
//    }
//}


package spring_security.JWT_Token.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import spring_security.JWT_Token.EnumConstants;
import spring_security.JWT_Token.Utils.EmailService;
import spring_security.JWT_Token.dto.*;
import spring_security.JWT_Token.entity.Payment;
import spring_security.JWT_Token.entity.ProductEntity;
import spring_security.JWT_Token.entity.UserInfoEntity;
import spring_security.JWT_Token.repository.PaymentRepository;
import spring_security.JWT_Token.repository.ProductRepository;
import spring_security.JWT_Token.repository.UserInfoRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class PaymentService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    @Async
    public CompletableFuture<PaymentResponseDTO> processPayment(PaymentRequestDTO paymentRequestDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Retrieve user
                UserInfoEntity user = userInfoRepository.findById(paymentRequestDTO.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // Process products
                List<ProductDTO> productDTOList = new ArrayList<>();
                List<Payment> existingPayments = paymentRepository.findByUserIdAndStatus(user.getId(), EnumConstants.PROCESSING);

                Payment paymentToUpdate = existingPayments.isEmpty() ? null : existingPayments.get(0);

                // Create new payment if no existing processing payment found
                if (paymentToUpdate == null) {
                    paymentToUpdate = createPayment(paymentRequestDTO, user, null); // Create payment without products initially
                }

                for (Integer productId : paymentRequestDTO.getProductIds()) {
                    ProductEntity product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

                    // Check product quantity before decrementing
                    if (product.getQty() > 0) {
                        // Decrement quantity
                        product.setQty(product.getQty() - 1);

                        // Add product to payment
                        paymentToUpdate.getProducts().add(product); // Assuming ManyToMany relationship
                        productDTOList.add(mapToProductDTO(product)); // Convert to DTO

                        // If the quantity is now zero after decrementing, set status to SUCCESS
                        if (product.getQty() == 0) {
                            paymentToUpdate.setStatus(EnumConstants.SUCCESS);
                        } else {
                            paymentToUpdate.setStatus(EnumConstants.PROCESSING); // Keep as PROCESSING if still in stock
                        }

                        productRepository.save(product); // Save updated product
                    } else {
                        // If product quantity is already zero, set status to SUCCESS
                        paymentToUpdate.setStatus(EnumConstants.SUCCESS);
                    }
                }

                // Save payment (will update the existing payment)
                paymentRepository.save(paymentToUpdate);

                // Create response
                PaymentResponseDTO responseDTO = createPaymentResponseDTO(paymentRequestDTO, user, productDTOList);

                // Generate and send invoice email
//                File pdfInvoice = emailService.generateInvoicePdf(responseDTO);
                sendPaymentConfirmationEmail(responseDTO, null);

                return responseDTO;
            } catch (Exception e) {
                log.error("Payment processing failed", e);
                throw new PaymentProcessingException("Payment processing failed", e);
            }
        });
    }


    private Payment createPayment(PaymentRequestDTO paymentRequestDTO, UserInfoEntity user, ProductEntity product) {
        Payment payment = new Payment();
        payment.setAmount(paymentRequestDTO.getAmount());
        payment.setPaymentMethod(paymentRequestDTO.getPaymentMethod());
        payment.setStatus(EnumConstants.PROCESSING);
        payment.setPaymentDate(new Date());
        payment.setUser(user);
        payment.setProducts(List.of(product));
        return payment;
    }




    private void simulatePaymentProcessingDelay() {
        try {
            Thread.sleep(3000); // Simulate delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment processing was interrupted", e);
        }
    }

    private ProductDTO mapToProductDTO(ProductEntity product) {
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    private PaymentResponseDTO createPaymentResponseDTO(PaymentRequestDTO paymentRequestDTO, UserInfoEntity user, List<ProductDTO> productDTOList) {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setAmount(paymentRequestDTO.getAmount());
        responseDTO.setPaymentMethod(paymentRequestDTO.getPaymentMethod());
        responseDTO.setPaymentDate(new Date());
        responseDTO.setStatus("SUCCESS");

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user, userInfoDTO);
        responseDTO.setUser(userInfoDTO);

        responseDTO.setProducts(productDTOList);
        return responseDTO;
    }

    private void sendPaymentConfirmationEmail(PaymentResponseDTO responseDTO, File pdfInvoice) {
        String recipientEmail = "sbalaraju7989@gmail.com";
        String cc = "sbalaraju12@gmail.com,sbalaraju31@gmail.com";
        String bcc = "settipalli20@gmail.com,balaraju.leadwinner@gmail.com";
        String subject = "Payment Confirmation";
        String text = "Thank you for your payment. Please find the attached invoice for your records.";

        emailService.sendEmailWithAttachment(recipientEmail, cc, bcc, subject, text, null, pdfInvoice);
    }

    public PaymentStatusDTO getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        PaymentStatusDTO statusDTO = new PaymentStatusDTO();
        statusDTO.setPaymentId(payment.getId());
        statusDTO.setStatus(String.valueOf(payment.getStatus()));
        return statusDTO;
    }

    public RefundResponseDTO processRefund(RefundRequestDTO refundRequestDTO) {
        // Find the payment
        Payment payment = paymentRepository.findById(refundRequestDTO.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // Validate refund request
        if (refundRequestDTO.getAmount() <= 0) {
            throw new IllegalArgumentException("Refund amount must be greater than zero");
        }
        if (refundRequestDTO.getAmount() > payment.getAmount()) {
            throw new IllegalArgumentException("Refund amount cannot be greater than the original payment amount");
        }

        // Check if the payment is eligible for a refund
        if (!EnumConstants.SUCCESS.equals(payment.getStatus())) {
            throw new IllegalArgumentException("Payment is not eligible for a refund");
        }

        // Update payment status to "REFUNDED" and add refund details
        payment.setStatus(EnumConstants.REFUNDED);
        payment.setRefundAmount(refundRequestDTO.getAmount());
        payment.setRefundReason(refundRequestDTO.getReason());
        payment.setRefundDate(new Date());
        paymentRepository.save(payment);

        // Prepare the response DTO
        RefundResponseDTO responseDTO = new RefundResponseDTO();
        responseDTO.setPaymentId(refundRequestDTO.getPaymentId());
        responseDTO.setAmount(refundRequestDTO.getAmount());
        responseDTO.setStatus(String.valueOf(EnumConstants.REFUNDED));

        return responseDTO;
    }

    public List<PaymentDTO> getPaymentHistory(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream().map(this::mapToPaymentDTO).collect(Collectors.toList());
    }

    private PaymentDTO mapToPaymentDTO(Payment payment) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(payment.getId());
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setStatus(String.valueOf(payment.getStatus()));
        paymentDTO.setPaymentDate(payment.getPaymentDate());
        // Add more fields as needed
        return paymentDTO;
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class PaymentProcessingException extends RuntimeException {
        public PaymentProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
