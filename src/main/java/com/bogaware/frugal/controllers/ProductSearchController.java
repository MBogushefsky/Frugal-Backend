package com.bogaware.frugal.controllers;

import com.bogaware.frugal.dto.SearchProductDTO;
import com.bogaware.frugal.dto.SearchProductFilterDTO;
import com.bogaware.frugal.services.AmazonService;
import com.bogaware.frugal.services.ProductSearchService;
import com.bogaware.frugal.services.WalmartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequestMapping("products")
@Slf4j
public class ProductSearchController {

    @Autowired
    private ProductSearchService productSearchService;

    @PutMapping("search")
    @ResponseBody
    public List<SearchProductDTO> searchProducts(@RequestBody SearchProductFilterDTO filter) throws UnsupportedEncodingException {
        if (filter.getKeyword().trim().isEmpty()) { return null; }
        return productSearchService.searchProducts(filter);
    }

}
