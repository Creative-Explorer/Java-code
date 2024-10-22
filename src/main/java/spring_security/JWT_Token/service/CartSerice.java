package spring_security.JWT_Token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spring_security.JWT_Token.EnumConstants;
import spring_security.JWT_Token.Utils.EmailService;
import spring_security.JWT_Token.dto.CartDTO;
import spring_security.JWT_Token.entity.CartEntity;
import spring_security.JWT_Token.entity.ProductEntity;
import spring_security.JWT_Token.entity.UserInfoEntity;
import spring_security.JWT_Token.repository.CartItemRepository;
import spring_security.JWT_Token.repository.PaymentRepository;
import spring_security.JWT_Token.repository.ProductRepository;
import spring_security.JWT_Token.repository.UserInfoRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartSerice {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaymentRepository paymentRepository;


    @Async // perform background operations asynchronously
//    @Scheduled(fixedRate = 5000) // Run every 10 seconds
//    @Scheduled(cron = "0 0 0 * * ?")  // Runs every day at midnight
    public void addToCart(Integer productId, String phoneNumber) {
        Optional<ProductEntity> productOptional = productRepository.findById(productId);
        Optional<UserInfoEntity> userInfo = userInfoRepository.findByPhoneNumber(phoneNumber);
        if (productOptional.isPresent() && userInfo.isPresent()) {
            ProductEntity product = productOptional.get();
            UserInfoEntity user = userInfo.get();
            CartEntity cartEntity = new CartEntity();
            cartEntity.setProduct(product);
            cartEntity.setUser(user);
            cartEntity.setQty(product.getQty());
            cartEntity.setName(product.getName());
            cartEntity.setPrice(product.getPrice());
            cartEntity.setCreatedAt(new Date());
            cartEntity.setImageData(product.getImageData());
            cartItemRepository.save(cartEntity);
            String recipientEmail = "sbalaraju7989@gmail.com";
//            String cc = "cc1@example.com,cc2@example.com";
            String cc = "sbalaraju12@gmail.com,sbalaraju31@gmail.com.com";
            String bcc = "settipalli20@gmail.com,balaraju.leadwinner@gmail.com";
            String subject = "Item Added to Cart";
            String text = "The product " + product.getName() + " has been added to your cart.\n\n" + "Price: " + product.getPrice();

            File imageFile = null;
            try {
                byte[] imageData = product.getImageData();
                if (imageData != null) {
                    imageFile = File.createTempFile("productImage", ".jpg");
                    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                        fos.write(imageData);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            File pdfInvoice = emailService.generateInvoicePdf(cartEntity);
//            emailService.sendEmailWithAttachment(recipientEmail, cc, bcc, subject, text, imageFile, pdfInvoice);
            if (imageFile != null && imageFile.exists()) {
                imageFile.delete();
            }
        } else {
            throw new RuntimeException("Product or User not found");
        }
    }

    public List<CartDTO> getAllCartItems(String phoneNumber) {
        // Fetch the user by phone number
        Optional<UserInfoEntity> userOptional = userInfoRepository.findByPhoneNumber(phoneNumber);

        if (userOptional.isPresent()) {
            UserInfoEntity userInfo = userOptional.get();

            // Fetch all cart items for the specific user
            List<CartEntity> cartEntities = cartItemRepository.findByUserId(Math.toIntExact(userInfo.getId()));

            // Fetch all successful payments
            List<Integer> successfulProductIds = paymentRepository.findAll().stream()
                    .filter(payment -> EnumConstants.SUCCESS.equals(payment.getStatus())) // Filter successful payments
                    .flatMap(payment -> payment.getProducts().stream()) // Flatten products from payments
                    .filter(product -> product != null && product.getProductId() != null) // Avoid nulls
                    .map(ProductEntity::getProductId) // Extract productId
                    .distinct() // Ensure unique IDs
                    .toList(); // Collect to a list

            // Log for debugging
            System.out.println("Successful Product IDs: " + successfulProductIds);

            // Convert entities to DTOs
            List<CartDTO> cartDTOs = cartEntities.stream()
                    .map(this::convertToDTO)
                    .toList();

            // Filter out cart items associated with successful payments
            List<CartDTO> filteredCartItems = cartDTOs.stream()
                    .filter(cartItem -> !successfulProductIds.contains(cartItem.getProductId())) // Filter based on productId
                    .collect(Collectors.toList()); // Collect to a list

            // Log for debugging
            filteredCartItems.forEach(cartItem ->
                    System.out.println("Cart Item ID: " + cartItem.getId() + ", Product ID: " + cartItem.getProductId())
            );

            return filteredCartItems;
        } else {
            // Handle the case where the user is not found
            throw new RuntimeException("User not found for phone number: " + phoneNumber);
        }
    }



    private CartDTO convertToDTO(CartEntity cartEntity) {
        return new CartDTO(
                cartEntity.getId(),
                cartEntity.getProduct().getProductId(),
                cartEntity.getName(),
                cartEntity.getQty(),
                cartEntity.getImageData(),
                cartEntity.getPrice(),
                cartEntity.getCreatedAt()
        );
    }





    public int getAllCount(String phoneNumber) {
        // Retrieve the user by phone number
        Optional<UserInfoEntity> user = userInfoRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            return 0; // Or throw an exception if the user is not found
        }

        // Retrieve all successful product IDs
        List<Integer> successfulProductIds = paymentRepository.findAll().stream()
                .filter(payment -> EnumConstants.SUCCESS.equals(payment.getStatus()))
                .flatMap(payment -> payment.getProducts().stream())
                .map(ProductEntity::getProductId)
                .distinct()
                .toList();

        // Count cart items for the found user that do not have successful product IDs
        long count = cartItemRepository.findAll().stream()
                .filter(cartItem ->
                        cartItem.getUser().getId().equals(user.get().getId()) && // Match user ID
                                !successfulProductIds.contains(cartItem.getProduct().getProductId())) // Exclude successful products
                .count();

        return (int) count;
    }

    public void deleteById(Integer id) {
        cartItemRepository.deleteById(id);
    }
}
