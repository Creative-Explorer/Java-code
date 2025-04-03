package spring_security.JWT_Token.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring_security.JWT_Token.EnumConstants;
import spring_security.JWT_Token.dto.*;
import spring_security.JWT_Token.entity.ProductEntity;
import spring_security.JWT_Token.entity.UserInfoEntity;
import spring_security.JWT_Token.entity.VideoData;
import spring_security.JWT_Token.repository.ProductRepository;
import spring_security.JWT_Token.repository.UserInfoRepository;
import spring_security.JWT_Token.Utils.JwtService;
import spring_security.JWT_Token.repository.VideoRepository;
import spring_security.JWT_Token.service.ProductService;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final String VIDEO_DIRECTORY = "D:/";
    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private VideoRepository videoRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Convert MultipartFile to InputStream and pass to the service for processing
            String result = service.countCharacterFrequency(file.getInputStream());

            // Return the result as a response
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }

    //    @GetMapping("/welcome")
//    public String welcome() {
//        return "Welcome this endpoint is not secure";
//    }
//
//    @GetMapping("/products")
//    @CrossOrigin(origins = "http://localhost:4200")
//    public ResponseEntity<?> getProducts(
//            @RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
//
//        // Normalize phone number format
////        if (phoneNumber != null) {
////            phoneNumber = phoneNumber.replaceAll("\\s+", "");
////            if (!phoneNumber.startsWith("+")) {
////                phoneNumber = "+" + phoneNumber;
////            }
////        }
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName(); // Assuming username is used as identifier
//
//        // Fetch user by phone number
//        UserInfoEntity user = userInfoRepository.findByPhoneNumber(phoneNumber)
//                .orElseThrow(() -> new EntityNotFoundException("User not found with phone number: " + phoneNumber));
//
//        List<ProductEntity> allProducts;
//
//        boolean isAdmin = authentication.getAuthorities().stream()
//                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
//
//        if (isAdmin) {
//            // Admin can see all products
//            allProducts = productRepository.findAll();
//            return ResponseEntity.ok(allProducts);
//        } else {
//            // Non-admin users can see products with a disabled state and an authorization message
//            allProducts = productRepository.findByUserId(user.getId());
//
//            // Filter out products with SUCCESS status
//            List<ProductEntity> filteredProducts = allProducts.stream()
//                    .filter(product -> product.getPayments().isEmpty() ||
//                            product.getPayments().stream()
//                                    .noneMatch(payment -> EnumConstants.SUCCESS.equals(payment.getStatus())))
//                    .collect(Collectors.toList());
//
//            // Sort products by creation date in ascending order
//            List<ProductEntity> sortedProducts = filteredProducts.stream()
//                    .sorted(Comparator.comparing(ProductEntity::getCreatedAt))
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(new AuthResponse(sortedProducts, "You do not have authorization to access some products."));
//        }
//        }
    @GetMapping("/products")
//    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<ProductResponse>> getProducts(@RequestParam(value = "phoneNumber", required = false) String phoneNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoEntity user = userInfoRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new EntityNotFoundException("User not found with phone number: "));

        // Fetch all products
        List<ProductEntity> products = productRepository.findAll();

        // Create response with enabled/disabled state
        List<ProductResponse> productResponses = products.stream().map(product -> {
            boolean isDisabled;
            String paymentStatus = checkPaymentStatus(product);

            if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                // Admin sees all products; no products are disabled
                isDisabled = false;
            } else {
                // Regular user sees only their own products; others are disabled
                isDisabled = product.getUser().getId() != user.getId(); // Disable if not owned by the user
            }

            return new ProductResponse(product, isDisabled, paymentStatus);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/product-count")
//    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Integer> getProductCount(@RequestParam(value = "phoneNumber") String phoneNumber) {

        // Retrieve user by phone number
        UserInfoEntity user = userInfoRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new EntityNotFoundException("User not found with phone number: " + phoneNumber));

        // Count products owned by the user
        long count = productRepository.countByUserId(user.getId()); // Assuming you have a method to count products by user ID

        return ResponseEntity.ok((int) count);
    }


    private String checkPaymentStatus(ProductEntity product) {
        // Return the status of the first payment as a string, or "No payments" if no payments exist
        if (product.getPayments().isEmpty()) {
            return "PENDING";
        } else {
            // Assuming the status is an enum, convert it to a string
            return product.getPayments().get(0).getStatus().name(); // Use name() to get the enum's string representation
        }
    }

    @GetMapping("/list")
//    @CrossOrigin(origins = "http://localhost:4200")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<ProductEntity> getAllProducts() {
        List<ProductEntity> listOfProducts = productRepository.findAll();
        return listOfProducts.stream().sorted(Comparator.comparing(ProductEntity::getCreatedAt)) // Ascending order
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable Integer id) {
        return productRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user-list")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<ProductEntity> getProductsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Assuming username is used as identifier
        UserInfoEntity user = userInfoRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<ProductEntity> listOfProducts = productRepository.findByUserId(user.getId());
        return listOfProducts.stream().sorted(Comparator.comparing(ProductEntity::getCreatedAt)) // Ascending order
                .collect(Collectors.toList());
    }

    @PostMapping("/save/add")
//    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ProductResponseDTO> addProduct(@ModelAttribute ProductDTO productDTO) {
        try {
            ProductEntity savedProduct = service.addProduct(productDTO);
            ProductResponseDTO responseDTO = convertToResponseDTO(savedProduct);
            return ResponseEntity.ok(responseDTO);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @PostMapping("/save/add")
//    @CrossOrigin(origins = "http://localhost:4200")
////    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<ProductEntity> addProduct(@RequestParam(value = "name", required = false) String name,
//                                                    @RequestParam(value = "qty", required = false) Integer qty,
//                                                    @RequestParam(value = "price", required = false) Double price,
//                                                    @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
//                                                    @RequestParam(value = "category", required = false) String category,
//                                                    @RequestParam(value = "subCategory", required = false) String subCategory, // "Electronics" or "Clothes"
//                                                    @RequestParam(value = "imageData", required = false) MultipartFile file)
//                                                     {
//
//        try {
//            ProductEntity product = new ProductEntity();
//            if ((name != null && !name.isEmpty()) && (qty != null && qty > 0)
//                    && (price != null && price > 0)) {
//                product.setName(name);
//                product.setQty(qty);
//                product.setPrice(price);
//            }
//            product.setCategory(category);
//            product.setSubCategory(subCategory);
//            product.setCreatedAt(new Date());
//            ProductEntity savedProduct = service.addProduct(product, file, phoneNumber);
//            return ResponseEntity.ok(savedProduct);

//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    private ProductResponseDTO convertToResponseDTO(ProductEntity product) {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        BeanUtils.copyProperties(product, responseDTO);
        return responseDTO;
    }

    @PostMapping("/excel/upload")
//    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> uploadProducts(@RequestParam("file") MultipartFile file, @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            List<ProductDTO> products = service.parseExcelFile(file.getInputStream());

            // Validate if the number of images matches the number of products
            if (images != null && images.size() != products.size()) {
                return ResponseEntity.badRequest().body("Number of images must match the number of products");
            }

            // Map images to products
            for (int i = 0; i < products.size(); i++) {
                ProductDTO productDTO = products.get(i);
                if (images != null && i < images.size()) {
                    productDTO.setImageData(images.get(i));
                }
                service.addProduct(productDTO);
            }

            return ResponseEntity.ok("Products uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload products");
        }
    }


//    @PostMapping("/excel/upload")
//    @CrossOrigin(origins = "http://localhost:4200")
//    public ResponseEntity<String> uploadProducts(@RequestParam("file") MultipartFile file) {
//        try (InputStream is = file.getInputStream()) {
//            Workbook workbook = new XSSFWorkbook(is);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            // Validate header
//            Row headerRow = sheet.getRow(0);
//            if (headerRow == null) {
//                return ResponseEntity.badRequest().body("Invalid file format");
//            }
//
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) { // Skip header row
//                    continue;
//                }
//
//                try {
//                    String name = row.getCell(0).getStringCellValue();
//                    int qty = (int) row.getCell(1).getNumericCellValue();
//                    double price = row.getCell(2).getNumericCellValue();
////                    String phoneNumber = getCellValueAsString(row.getCell(3)); // Handle phone number cell
//                    productDTO.setPhoneNumber(getStringCellValue(row, 3));
//                    String category = row.getCell(4).getStringCellValue();
//                    String subCategory = row.getCell(5).getStringCellValue();
//
//                    ProductDTO productDTO = new ProductDTO();
//                    productDTO.setName(name);
//                    productDTO.setQty(qty);
//                    productDTO.setPrice(price);
//                    productDTO.setCategory(category);
//                    productDTO.setSubCategory(subCategory);
//                    productDTO.setPhoneNumber(phoneNumber);
//
//                    service.addProduct(productDTO);
//                } catch (Exception e) {
//                    // Log row-specific errors
//                    System.err.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
//                }
//            }
//            workbook.close();
//            return ResponseEntity.ok("Products uploaded successfully");
//        } catch (Exception e) {
//            e.printStackTrace(); // Print stack trace to server logs
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload products");
//        }
//    }

//    @PostMapping("/excel/upload")
//    @CrossOrigin(origins = "http://localhost:4200")
//    public ResponseEntity<String> uploadProducts(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("No file uploaded");
//        }
//
//        try (InputStream inputStream = file.getInputStream()) {
//            service.bulkUploadProducts(inputStream);
//            return ResponseEntity.ok("Products uploaded successfully");
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Failed to upload products: " + e.getMessage());
//        }
//    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Handle numeric cells carefully; phone numbers should be converted to strings
                return Double.toString(cell.getNumericCellValue());
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                // Handle formula cells if necessary
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    @GetMapping("/detailed")
    public ResponseEntity<DetailedProductsResponse> getDetailedProducts() {
        DetailedProductsResponse response = service.getDetailedProducts();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-product")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ProductResponseDTO> updateProductDetails(@ModelAttribute ProductDTO productDTO) {
        try {
            ProductEntity updatedProduct = service.updateProductDetails(productDTO);
            ProductResponseDTO responseDTO = convertToResponseDTO(updatedProduct);
            return ResponseEntity.ok(responseDTO);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete")
//    @CrossOrigin(origins = "http://localhost:4200")
    public void deleteProduct(@RequestParam Integer productId) {
        productRepository.deleteById(productId);
    }

//    private ProductResponseDTO convertToResponseDTO(ProductEntity product) {
//        ProductResponseDTO responseDTO = new ProductResponseDTO();
//        try {
//            BeanUtils.copyProperties(responseDTO, product);
//        } catch (Exception e) {
//            throw new RuntimeException("Error copying properties", e);
//        }
//        return responseDTO;
//    }

    @PostMapping("/authenticate")
//    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> authenticateAndGetToken(@RequestParam String phoneNumber, @RequestParam String password, HttpServletRequest request) {

        Optional<UserInfoEntity> userInfoOptional = userInfoRepository.findByPhoneNumber(phoneNumber);
        if (!userInfoOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
        UserInfoEntity userInfo = userInfoOptional.get();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userInfo.getName(), password));
        if (authentication.isAuthenticated()) {
            // Update login times
            LocalDateTime previousLoginTime = userInfo.getLastLogin();
            userInfo.setPreviousLogin(previousLoginTime);
            // Set the new login time
            LocalDateTime currentLoginTime = LocalDateTime.now();
            userInfo.setLastLogin(currentLoginTime);
            // Save updated user info
            userInfoRepository.save(userInfo);
            String token = jwtService.generateToken(userInfo.getName());
            String username = userInfo.getName();
            String role = userInfo.getRoles();
//            String phoneNumber =userInfo.getPhoneNumber();
            AuthenticationResponse response = new AuthenticationResponse(token, previousLoginTime, username, phoneNumber, role);
            return ResponseEntity.ok(response);
        } else {
            throw new UsernameNotFoundException("Invalid User Request!");
        }
    }

    @GetMapping("/image-upload")
//    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> getImage(@RequestParam Integer productId) {
        Optional<ProductEntity> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            ProductEntity product = productOptional.get();
            byte[] imageData = product.getImageData();
            if (imageData != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/video/upload")
//    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file was uploaded.");
        }

        try {
            // Save the uploaded video file temporarily
            File tempFile = File.createTempFile("uploaded_video", ".tmp");
            file.transferTo(tempFile); // Save the uploaded file

            // Compress the video
//            File compressedVideo = VideoCompressor.compressVideo(tempFile);

            // Save the video data as a byte array
            VideoData video = VideoData.builder()
//                    .name(compressedVideo.getName())
                    .type(file.getContentType())
//                    .videoData(Files.readAllBytes(compressedVideo.toPath())) // Read the compressed video bytes
                    .build();

            videoRepository.save(video); // Save video metadata and data to the database

            // Clean up temporary files
            tempFile.delete();
//            compressedVideo.delete();

            return ResponseEntity.ok("Video uploaded and saved successfully: " + video.getName());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload video: " + e.getMessage());
        }
    }

    @GetMapping("/video/list")
//    @CrossOrigin(origins = "http://localhost:4200")
    public void getAllVideos(HttpServletResponse response) {
        try {
            List<VideoData> videos = videoRepository.findAll();
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "inline");

            // Write videos as JSON
            OutputStream out = response.getOutputStream();
            String jsonResponse = new ObjectMapper().writeValueAsString(videos);
            out.write(jsonResponse.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // Log error if necessary
        }
    }

    @PostMapping("/csvTojson")
    public ResponseEntity<String> convertCsvToJson(
            @RequestParam(value = "file", required = true) MultipartFile file) {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = csvReader.readAll();
            if (records.isEmpty() || records == null) {
                return ResponseEntity.badRequest().body("CSV file is empty.");
            }

            String[] header = records.get(0);
            List<Map<String, String>> jsonList = records.stream()
                    .skip(1)
                    .map(row -> IntStream.range(0, header.length)
                            .boxed().collect(Collectors.toMap(i -> header[i], i -> row[i])))
                    .collect(Collectors.toList());
            // Convert list to JSON string
            String jsonResponse = new ObjectMapper().writeValueAsString(jsonList);
            return ResponseEntity.ok(jsonResponse);

        } catch (IOException | CsvException e) {
            return ResponseEntity.status(500).body("Error processing CSV file: " + e.getMessage());
        }
    }


    public static class AuthResponse {
        private final List<ProductEntity> products;
        private final String message;

        public AuthResponse(List<ProductEntity> products, String message) {
            this.products = products;
            this.message = message;
        }

        public List<ProductEntity> getProducts() {
            return products;
        }

        public String getMessage() {
            return message;
        }
    }
}




