package therenegadee.translator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import therenegadee.translator.dto.TranslateRequest;
import therenegadee.translator.service.TranslatorService;

@RestController
@RequestMapping("/api/translator")
@RequiredArgsConstructor
public class TranslateController {
    private final TranslatorService translatorService;

    @GetMapping("/languages")
    @Operation(summary = "Получение списка доступных языков.",
            description = "Получение списка кодов доступных языков от сервиса переводов.")
    public ResponseEntity<?> getAvailableLanguages() {
        return ResponseEntity.ok(translatorService.getAvailableLanguages());
    }

    @Operation(summary = "Перевод текста.",
            description = "Перевод текста с выбранного исходного языка на выбранный целевой язык.")
    @PostMapping("/translate")
    public ResponseEntity<?> translate(@Valid @RequestBody
                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                                               examples = {@ExampleObject(name = "Запрос на перевод текста",
                                                       value = "{\n" +
                                                               "    \"textToTranslate\" = \"hello world, i am human\"\n" +
                                                               "    \"sourceLanguage\" = \"en\"\n" +
                                                               "    \"destinationLanguage\" = \"ru\"\n" +
                                                               "}")}
                                       )) TranslateRequest request) {
        return ResponseEntity.ok(translatorService.translate(request));
    }
}
