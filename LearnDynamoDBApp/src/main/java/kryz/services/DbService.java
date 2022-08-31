package kryz.services;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DbService {
    private final String tableName;

    private static final DynamoDbClient client = DynamoDbClient.builder().build();

    public DbService(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, Object> getItemById(UUID id, String username) {
        var request = GetItemRequest
                .builder()
                .tableName(tableName)
                .key(Map.of(
                        "id",
                        AttributeValue.builder().s(id.toString()).build(),
                        "username",
                        AttributeValue.builder().s(username).build()
                ))
                .build();

        var item = client.getItem(request).item();

        Map<String, Object> result = new HashMap<>();
        result.put("id", item.get("id").s());
        result.put("username", item.get("username").s());
        result.put("age", item.get("age").n());
        result.put("tags", item.get("tags").ss());

        return result;
    }
}
