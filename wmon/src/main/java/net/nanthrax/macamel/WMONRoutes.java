package net.nanthrax.macamel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.jms.ConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class WMONRoutes extends RouteBuilder {

    @Inject
    @Identifier("ibmMQMFConnectionFactory")
    private ConnectionFactory ibmMQConnectionFactory;

    @Inject
    @Identifier("activeMQConsumerConnectionFactory")
    private ActiveMQConnectionFactory activeMQConsumerConnectionFactory;

    @ConfigProperty(name = "mq.wmon.notices.queue.name")
    private String wmon_notices;
    @ConfigProperty(name = "mq.wmon.tracking.queue.name")
    private String wmon_tracking;
    @ConfigProperty(name = "mq.wmon.cost.queue.name")
    private String wmon_cost;
    @ConfigProperty(name = "mq.wmon.shipment.queue.name")
    private String wmon_shipment;
    @ConfigProperty(name = "mq.wmon.shipment.track.queue.name")
    private String wmon_shipment_track;

    @Override
    public void configure() throws Exception {
        from("jms:queue:" + wmon_cost + "?connectionFactory=#activeMQConsumerConnectionFactory&concurrentConsumers=20&transacted=true")
                .routeId("WMON_MAIN1")
                .log("Recieved ActiveMq-WMON-cost: ${body}")
                .setHeader("JMS_IBM_Format", constant("MQSTR   "))
                .to("jms:queue:" + wmon_cost + "?connectionFactory=#ibmMQMFConnectionFactory&destinationResolver=#ibmMQDestinationResolver")
                .log("Sent IBM-WMON-cost: ${body}");
    }
}