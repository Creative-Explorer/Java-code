package spring_security.JWT_Token.service;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring_security.JWT_Token.Utils.AESUtils;
import spring_security.JWT_Token.dto.DetailedProductsResponse;
import spring_security.JWT_Token.dto.ProductDTO;
import spring_security.JWT_Token.entity.ImageData;
import spring_security.JWT_Token.dto.Product;
import spring_security.JWT_Token.dto.UserInfoDTO;
import spring_security.JWT_Token.entity.ProductEntity;
import spring_security.JWT_Token.entity.UserInfoEntity;
import spring_security.JWT_Token.Utils.ImageUtils;
import spring_security.JWT_Token.repository.ImageRepository;
import spring_security.JWT_Token.repository.ProductRepository;
import spring_security.JWT_Token.repository.UserInfoRepository;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProductService {

    private static final String NAME_REGEX = "^[a-zA-Z0-9]+$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final String ROLES_REGEX = "^[a-zA-Z0-9,]+$";
    private static final String IV = "RandomInitVector";
    List<Product> productList = null;
    @Value("${aes.secret.key}")
    private String secretKeyStr;
    @Autowired
    private UserInfoRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;

    @PostConstruct
    public void loadProductsFromDB() {
        productList = IntStream.rangeClosed(1, 100).mapToObj(i -> Product.builder().id((long) i).name("product " + i).qty(new Random().nextInt(10)).price(new Random().nextInt(5000)).build()).collect(Collectors.toList());
    }


    public List<Product> getProducts() {
        return productList;
    }

    public Product getProduct(int id) {
        return productList.stream().filter(product -> product.getId() == id).findAny().orElseThrow(() -> new RuntimeException("product " + id + " not found"));
    }


    public String addUser(UserInfoDTO userInfoDTO) {
        if (userInfoDTO == null) {
            return "Failed to create user: userInfoDTO object is null";
        }

        try {
            // Convert the base64 encoded key string to a SecretKey
            SecretKey secretKey = AESUtils.getKeyFromString(secretKeyStr);
//            String encryptedPassword = AESUtils.encrypt(userInfoDTO.getPassword(), secretKey, IV);
            String encryptedPassword = passwordEncoder.encode(userInfoDTO.getPassword());
            userInfoDTO.setPassword(encryptedPassword);
            UserInfoEntity userInfo = new UserInfoEntity();
            userInfo.setName(userInfoDTO.getName());
            userInfo.setEmail(userInfoDTO.getEmail());
            userInfo.setPassword(userInfoDTO.getPassword());
            userInfo.setRoles(userInfoDTO.getRoles());
            repository.save(userInfo);
            return "User Successfully Created: " + userInfo.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to create user: " + e.getMessage();
        }
    }


//    @Transactional
//    public ProductEntity addProduct(ProductEntity product, MultipartFile file, String phoneNumber) throws IOException {
//        UserInfoEntity user = userInfoRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        if (file != null && !file.isEmpty()) {
//            ImageData imageData = ImageData.builder().name(file.getOriginalFilename())
//                    .type(file.getContentType())
//                    .imageData(ImageUtils.compressImage(file.getBytes()))
//                    .build();
//            imageData = imageRepository.save(imageData);
//            product.setImageData(imageData.getImageData());
//            product.setUser(user);
//        }
//        return productRepository.save(product);
//    }
private String formatPhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isEmpty()) {
        throw new IllegalArgumentException("Phone number cannot be null or empty");
    }
    if (!phoneNumber.startsWith("+")) {
        phoneNumber = "+" + phoneNumber;
    }
    return phoneNumber;
}
    @Transactional
    public ProductEntity addProduct(ProductDTO productDTO) throws IOException {
//        String formattedPhoneNumber = formatPhoneNumber(productDTO.getPhoneNumber());
        UserInfoEntity user = userInfoRepository.findByPhoneNumber(productDTO.getPhoneNumber())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productDTO, product);

        product.setCreatedAt(new Date());

        if (productDTO.getImageData() != null && !productDTO.getImageData().isEmpty()) {
            ImageData imageData = ImageData.builder()
                    .name(productDTO.getImageData().getOriginalFilename())
                    .type(productDTO.getImageData().getContentType())
                    .imageData(ImageUtils.compressImage(productDTO.getImageData().getBytes()))
                    .build();
            imageData = imageRepository.save(imageData);
            product.setImageData(imageData.getImageData());
        }
        product.setUser(user);
        return productRepository.save(product);
    }


    @Transactional
    public List<ProductDTO> parseExcelFile(InputStream is) throws IOException {
        List<ProductDTO> productDTOs = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);

        // DataFormatter to convert cell value to String
        DataFormatter dataFormatter = new DataFormatter();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }

            String name = getCellStringValue(row.getCell(0), dataFormatter);
            int qty = (int) getCellNumericValue(row.getCell(1));
            double price = getCellNumericValue(row.getCell(2));
            String phoneNumber = getCellStringValue(row.getCell(3), dataFormatter); // Read as text
            String category = getCellStringValue(row.getCell(4), dataFormatter);
            String subCategory = getCellStringValue(row.getCell(5), dataFormatter);

            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(name);
            productDTO.setQty(qty);
            productDTO.setPrice(price);
            productDTO.setPhoneNumber(phoneNumber);
            productDTO.setCategory(category);
            productDTO.setSubCategory(subCategory);

            productDTOs.add(productDTO);
        }
        workbook.close();
        return productDTOs;
    }

    private String getCellStringValue(Cell cell, DataFormatter dataFormatter) {
        if (cell == null) {
            return "";
        }
        // Use DataFormatter to ensure numeric values are formatted as strings
        return dataFormatter.formatCellValue(cell);
    }

    private double getCellNumericValue(Cell cell) {
        if (cell == null) {
            return 0.0;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }



//    @Transactional
//    public void bulkUploadProducts(InputStream inputStream) throws IOException {
//        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//
//            if (rowIterator.hasNext()) {
//                rowIterator.next(); // Skip header row
//            }
//
//            while (rowIterator.hasNext()) {
//                Row row = rowIterator.next();
//                ProductDTO productDTO = new ProductDTO();
//
//                try {
//                    productDTO.setName(getStringCellValue(row, 0));
//                    productDTO.setQty((int) getNumericCellValue(row, 1));
//                    productDTO.setPrice(getNumericCellValue(row, 2));
//                    productDTO.setPhoneNumber(getStringCellValue(row, 3)); // Read as string
//                    productDTO.setCategory(getStringCellValue(row, 4));
//                    productDTO.setSubCategory(getStringCellValue(row, 5));
//
//                    addProduct(productDTO);
//                } catch (Exception e) {
//                    System.err.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
//                }
//            }
//        }
//    }
//
//    private String getStringCellValue(Row row, int cellIndex) {
//        Cell cell = row.getCell(cellIndex);
//        if (cell == null) {
//            return "";
//        }
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue();
//            case NUMERIC:
//                return String.valueOf((long) cell.getNumericCellValue());
//            default:
//                return "";
//        }
//    }
//
//    private double getNumericCellValue(Row row, int cellIndex) {
//        Cell cell = row.getCell(cellIndex);
//        if (cell == null) {
//            return 0;
//        }
//        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0;
//    }
//
    @Transactional
    public ProductEntity updateProductDetails(ProductDTO productDTO) throws IOException {
        // Retrieve the existing product
        ProductEntity existingProduct = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new NoSuchElementException("Product not found with id " + productDTO.getProductId()));
        BeanUtils.copyProperties(existingProduct, productDTO);
        existingProduct.setCreatedAt(new Date());
        if (productDTO.getImageData() != null && !productDTO.getImageData().isEmpty()) {
            handleImageUpload(productDTO.getImageData(), existingProduct);
        }
        return productRepository.save(existingProduct);
    }

    private void handleImageUpload(MultipartFile file, ProductEntity product) throws IOException {
        if (file != null && !file.isEmpty()) {
            byte[] imageData = ImageUtils.compressImage(file.getBytes());
            product.setImageData(imageData);
        }
    }


    public boolean login(String username, String password) {
        Optional<UserInfoEntity> userOptional = repository.findByName(username);
        if (userOptional.isPresent()) {
            UserInfoEntity userInfo = userOptional.get();
            LocalDateTime previousLoginTime = userInfo.getLastLogin();
            userInfo.setLastLogin(previousLoginTime);
            if (passwordEncoder.matches(password, userInfo.getPassword())) {
//                userInfo.setLastLogin(LocalDateTime.now());
                repository.save(userInfo);
                return true;
            }
        }
        return false;
    }

    public boolean updatePassword(String phoneNumber, String newPassword) {
        phoneNumber = phoneNumber.replaceAll("\\s+", "");
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber;
        }
        Optional<UserInfoEntity> userOptional = repository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            UserInfoEntity userInfo = userOptional.get();
            String hashedPassword = passwordEncoder.encode(newPassword);
            userInfo.setPassword(hashedPassword);
            repository.save(userInfo);
            return true;
        }
        return false;
    }

    public boolean verifyUser(String phoneNumber) {
//        phoneNumber = phoneNumber.replaceAll("\\s+", "");
//        if (!phoneNumber.startsWith("+")) {
//            phoneNumber = "+91" + phoneNumber;
//        }
        return userInfoRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Transactional
    public Optional<UserInfoEntity> getUserDetails(String phoneNumber) {
        return userInfoRepository.findByPhoneNumber(phoneNumber);
    }

    public DetailedProductsResponse getDetailedProducts() {
        List<ProductEntity> allProducts = productRepository.findAll();

        // Separate electronics and clothes
        Map<String, List<ProductEntity>> electronics = allProducts.stream()
                .filter(p -> "Electronics".equals(p.getCategory()))
                .collect(Collectors.groupingBy(ProductEntity::getSubCategory));

        Map<String, List<ProductEntity>> clothes = allProducts.stream()
                .filter(p -> "Clothes".equals(p.getCategory()))
                .collect(Collectors.groupingBy(ProductEntity::getSubCategory));

        DetailedProductsResponse response = new DetailedProductsResponse();
        response.setElectronics(electronics);
        response.setClothes(clothes);

        return response;
    }

    public String countCharacterFrequency(InputStream inputStream) throws IOException {
        // StringBuilder to accumulate result
        StringBuilder sb = new StringBuilder();

        // Guava Multiset to hold character counts
        Multiset<Character> bagOfChars = HashMultiset.create();

        // Wrap input stream into a reader
        try (Reader reader = new InputStreamReader(new BufferedInputStream(inputStream), StandardCharsets.UTF_8)) {
            int characterRead;
            // Read each character from the input
            while ((characterRead = reader.read()) != -1) {
                // Convert character to lowercase and add to Multiset
                bagOfChars.add((char) Character.toLowerCase(characterRead));
            }
        } catch (IOException e) {
            throw new IOException("Error reading the input stream", e);
        }

        // Append character counts to the StringBuilder
        for (char letter = 'a'; letter <= 'z'; letter++) {
            sb.append(letter).append(": ").append(bagOfChars.count(letter))
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }

    public void writeResultToOutput(WritableByteChannel writeChannel, String result) throws IOException {
        try (OutputStream outputStream = Channels.newOutputStream(writeChannel)) {
            IOUtils.write(result, outputStream, StandardCharsets.UTF_8);
        }
    }
}
