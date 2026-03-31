package com.lpu.application_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "DOCUMENT-SERVICE")
public interface DocumentClient {

    @PostMapping(value = "/documents/upload", consumes = "multipart/form-data")
    Object uploadDocument(@RequestParam("applicationId") Long applicationId, @RequestPart("file") MultipartFile file);
}
