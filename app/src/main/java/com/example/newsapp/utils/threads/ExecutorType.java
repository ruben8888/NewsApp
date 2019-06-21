package com.example.newsapp.utils.threads;

import androidx.annotation.IntDef;

@IntDef({
        ExecutorType.MAIN_WITH_DELAY,
        ExecutorType.MAIN,
        ExecutorType.DB_COMMUNICATION,
        ExecutorType.BACKGROUND,
        ExecutorType.BG_SCHEDULER,
})
public @interface ExecutorType {
    int MAIN_WITH_DELAY = -2;
    int MAIN = -1;
    int DB_COMMUNICATION = 1;
    int BACKGROUND = 2;
    int BG_SCHEDULER = 3;
}