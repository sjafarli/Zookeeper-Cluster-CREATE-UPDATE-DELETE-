package com.zooktutorial.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperProject {
	private static String fileName;
	private static String text;
	private static ZooKeeper zk;
	private static ZkConnector zkc = new ZkConnector();
	private static List<String> znodeList = new ArrayList<String>();
	static Scanner sc = new Scanner(System.in);
	static boolean session = true;
	static String fullText = "";

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		System.out.println("Enter host to connect");
		String host= sc.nextLine();
		connectToZoo(host);
		while (session) {
			System.out.println("Do you want to CREATE APPEND, READ or DELETE a file?");
			listTheFiles(znodeList);
			String opt = (new BufferedReader(new InputStreamReader(System.in))).readLine();
			String[] in =opt.split(" ");
			doWork(in);
		}
		System.out.println("You exitted");
	}

	private static void doWork(String []optt) throws IOException, InterruptedException, KeeperException {
			String opt = optt[0].substring(0, 1);
		if (opt.equals("C") || opt.equals("c")) {
			fileName = optt[1];
			String text = "";
			createZnode(fileName, text);

		} else if (opt.equals("A") || opt.equals("a")) {
			fileName = optt[1];
			if (isZnodeExists(fileName)) {
				System.out.println("Conent: ");
				String txt = sc.nextLine();
				if (!fullText.equals("")) {
					fullText += txt+"\n";
				} else {
					fullText += txt;
				}
				byte[] by = fullText.getBytes();
				appendZnode(fileName, by);
			} else {
				System.out.println("No such file exists, please choose the correct file name");
				//listTheFiles(znodeList);
			}

		} else if (opt.equals("D") || opt.equals("d") ) {
			fileName = optt[1];
			if (isZnodeExists(fileName)) {
				deleteZnode(fileName);
			} else {
				System.out.println("No such file exists, please choose the correct file name");
				//listTheFiles(znodeList);
			}
		} else if (opt.equals("R") || opt.equals("r") || opt.equals("read")) {
			fileName = optt[1];
			if (isZnodeExists(fileName)) {
				String s = new String(readZnode("/" + fileName));
				System.out.println(s);
			} else {
				System.out.println("No such file exists, please choose the correct file name");
			}
		} else if (opt.equals("exit")) {

			zkc.close();
			session = false;
		}

	}

	private static byte[] readZnode(String fileName) throws KeeperException, InterruptedException {
		return zk.getData(fileName, true, zk.exists(fileName, true));

	}

	private static void listTheFiles(List<String> znodeList) {
		for (String nodes : znodeList) {
			System.out.println(nodes);
		}

	}

	private static void connectToZoo(String host) throws IOException, InterruptedException, KeeperException {
		zkc.connect(host);
//		zkc.connect("ec2-34-211-128-192.us-west-2.compute.amazonaws.com");
//		zkc.connect("ec2-54-148-187-102.us-west-2.compute.amazonaws.com");
//		zkc.connect("ec2-34-216-118-25.us-west-2.compute.amazonaws.com");
//		zkc.connect("ec2-35-164-226-45.us-west-2.compute.amazonaws.com");
		zk = zkc.getZooKeeper();
		znodeList = zk.getChildren("/", true);

		
	}

	private static boolean isZnodeExists(String znode) throws KeeperException, InterruptedException {
		int flag = 0;
		for (String nodes : znodeList) {
			if (nodes.equals(znode)) {
				flag = 1;
				break;
			}
		}
		if (flag == 1)
			return true;
		else
			return false;

	}

	private static void createZnode(String path, String text)
			throws IOException, InterruptedException, KeeperException {
		zk.create("/" + path, text.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println(path + " created");
		znodeList = zk.getChildren("/", true);
	}

	public static void appendZnode(String path, byte[] data) throws KeeperException, InterruptedException {
		path = "/" + path;
		Stat stat = zk.exists(path, true);
		zk.setData(path, data, stat.getVersion());
		System.out.println("Your text is added");
	}

	public static void deleteZnode(String path) throws KeeperException, InterruptedException {
		path = "/" + path;
		Stat stat = zk.exists(path, true);
		zk.delete(path, stat.getVersion());
		System.out.println(path + " deleted");
		System.out.println();
		znodeList = zk.getChildren("/", true);
	}

}