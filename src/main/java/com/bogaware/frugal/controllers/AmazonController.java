package com.bogaware.frugal.controllers;

import com.bogaware.frugal.services.AmazonScrapperService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("amazon")
@Slf4j
public class AmazonController {

    @Autowired
    private AmazonScrapperService amazonScrapperService;

    @GetMapping("products")
    @ResponseBody
    public List<ObjectNode> getProductsByKeyword(@RequestParam(name = "keyword") String keyword,
                                                 @RequestParam(name = "page") int page) throws UnsupportedEncodingException {
        log.info("Calling for Amazon's Products with the keyword: " + keyword + ", on page: " + page);
        if (keyword.trim().equalsIgnoreCase("")) {
            return new ArrayList<>();
        }
        return amazonScrapperService.getProductsByKeyword(keyword, page);
    }

    @GetMapping("products/specs/{product-id}")
    @ResponseBody
    public ObjectNode getProductSpecsById(@PathVariable("product-id") String productId) throws UnsupportedEncodingException {
        log.info("Calling for Amazon's Products Specs with ID: " + productId);
        if (productId == null || productId.trim().equalsIgnoreCase("")) {
            return null;
        }
        return amazonScrapperService.getProductSpecsById(productId);
    }
}
