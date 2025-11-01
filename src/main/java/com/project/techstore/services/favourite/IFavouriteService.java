package com.project.techstore.services.favourite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.techstore.models.Favourite;
import com.project.techstore.models.User;

public interface IFavouriteService {
    void addProductToFavourites(String productId, User user) throws Exception;

    void removeProductFromFavourites(String productId, User user) throws Exception;

    Page<Favourite> getFavouritesByUser(User user, Pageable pageable) throws Exception;

    boolean isProductInFavourites(String productId, User user) throws Exception;
}
