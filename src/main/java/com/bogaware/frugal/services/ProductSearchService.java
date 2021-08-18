package com.bogaware.frugal.services;

import com.bogaware.frugal.dto.SearchProductDTO;
import com.bogaware.frugal.dto.SearchProductFilterDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductSearchService {

    @Autowired
    private AmazonService amazonService;

    @Autowired
    private EbayService ebayService;

    @Autowired
    private WalmartService walmartService;

    @Autowired
    private GoogleShoppingService googleShoppingService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();

    public List<SearchProductDTO> searchProducts(SearchProductFilterDTO filter) throws UnsupportedEncodingException {
        List<SearchProductDTO> resultProducts = new ArrayList<>();
        for (SearchProductFilterDTO.Store store : filter.getStores()) {
            if (store == SearchProductFilterDTO.Store.AMAZON) {
                resultProducts.addAll(amazonService.getProductsByKeyword(filter));
            }
            else if (store == SearchProductFilterDTO.Store.EBAY) {
                resultProducts.addAll(ebayService.getProductsByKeyword(filter));
            }
            else if (store == SearchProductFilterDTO.Store.WALMART) {
                resultProducts.addAll(walmartService.getProductsByKeyword(filter));
            }
            else if (store == SearchProductFilterDTO.Store.GOOGLE_SHOPPING) {
                resultProducts.addAll(googleShoppingService.getProductsByKeyword(filter));
            }
        }
        resultProducts = postProductsRetrieveRemoveDuplicates(filter, resultProducts);
        resultProducts = postProductsRetrieveSort(filter, resultProducts);
        return resultProducts;
    }

    private List<SearchProductDTO> postProductsRetrieveRemoveDuplicates(SearchProductFilterDTO filter, List<SearchProductDTO> products) {
        Set<String> distinctSet = new HashSet<>();
        products = products.stream().filter(product -> distinctSet.add(product.getId())).collect(Collectors.toList());
        return products;
    }

    private List<SearchProductDTO> postProductsRetrieveSort(SearchProductFilterDTO filter, List<SearchProductDTO> products) {
        if (filter.getSortBy() == SearchProductFilterDTO.SortBy.PRICE_LOW_TO_HIGH) {
            return products.stream()
                    .sorted(Comparator.comparing(p -> (p.getPrice())))
                    .collect(Collectors.toList());
        }
        else if (filter.getSortBy() == SearchProductFilterDTO.SortBy.PRICE_HIGH_TO_LOW) {
            return products.stream()
                    .sorted((p1, p2) -> ((Double) p2.getPrice()).compareTo(p1.getPrice()))
                    .collect(Collectors.toList());
        }
        else if (filter.getSortBy() == SearchProductFilterDTO.SortBy.RATING_HIGH_TO_LOW) {
            return products.stream()
                    .sorted((p1, p2) -> {
                        if (p1.getRating() == null && p2.getRating() == null) { return 0; }
                        if (p1.getRating() == null && p2.getRating() != null) { return 1; }
                        if (p1.getRating() != null && p2.getRating() == null) { return -1; }
                        return p2.getRating().compareTo(p1.getRating());
                    })
                    .collect(Collectors.toList());
        }
        return products;
    }
}
