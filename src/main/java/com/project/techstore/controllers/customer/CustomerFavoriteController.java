package com.project.techstore.controllers.customer;

import org.apache.http.HttpStatus;
import org.springframework.data.domain.Page;
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

import com.project.techstore.models.Favorite;
import com.project.techstore.models.User;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.favorite.FavoriteResponse;
import com.project.techstore.services.favourite.IFavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/customer/favourites")
@RequiredArgsConstructor
public class CustomerFavoriteController {

    private final IFavoriteService favouriteService;

    @PostMapping("/product/{productId}")
    public ResponseEntity<?> addProductToFavorites(@PathVariable String productId, @AuthenticationPrincipal User user)
            throws Exception {
        favouriteService.addProductToFavorites(productId, user);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(ApiResponse.ok("Đã thêm vào danh sách yêu thích"));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> removeProductFromFavorites(@PathVariable String productId,
            @AuthenticationPrincipal User user) throws Exception {
        favouriteService.removeProductFromFavorites(productId, user);
        return ResponseEntity.ok(ApiResponse.ok("Đã xóa khỏi danh sách yêu thích"));
    }

    @GetMapping("")
    public ResponseEntity<?> getFavorites(@AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Favorite> favourites = favouriteService.getFavoritesByUser(user, pageable);
        return ResponseEntity.ok(ApiResponse.ok(favourites.map(FavoriteResponse::fromFavourite)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> isProductInFavorites(@PathVariable String productId,
            @AuthenticationPrincipal User user) throws Exception {
        boolean isFavourite = favouriteService.isProductInFavorites(productId, user);
        return ResponseEntity.ok(ApiResponse.ok(isFavourite));
    }

}
