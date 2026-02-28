package org.example.cloudit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.cloudit.dto.MediaAttachment;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MastodonMediaWrapper {

    private String id;

    @JsonProperty("created_at")
    private OffsetDateTime created_At;

    private List<MediaAttachment> media_attachments;

    public String getId() {
        return id;
    }

    public OffsetDateTime getCreatedAt() {
        return created_At;
    }

    public List<MediaAttachment> getMedia_attachments() {
        return media_attachments;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.created_At = createdAt;
    }

    public void setMedia_attachments(List<MediaAttachment> media_attachments) {
        this.media_attachments = media_attachments;
    }
}