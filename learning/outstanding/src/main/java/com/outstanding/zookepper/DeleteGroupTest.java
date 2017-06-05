package com.outstanding.zookepper;

import org.apache.zookeeper.KeeperException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class DeleteGroupTest {
    private static final String HOSTS = "192.168.1.137";
    private static final String groupName = "zoo";

    private DeleteGroup deleteGroup = null;

    @Before
    public void init() throws IOException, InterruptedException {
        deleteGroup = new DeleteGroup();
        deleteGroup.connection(HOSTS);
    }

    @Test
    public void testDelete() throws IOException, InterruptedException, KeeperException {
        deleteGroup.delete(groupName);
    }

    @After
    public void destroy() throws InterruptedException {
        if(null != deleteGroup){
            try {
                deleteGroup.close();
            } catch (InterruptedException e) {
                throw e;
            }finally{
                deleteGroup = null;
                System.gc();
            }
        }
    }
}