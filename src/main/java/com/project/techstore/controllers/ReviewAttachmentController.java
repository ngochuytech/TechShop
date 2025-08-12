package com.project.techstore.controllers;

import com.project.techstore.services.IReviewAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api.prefix}/review-attachments")
@RequiredArgsConstructor
public class ReviewAttachmentController {
    private final IReviewAttachmentService reviewAttachmentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAttachment(@RequestParam("reviewId") String reviewId,
                                              @RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "comment", required = false) String comment){

        try {
            reviewAttachmentService.createReviewAttachment(reviewId, file, comment);
            return ResponseEntity.ok("Upload successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
