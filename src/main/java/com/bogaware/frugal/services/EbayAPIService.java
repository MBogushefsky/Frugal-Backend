package com.bogaware.frugal.services;

import com.bogaware.frugal.configurations.EbayConfiguration;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class EbayAPIService {

    private OAuth2Api ebayAuthApi;
    private String accessToken;

    @Autowired
    private EbayConfiguration ebayConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Autowired
    public EbayAPIService(OAuth2Api ebayAuthApi) throws IOException {
        this.ebayAuthApi = ebayAuthApi;
        OAuthResponse oAuthResponse = ebayAuthApi.getApplicationToken(Environment.PRODUCTION, Arrays.asList("https://api.ebay.com/oauth/api_scope"));
        accessToken = oAuthResponse.getAccessToken().get().getToken();
    }

    public List<ObjectNode> getProductsByKeyword(String keyword, int page) throws UnsupportedEncodingException, JsonProcessingException {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("OPERATION-NAME", "findItemsByKeywords");
        queryParams.put("SERVICE-VERSION", "1.0.0");
        queryParams.put("SECURITY-APPNAME", "Mitchell-Frugal-PRD-b4195c7c8-244f47ca");
        queryParams.put("RESPONSE-DATA-FORMAT", "JSON");
        queryParams.put("REST-PAYLOAD", "");
        queryParams.put("keywords", keyword);
        queryParams.put("paginationInput.entriesPerPage", "20");
        queryParams.put("paginationInput.pageNumber", String.valueOf(page));
        HttpEntity<String> response = restTemplate.exchange(
                ebayConfiguration.getApiUrl() + "/services/search/FindingService/v1?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                getHeaders(),
                String.class);
        ObjectNode responseNode = (ObjectNode) objectMapper.readTree(response.getBody());
        return productsFromRootNode(responseNode);
    }

    private List<ObjectNode> productsFromRootNode(ObjectNode rootNode) {
        List<ObjectNode> resultProducts = new ArrayList<>();
        ArrayNode productsArray = rootNode.withArray("findItemsByKeywordsResponse").get(0)
                .withArray("searchResult").get(0).withArray("item");
        for (JsonNode productNode : productsArray) {
            ObjectNode product = getProductEssentialsByElement(productNode);
            if (product != null) {
                product.set("extras", getProductExtrasByElement(productNode));
                resultProducts.add(product);
            }
        }
        return resultProducts;
    }

    public ObjectNode getProductEssentialsByElement(JsonNode productNode) {
        try {
            ObjectNode resultNode = objectMapper.createObjectNode();
            String price = null;
            ArrayNode priceEles = productNode.withArray("sellingStatus").get(0).withArray("currentPrice");
            for (JsonNode priceEle : priceEles) {
                if (priceEle.get("@currencyId") != null && priceEle.get("@currencyId").asText()
                        .equalsIgnoreCase("USD")) {
                    price = "$" + decimalFormat.format(Double.parseDouble(priceEle.get("__value__").asText()));
                }
            }
            if (price == null) { return null; }
            resultNode.put("id", productNode.withArray("itemId").get(0).asText());
            resultNode.put("title", productNode.withArray("title").get(0).asText());
            resultNode.put("link", productNode.withArray("viewItemURL").get(0).asText());
            resultNode.put("price", price);
            resultNode.put("imageSrc", productNode.withArray("galleryURL").get(0).asText());
            return resultNode;
        }
        catch (Exception e) {
            return null;
        }
    }

    public ObjectNode getProductExtrasByElement(JsonNode productNode) {
        try {
            ObjectNode resultNode = objectMapper.createObjectNode();
            String shippingPrice = null;
            ArrayNode priceEles = productNode.withArray("shippingInfo").get(0).withArray("shippingServiceCost");
            for (JsonNode priceEle : priceEles) {
                if (priceEle.get("@currencyId") != null && priceEle.get("@currencyId").asText()
                        .equalsIgnoreCase("USD")) {
                    shippingPrice = "$" + decimalFormat.format(Double.parseDouble(priceEle.get("__value__").asText()));
                }
            }
            String shippingType = productNode.withArray("shippingInfo").get(0).withArray("shippingType").get(0).asText();
            resultNode.put("condition", productNode.withArray("condition").get(0).withArray("conditionDisplayName").get(0)
                    .asText());
            resultNode.put("watchCount", Integer.parseInt(productNode.withArray("listingInfo").get(0).withArray("watchCount").get(0).asText()));
            resultNode.put("location", productNode.withArray("location").get(0).asText());
            resultNode.put("shippingTo", productNode.withArray("shippingInfo").get(0).withArray("shipToLocations").get(0).asText());
            resultNode.put("shippingType", shippingType);
            resultNode.put("shippingCost", shippingPrice == null || shippingType.equalsIgnoreCase("Calculated") ?
                    "Calculated" : shippingPrice);
            resultNode.put("freeShipping", shippingPrice.equalsIgnoreCase("$0.00") || shippingType.equalsIgnoreCase("Free"));
            resultNode.put("fastShipping", productNode.withArray("shippingInfo").get(0).withArray("expeditedShipping").get(0)
                    .asText().equalsIgnoreCase("true"));
            return resultNode;
        }
        catch (Exception e) {
            return null;
        }
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

    private HttpEntity<?> getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", accessToken);
        return new HttpEntity<>(headers);
    }
}
