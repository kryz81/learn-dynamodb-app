package kryz.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import kryz.services.DbService;

import java.util.UUID;

public class TodoClose implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DbService dbService = new DbService(System.getenv("TODO_TABLE"));

    private final Gson gson = new Gson();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        var response = new APIGatewayProxyResponseEvent();
        String todoId = event.getPathParameters().get("id");
        String username = event.getPathParameters().get("username");
        try {
            UUID id = UUID.fromString(todoId);
            var item = dbService.getItemById(id, username);

            if (item == null) {
                return response.withStatusCode(404).withBody("Not found");
            }

            dbService.closeItem(item.get("id").toString(), username);
            return response.withStatusCode(200).withBody("");
        } catch (IllegalArgumentException e) {
            return response.withStatusCode(400).withBody("Invalid ID");
        }
    }
}
