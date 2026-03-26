package com.lpu.application_service.cilent;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "DOCUMENT-SERVICE")
public interface DocumentClient {

    @PostMapping("/documents/upload/{applicationId}")
    String uploadDocument(@PathVariable Long applicationId);
}