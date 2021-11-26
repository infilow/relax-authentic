package com.infilos.abac.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.infilos.abac.api.PolicyRepository;
import com.infilos.abac.api.PolicyRule;
import com.infilos.abac.core.spel.SpelDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

// TODO: watch file change and reload
@Slf4j
public class JsonPolicyRepository implements PolicyRepository {

    private final ObjectMapper objectMapper;
    private final String policyFilePath;
    private final List<PolicyRule> policyRules = new ArrayList<>();

    public JsonPolicyRepository(ObjectMapper objectMapper, String policyFilePath) {
        this.objectMapper = objectMapper;
        this.policyFilePath = policyFilePath;
        this.initiate();
    }

    /**
     * Load rules from json resource file.
     */
    private void initiate() {
        if (!StringUtils.hasText(policyFilePath)) {
            log.error("Load ABAC Policy failed: path invalid");
            return;
        }

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Expression.class, new SpelDeserializer());
        objectMapper.registerModule(module);

        try {
            log.debug("Checking ABAC policy file at: {}", policyFilePath);

            String filePath = policyFilePath.startsWith("/") ? policyFilePath : "/" + policyFilePath;
            URL fileUrl = this.getClass().getResource(filePath);
            if (Objects.isNull(fileUrl)) {
                log.error("Load ABAC Policy failed: file not exists");
                return;
            }

            log.info("Loading ABAC policy from custom file: {}", policyFilePath);

            List<PolicyRule> rules = objectMapper.readValue(ResourceUtils.getFile(fileUrl), new TypeReference<List<PolicyRule>>() {
            });
            if (Objects.nonNull(rules)) {
                policyRules.addAll(rules);
            }

            if (rules.isEmpty()) {
                log.warn("Load ABAC Policy succed: empty.");
            } else {
                log.info("Load ABAC Policy succed: " + rules.size());
            }
        } catch (JsonMappingException e) {
            log.error("An error occurred while parsing the ABAC policy file.", e);
        } catch (IOException e) {
            log.error("An error occurred while reading the ABAC policy file.", e);
        }
    }

    @Override
    public List<PolicyRule> findAllPolicyRules() {
        return policyRules;
    }
}
