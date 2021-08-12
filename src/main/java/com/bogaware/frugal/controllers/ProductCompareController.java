package com.bogaware.frugal.controllers;

import com.bogaware.frugal.services.ProductCompareService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("products")
@Slf4j
public class ProductCompareController {

    @Autowired
    private ProductCompareService productCompareService;

    @GetMapping("compare")
    @ResponseBody
    public ObjectNode getProductsByKeyword(@RequestParam(name = "first-product-id") String firstProductId,
                                                 @RequestParam(name = "first-product-platform") String firstProductPlatform,
                                                 @RequestParam(name = "second-product-id") String secondProductId,
                                                 @RequestParam(name = "second-product-platform") String secondProductPlatform) throws UnsupportedEncodingException {
        ObjectNode firstProductSpecs = productCompareService.getProductSpecs(firstProductId, firstProductPlatform);
        ObjectNode secondProductSpecs = productCompareService.getProductSpecs(secondProductId, secondProductPlatform);
        return productCompareService.compareProductSpecs(firstProductSpecs, secondProductSpecs);
    }
}
