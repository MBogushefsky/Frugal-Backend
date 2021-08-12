package com.bogaware.frugal.services;

import com.bogaware.frugal.configurations.BlueCartConfiguration;
import com.bogaware.frugal.configurations.WalmartConfiguration;
import com.bogaware.frugal.configurations.WebScrapingConfiguration;
import com.bogaware.frugal.dto.SearchProductDTO;
import com.bogaware.frugal.dto.SearchProductFilterDTO;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
public class WalmartService {

    @Autowired
    private BlueCartConfiguration blueCartConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<SearchProductDTO> getProductsByKeyword(SearchProductFilterDTO filter) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = getRequiredQueryParams();
        queryParams.put("type", "search");
        queryParams.put("search_term", filter.getKeyword());
        queryParams.put("page", String.valueOf(filter.getPageNum()));
//        queryParams.put("customer_zipcode", filter.getZipCode());
        queryParams.put("sort_by", sortByToValue(filter.getSortBy()));
        HttpEntity<ObjectNode> response = restTemplate.exchange(
                blueCartConfiguration.getApiUrl() + "/request?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                null,
                ObjectNode.class);
        return getDTOFromRawData(response.getBody().withArray("search_results"));
    }

    private List<SearchProductDTO> getDTOFromRawData(ArrayNode arrayNode) {
        List<SearchProductDTO> resultProducts = new ArrayList<>();
        for (JsonNode node : arrayNode) {
            if (node.get("offers").get("primary").has("price")) {
                SearchProductDTO searchProductDTO = new SearchProductDTO();
                searchProductDTO.setId(node.get("product").get("item_id").asText());
                searchProductDTO.setStore("WALMART");
                searchProductDTO.setTitle(node.get("product").get("title").asText());
                searchProductDTO.setBrand(node.get("product").get("brand").asText());
                searchProductDTO.setPrice(node.get("offers").get("primary").get("price").asDouble());
                searchProductDTO.setLink(node.get("product").get("link").asText());
                searchProductDTO.setImageUrl(node.get("product").get("primary_image").asText());
                searchProductDTO.setRating(node.get("product").has("rating") ?
                        node.get("product").get("rating").asDouble() : null);
                searchProductDTO.setRatingCount(node.get("product").has("ratings_total") ?
                        node.get("product").get("ratings_total").asInt() : null);
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
        requiredQueryParams.put("api_key", blueCartConfiguration.getApiKey());
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
