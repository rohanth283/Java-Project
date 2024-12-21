package com.rohanth.diary.model;

import java.time.LocalDateTime;

public class DiaryEntry {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    // Constructors, getters, and setters
    public DiaryEntry(Long id, Long userId, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
}