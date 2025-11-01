package com.project.techstore.services.favourite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.techstore.models.Favourite;
import com.project.techstore.models.Product;
import com.project.techstore.models.User;
import com.project.techstore.repositories.FavouriteRepository;
import com.project.techstore.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavouriteService implements IFavouriteService {
    private final FavouriteRepository favouriteRepository;
    
    private final ProductRepository productRepository;
    
    @Override
    public void addProductToFavourites(String productId, User user) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm"));
        Favourite favourite = Favourite.builder()
                .user(user)
                .product(product)
                .build();

        favouriteRepository.save(favourite);
    }

    @Override
    public void removeProductFromFavourites(String productId, User user) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm"));

        Favourite favourite = favouriteRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElseThrow(() -> new Exception("Sản phẩm không có trong danh sách yêu thích"));

        favouriteRepository.delete(favourite);
    }

    @Override
    public Page<Favourite> getFavouritesByUser(User user, Pageable pageable) throws Exception {
        return favouriteRepository.findByUserId(user.getId(), pageable);
    }

    @Override
    public boolean isProductInFavourites(String productId, User user) throws Exception {
        Favourite favourite = favouriteRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElse(null);
        return favourite != null;
    }
}