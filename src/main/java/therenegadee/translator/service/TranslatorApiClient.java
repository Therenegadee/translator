package therenegadee.translator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
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

    public String getTranslation(String textToTranslate, String sourceLanguage, String destinationLanguage) {
        String urlTemplate = UriComponentsBuilder.fromPath("/translate")
                .queryParam("sl", sourceLanguage)
                .queryParam("dl", destinationLanguage)
                .queryParam("text", textToTranslate)
                .encode()
                .toUriString();
        return getRestTemplate()
                .exchange(urlTemplate,
                        HttpMethod.GET,
                        null,
                        String.class)
                .getBody();
    }
}
