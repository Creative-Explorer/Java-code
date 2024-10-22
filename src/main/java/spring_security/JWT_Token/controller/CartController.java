package spring_security.JWT_Token.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring_security.JWT_Token.dto.CartDTO;
import spring_security.JWT_Token.service.CartSerice;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {


    @Autowired
    private CartSerice cartSerice;

    @PostMapping("/add-cart-items")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> addToCart(@RequestParam Integer productId,
                                            @RequestParam String phoneNumber) {
        try {
            cartSerice.addToCart(productId, phoneNumber);
            return new ResponseEntity<>("Item added to cart successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/cart-items")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<CartDTO>> getAllCartItems(@RequestParam String phoneNumber) {
        List<CartDTO> list = cartSerice.getAllCartItems(phoneNumber);
        return ResponseEntity.ok(list);
    }



//    @GetMapping("/cart-items")
//    @CrossOrigin(origins = "http://localhost:4200")
//    public ResponseEntity<List<CartEntity>> getAllCartItems(@RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "application/json") String acceptHeader) {
//        List<CartEntity> list = cartSerice.getallCartItems();
//
//        if (acceptHeader.contains("application/x-yaml")) {
//            // Respond with YAML format
//            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/x-yaml").body(list);
//        } else {
//            // Default to JSON format
//            return ResponseEntity.ok(list);
//        }
//    }

    @GetMapping("/item-count")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Integer> getAllCount(@RequestParam String phoneNumber) {
        int count = cartSerice.getAllCount(phoneNumber);
        return ResponseEntity.ok(count);
    }
    @DeleteMapping("/cart-delete")
    @CrossOrigin(origins = "http://localhost:4200")
    public void deleteCartItem(@RequestParam Integer id) {
        cartSerice.deleteById(id);
    }
}
