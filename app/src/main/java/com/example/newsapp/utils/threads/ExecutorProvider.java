package com.example.newsapp.utils.threads;

import java.util.concurrent.Executor;

public interface ExecutorProvider {
    Executor getExecutor(@ExecutorType int type);
}
