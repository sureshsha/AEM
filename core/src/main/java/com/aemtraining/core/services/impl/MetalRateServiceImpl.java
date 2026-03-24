package com.aemtraining.core.services.impl;

import com.aemtraining.core.configs.MetalPriceApiConfig;
import com.aemtraining.core.services.MetalRateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component(service = MetalRateService.class)
@Designate(ocd = MetalPriceApiConfig.class)
public class MetalRateServiceImpl implements MetalRateService {

    private static final Logger LOG = LoggerFactory.getLogger(MetalRateServiceImpl.class);
    private static final String API_URL = "https://api.metalpriceapi.com/v1/latest";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private volatile MetalPriceApiConfig config;

    @Activate
    @Modified
    protected void activate(MetalPriceApiConfig config) {
        this.config = config;
    }

    @Override
    public void fetchAndStoreRates() {
        if (config == null || StringUtils.isBlank(config.apiKey())) {
            LOG.warn("MetalpriceAPI key missing");
            return;
        }

        HttpURLConnection connection = null;

        try {
            String endpoint = API_URL
                    + "?api_key=" + config.apiKey()
                    + "&base=" + config.base()
                    + "&currencies=" + config.currencies();

            LOG.info("Calling MetalpriceAPI latest endpoint");

            connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                LOG.error("MetalpriceAPI call failed. HTTP {}", responseCode);
                return;
            }

            try (InputStream is = connection.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(is);

                /*
                 * Typical latest response contains:
                 * {
                 *   "success": true,
                 *   "base": "INR",
                 *   "rates": {
                 *      "XAU": ...,
                 *      "XAG": ...
                 *   }
                 * }
                 */
                boolean success = root.path("success").asBoolean(false);
                if (!success) {
                    LOG.error("MetalpriceAPI returned success=false: {}", root.toPrettyString());
                    return;
                }

                JsonNode rates = root.path("rates");
                double goldRate = rates.path("XAU").asDouble();
                double silverRate = rates.path("XAG").asDouble();

                saveRates(goldRate, silverRate, root.path("base").asText(config.base()));
            }

        } catch (Exception e) {
            LOG.error("Error fetching/storing metal prices", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void saveRates(double goldRate, double silverRate, String base) {
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, "metalrateservice");

        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            Resource resource = ResourceUtil.getOrCreateResource(
                    resolver,
                    config.storagePath(),
                    "nt:unstructured",
                    "nt:unstructured",
                    true
            );

            ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
            if (map == null) {
                LOG.error("Could not adapt resource to ModifiableValueMap");
                return;
            }

            map.put("goldRate", goldRate);
            map.put("silverRate", silverRate);
            map.put("base", base);
            map.put("updatedAt", Instant.now().toString());

            resolver.commit();

            LOG.info("Stored Gold={} Silver={} Base={}", goldRate, silverRate, base);
        } catch (Exception e) {
            LOG.error("Error saving rates to JCR", e);
        }
    }
}