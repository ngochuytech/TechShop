package com.project.techstore.services.favourite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.techstore.models.Favorite;
import com.project.techstore.models.User;

public interface IFavoriteService {
    void addProductToFavorites(String productId, User user) throws Exception;

    void removeProductFromFavorites(String productId, User user) throws Exception;

    Page<Favorite> getFavoritesByUser(User user, Pageable pageable) throws Exception;
    boolean isProductInFavorites(String productId, User user) throws Exception;
}
