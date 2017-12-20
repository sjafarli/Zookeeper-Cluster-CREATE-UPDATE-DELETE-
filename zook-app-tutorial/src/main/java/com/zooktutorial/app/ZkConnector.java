package com.zooktutorial.app;

import java.io.IOException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

public class ZkConnector {
	private ZooKeeper zooKeeper;
	
	private java.util.concurrent.CountDownLatch connectedSignal= new java.util.concurrent.CountDownLatch(1);
	
	public void connect(String host) throws IOException, InterruptedException {
	    zooKeeper = new ZooKeeper(host, 5000, 
	                              new Watcher() {
	                                  public void process(WatchedEvent event) {
	                                      if (event.getState() == KeeperState.SyncConnected) {
	                                          connectedSignal.countDown();
	                                      }
	                                  }
	                              });
	                              connectedSignal.await();
	}
	
	public void close() throws InterruptedException {
	    zooKeeper.close();
	}
	
	public ZooKeeper getZooKeeper() {
	    if (zooKeeper == null ) {
	        throw new IllegalStateException("ZooKeeper is not connected.");
	    }
	    return zooKeeper;
	}
	
}
