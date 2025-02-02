package therenegadee.translator.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().is5xxServerError() ||
                httpResponse.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode().is5xxServerError()) {
            switch (httpResponse.getStatusCode()) {
                case HttpStatus.INTERNAL_SERVER_ERROR -> throw new InternalServerError("Ошибка на стороне сервиса переводов");
                default -> throw new HttpClientErrorException(httpResponse.getStatusCode());
            }
        } else if (httpResponse.getStatusCode().is4xxClientError()) {
            switch (httpResponse.getStatusCode()) {
                case HttpStatus.BAD_REQUEST -> throw new BadRequestException(httpResponse.getStatusText());
                case HttpStatus.NOT_FOUND -> throw new NotFoundException(httpResponse.getStatusText());
                default ->  throw new HttpClientErrorException(httpResponse.getStatusCode());
            }
        }
    }
}
