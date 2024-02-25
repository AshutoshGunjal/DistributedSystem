# DistributedSystem

Porblems with a Centralized System:
1. Performance and Storage - limited vertical scalability.
2. Single Point of failure - loss of money and trust.
3. High latency - poor user experience.
4. Security and Privacy. 

Why Distributed Systems?
Companies are running highly scalable, distributed systems, in order to:
1. Handle **millions** of users
2. **Petabytes** of data
3. Provides consistent **user experience**

"*Distributed System* is a system of several __processes__, running on __different computers__, communicating with each other through the __network__, and are sharing a __state__ or a are working together to achieve __common goal__."

Terminologies:
- Node - A process runnning on a dedicated machine
- Cluster - Collection of computers/ nodes conected to each other. The nodes in the cluster are working on the same task, and typically are running the same code.

### Challenges of Master-Worker Architecuture:
1. Automatic and System Leader election is not a trivial task to solve, even among people.
2. Arriving to an agreement on a leader in a large cluster of nodes is even harder.
3. By default each node knows only about itself - Service registry and discovery is required.
4. Failure Detection mechanism is necessary to trigger automatic leader reelection in a cluster.

### Master-Workers Coordination Solution
1. implement distributed algorithms for consensus and failover from scratch.
2. __Apache Zookeeper__ - High Performance Distributed System Coordination Service.

## Apache Zookeper
1. High Performance coordination service designed specifically for distriubted systems.
2. Popular technology used by many companies and projects (Kafka, Hadoop etc.)
3. Provides an abstraction layer for higher level distributed algorithms.

What Makes ZooKeeper a good solution?
1. Zookeeper is a distributed system itself that provides us high availability and reliability.
2. Typically runs in a cluster of an odd number of nodes, higher than 3.
3. Uses redundancy to allow failures and stay functional.

## Znodes' Properties
- Hybrid between a file and a directory
  - Znodes can store any data inside (like a file)
  - Znodes can have children znodes (like a directory)
 
- Znode Types:
  - Persistent - persists between sessions. Persistent Znode stays within zookeeper until it is explicitly deleted. Using Persistent Znode       we can store data in between sessions.
  - Ephemeral - is deleted when the session ends. This node deleted automatically as soon as its creator process disconnects from Zookeper.
    Using Ephemeral znode, we can detect that a process died or disconnected from the Zookeeper service.

How to implement a simple leader election using Apache Zookeeper?
- Each node in the cluster will try to create a znode with the lowest sequence number available as the znode's name.
- When a node detects that its znode has the lowest sequence number, it becomes the leader.
- When a node detects that its znode doesn't have the lowest sequence number, it becomes the follower.
- Zookeeper gurantees a monotonically increasing, unique sequence number for each node that requests a sequence siffixed znode.

## Watchers and Triggers:
-  We can register a watcher when we call the methods like,
    -  getChildren(.., watcher): get notification when the list of a znode's children changes.
    -  getData(..): get notified if a znode's data gets modified.
    -  exists(..): get notified if a znode gets deleted or created.
 
-  The watcher is an object that alllows us to get a notification when a change happens



