package net.nanthrax.macamel;

import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class WMONRoutes extends RouteBuilder {

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
        from("activemq-in:queue:" + wmon_cost + "?concurrentConsumers=20&transacted=true")
                .routeId("WMON_MAIN1")
                .log("Recieved ActiveMq-WMON-cost: ${body}")
                .setHeader("JMS_IBM_Format", constant("MQSTR   "))
                .to("ibmmq-out:queue:" + wmon_cost)
                .log("Sent IBM-WMON-cost: ${body}");
    }
}