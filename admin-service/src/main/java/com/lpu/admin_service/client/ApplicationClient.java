package com.lpu.admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "APPLICATION-SERVICE")
public interface ApplicationClient {

    @GetMapping("/applications/{id}")
    Object getApplication(@PathVariable Long id);
}