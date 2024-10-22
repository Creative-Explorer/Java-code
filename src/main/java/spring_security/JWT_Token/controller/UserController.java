package spring_security.JWT_Token.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import spring_security.JWT_Token.Utils.AESUtils;
import spring_security.JWT_Token.dto.PasswordResetRequest;
import spring_security.JWT_Token.dto.UserInfoDTO;
import spring_security.JWT_Token.entity.ProductEntity;
import spring_security.JWT_Token.entity.UserInfoEntity;
import spring_security.JWT_Token.Utils.BlackList;
import spring_security.JWT_Token.repository.ProductRepository;
import spring_security.JWT_Token.repository.UserInfoRepository;
import spring_security.JWT_Token.Utils.JwtService;
import spring_security.JWT_Token.service.ProductService;
import spring_security.JWT_Token.Utils.TwilloService;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Users")
public class UserController {


    private static final String IV = "RandomInitVector";
    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private BlackList blackList;
    @Value("${aes.secret.key}")
    private String secretKeyStr;
    @Autowired
    private TwilloService twilloService;


    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        boolean loggedIn = service.login(username, password);
        if (loggedIn) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @PostMapping("/logout")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null) {
            try {
                blackList.blackListToken(token);
                return ResponseEntity.ok("You have been logged out successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing");
        }
    }

    @PostMapping("/encryptPassword")
    @CrossOrigin(origins = "http://localhost:4200")
    public String addNewUser(@RequestBody UserInfoDTO userInfo) {
        return service.addUser(userInfo);
    }

    @GetMapping("/decrypt-password")
    public ResponseEntity<String> decryptPassword(@RequestParam String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("\\s+", "");
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber;
        }

        Optional<UserInfoEntity> userOptional = userInfoRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            UserInfoEntity userInfo = userOptional.get();
            String encryptedPassword = userInfo.getPassword();
            try {
                SecretKey secretKey = AESUtils.getKeyFromString(secretKeyStr);
                String decryptedPassword = AESUtils.decrypt(encryptedPassword, secretKey, IV);
                return ResponseEntity.ok(decryptedPassword);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error decrypting password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/user-details")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<Map<String, String>>> getUserById(@RequestParam("phoneNumber") String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("\\s+", "");
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber;
        }
        Optional<UserInfoEntity> user = service.getUserDetails(phoneNumber);
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user.get().getName());
        userMap.put("phoneNumber", user.get().getPhoneNumber());
        userMap.put("email", user.get().getEmail());
        userMap.put("password", user.get().getPassword());
        userMap.put("role", user.get().getRoles());
        userMap.put("lastLogin", String.valueOf(user.get().getPreviousLogin()));
        List<Map<String, String>> userList = Collections.singletonList(userMap);
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/verifyUser")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> verifyUser(@RequestParam String phoneNumber) {
        boolean isVerified = service.verifyUser(phoneNumber);
        return isVerified ? ResponseEntity.ok("User is Verified") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not verified or does not exist.");
    }

    @GetMapping("/user/list")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<ProductEntity> getUserProducts(HttpServletRequest request) {
        String username = jwtService.extractUsernameFromRequest(request);
        UserInfoEntity user = userInfoRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<ProductEntity> products = productRepository.findByUserId(user.getId());
        return products.stream().sorted(Comparator.comparing(ProductEntity::getCreatedAt)).collect(Collectors.toList());
    }

    @PostMapping("/send-otp")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> sendOtp(@RequestParam String phoneNumber) {
        if (twilloService.sendVerificationCode(phoneNumber)) {
            return ResponseEntity.ok("OTP sent successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to send OTP");
        }
    }

    @PostMapping("/verify-otp")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> verifyOtp(@RequestParam String phoneNumber, @RequestParam String code) {
        if (twilloService.checkVerificationCode(phoneNumber, code)) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }


    @PostMapping("/reset-password")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        boolean passwordUpdated = service.updatePassword(request.getPhoneNumber(), request.getNewPassword());
        if (passwordUpdated) {
            return ResponseEntity.ok("Password updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to update password");
        }
    }
}
