package com.example.sunxiaodong.threadcooperation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.PriorityQueue;

/**
 * 使用Object的wait()、notify()和notifyAll()方法协作
 * Created by sunxiaodong on 16/4/29.
 */
public class ObjectCooperationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStart;
    private TextView mQueueSize;
    private Button mProduce;
    private Button mConsume;

    private static final int QUEUE_SIZE = 5;
    private PriorityQueue<Integer> mQueue = new PriorityQueue<Integer>(QUEUE_SIZE);

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
        mProduce = (Button) findViewById(R.id.producer);
        mProduce.setOnClickListener(this);
        mConsume = (Button) findViewById(R.id.consumer);
        mConsume.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                start();
                break;
            case R.id.producer:
                produce();
                break;
            case R.id.consumer:
                consume();
                break;
        }
    }

    private void start() {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();
        producer.start();
        consumer.start();
    }

    private Producer mProducer;
    private Consumer mConsumer;

    private void produce() {
        if (mProducer != null && mProducer.isAlive()) {
            return;
        }
        mProducer = new Producer();
        mProducer.start();
    }

    private void consume() {
        if (mConsumer != null && mConsumer.isAlive()) {
            return;
        }
        mConsumer = new Consumer();
        mProducer.start();
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
                synchronized (mQueue) {
                    while (mQueue.size() == 0) {
                        try {
                            mQueueSize.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ObjectCooperationActivity.this, "仓储空了，消费者等待", Toast.LENGTH_LONG).show();
                                }
                            });
                            mQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            mQueue.notify();
                        }
                    }
                    mQueue.poll();//每次移走队首元素
                    mQueue.notify();
                    mQueueSize.post(new Runnable() {
                        @Override
                        public void run() {
                            mQueueSize.setText(getResources().getString(R.string.queue_size_tip, mQueue.size()));
                        }
                    });
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
                synchronized (mQueue) {
                    while (mQueue.size() == QUEUE_SIZE) {
                        try {
                            mQueueSize.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ObjectCooperationActivity.this, "仓储已满，生产者等待", Toast.LENGTH_LONG).show();
                                }
                            });
                            mQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            mQueue.notify();
                        }
                    }
                    mQueue.offer(1);//每次插入一个元素
                    mQueue.notify();
                    mQueueSize.post(new Runnable() {
                        @Override
                        public void run() {
                            mQueueSize.setText(getResources().getString(R.string.queue_size_tip, mQueue.size()));
                        }
                    });
                }
            }
        }
    }

}