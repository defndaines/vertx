package rtb.role;

import ec.util.MersenneTwister;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.stream.IntStream;

public final class RoleEmbedded extends AbstractVerticle {

    public static void main(String... args) {
        RoleEmbedded rpg = new RoleEmbedded();
        rpg.init(Vertx.vertx(), null);
        rpg.start();
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/roll/abilities").handler(this::handleAbilities);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    /** Generate six ability score rolls. */
    private void handleAbilities(RoutingContext routingContext) {
        MersenneTwister twister = new MersenneTwister();
        JsonArray rolls = new JsonArray();
        for (int i = 0; i < 6; i++)
            rolls.add(IntStream.generate(() -> twister.nextInt(6))
                               .limit(4)
                               .sorted()
                               .skip(1)
                               .sum());

        routingContext.response()
                      .putHeader("content-type", "application/json")
                      .end(rolls.encodePrettily());
    }
}
