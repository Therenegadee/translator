package therenegadee.translator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import therenegadee.translator.dto.TranslateRequest;
import therenegadee.translator.exceptions.RestTemplateResponseErrorHandler;
import therenegadee.translator.interceptor.LoggingClientHttpRequestInterceptor;

@Service
@RequiredArgsConstructor
public class TranslatorApiClient {

    private final RestTemplateBuilder restTemplateBuilder;
    @Value("${translator-api.url}")
    private String translatorApiUrl;

    private RestTemplate getRestTemplate() {
        return restTemplateBuilder
                .rootUri(translatorApiUrl)
                .interceptors(new LoggingClientHttpRequestInterceptor())
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    public String getAvailableLanguages() {
        return getRestTemplate()
                .exchange("/languages", HttpMethod.GET, null, String.class)
                .getBody();

    }

    public String getTranslation(TranslateRequest request) {
        String urlTemplate = UriComponentsBuilder.fromPath("/translate")
                .queryParam("sl", request.getSourceLanguage())
                .queryParam("dl", request.getDestinationLanguage())
                .queryParam("text", request.getTextToTranslate())
                .encode()
                .toUriString();
        return getRestTemplate()
                .exchange(urlTemplate,
                        HttpMethod.GET,
                        null,
                        String.class,
                        request.getSourceLanguage(),
                        request.getTextToTranslate(),
                        request.getDestinationLanguage())
                .getBody();
    }
}
