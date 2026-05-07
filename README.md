# macamel

Apache Camel Quarkus bridge that consumes messages from ActiveMQ queues and forwards them to IBM MQ.

## Modules

| Module | Description |
|--------|-------------|
| `wmon` | Bridges WMON queues (cost, tracking, notices, shipment) from ActiveMQ to IBM MQ |

## Prerequisites

- Java 17+
- Maven 3.9+
- A running ActiveMQ broker
- A running IBM MQ queue manager

## Build

Build the project from the root directory:

```bash
mvn clean package
```

This produces an uber-jar at:

```
wmon/target/wmon-1.0-SNAPSHOT-runner.jar
```

## Configuration

All configuration is in `wmon/src/main/resources/application.properties`. The defaults are:

| Property | Default | Description |
|----------|---------|-------------|
| `ibm.mq.host` | `localhost` | IBM MQ broker host |
| `ibm.mq.port` | `1414` | IBM MQ broker port |
| `ibm.mq.channel` | `DEV.APP.SVRCONN` | IBM MQ server-connection channel |
| `ibm.mq.queueManagerName` | `QM1` | IBM MQ queue manager name |
| `ibm.mq.user` | `app` | IBM MQ user |
| `activemq.brokerURL` | `tcp://localhost:61616` | ActiveMQ broker URL |
| `activemq.user` | `admin` | ActiveMQ user |
| `activemq.password` | `admin` | ActiveMQ password |

Any property can be overridden at runtime via a system property or environment variable (see below).

## Run

```bash
java -jar wmon/target/wmon-1.0-SNAPSHOT-runner.jar
```

### Override configuration at runtime

Use `-D` system properties to override any value without rebuilding:

```bash
java \
  -Dibm.mq.host=mq.example.com \
  -Dibm.mq.port=1414 \
  -Dibm.mq.channel=PROD.APP.SVRCONN \
  -Dibm.mq.queueManagerName=PRODQM \
  -Dibm.mq.user=mquser \
  -Dactivemq.brokerURL=tcp://activemq.example.com:61616 \
  -Dactivemq.user=admin \
  -Dactivemq.password=secret \
  -jar wmon/target/wmon-1.0-SNAPSHOT-runner.jar
```

Alternatively, Quarkus also accepts environment variables using the uppercased, dot-to-underscore form:

```bash
IBM_MQ_HOST=mq.example.com \
IBM_MQ_PORT=1414 \
java -jar wmon/target/wmon-1.0-SNAPSHOT-runner.jar
```

## Queues

The following queues are bridged from ActiveMQ to IBM MQ:

| Queue | Description |
|-------|-------------|
| `WMON.COST` | Cost messages |
| `WMON.TRACKING` | Tracking messages |
| `WMON.NOTICES` | Notice messages |
| `WMON.SHIPMENT` | Shipment messages |
| `WMON.SHIPMENT.TRACK` | Shipment tracking messages |

Messages are forwarded as-is with MQMD format set to `MQSTR` and no RFH2 header, so they are directly consumable by native IBM MQ applications.
