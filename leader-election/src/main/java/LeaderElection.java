import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class LeaderElection implements Watcher {
    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 5000;
    private static final String ELECTION_NAMESPACE = "/election";
    private String currentZnodeName;
    private ZooKeeper zookeeper;

    public void connectToZookeeper() throws IOException {
        this.zookeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
    }

    public void run() throws InterruptedException {
        synchronized (zookeeper) {
            zookeeper.wait();
        };
    }

    public void close() throws InterruptedException{
        zookeeper.close();
    }

    // Process method will be called by zookeeper library on a separate thread whenever there is a new event coming
    // from a zookeeper server.
    @Override
    public void process (WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Successfully connected to zookeeper");
                } else {
                    synchronized (zookeeper) {
                        System.out.println("Disconnected from Zookeeper event");
                        zookeeper.notifyAll();
                    }
                }
        }
    }

    /**
     * Volunteer for leadership by creating a znode using the candidate leadership method.
     *
     * In this method:
     * - Znode prefix is set to denote candidacy.
     * - ZooKeeper's create method is invoked, specifying:
     *   - Znode prefix for identification.
     *   - Empty byte array as the data to be stored in the znode.
     *   - Open unsafe access control list (ACL) for unrestricted access.
     *   - Znode creation mode set to ephemeral sequential, ensuring automatic deletion upon disconnection.
     *
     * The return value from ZooKeeper contains the full path of the created znode.
     * Extract and store this information in a class member variable.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for a response.
     * @throws KeeperException      if a ZooKeeper operation fails.
     */

    public void volunteerForLeadership() throws InterruptedException, KeeperException {
        String znodePrefix = ELECTION_NAMESPACE + "/c_";
        String znodeFullPath = zookeeper.create(znodePrefix, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println("Znode Name: " + znodeFullPath);
        this.currentZnodeName = znodeFullPath.replace(ELECTION_NAMESPACE + "/", "");
    }

    /**
     * Elects a leader using the volunteer leadership method.
     *
     * In this method:
     * - Retrieves the list of children znodes under the specified namespace using ZooKeeper's getChildren method.
     * - Sorts the list of children znodes to determine the smallest znode.
     * - Compares the current znode name with the smallest znode in the list.
     * - Prints a message indicating whether the current znode is the leader or not.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for a response.
     * @throws KeeperException      if a ZooKeeper operation fails.
     */

    public void electLeader() throws InterruptedException, KeeperException {
        List<String> children = zookeeper.getChildren(ELECTION_NAMESPACE, false);

        Collections.sort(children);
        String smallestChild = children.get(0);

        if (smallestChild.equals(currentZnodeName)) {
            System.out.println("I am the leader znode");
            return;
        }
        System.out.println("I ma not a leader znode, " + smallestChild + " is the leader");
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        LeaderElection leaderElection = new LeaderElection();
        leaderElection.connectToZookeeper();
        leaderElection.volunteerForLeadership();
        leaderElection.electLeader();
        leaderElection.run();
        leaderElection.close();
        System.out.println("Disconnected from Zookeeper, exiting the application");
    }
}
