package therenegadee.translator.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import therenegadee.translator.dto.TranslateQuery;

@Repository
@RequiredArgsConstructor
public class TranslatorRepository {
    private final JdbcTemplate jdbcTemplate;

    public Long saveTranslateQuery(TranslateQuery translateQuery) {
        String query = "INSERT INTO translator.translate_queries (ip_address_id,text_to_translate,translated_text) "
                + "VALUES (?, ?, ?) RETURNING id";
        Long ipAddressId = getIpAddressId(translateQuery.getIpAddress());
        return jdbcTemplate.queryForObject(query, Long.class,
                ipAddressId, translateQuery.getTextToTranslate(), translateQuery.getTranslatedText());
    }

    public Long getIpAddressId(String ipAddress) {
        String query = "WITH temp AS " +
                "(INSERT INTO translator.ip_addresses (ip_address) VALUES (?) ON CONFLICT DO NOTHING RETURNING id)" +
                "SELECT * FROM temp UNION ALL SELECT i.id FROM translator.ip_addresses i " +
                "WHERE ip_address = ? LIMIT 1;";
        return jdbcTemplate.queryForObject(query, Long.class, ipAddress, ipAddress);
    }
}
