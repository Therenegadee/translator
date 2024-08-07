package therenegadee.translator.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import therenegadee.translator.dto.TranslateQuery;
import therenegadee.translator.dto.TranslateRequest;
import therenegadee.translator.exceptions.BadRequestException;
import therenegadee.translator.exceptions.InternalServerError;
import therenegadee.translator.repository.TranslatorRepository;
import therenegadee.translator.util.IpAddressIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TranslatorService {
    private final TranslatorApiClient translatorApiClient;
    private final TranslatorRepository translatorRepository;
    private Map<String, Object> availableLanguages;
    private static final ReentrantLock LOCK = new ReentrantLock();

    public Map<String, Object> getAvailableLanguages() {
        LOCK.lock();
        if (availableLanguages == null || availableLanguages.isEmpty()) {
            availableLanguages = new JSONObject(translatorApiClient.getAvailableLanguages()).toMap();
        }
        LOCK.unlock();
        return availableLanguages;
    }

    public String translate(TranslateRequest request) {
        if (!getAvailableLanguages().containsKey(request.getSourceLanguage()) ||
                !getAvailableLanguages().containsKey(request.getDestinationLanguage())) {
            throw new BadRequestException("Исходный язык и язык перевода должны соответствовать кодам из перечня доступных языков!");
        }
        HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String ipAddress = IpAddressIdentifier.getIpAddress(servletRequest);

        String[] wordsForTranslation = request.getTextToTranslate().split(" ");
        String[] translatedWords = new String[wordsForTranslation.length];
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Future<String>[] futures = new Future[wordsForTranslation.length];

        for (int i = 0; i < wordsForTranslation.length; i++) {
            final int index = i;
            futures[i] = executorService.submit(() -> translatorApiClient
                    .getTranslation(wordsForTranslation[index], request.getSourceLanguage(), request.getDestinationLanguage()));
        }

        for (int i = 0; i < futures.length; i++) {
            try {
                translatedWords[i] = new JSONObject(futures[i].get())
                        .get("destination-text")
                        .toString();;
            } catch (Exception e) {
                throw new InternalServerError("Произошла ошибка при переводе текста.");
            }
        }
        String translatedText = String.join(" ", translatedWords);
        TranslateQuery translateQuery = TranslateQuery.builder()
                .textToTranslate(request.getTextToTranslate())
                .translatedText(translatedText)
                .ipAddress(ipAddress)
                .build();
        translatorRepository.saveTranslateQuery(translateQuery);
        executorService.shutdown();
        return translatedText;
    }
}
