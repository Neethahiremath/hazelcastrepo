package com.storagenode.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "consul", url = "${hazelcast.consul-discovery.url}")
public interface ConsulAPI {
    @GetMapping
    List<Map> getClusterMembers();
}
