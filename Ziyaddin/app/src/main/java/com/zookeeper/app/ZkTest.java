package com.zookeeper.app;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZkTest {
	
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		ZooKeeper zk;
		
		ZkConnector zkc = new ZkConnector();
		
		BasicConfigurator.configure();
		
		zkc.connect("ec2-34-210-116-135.us-west-2.compute.amazonaws.com:2181");
		zk = zkc.getZooKeeper();
		zk.create("/newznode", "new znode".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
}
