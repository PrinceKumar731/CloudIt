package org.example.cloudit.service;

import org.example.cloudit.dto.MastodonMediaWrapper;
import org.example.cloudit.dto.MastodonResponse;
import org.example.cloudit.dto.MediaAttachment;
import org.example.cloudit.model.Folder;
import org.example.cloudit.model.Photo;
import org.example.cloudit.repository.PhotoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExternalApiService {
    @Autowired
    PhotoRepo photoRepo;

    @Value("${mastodon.token}")
    private String mastodonToken;

    private final WebClient webClient;

    public ExternalApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<MastodonMediaWrapper> fetchStatuses() {

        System.out.println(mastodonToken);

        List<MastodonMediaWrapper> statuses = new ArrayList<>();

        photoRepo.findAllByOrderByCreatedAtDesc().forEach(photo -> {

            try {

                MastodonMediaWrapper status = webClient.get()
                        .uri("/api/v1/statuses/" + photo.getPhotoId())
                        .retrieve()
                        .onStatus(
                                statusCode -> statusCode.value() == 404,
                                response -> Mono.empty()
                        )
                        .bodyToMono(MastodonMediaWrapper.class)
                        .block();

                if (status != null) {
                    statuses.add(status);
                }

            } catch (Exception e) {
                System.out.println("Status not found or deleted: " + photo.getPhotoId());
                photoRepo.delete(photo);
            }
        });

        return statuses;
    }

    public List<MastodonMediaWrapper> fetchStatuses(String folderName) {

        List<MastodonMediaWrapper> statuses = new ArrayList<>();

        photoRepo.findAllByFolderNameOrderByCreatedAtDesc(folderName).forEach(photo -> {

            try {

                MastodonMediaWrapper status = webClient.get()
                        .uri("/api/v1/statuses/" + photo.getPhotoId())
                        .retrieve()
                        .onStatus(
                                statusCode -> statusCode.value() == 404,
                                response -> Mono.empty()
                        )
                        .bodyToMono(MastodonMediaWrapper.class)
                        .block();

                if (status != null) {
                    statuses.add(status);
                }

            } catch (Exception e) {
                System.out.println("Status not found or deleted: " + photo.getPhotoId());
                photoRepo.delete(photo);
            }
        });

        return statuses;
    }

    public MediaAttachment upload(MultipartFile file) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource());

        return webClient.post()
                .uri("/api/v2/media")
                .header("Authorization", "Bearer " + mastodonToken) // ADD THIS
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(MediaAttachment.class)
                .block();
    }

    public void createPostWithMedia(String mediaId, String folderName) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", "My uploaded image");
        body.put("media_ids", List.of(mediaId));

        MastodonResponse photoData = webClient.post()
                .uri("/api/v1/statuses")
                .header("Authorization", "Bearer " + mastodonToken  )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(MastodonResponse.class)
                .block();

        Photo photo = new Photo();
        photo.setPhotoId(photoData.getId());
        photo.setCreatedAt(photoData.getCreated_at());
        photo.setFolderName(folderName);
        photoRepo.save(photo);

    }

    public List<Folder> getAllFolders() {
        List<Folder> folders = new ArrayList<>();

        photoRepo.findDistinctFolderNames().forEach(photo -> {
            System.out.println(photo);
            if(photo != null) {
                Folder folder = new Folder();
                folder.setFolderName(photo);
                folder.setPhotoCount(photoRepo.countByFolderName(photo));
                folders.add(folder);
            }
        });
        return folders;
    }

    public void deletePhotoById(String id) {
        photoRepo.deleteById(id);
    }
}
