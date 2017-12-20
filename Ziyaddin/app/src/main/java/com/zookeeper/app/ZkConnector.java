package com.zookeeper.app;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

public class ZkConnector {

	private static ZooKeeper zookeeper;

	private java.util.concurrent.CountDownLatch connectedSignal = new java.util.concurrent.CountDownLatch(1);

	public void connect(String host) throws IOException, InterruptedException {

		zookeeper = new ZooKeeper(host, 5000, new Watcher() {
			public void process(WatchedEvent event) {
				if (event.getState() == KeeperState.SyncConnected) {
					connectedSignal.countDown();
				}
			}
		});

		connectedSignal.await();
	}

	public void close() throws InterruptedException {
		zookeeper.close();
	}

	public ZooKeeper getZooKeeper() {
		if (zookeeper == null || !zookeeper.getState().equals(States.CONNECTED)) {
			throw new IllegalStateException("ZooKeeper is not connected.");
		}
		return zookeeper;
	}

	public void create(String path) throws KeeperException, InterruptedException {
		try {
			zookeeper.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} catch(InterruptedException intrEx) {
			// do something to prevent the program from crashing
			System.out.println(path + " already exists!");
		} catch(KeeperException kpEx) {
			// do something to prevent the program from crashing
			System.out.println(path + " already exists!");
		}
	}

	public void delete(String path) throws KeeperException, InterruptedException {
		try {
			Stat stat = zookeeper.exists(path, true);
			zookeeper.delete(path, stat.getVersion());
		} catch(InterruptedException intrEx) {
			// do something to prevent the program from crashing
			// System.out.println("\""+ path + "\" does not exist!");
			System.out.println(path + " does not exist!");
		} catch(KeeperException kpEx) {
			// do something to prevent the program from crashing
			System.out.println(path + " does not exist!");
		}
	}

	public byte[] read(String path) throws KeeperException, InterruptedException {
		try {
			return zookeeper.getData(path, true, zookeeper.exists(path, true));
		} catch(InterruptedException intrEx) {
			// do something to prevent the program from crashing
			System.out.println(path + " does not exist!");
		} catch(KeeperException kpEx) {
			// do something to prevent the program from crashing
			System.out.println(path + " does not exist!");
		}
		return null;
	}

	public void append(String path, String data) throws KeeperException, InterruptedException {
		try {
			Stat stat = zookeeper.exists(path, true);
			zookeeper.setData(path, data.getBytes(), stat.getVersion());
		} catch(InterruptedException intrEx) {
			// do something to prevent the program from crashing
			System.out.println("\"new_znode\" does not exist!");
		} catch(KeeperException kpEx) {
			// do something to prevent the program from crashing
			System.out.println("\"new_znode\" does not exist!");
		}
	}

}
