package com.axalent.presenter;

import com.axalent.util.LogUtils;

import android.os.SystemClock;

/**
 * Created by Jason on 2016/6/21.
 */
public abstract class BaseActionThread extends Thread {

    protected ActionStatus actionStatus = ActionStatus.START;
    private static final int DEF_SLEEP_TIME = 1000;
    private int sleepTime = DEF_SLEEP_TIME;
    private boolean running = false;

    public BaseActionThread() {
        LogUtils.i("BaseActionThread Create!");
    }

    public enum ActionStatus {
        START,
        STOP,
        DESTROY
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        while (actionStatus != ActionStatus.DESTROY) {
            switch (actionStatus) {
                case START:
                    onActionStart();
                    SystemClock.sleep(sleepTime);
                    break;
                case STOP:
                    onActionStop();
                    break;
            }
        }
        running = false;
        onActionDestroy();
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    public abstract void onActionStart();

    public abstract void onActionStop();

    public abstract void onActionDestroy();

    public void startAction() {
        LogUtils.i("BaseActionThread start");
        actionStatus = ActionStatus.START;
        if (!isRunning()) {
            start();
        }
    }

    public void stopAction() {
        LogUtils.i("BaseActionThread stop");
        actionStatus = ActionStatus.STOP;
    }

    public void destroyAction() {
        LogUtils.i("BaseActionThread Destroy");
        actionStatus = ActionStatus.DESTROY;
    }

    public boolean isStart() {
        return actionStatus == ActionStatus.START;
    }

    public boolean isRunning() {
        return running;
    }


}
