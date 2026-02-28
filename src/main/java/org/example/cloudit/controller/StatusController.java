package org.example.cloudit.controller;

import org.example.cloudit.dto.MastodonMediaWrapper;
import org.example.cloudit.dto.MastodonResponse;
import org.example.cloudit.dto.MediaAttachment;
import org.example.cloudit.model.Folder;
import org.example.cloudit.model.Photo;
import org.example.cloudit.repository.PhotoRepo;
import org.example.cloudit.service.ExternalApiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class StatusController {

    private final ExternalApiService externalApiService;
    private final PhotoRepo photoRepo;

    public StatusController(ExternalApiService externalApiService, PhotoRepo photoRepo) {
        this.externalApiService = externalApiService;
        this.photoRepo = photoRepo;
    }

    @GetMapping("/statuses")
    public List<MastodonMediaWrapper> getStatuses() {
        return externalApiService.fetchStatuses();
    }

    @GetMapping({"/statuses/{folderName}"})
    public List<MastodonMediaWrapper> getStatuses(@PathVariable(value = "folderName", required = false) String folderName) {
        return  externalApiService.fetchStatuses(folderName);
    }

    @GetMapping("/folder")
    public List<Folder> getAllFolders() {
        return externalApiService.getAllFolders();
    }

    @PostMapping({"/api/upload", "/api/upload/{folderName}"})
    public String upload(
            @PathVariable(value = "folderName", required = false) String folderName,
            @RequestParam("file") MultipartFile file) {
        MediaAttachment media = externalApiService.upload(file);
        externalApiService.createPostWithMedia(media.getId(),folderName);
        return "Uploaded";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable(value = "id") String id) {
        externalApiService.deletePhotoById(id);
        return "Deleted";
    }
}