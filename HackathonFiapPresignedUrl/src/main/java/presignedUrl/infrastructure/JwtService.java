package presignedUrl.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Optional;

public class JwtService {
    private final ObjectMapper objectMapper;

    public JwtService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<String> extractUsername(String token) {
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                return Optional.empty();
            }

            JsonNode payload = objectMapper.readTree(
                new String(Base64.getDecoder().decode(chunks[1]))
            );

            if (payload.has("email")) {
                return Optional.of(payload.get("email").asText());
            }
            if (payload.has("username")) {
                return Optional.of(payload.get("username").asText());
            }

            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}