package com.example.sunxiaodong.threadcooperation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Condition的await()和signal()方法协作
 * Created by sunxiaodong on 16/4/29.
 */
public class ConditionCooperationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStart;
    private TextView mQueueSize;

    private static final int QUEUE_SIZE = 5;
    private PriorityQueue<Integer> mQueue = new PriorityQueue<Integer>(QUEUE_SIZE);
    private Lock mLock = new ReentrantLock();
    private Condition mNotFull = mLock.newCondition();
    private Condition mNotEmpty = mLock.newCondition();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooperation_activity);
        initView();
    }

    private void initView() {
        mStart = (Button) findViewById(R.id.start);
        mStart.setOnClickListener(this);
        mQueueSize = (TextView) findViewById(R.id.queue_size);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                start();
                break;
        }
    }

    private void start() {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        producer.start();
        consumer.start();
    }

    /**
     * 消费者
     */
    class Consumer extends Thread {

        @Override
        public void run() {
            consume();
        }

        private void consume() {
            while (true) {
                mLock.lock();
                try {
                    while (mQueue.size() == 0) {
                        try {
//                            System.out.println("队列空，等待数据");
                            mNotEmpty.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mQueue.poll();//每次移走队首元素
                    mNotFull.signal();
//                    System.out.println("从队列取走一个元素，队列剩余" + mQueue.size() + "个元素");
                } finally {
                    mLock.unlock();
                }
            }
        }
    }

    /**
     * 生产者
     */
    class Producer extends Thread {

        @Override
        public void run() {
            produce();
        }

        private void produce() {
            while (true) {
                mLock.lock();
                try {
                    while (mQueue.size() == QUEUE_SIZE) {
                        try {
//                            System.out.println("队列满，等待有空余空间");
                            mNotFull.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mQueue.offer(1);//每次插入一个元素
                    mNotEmpty.signal();
//                    System.out.println("向队列取中插入一个元素，队列剩余空间：" + (queueSize - queue.size()));
                } finally {
                    mLock.unlock();
                }
            }
        }
    }

}
