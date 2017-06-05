package com.outstanding.zookepper;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by songll on 2017/4/20.
 */
public class CreateGroup implements Watcher {
    private static final int SESSION_TIMEOUT = 1000;//会话延时

    private ZooKeeper zk = null;
    private CountDownLatch countDownLatch = new CountDownLatch(1);//同步计数器

    public void process(WatchedEvent event) {
        if(event.getState() == Event.KeeperState.SyncConnected){
            countDownLatch.countDown();//计数器减一
        }
    }

    /**
     * 创建zk对象
     * 当客户端连接上zookeeper时会执行process(event)里的countDownLatch.countDown()，计数器的值变为0，则countDownLatch.await()方法返回。
     * @param hosts
     * @throws IOException
     * @throws InterruptedException
     */
    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
        countDownLatch.await();//阻塞程序继续执行
    }

    /**
     * 创建group
     *
     * @param groupName 组名
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void create(String groupName) throws KeeperException, InterruptedException {
        String path = "/" + groupName;
        String createPath = zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE/*允许任何客户端对该znode进行读写*/, CreateMode.PERSISTENT/*持久化的znode*/);
        System.out.println("Created " + createPath);
    }

    /**
     * 关闭zk
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        if(zk != null){
            try {
                zk.close();
            } catch (InterruptedException e) {
                throw e;
            }finally{
                zk = null;
                System.gc();
            }
        }
    }
}