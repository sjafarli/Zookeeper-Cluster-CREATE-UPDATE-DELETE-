package com.zookeeper.app;

import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args ) throws IOException, InterruptedException, KeeperException
	{
		ZooKeeper zk;

		ZkConnector zkc = new ZkConnector();

		BasicConfigurator.configure();

		zkc.connect("ec2-34-210-116-135.us-west-2.compute.amazonaws.com:2181");
		zk = zkc.getZooKeeper();

		String query, command, filename, line;
		String[] arguments;

		Scanner sc = new Scanner(System.in);

		while (sc.hasNext()) {
			query = sc.nextLine();
			arguments = query.split(" ");

			command = arguments[0];

			if(command.equals("c")) {
				zkc.close();
				break;
			}

			filename = arguments[1];

			if(command.equals("create")) {
				zkc.create("/" + filename);
			}
			if(command.equals("delete")) {
				zkc.delete("/" + filename);
			}
			if(command.equals("read")) {
				String content = zkc.read("/" + filename).toString();
				System.out.println(content);
			}
			if(command.equals("append")) {
				line = arguments[2];
				zkc.append("/" + filename, line);
			}
		}

		sc.close();
	}
}
