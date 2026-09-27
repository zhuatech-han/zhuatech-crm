/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 调用可配置的 OpenAI 兼容模型，并在未配置凭证时拒绝真实模型调用。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Component
public class OpenAiCompatibleGateway {
    private final String provider;
    private final String model;
    private final String apiKey;
    private final RestClient client;


    public OpenAiCompatibleGateway(
        @Value("${zhuatech.ai.provider:local}") String provider,
        @Value("${zhuatech.ai.base-url:https://api.deepseek.com}") String baseUrl,
        @Value("${zhuatech.ai.model:deepseek-chat}") String model,
        @Value("${zhuatech.ai.api-key:}") String apiKey
    ) {
        this.provider = provider;
        this.model = model;
        this.apiKey = apiKey;
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * 返回模型配置状态而不暴露 API 密钥。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Metadata metadata() {
        return new Metadata(provider, model, configured());
    }

    /**
     * 向已配置的模型发起请求并校验模型响应结构。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Optional<String> complete(String systemPrompt, String businessContext) {
        if (!configured()) return Optional.empty();
        try {
            Map<?, ?> response = client.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "model", model,
                    "temperature", 0.2,
                    "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", businessContext)
                    )
                ))
                .retrieve()
                .body(Map.class);
            return extractContent(response);
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    /**
     * 判断模型调用所需配置是否齐备。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    private boolean configured() {
        return !apiKey.isBlank() && !"local".equalsIgnoreCase(provider);
    }

    /**
     * 调用可配置的 OpenAI 兼容模型，并在未配置凭证时拒绝真实模型调用中的 extractContent 操作。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    private Optional<String> extractContent(Map<?, ?> response) {
        if (response == null || !(response.get("choices") instanceof List<?> choices) || choices.isEmpty()) {
            return Optional.empty();
        }
        if (!(choices.getFirst() instanceof Map<?, ?> choice)
            || !(choice.get("message") instanceof Map<?, ?> message)
            || !(message.get("content") instanceof String content)
            || content.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(content.trim());
    }

    /**
     * 封装 Metadata 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Metadata(String provider, String model, boolean configured) {}
}

