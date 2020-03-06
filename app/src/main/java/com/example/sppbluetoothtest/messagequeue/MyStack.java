package com.example.sppbluetoothtest.messagequeue;

/**
 * Created by Administrator on 2019/9/9.
 */

import java.util.LinkedList;

/**
 * --------->>>>>>队列的实现--------------
 */
public class MyStack<T> {
    private LinkedList<T> storage = new LinkedList<T>();

    public synchronized void push(T e) {//需要加上同步
        storage.addFirst(e);
    }

    public T peek() {
        if (storage.size() > 0) {
            return storage.getFirst();
        } else {
            return null;
        }
    }

    //poll的功能也和peek相似，它可以获取到队尾或者队首的元素，但不一样的是，poll会从队列中移除该获取的元素。也就是说，poll具有出队的功能。
    public T poll() {
        if (storage.size() > 0) {
            return storage.pollFirst();
        } else {
            return null;
        }
    }

    public void pop() {
        storage.removeFirst();
    }

    public boolean empty() {
        return storage.isEmpty();
    }

    public void clearData() {
        storage.clear();
    }

    public void addMsg(T obj) {
        if (storage.size() < 20) {
            storage.add(obj);
        }
    }

    public int size() {
        return storage.size();
    }

    @Override
    public String toString() {
        return storage.toString();
    }

}