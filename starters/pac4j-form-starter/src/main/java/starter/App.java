package starter;

import io.jooby.Jooby;
import io.jooby.ModelAndView;
import io.jooby.flyway.FlywayModule;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.hikari.HikariModule;
import io.jooby.jdbi.JdbiModule;
import io.jooby.jdbi.TransactionalRequest;
import io.jooby.pac4j.Pac4jModule;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.client.indirect.FormClient;
import starter.domain.UserRepository;
import starter.service.PasswordService;
import starter.service.UserAuthenticator;

public class App extends Jooby {
  {
    /** DataSource module: */
    install(new HikariModule());

    /** Database migration module: */
    install(new FlywayModule());

    /** Jdbi module: */
    install(new JdbiModule().sqlObjects(UserRepository.class));

    /** Template engine: */
    install(new HandlebarsModule());

    /** Open handle per request: */
    decorator(new TransactionalRequest());

    get("/login", ctx -> new ModelAndView("login.hbs"));

    install(new Pac4jModule()
        .client(conf -> new FormClient("/login", authenticator()))
    );

    get("/", ctx -> new ModelAndView("welcome.hbs").put("user", ctx.getUser()));
    post("/test", ctx -> "ok");
  }

  private Authenticator<UsernamePasswordCredentials> authenticator() {
    return (credentials, context) ->
        new UserAuthenticator(require(UserRepository.class), new PasswordService())
            .validate(credentials, context);
  }

  public static void main(String[] args) {
    runApp(args, App::new);
  }
}
