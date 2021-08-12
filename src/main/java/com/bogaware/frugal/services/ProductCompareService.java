package com.bogaware.frugal.services;

import com.bogaware.frugal.configurations.ProductCompareConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class ProductCompareService {

    @Autowired
    private ProductCompareConfiguration productCompareConfiguration;

    @Autowired
    private AmazonScrapperService amazonScrapperService;

    @Autowired
    private EbayAPIService ebayAPIService;

    @Autowired
    private WalmartService walmartService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();

    public ObjectNode getProductSpecs(String productId, String productPlatform) throws UnsupportedEncodingException {
        ObjectNode resultNode = objectMapper.createObjectNode();
        if (productPlatform.equalsIgnoreCase("amazon")) {
            resultNode = amazonScrapperService.getProductSpecsById(productId);
        }
        else if (productPlatform.equalsIgnoreCase("ebay")) {
            //resultNode = ebayService.getProductSpecsById(productId);
        }
        else if (productPlatform.equalsIgnoreCase("walmart")) {
            //resultNode = walmartService.getProductSpecsById(productId);
        }
        return resultNode;
    }

    public ObjectNode compareProductSpecs(ObjectNode firstProductSpecs, ObjectNode secondProductSpecs) {
        ObjectNode resultCompared = objectMapper.createObjectNode();
        List<ObjectNode> compareListProductSpecs = new ArrayList<>();
        Iterator<String> firstIterator = firstProductSpecs.fieldNames();
        while (firstIterator.hasNext()) {
            String firstKey = firstIterator.next();
            String firstValue = firstProductSpecs.get(firstKey).asText();
            Iterator<String> secondIterator = secondProductSpecs.fieldNames();
            while (secondIterator.hasNext()) {
                ObjectNode comparedProductSpecs = objectMapper.createObjectNode();
                String secondKey = secondIterator.next();
                String secondValue = secondProductSpecs.get(secondKey).asText();
                double compareFullPercent = jaroWinklerDistance.apply(firstKey + firstValue, secondKey + secondValue);
                double compareKeyPercent = jaroWinklerDistance.apply(firstKey, secondKey);
                double compareValuePercent = jaroWinklerDistance.apply(firstValue, secondValue);
                comparedProductSpecs.put("firstKey", firstKey);
                comparedProductSpecs.put("firstValue", firstValue);
                comparedProductSpecs.put("secondKey", secondKey);
                comparedProductSpecs.put("secondValue", secondValue);
                comparedProductSpecs.put("fullSimilarity", compareFullPercent);
                comparedProductSpecs.put("keySimilarity", compareKeyPercent);
                comparedProductSpecs.put("valueSimilarity", compareValuePercent);
                compareListProductSpecs.add(comparedProductSpecs);
            }
        }
        Collections.sort(compareListProductSpecs, (product1, product2) -> {
//            double product1Similarity = (product1.get("fullSimilarity").asDouble() +
//                    product1.get("keySimilarity").asDouble() +
//                    product1.get("valueSimilarity").asDouble()) / 3;
//            double product2Similarity = (product2.get("fullSimilarity").asDouble() +
//                    product2.get("keySimilarity").asDouble() +
//                    product2.get("valueSimilarity").asDouble()) / 3;
            double product1Similarity = product1.get("keySimilarity").asDouble();
            double product2Similarity = product2.get("keySimilarity").asDouble();
            if (product1Similarity < product2Similarity) {
                return 1;
            }
            else if (product1Similarity > product2Similarity) {
                return -1;
            }
            return 0;
        });
        List<String> firstKeyUsed = new ArrayList<>();
        List<String> secondKeyUsed = new ArrayList<>();
        List<ObjectNode> resultComparedProductSpecs = new ArrayList<>();
        for (ObjectNode compareProductSpec : compareListProductSpecs) {
            if (firstKeyUsed.size() == firstProductSpecs.size() && secondKeyUsed.size() == secondProductSpecs.size()) {
                break;
            }
            else {
                if (!firstKeyUsed.contains(compareProductSpec.get("firstKey").asText())) {
                    firstKeyUsed.add(compareProductSpec.get("firstKey").asText());
                }
                if (!secondKeyUsed.contains(compareProductSpec.get("secondKey").asText())) {
                    secondKeyUsed.add(compareProductSpec.get("secondKey").asText());
                }
                resultComparedProductSpecs.add(compareProductSpec);
            }
        }
        resultCompared.set("comparison", objectMapper.valueToTree(resultComparedProductSpecs));
        resultCompared.set("firstProductSpecs", firstProductSpecs);
        resultCompared.set("secondProductSpecs", secondProductSpecs);
        return resultCompared;
    }
}
