package api;

import api.handlers.Check;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class Main {

    private static final String API_KEY = System.getenv("API_KEY");

    public static void main(String[] args) {
        System.out.println(System.getenv("API_KEY"));
        Javalin app = Javalin.create();

        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        app.before(ctx -> checkApiKey(ctx));
        
        app.options("/*", ctx -> {
            ctx.status(204);
        });

        app.get("/{time}/{series}/{number}", Check::getData);

        app.start(7070);
    }

    private static void checkApiKey(Context ctx) {
        String apiKey = ctx.header("x-api-key");

        if (apiKey == null || !apiKey.equals(API_KEY)) {
            throw new io.javalin.http.HttpResponseException(401, "Unauthorized: Invalid or missing API Key");
        }
    }
}
