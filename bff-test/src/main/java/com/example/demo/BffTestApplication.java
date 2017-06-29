package com.example.demo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@EnableHystrix
@EnableDiscoveryClient
@RestController
@SpringBootApplication
public class BffTestApplication {

    private static final String GET_CATEGORY_URL = "http://service-product/categories";

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @HystrixCommand(fallbackMethod = "categoryFallback")
    @RequestMapping("/categoryNames")
    public List<String> categoryNames() {
        List<Category> categories = restTemplate().exchange(GET_CATEGORY_URL, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<Category>>() {
        }).getBody();
//		restTemplate().getForEntity(GET_CATEGORY_URL, ).getBody();
        return categories.stream().map(Category::getCategoryName).collect(Collectors.toList());
    }

    private List<String> categoryFallback() {
        return Collections.emptyList();
    }

    public static void main(String[] args) {
        SpringApplication.run(BffTestApplication.class, args);
    }
}

class Category {
    private Integer id;
    private String categoryName;

    public Category() {
    }

    public Category(Integer id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}