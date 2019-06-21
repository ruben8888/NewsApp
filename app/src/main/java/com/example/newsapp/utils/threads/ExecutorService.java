package com.example.newsapp.utils.threads;

public class ExecutorService {
    private static ExecutorProvider executorProvider;

    private static ExecutorService executorService = new ExecutorService();

    private ExecutorService() {
        executorProvider = new ExecutorProviderImpl();
    }

    public static ExecutorService getInstance(){
        if (executorService == null){
            executorService = new ExecutorService();
        }
        return executorService;
    }

    public static ExecutorProvider getExecutorProvider(){
        return executorProvider;
    }
}
