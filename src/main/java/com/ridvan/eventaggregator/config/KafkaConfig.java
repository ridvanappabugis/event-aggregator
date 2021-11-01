package com.ridvan.eventaggregator.config;

import com.ridvan.eventaggregator.model.vehicle.VehicleSignal;
import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;
import com.ridvan.eventaggregator.util.JSONConverter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(final KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(final KafkaProperties kafkaProperties) {
        final ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    /***
     * Embedded kafka configuration, this app starts up an embeded kafka server.
     */

    @Bean
    EmbeddedKafkaBroker broker() {
        return new EmbeddedKafkaBroker(1)
                .kafkaPorts(9092)
                .brokerListProperty("spring.kafka.bootstrap-servers");
    }

    @Bean
    public NewTopic topic(@Value("${spring.kafka.template.default-topic}") final String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    /**
     * Sends 2 vehicle ID messages every 1HZ. These messages get picked up by the {@link com.ridvan.eventaggregator.services.KafkaReceiverService}.
     */
    @Bean
    public ApplicationRunner runner(@Value("${spring.kafka.template.default-topic}") final String topic,
                                    @Value("${kafka.embedded.runner.interval}") final Long interval,
                                    final KafkaTemplate<String, String> template) {
        return args -> {
            final UUID id = UUID.randomUUID();
            final UUID id2 = UUID.randomUUID();

            while(true) {
                final VehicleTelemetry telemetry = new VehicleTelemetry(id, System.currentTimeMillis(), getSignal());
                template.send(topic, JSONConverter.toJSON(telemetry));
                final VehicleTelemetry telemetry2 = new VehicleTelemetry(id2, System.currentTimeMillis(), getSignal());
                template.send(topic, JSONConverter.toJSON(telemetry2));
                Thread.sleep(interval);
            }
        };
    }

    private Map<VehicleSignal, Double> getSignal() {
        final Map<VehicleSignal, Double> signals = new HashMap<>();
        signals.put(VehicleSignal.CURRENT_SPEED, Math.random());
        signals.put(VehicleSignal.DRIVING_TIME, Math.random());
        signals.put(VehicleSignal.IS_CHARGING, 1d);
        signals.put(VehicleSignal.ODOMETER, 10000d);

        return signals;
    }
}
