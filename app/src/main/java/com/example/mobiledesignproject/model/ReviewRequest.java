package com.example.mobiledesignproject.model;

public class ReviewRequest {
    private String userId;
    private String rating;
    private String reviewTopic;
    private String reviewBody;

    public ReviewRequest(String userId, String rating, String reviewTopic, String reviewBody){
        this.userId = userId;
        this.rating = rating;
        this.reviewTopic = reviewTopic;
        this.reviewBody = reviewBody;
    }

    public String getUserId() {
        return userId;
    }

    public String getRating() {
        return rating;
    }

    public String getReviewTopic() {
        return reviewTopic;
    }

    public String getReviewBody() {
        return reviewBody;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setReviewTopic(String reviewTopic) {
        this.reviewTopic = reviewTopic;
    }

    public void setReviewBody(String reviewBody) {
        this.reviewBody = reviewBody;
    }
}
