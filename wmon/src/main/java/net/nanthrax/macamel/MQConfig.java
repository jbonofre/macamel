package net.nanthrax.macamel;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.mq.jakarta.jms.MQDestination;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.camel.component.jms.JmsComponent;
import org.eclipse.microprofile.config.ConfigProvider;
import org.springframework.jms.support.destination.DestinationResolver;

@ApplicationScoped
public class MQConfig {

    @Produces
    @Identifier("ibmMQMFConnectionFactory")
    @Named("ibmMQMFConnectionFactory")
    public ConnectionFactory createIBMMQConnectionFactory() {
        MQConnectionFactory factory = new MQConnectionFactory();
        try {
            factory.setHostName(ConfigProvider.getConfig().getValue("ibm.mq.host", String.class));
            factory.setPort(ConfigProvider.getConfig().getValue("ibm.mq.port", Integer.class));
            factory.setChannel(ConfigProvider.getConfig().getValue("ibm.mq.channel", String.class));
            factory.setQueueManager(ConfigProvider.getConfig().getValue("ibm.mq.queueManagerName", String.class));
            factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            factory.setBooleanProperty(WMQConstants.WMQ_MQMD_WRITE_ENABLED, true);
            factory.setBooleanProperty(WMQConstants.WMQ_MQMD_READ_ENABLED, true);
            factory.setIntProperty(WMQConstants.WMQ_TARGET_CLIENT,1);
            factory.setIntProperty(WMQConstants.WMQ_MESSAGE_BODY, WMQConstants.WMQ_MESSAGE_BODY_MQ);
            factory.setStringProperty(WMQConstants.USERID, ConfigProvider.getConfig().getValue("ibm.mq.user", String.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create IBM MQ Connection Factory", e);
        }
        return factory;
    }
    private DestinationResolver createIBMMQDestinationResolver() {
        return (session, destinationName, pubSubDomain) -> {
            jakarta.jms.Queue queue = session.createQueue(destinationName);
            ((MQDestination) queue).setIntProperty(WMQConstants.WMQ_TARGET_CLIENT, 1);
            return queue;
        };
    }

    @Produces
    @Identifier("activeMQConsumerConnectionFactory")
    @Named("activeMQConsumerConnectionFactory")
    public ActiveMQConnectionFactory createActiveMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(ConfigProvider.getConfig().getValue("activemq.brokerURL", String.class));
        factory.setExclusiveConsumer(true);
        factory.setUserName(ConfigProvider.getConfig().getValue("activemq.user", String.class));
        factory.setPassword(ConfigProvider.getConfig().getValue("activemq.password", String.class));
        RedeliveryPolicy redeliveryPolicy = factory.getRedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(-1);
        redeliveryPolicy.setInitialRedeliveryDelay(10000);
        redeliveryPolicy.setRedeliveryDelay(10000);
        redeliveryPolicy.setUseExponentialBackOff(false);
        return factory;
    }

    @Produces
    @Named("activemq-in")
    public JmsComponent activeMQInComponent(
            @Identifier("activeMQConsumerConnectionFactory") ActiveMQConnectionFactory factory) {
        JmsComponent component = new JmsComponent();
        component.setConnectionFactory(factory);
        return component;
    }

    @Produces
    @Named("ibmmq-out")
    public JmsComponent ibmMQOutComponent(
            @Identifier("ibmMQMFConnectionFactory") ConnectionFactory factory) {
        JmsComponent component = new JmsComponent();
        component.setConnectionFactory(factory);
        component.setDestinationResolver(createIBMMQDestinationResolver());
        return component;
    }

}
