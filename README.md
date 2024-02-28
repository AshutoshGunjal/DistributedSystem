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

## The Herd Effect

  - A large number of nodes waiting for an event.
  - When the event happens all nodes get notified and they all wake up.
  - Even though all nodes wakes up, only one node can "succeed"
  - It indicates bad design, and can negatively imapct the performance and can completely freeze the cluster.

## Fault Tolerance

  - In order for our system to be fault tolerant
  - The leader election algorithm needs to be able to recover from the failures
  - And re-elect a new leader automatically
(To Make it a Fault Tolerant, we have implemented watchers where each node will watch the previous nodes ephemeral z node and gets notified if that z nodes gets deleted. If the deleted Z node belonged to the leader then the notified node becomes the leader itslef, and if the deleted z node did not belong to the leader then the notified node simply closes the gap.)

## Summary for Leader Re-election algorithm
- Implemented popular, useful and important algorithm in ditributed system, called Leader - Reelection
- Fault Tolerant implementation - any number of nodes can fail, and the cluster stays functional
- Horizontal Scalable - we can add nodes dynamically
- No performance bottlenecks due to Herd Effect elimination
- Fault Tolerance and Horizontal Scalability are very important properties
  - Fault Tolerance - buiness can run 24*7 with no interruption
  - Horizontal Scaling - can dynamically grow business on demand
- Having these two "badges of honor" is not a trivial achievement we will try to repeat for every distributed system. 

## SERVICE DISCOVERY
- When a group of computers start up, the only device they are aware about is themsevles, even if they are all connected to the same network.
- The formal logical clustering of different nodes within a system necessitates a method for them to discover each other and establish communication channels.
- A straightforward solution involves static configuration, wherein all node addresses are predetermined and compiled into a single configuration file distributed to all nodes before application launch.
- This enables inter-node communication based on the specified addresses. However, this approach poses challenges if a node becomes unavailable or changes its address, as other nodes would persist in using outdated information, hindering discovery of the new address.
- Additionally, expanding the cluster requires regenerating and redistributing the configuration file to all nodes, which can be cumbersome even with automation.
- These days, lot of companies still manage their clusters in a similar way, with some degree of automation.
- Everytime a new node is added - one central configuration is updated.
- An automated configuration management tool like Chef or Puppet, can pick up the configuration and distribute it among the nodes in the cluster.
- More dynamic but still involves a human to update the configurations.

- Hench the best possible approach is "Fully Automated Service Discovery using Zookeeper"
  - Establish a permanent Z node called "service registry" to serve as the backbone of the cluster.
  - Upon joining the cluster, each node adds a sequential Z node under the "service registry" node, containing its own address.
  - Unlike traditional leader election, these Z nodes are not empty; each node embeds its address within its respective Z node.
  - For service discovery, a node simply registers a watcher on the "service registry" Z node using the get children method.
  - When a node needs to communicate with another, it retrieves the address data by calling the get data method on the relevant Z node.
  - Any changes in the cluster trigger immediate notifications to the nodes via the node children changed event.
  - This implementation facilitates a fully peer-to-peer architecture, enabling seamless communication between any nodes in the cluster.
- In this architecture, nodes adapt dynamically to changes such as node departures or new additions, ensuring continuous operation.
- In a leader-workers architecture, workers operate independently without needing awareness of other nodes, and the leader doesn't register itself in the registry.
- Workes will register themselves with cluster
- Only the leader will register for notifications
- Leader will know about the state of the cluster at all times and distribute the work accordingly
- If a leader dies, then the new leader will remove itself from the service registry and continue distributing the work



