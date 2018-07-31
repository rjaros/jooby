package io.jooby;

import javax.annotation.Nonnull;

public interface ErrorHandler {

  ErrorHandler DEFAULT = (ctx, cause, statusCode) -> {
    String message = cause.getMessage();
    StringBuilder html = new StringBuilder("<!doctype html>\n")
        .append("<html>\n")
        .append("<head>\n")
        //        .append("<meta charset=\"").append(ctx.charset().name()).append("\">\n")
        .append("<style>\n")
        .append("body {font-family: \"open sans\",sans-serif; margin-left: 20px;}\n")
        .append("h1 {font-weight: 300; line-height: 44px; margin: 25px 0 0 0;}\n")
        .append("h2 {font-size: 16px;font-weight: 300; line-height: 44px; margin: 0;}\n")
        .append("footer {font-weight: 300; line-height: 44px; margin-top: 10px;}\n")
        .append("hr {background-color: #f7f7f9;}\n")
        .append("div.trace {border:1px solid #e1e1e8; background-color: #f7f7f9;}\n")
        .append("p {padding-left: 20px;}\n")
        .append("p.tab {padding-left: 40px;}\n")
        .append("</style>\n")
        .append("<title>\n")
        .append(statusCode).append(" ").append(statusCode.reason())
        .append("\n</title>\n")
        .append("<body>\n")
        .append("<h1>").append(statusCode.reason()).append("</h1>\n")
        .append("<hr>");

    html.append("<h2>message: ").append(message).append("</h2>\n");
    html.append("<h2>status: ").append(statusCode).append("</h2>\n");

    html.append("</body>\n")
        .append("</html>");

    ctx.statusCode(statusCode)
        .send(html.toString());
  };

  @Nonnull void apply(@Nonnull Context ctx, @Nonnull Throwable cause, @Nonnull StatusCode statusCode);

  @Nonnull default ErrorHandler then(@Nonnull ErrorHandler next) {
    return (ctx, cause, statusCode) -> {
      apply(ctx, cause, statusCode);
      if (!ctx.isResponseStarted()) {
        next.apply(ctx, cause, statusCode);
      }
    };
  }
}