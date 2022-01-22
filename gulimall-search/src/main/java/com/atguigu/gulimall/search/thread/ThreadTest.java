package com.atguigu.gulimall.search.thread;

import java.util.concurrent.*;

public class ThreadTest {

//     @param corePoolSize the number of threads to keep in the pool, even
//     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
//     * @param maximumPoolSize the maximum number of threads to allow in the
//     *        pool
//     * @param keepAliveTime when the number of threads is greater than
//     *        the core, this is the maximum time that excess idle threads
//     *        will wait for new tasks before terminating.
//     * @param unit the time unit for the {@code keepAliveTime} argument
//     * @param workQueue the queue to use for holding tasks before they are
//     *        executed.  This queue will hold only the {@code Runnable}
//     *        tasks submitted by the {@code execute} method.
//     * @throws IllegalArgumentException if one of the following holds:<br>
//     *         {@code corePoolSize < 0}<br>
//     *         {@code keepAliveTime < 0}<br>
//     *         {@code maximumPoolSize <= 0}<br>
//     *         {@code maximumPoolSize < corePoolSize}
//     * @throws NullPointerException if {@code workQueue} is null

    //      corePoolSize 核心线程数（allowCoreThreadTimeOut）
//      最大线程数量，控制资源
//      keepAliveTime  存活时间 如果当前线程数量大于core数量，释放空闲线程，只要空闲线程大于指定的keepAlivetime  maximumPoolSize-corePoolSize
//      unit 时间单位
//      workQueue 阻塞队列，如果任务有很多就会将多的任务放入队列，只要有空闲线程就会去队列取出新的任务继续执行
//      ThreadFactory 创建线程工厂
//      RejectedExecutionHandler 拒绝策略
//ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
//        5,
//        200,
//        10,
//        TimeUnit.SECONDS,
//        new LinkedBlockingDeque<>(100000), //new LinkedBlockingDeque<>() 默认是Interger的最大值
//        Executors.defaultThreadFactory(),
//        new ThreadPoolExecutor.AbortPolicy());
    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        方式一 :传入无返回值的runable对象
//        System.out.println("main.........start");
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程: " + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果: " + i);
//        }, executor);
//        System.out.println("main.........end");

//        方式二 :传入有返回值的runable对象,但是无入参
//        System.out.println("main.........start");
//        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程: " + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果: " + i);
//            try {
//                Thread.sleep(100000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return i;
//        }, executor);
//        Integer integer = completableFuture.get();
//        主线程获得返回值时是阻塞的
//        System.out.println("main.........end"+integer);

//        方式三 :传入有返回值的runable对象,但是无入参  , 手动捕获异常
//        System.out.println("main.........start");
//        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
//                    System.out.println("当前线程: " + Thread.currentThread().getId());
//                    int i = 10;
//                    try {
//                        i = 10 / 0;
//                        System.out.println("运行结果: " + i);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    return i;
//                }, executor).whenCompleteAsync((res, exce) -> {
////           虽然能得到异常,但是没法返回结果
//                    System.out.println("当前whenCompleteAsync线程: " + Thread.currentThread().getId());
//                    System.out.println("异步任务完成: 结果是" + res + "异常是 " + exce);
//                })
//                .exceptionally((throwable -> {
////          得到异常返回结果
//                    return 10;
//                }));
//        Integer integer = completableFuture.get();
//        System.out.println("main.........end    " + integer);


//        方式四 :传入有返回值的runable对象,但是无入参
//        System.out.println("main.........start");
//        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
//                    System.out.println("当前线程: " + Thread.currentThread().getId());
//                    int i  = 10 / 0;
//                        System.out.println("运行结果: " + i);
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    return i;
//                }, executor).whenCompleteAsync((res, exce) -> {
////           虽然能得到异常,但是没法返回结果
//                    System.out.println("当前whenCompleteAsync线程: " + Thread.currentThread().getId());
//                    System.out.println("异步任务完成: 结果是" + res + "异常是 " + exce);
//                })
//                .exceptionally((throwable -> {
////          得到异常返回结果
//                    return 10;
//                }));
//        Integer integer = completableFuture.get();
//        System.out.println("main.........end    " + integer);


//        方式五2 :传入有返回值的runable对象,但是无入参  ，主方法退出
//        System.out.println("main.........start");
//        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
//                    System.out.println("当前线程: " + Thread.currentThread().getId());
//                    int i  = 10 / 0;
//                        System.out.println("运行结果: " + i);
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    return i;
//                }, executor).whenCompleteAsync((res, exce) -> {
////           虽然能得到异常,但是没法返回结果
//                    System.out.println("当前whenCompleteAsync线程: " + Thread.currentThread().getId());
//                    System.out.println("异步任务完成: 结果是" + res + "异常是 " + exce);
//                });
//        System.out.println("main.........end    " );


//        方式7 :方法执行完后的处理
//        System.out.println("main.........start");
//        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程: " + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果: " + i);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return i;
//        }, executor).handle((res,thr)->{
//            if (res != null) {
//                return res * 2;
//            } else if (res == null) {
//                return 0;
//            }
//            return -1;
//        });
//        System.out.println("main.........end    "+completableFuture.get() );


//        方式6 :方法执行完后的处理
//        System.out.println("main.........start");
//        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程: " + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果: " + i);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return i;
//        }, executor).handle((res,thr)->{
//            if (res != null) {
//                return res * 2;
//            } else if (res == null) {
//                return 0;
//            }
//            return -1;
//        });
//        System.out.println("main.........end    "+completableFuture.get() );


        //串行化 thenRunAsync 不能获得上一步执行结果
//        System.out.println("main.........start");
//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程: " + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果: " + i);
//            return i;
//        }, executor).thenRunAsync(()->{
//            System.out.println("任务二启动");
//        },executor);
//        System.out.println("main.........end");

        //串行化 thenRunAsync 获得上一步执行结果
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程: " + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("运行结果: " + i);
            return i;
        }, executor).thenAccept(res -> {
            System.out.println("任务二启动了" + res);
        });


        //串行化 thenRunAsync 获得上一步执行结果,并由返回值
//        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程: " + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果: " + i);
//            return i;
//        }, executor).thenApplyAsync(res -> {
//            System.out.println("任务二启动了" + res);
//            return "hello" + res;
//        });
//        System.out.println(stringCompletableFuture.get()+"result");


//       两任务共同完成
        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务一 当前线程: " + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("任务一运行结果: " + i);
            return i;
        }, executor);


        CompletableFuture<Integer> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务二 当前线程: " + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("任务二运行结果: " + i);
            return i;
        }, executor);

//不感知结果
        future01.runAfterBothAsync(future02, () -> {
            System.out.println("任务三 当前线程: " + Thread.currentThread().getId());
            System.out.println("任务三 运行结果: ");
        }, executor);

//感知结果
        future01.thenAcceptBothAsync(future02, (r1, r2) -> {
            System.out.println("任务三 当前线程: " + Thread.currentThread().getId());
            System.out.println("任务三 运行结果: r1=" + r1 + "  r2=" + r2);
        }, executor);


//感知结果,有返回值
        CompletableFuture<String> stringCompletableFuture = future01.thenCombineAsync(future02, (r1, r2) -> {
            System.out.println("任务三 当前线程: " + Thread.currentThread().getId());
            System.out.println("任务三 运行结果: r1=" + r1 + "  r2=" + r2);
            return "work3return";
        }, executor);
        System.out.println(stringCompletableFuture.get());


//       两任务一个完成


//不感知结果
        future01.runAfterEitherAsync(future02, () -> {
            System.out.println("任务三 当前线程: " + Thread.currentThread().getId());
            System.out.println("任务三 运行结果: ");
        }, executor);

//感知结果
        future01.acceptEitherAsync(future02, (r1) -> {
            System.out.println("任务三 当前线程: " + Thread.currentThread().getId());
            System.out.println("任务三 运行结果: r1=" + r1);
        }, executor);


//感知结果,有返回值
        CompletableFuture<String> stringCompletableFuture1 = future01.applyToEitherAsync(future02, (r1) -> {
            System.out.println("任务三 当前线程: " + Thread.currentThread().getId());
            return "work3return" + r1;
        }, executor);
        System.out.println(stringCompletableFuture1.get());


        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.allOf(future01, future02);
        voidCompletableFuture1.get();
        Integer integer = future01.get();
        Integer integer1 = future02.get();

        CompletableFuture<Object> objectCompletableFuture = CompletableFuture.anyOf(future01, future02);
        voidCompletableFuture1.get();

    }
}
