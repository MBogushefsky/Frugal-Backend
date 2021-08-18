package com.bogaware.frugal.services;

import com.bogaware.frugal.configurations.BlueCartConfiguration;
import com.bogaware.frugal.configurations.SerpApiConfiguration;
import com.bogaware.frugal.dto.SearchProductDTO;
import com.bogaware.frugal.dto.SearchProductFilterDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleShoppingService {

    @Autowired
    private SerpApiConfiguration serpApiConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<SearchProductDTO> getProductsByKeyword(SearchProductFilterDTO filter) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = getRequiredQueryParams();
        queryParams.put("tbm", "shop");
        queryParams.put("hl", "en");
        queryParams.put("q", filter.getKeyword());
        queryParams.put("page", String.valueOf(filter.getPageNum()));
//        queryParams.put("customer_zipcode", filter.getZipCode());
        queryParams.put("sort_by", sortByToValue(filter.getSortBy()));
        HttpEntity<ObjectNode> response = restTemplate.exchange(
                serpApiConfiguration.getApiUrl() + "/search.json?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                null,
                ObjectNode.class);
        return getDTOFromRawData(response.getBody().withArray("shopping_results"));
    }

    private List<SearchProductDTO> getDTOFromRawData(ArrayNode arrayNode) {
        List<SearchProductDTO> resultProducts = new ArrayList<>();
        for (JsonNode node : arrayNode) {
            if (node.has("extracted_price")) {
                SearchProductDTO searchProductDTO = new SearchProductDTO();
                searchProductDTO.setId(node.has("product_id") ? node.get("product_id").asText() :
                        java.util.UUID.randomUUID().toString().toUpperCase());
                searchProductDTO.setStore("GOOGLE_SHOPPING");
                searchProductDTO.setTitle(node.get("title").asText());
                searchProductDTO.setBrand(node.get("source").asText());
                searchProductDTO.setPrice(node.get("extracted_price").asDouble());
                searchProductDTO.setLink(node.get("link").asText());
                searchProductDTO.setImageUrl(node.has("thumbnail") ? node.get("thumbnail").asText() : null);
                searchProductDTO.setRating(node.has("rating") ? node.get("rating").asDouble() : null);
                searchProductDTO.setRatingCount(node.has("reviews") ? node.get("reviews").asInt() : null);
                resultProducts.add(searchProductDTO);
            }
        }
        return resultProducts;
    }

    private String sortByToValue(SearchProductFilterDTO.SortBy sortBy) {
        if (sortBy == SearchProductFilterDTO.SortBy.RELEVANCE) {
            return "best_match";
        }
        else if (sortBy == SearchProductFilterDTO.SortBy.RATING_HIGH_TO_LOW) {
            return "highest_rating";
        }
        return sortBy.label;
    }

    public HashMap<String, String> getRequiredQueryParams() throws UnsupportedEncodingException {
        HashMap<String, String> requiredQueryParams = new HashMap<>();
        requiredQueryParams.put("api_key", serpApiConfiguration.getApiKey());
        requiredQueryParams.put("gl", serpApiConfiguration.getDomain());
        return requiredQueryParams;
    }

    private String getQueryParamsString(HashMap<String, String> queryParams) throws UnsupportedEncodingException {
        if (queryParams.size() == 0) {
            return "";
        }
        String resultQueryParamsString = "";
        int index = 0;
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            resultQueryParamsString += key + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
            if (index != (queryParams.size() - 1)) {
                resultQueryParamsString += "&";
            }
            index++;
        }
        return resultQueryParamsString;
    }
}
