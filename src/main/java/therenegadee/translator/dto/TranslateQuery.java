package therenegadee.translator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TranslateQuery {
    private String textToTranslate;
    private String translatedText;
    private String ipAddress;
}
