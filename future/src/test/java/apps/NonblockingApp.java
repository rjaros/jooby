package apps;

import io.jooby.App;
import io.jooby.Mode;
import io.jooby.netty.Netty;
import io.jooby.utow.Utow;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class NonblockingApp extends App {
  {
    get("/future", ctx -> {
      System.out.println("Scheduled: " + Thread.currentThread().getName());
      return supplyAsync(() -> ctx.query("n").intValue(1))
          .thenApply(x -> {
            System.out.println("Apply1: " + Thread.currentThread().getName());
            return x * 2;
          })
          .thenApply(x -> {
            System.out.println("Apply2: " + Thread.currentThread().getName());
            return x * 2;
          });
    });

    get("/flowable", ctx -> {
      System.out.println("Scheduled: " + Thread.currentThread().getName());
      return Flowable.fromCallable(() -> ctx.query("n").intValue(1))
          .map(x -> {
            System.out.println("Apply1: " + Thread.currentThread().getName());
            return x * 2;
          })
          .map(x -> {
            System.out.println("Apply2: " + Thread.currentThread().getName());
            return x * 2;
          })
          .observeOn(Schedulers.computation())
          .subscribeOn(Schedulers.io());
    });

    get("/single", ctx -> {
      System.out.println("Scheduled: " + Thread.currentThread().getName());
      return Single.fromCallable(() -> ctx.query("n").intValue(1))
          .map(x -> {
            System.out.println("Apply1: " + Thread.currentThread().getName());
            return x * 2;
          })
          .map(x -> {
            System.out.println("Apply2: " + Thread.currentThread().getName());
            return x * 2;
          })
          .observeOn(Schedulers.computation())
          .subscribeOn(Schedulers.io());
    });
  }

  public static void main(String[] args) {
    new Netty()
        .deploy(new NonblockingApp().mode(Mode.NIO))
        .start()
        .join();
  }
}