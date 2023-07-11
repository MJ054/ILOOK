package com.example.ilook.Model;

import java.time.LocalDateTime;
import java.util.Date;

public class PickFileVo {
    private String content;
    private String category;
    private String date;

    public PickFileVo(String content, String category, String  date) {
        this.content = content;
        this.category = category;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public String  getDate() {
        return date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
