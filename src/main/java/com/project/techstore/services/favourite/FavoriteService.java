package com.project.techstore.services.favourite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.techstore.models.Favorite;
import com.project.techstore.models.Product;
import com.project.techstore.models.User;
import com.project.techstore.repositories.FavoriteRepository;
import com.project.techstore.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService implements IFavoriteService {
    private final FavoriteRepository favouriteRepository;
    
    private final ProductRepository productRepository;
    
    @Override
    public void addProductToFavorites(String productId, User user) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm"));
        Favorite favourite = Favorite.builder()
                .user(user)
                .product(product)
                .build();

        favouriteRepository.save(favourite);
    }

    @Override
    public void removeProductFromFavorites(String productId, User user) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm"));

        Favorite favourite = favouriteRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElseThrow(() -> new Exception("Sản phẩm không có trong danh sách yêu thích"));

        favouriteRepository.delete(favourite);
    }

    @Override
    public Page<Favorite> getFavoritesByUser(User user, Pageable pageable) throws Exception {
        return favouriteRepository.findByUserId(user.getId(), pageable);
    }

    @Override
    public boolean isProductInFavorites(String productId, User user) throws Exception {
        Favorite favourite = favouriteRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElse(null);
        return favourite != null;
    }
}