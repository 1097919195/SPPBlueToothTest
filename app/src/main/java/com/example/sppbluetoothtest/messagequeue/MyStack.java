package com.example.sppbluetoothtest.messagequeue;

/**
 * Created by Administrator on 2019/9/9.
 */

import java.util.LinkedList;

/**
 * --------->>>>>>队列的实现--------------
 * LinkedList的常用方法：https://blog.csdn.net/huyang0304/article/details/82389595
 * ArrayList和LinkedList的区别：https://blog.csdn.net/qq998701/article/details/95489618
 * SynchronizedList和Vector的区别：https://www.cnblogs.com/lujiahua/p/11408789.html
 */
public class MyStack<T> {
    private LinkedList<T> storage = new LinkedList<T>();

    public synchronized void push(T e) {//需要加上同步
        storage.push(e);
    }

    public T peek() {
        if (storage.size() > 0) {
//            return storage.getFirst();//这个方法可能会报异常（因为linkedList不是线程安全的，要么保障线程安全）
            return storage.peek();
        } else {
            return null;
        }
    }

    //poll的功能也和peek相似，它可以获取到队尾或者队首的元素，但不一样的是，poll会从队列中移除该获取的元素。也就是说，poll具有出队的功能。
    public T poll() {
        if (storage.size() > 0) {
            return storage.poll();
        } else {
            return null;
        }
    }

    //在一些线程安全集合中size() 和 isEmpty() 都可以判断队列中是否还有没有元素。但是size()是遍历队列去判断，isEmpty()不用遍历，效率更高，但是isEmpty()不是原子的 不是线程同步的。
    public boolean empty() {
        return storage.isEmpty();
    }

    public int size() {
        return storage.size();
    }

    public synchronized void clearData() {
        storage.clear();
    }

    public void addMsg(T obj) {
        if (storage.size() < 20) {//保障下队列上限好了
            storage.add(obj);
        }
    }

    @Override
    public String toString() {
        return storage.toString();
    }

}