/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal.handler;

import io.jooby.Context;
import io.jooby.Route;

import javax.annotation.Nonnull;

public class SendDirect implements LinkedHandler {
  private Route.Handler next;

  public SendDirect(Route.Handler next) {
    this.next = next;
  }

  @Nonnull @Override public Object apply(@Nonnull Context ctx) throws Exception {
    try {
      next.apply(ctx);
      return ctx;
    } catch (Throwable x) {
      ctx.sendError(x);
      return x;
    }
  }

  @Override public Route.Handler next() {
    return next;
  }
}
