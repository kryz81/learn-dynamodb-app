package kryz.services;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .consistentRead(true)
                .attributesToGet("id", "username", "age", "tags")
                .build();

        var item = client.getItem(request).item();

        Map<String, Object> result = new HashMap<>();
        result.put("id", item.get("id").s());
        result.put("username", item.get("username").s());
        result.put("age", item.get("age").n());
        result.put("tags", item.get("tags").ss());

        return result;
    }

    public List<Map<String, Object>> getItems(String age) {
        var request = QueryRequest
                .builder()
                .tableName(tableName)
                .indexName("age-index")
                .consistentRead(false)
                .keyConditionExpression("#age = :age")
                .expressionAttributeNames(Map.of(
                        "#age",
                        "age")
                )
                .expressionAttributeValues(Map.of(
                        ":age",
                        AttributeValue.builder().n(age).build()
                ))
                .build();

        return client.query(request).items().stream().map(item -> {
            Map<String, Object> record = new HashMap<>();
            record.put("id", item.get("id").s());
            record.put("username", item.get("username").s());
            record.put("age", item.get("age").n());
            record.put("tags", item.get("tags").ss());
            return record;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> filterByAge(String age) {
        var request = ScanRequest
                .builder()
                .tableName(tableName)
                .filterExpression("#age >= :age")
                .expressionAttributeNames(Map.of(
                        "#age",
                        "age")
                )
                .expressionAttributeValues(Map.of(
                        ":age",
                        AttributeValue.builder().n(age).build()
                ))
                .build();

        return client.scan(request).items().stream().map(item -> {
            Map<String, Object> record = new HashMap<>();
            record.put("id", item.get("id").s());
            record.put("username", item.get("username").s());
            record.put("age", item.get("age").n());
            record.put("tags", item.get("tags").ss());
            return record;
        }).collect(Collectors.toList());
    }
}
