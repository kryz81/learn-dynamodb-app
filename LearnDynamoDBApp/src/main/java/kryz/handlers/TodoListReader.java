package kryz.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import kryz.services.DbService;

public class TodoListReader implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DbService dbService = new DbService(System.getenv("TODO_TABLE"));

    private final Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        var response = new APIGatewayProxyResponseEvent();
        String age = event.getQueryStringParameters().get("age");
        try {
            var items = dbService.filterByAge(age);
            return response.withStatusCode(200).withBody(gson.toJson(items));
        } catch (RuntimeException e) {
            return response.withStatusCode(500).withBody("Something went wrong: " + e.getMessage());
        }
    }
}
