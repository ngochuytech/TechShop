package com.project.techstore.controllers.customer;

import org.apache.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.User;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.favourite.IFavouriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/customer/favourites")
@RequiredArgsConstructor
public class CustomerFavouriteController {

    private final IFavouriteService favouriteService;

    @PostMapping("/product/{productId}")
    public ResponseEntity<?> addProductToFavourites(@PathVariable String productId, @AuthenticationPrincipal User user)
            throws Exception {
        favouriteService.addProductToFavourites(productId, user);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(ApiResponse.ok("Đã thêm vào danh sách yêu thích"));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> removeProductFromFavourites(@PathVariable String productId,
            @AuthenticationPrincipal User user) throws Exception {
        favouriteService.removeProductFromFavourites(productId, user);
        return ResponseEntity.ok(ApiResponse.ok("Đã xóa khỏi danh sách yêu thích"));
    }

    @GetMapping("")
    public ResponseEntity<?> getFavourites(@AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.ok(favouriteService.getFavouritesByUser(user, pageable)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> isProductInFavourites(@PathVariable String productId,
            @AuthenticationPrincipal User user) throws Exception {
        boolean isFavourite = favouriteService.isProductInFavourites(productId, user);
        return ResponseEntity.ok(ApiResponse.ok(isFavourite));
    }

}
