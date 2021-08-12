package com.bogaware.frugal.services;

import com.bogaware.frugal.configurations.AmazonConfiguration;
import com.bogaware.frugal.configurations.WebScrapingConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
import java.util.stream.Collectors;

@Service
public class AmazonScrapperService {

    @Autowired
    private AmazonConfiguration amazonConfiguration;

    @Autowired
    private WebScrapingConfiguration webScrapingConfiguration;

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<ObjectNode> getProductsByKeyword(String keyword, int page) throws UnsupportedEncodingException {
        List<ObjectNode> resultProducts = new ArrayList<>();
        String url = amazonConfiguration.getUrl() + "/s?k=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());
        if (page != 1) {
            url += "&page=" + page;
        }
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(webScrapingConfiguration.getUserAgent())
                    .referrer(webScrapingConfiguration.getReferrer())
                    .ignoreContentType(webScrapingConfiguration.isIgnoreContentType())
                    .followRedirects(webScrapingConfiguration.isFollowRedirects())
                    .timeout(webScrapingConfiguration.getTimeout())
                    .execute();
            Document document = response.parse();
            Elements searchResultElements = document.getElementsByAttributeValue("data-component-type", "s-search-result");
            for (Element searchResult : searchResultElements) {
                if (searchResult.hasAttr("data-uuid") &&
                        searchResult.getElementsByClass("s-sponsored-label-info-icon").size() == 0) {
                    try {
                        ObjectNode product = getProductEssentialsByElement(searchResult);
                        if (product != null) {
                            ObjectNode extras = getProductExtrasByElement(searchResult);
                            if (extras != null) {
                                product.set("extras", extras);
                            }
                            resultProducts.add(product);
                        }
                    } catch (Exception e) {
                        System.out.println("Could not parse a product");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultProducts;
    }

    public ObjectNode getProductSpecsById(String productId) throws UnsupportedEncodingException {
        ObjectNode resultSpecs = objectMapper.createObjectNode();
        String url = amazonConfiguration.getUrl() + "/dp/" + URLEncoder.encode(productId, StandardCharsets.UTF_8.toString());
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(webScrapingConfiguration.getUserAgent())
                    .referrer(webScrapingConfiguration.getReferrer())
                    .ignoreContentType(webScrapingConfiguration.isIgnoreContentType())
                    .followRedirects(webScrapingConfiguration.isFollowRedirects())
                    .timeout(webScrapingConfiguration.getTimeout())
                    .execute();
            Document document = response.parse();
            Elements widgetElements = document.select(".celwidget:contains(Detail)");
            for (Element widgetElement : widgetElements) {
                Elements tableElements = widgetElement.getElementsByTag("table");
                for (Element tableElement : tableElements) {
                    Elements trElementsOfTable = tableElement.getElementsByTag("tr");
                    for (Element trElementOfTable : trElementsOfTable) {
                        Elements thElements  = trElementOfTable.getElementsByTag("th");
                        Elements tdElements  = trElementOfTable.getElementsByTag("td");
                        if (thElements.size() == 1 && tdElements.size() == 1) {
                            resultSpecs.put(thElements.first().text(), tdElements.last().text());
                        }
                        else if (tdElements.size() == 2) {
                            resultSpecs.put(tdElements.first().text(), tdElements.last().text());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSpecs;
    }

    public ObjectNode getProductEssentialsByElement(Element productElement) {
        try {
            ObjectNode resultNode = objectMapper.createObjectNode();
            Element productImageAreaEle = productElement
                    .getElementsByAttributeValue("data-component-type", "s-product-image")
                    .first();
            Element productImageEle = productImageAreaEle
                    .getElementsByTag("img")
                    .first();
            Element priceAreaEle = productElement.getElementsByClass("a-price").first()
                    .getElementsByClass("a-offscreen").first();
            resultNode.put("id", productElement.attr("data-asin"));
            resultNode.put("title", productImageEle.attr("alt"));
            resultNode.put("link", amazonConfiguration.getUrl() + "/dp/" + resultNode.get("id").asText());
            resultNode.put("price", priceAreaEle.text());
            resultNode.put("imageSrc", productImageEle.attr("src"));
            return resultNode;
        }
        catch (Exception e) {
            return null;
        }
    }

    public ObjectNode getProductExtrasByElement(Element productElement) {
        try {
            ObjectNode resultNode = objectMapper.createObjectNode();
            Elements ariaLabelElements = productElement.getElementsByAttribute("aria-label");
            for (Element ariaLabel : ariaLabelElements) {
                if (ariaLabel.attr("aria-label").contains("out of")) {
                    String[] splitRating = ariaLabel.attr("aria-label").split(" out of ");
                    resultNode.put("rating", Double.parseDouble(splitRating[0]));
                    resultNode.put("totalRating", Double.parseDouble(splitRating[1]
                            .replace(" stars", "")));
                }
                else if (StringUtils.isNumeric(ariaLabel.attr("aria-label"))) {
                    resultNode.put("reviewCount", Integer.parseInt(ariaLabel.attr("aria-label")));
                }
            }
            resultNode.put("prime", productElement.getElementsByClass("a-icon-prime").size() > 0);
            return resultNode;
        }
        catch (Exception e) {
            return null;
        }
    }
}
