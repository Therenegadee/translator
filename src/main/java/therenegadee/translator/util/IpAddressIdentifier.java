package therenegadee.translator.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IpAddressIdentifier {

    public static String getIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        return Objects.nonNull(xForwardedForHeader)
                ? xForwardedForHeader.split(",")[0]
                : request.getRemoteAddr();
    }
}
