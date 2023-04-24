package com.wjl.gamll.product;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/24
 * @description
 */

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

// https://juejin.cn/post/6970558076642394142
@Slf4j
public class CompletableFutureTest {

    @Test
    void test() throws ExecutionException, InterruptedException {
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        return "a";
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenApplyAsync(res -> {
                    System.out.println(res);
                    return "1-" + res;
                });

        CompletableFuture<Void> async = CompletableFuture.supplyAsync(() -> 1).thenAcceptBothAsync(stringCompletableFuture, new BiConsumer<Integer, String>() {
            @Override
            public void accept(Integer integer, String s) {
                System.out.println("i" + integer + ",s=" + s);
            }
        });
        System.out.println(stringCompletableFuture.get());
    }

}
