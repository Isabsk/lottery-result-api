package api;

import api.handlers.Check;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class Main {

    private static final String API_KEY = System.getenv("LOTTO_API_KEY");

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        });

        app.before(ctx -> checkApiKey(ctx));

        app.get("/", ctx -> ctx.json("Lotto Checker API is running."));

        app.get("/{time}/{series}/{number}", Check::getData);

        String portEnv = System.getenv("X_ZOHO_CATALYST_LISTEN_PORT");
        int port;

        if (portEnv != null && !portEnv.isEmpty()) {
            port = Integer.parseInt(portEnv);
        } else {
            port = 7070;
        }

        app.start(port);
    }

    private static void checkApiKey(Context ctx) {
        String apiKey = ctx.queryParam("apiKey");

        if (apiKey == null || !apiKey.equals(API_KEY)) {
            throw new io.javalin.http.HttpResponseException(401, "Unauthorized: Invalid or missing API Key");
        }
    }
}
