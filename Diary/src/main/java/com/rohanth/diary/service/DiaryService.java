package com.rohanth.diary.service;

import com.rohanth.diary.model.DiaryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiaryService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<DiaryEntry> getEntriesForUser(Long userId) {
        String sql = "SELECT * FROM diary_entries WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new DiaryEntry(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ), userId);
    }

    public void saveEntry(DiaryEntry entry) {
        if (entry.getId() == null) {
            String sql = "INSERT INTO diary_entries (user_id, title, content, created_at) VALUES (?, ?, ?, NOW())";
            jdbcTemplate.update(sql, entry.getUserId(), entry.getTitle(), entry.getContent());
        } else {
            String sql = "UPDATE diary_entries SET title = ?, content = ? WHERE id = ? AND user_id = ?";
            jdbcTemplate.update(sql, entry.getTitle(), entry.getContent(), entry.getId(), entry.getUserId());
        }
    }
}
