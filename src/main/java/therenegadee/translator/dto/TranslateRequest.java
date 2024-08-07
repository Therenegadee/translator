package therenegadee.translator.dto;

import lombok.Getter;
import jakarta.validation.constraints.*;

@Getter
public class TranslateRequest {
    @NotNull(message = "Текст для перевода не может быть null.")
    @NotBlank(message = "Текст для перевода не может быть пустым.")
    private String textToTranslate;
    @NotNull(message = "Код исходного языка не может быть null.")
    @NotBlank(message = "Код исходного языка не может быть пустым.")
    private String sourceLanguage;
    @NotNull(message = "Код языка перевода не может быть null.")
    @NotBlank(message = "Код языка перевода не может быть пустым.")
    private String destinationLanguage;
}
