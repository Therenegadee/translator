package therenegadee.translator.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import therenegadee.translator.dto.TranslateQuery;
import therenegadee.translator.dto.TranslateRequest;
import therenegadee.translator.exceptions.BadRequestException;
import therenegadee.translator.repository.TranslatorRepository;
import therenegadee.translator.util.IpAddressIdentifier;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

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
        String translatedText = new JSONObject(translatorApiClient.getTranslation(request))
                .get("destination-text")
                .toString();
        TranslateQuery translateQuery = TranslateQuery.builder()
                .textToTranslate(request.getTextToTranslate())
                .translatedText(translatedText)
                .ipAddress(ipAddress)
                .build();
        translatorRepository.saveTranslateQuery(translateQuery);
        return translatedText;
    }
}
