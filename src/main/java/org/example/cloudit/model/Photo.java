package org.example.cloudit.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Scope("prototype")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    @Id
    private String photoId;
    private String folderName;
    private OffsetDateTime createdAt;

    @PrePersist
    public void setDefaultStatus() {
        if (Objects.equals(this.folderName, "")) {
            this.folderName = "AllPhotos";
        }
    }
}
