package com.bogaware.frugal.services;

import com.bogaware.frugal.configurations.CountdownConfiguration;
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
public class EbayService {

    @Autowired
    private CountdownConfiguration countdownConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<SearchProductDTO> getProductsByKeyword(SearchProductFilterDTO filter) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = getRequiredQueryParams();
        queryParams.put("type", "search");
        queryParams.put("search_term", filter.getKeyword());
        queryParams.put("page", String.valueOf(filter.getPageNum()));
//        queryParams.put("customer_zipcode", filter.getZipCode());
        if (filter.getSortBy() != SearchProductFilterDTO.SortBy.RATING_HIGH_TO_LOW) {
            queryParams.put("sort_by", sortByToValue(filter.getSortBy()));
        }
        HttpEntity<ObjectNode> response = restTemplate.exchange(
                countdownConfiguration.getApiUrl() + "/request?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                null,
                ObjectNode.class);
        return getDTOFromRawData(response.getBody().withArray("search_results"));
    }

    private List<SearchProductDTO> getDTOFromRawData(ArrayNode arrayNode) {
        List<SearchProductDTO> resultProducts = new ArrayList<>();
        for (JsonNode node : arrayNode) {
            if (node.get("price").has("value")) {
                SearchProductDTO searchProductDTO = new SearchProductDTO();
                searchProductDTO.setId(node.get("epid").asText());
                searchProductDTO.setStore("EBAY");
                searchProductDTO.setTitle(node.get("title").asText());
                searchProductDTO.setPrice(node.get("price").get("value").asDouble());
                searchProductDTO.setLink(node.get("link").asText());
                searchProductDTO.setImageUrl(node.get("image").asText());
                searchProductDTO.setRating(node.has("rating") ? node.get("rating").asDouble() : null);
                searchProductDTO.setRatingCount(node.has("ratings_total") ? node.get("ratings_total").asInt() : null);
                resultProducts.add(searchProductDTO);
            }
        }
        return resultProducts;
    }

    private String sortByToValue(SearchProductFilterDTO.SortBy sortBy) {
        if (sortBy == SearchProductFilterDTO.SortBy.RELEVANCE) {
            return "best_match";
        }
        return sortBy.label;
    }

    public HashMap<String, String> getRequiredQueryParams() throws UnsupportedEncodingException {
        HashMap<String, String> requiredQueryParams = new HashMap<>();
        requiredQueryParams.put("api_key", countdownConfiguration.getApiKey());
        requiredQueryParams.put("ebay_domain", countdownConfiguration.getDomain());
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
