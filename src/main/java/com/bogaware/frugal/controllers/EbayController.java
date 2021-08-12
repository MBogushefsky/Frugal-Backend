package com.bogaware.frugal.controllers;

import com.bogaware.frugal.services.EbayAPIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequestMapping("ebay")
@Slf4j
public class EbayController {

    @Autowired
    private EbayAPIService ebayAPIService;

    @GetMapping("products")
    @ResponseBody
    public List<ObjectNode> getProductsByKeyword(@RequestParam(name = "keyword") String keyword,
                                                 @RequestParam(name = "page") int page) throws UnsupportedEncodingException, JsonProcessingException {
        log.info("Calling for eBay's Products with the keyword: " + keyword + ", on page: " + page);
        if (keyword.trim().equalsIgnoreCase("")) {
            return null;
        }
        return ebayAPIService.getProductsByKeyword(keyword, page);
    }
}
