package kryz.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import kryz.services.DbService;

import java.util.UUID;

public class TodoReader implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DbService dbService = new DbService(System.getenv("TODO_TABLE"));

    private final Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        var response = new APIGatewayProxyResponseEvent();
        String todoId = event.getPathParameters().get("id");
        String username = event.getPathParameters().get("username");
        try {
            UUID id = UUID.fromString(todoId);
            var item = dbService.getItemById(id, username);
            return response.withStatusCode(200).withBody(gson.toJson(item));
        } catch (IllegalArgumentException e) {
            return response.withStatusCode(400).withBody("Invalid ID");
        }
    }
}
