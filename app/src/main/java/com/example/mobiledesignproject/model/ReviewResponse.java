package com.example.mobiledesignproject.model;

import java.util.Map;

public class ReviewResponse {
    private Map<String, Review> reviews;

    public Map<String, Review> getReviews() {
        return reviews;
    }

    public void setReviews(Map<String, Review> reviews) {
        this.reviews = reviews;
    }

    public static class Review {
        private String createdAt;
        private int rating;
        private String reviewBody;
        private String reviewTopic;
        private String userId;

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getReviewBody() {
            return reviewBody;
        }

        public void setReviewBody(String reviewBody) {
            this.reviewBody = reviewBody;
        }

        public String getReviewTopic() {
            return reviewTopic;
        }

        public void setReviewTopic(String reviewTopic) {
            this.reviewTopic = reviewTopic;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}

