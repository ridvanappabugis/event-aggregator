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
import java.util.concurrent.TimeUnit;

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
    EmbeddedKafkaBroker broker(@Value("${kafka.embedded.port}") final Integer port) {
        return new EmbeddedKafkaBroker(1)
                .kafkaPorts(port)
                .brokerListProperty("spring.kafka.bootstrap-servers");
    }

    @Bean
    public NewTopic topic(@Value("${spring.kafka.template.default-topic}") final String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    /**
     * Sends 2 vehicle ID messages every 1HZ. These messages get picked up by the {@link com.ridvan.eventaggregator.services.KafkaReceiverService}.
     * Simulates an accelerating vehicle.
     */
    @Bean
    public ApplicationRunner runner(@Value("${spring.kafka.template.default-topic}") final String topic,
                                    @Value("${kafka.embedded.runner.interval}") final Long interval,
                                    final KafkaTemplate<String, String> template) {
        return args -> {
            final UUID id = UUID.randomUUID();
            final UUID id2 = UUID.randomUUID();

            long iteration = 1;
            double odometer = 0D;
            while(true) {
                // Driving time
                final double seconds = TimeUnit.MILLISECONDS.toSeconds(interval) * iteration;
                // Max speed m/h 10m/s
                final double speed = seconds > 10 ? 10 : seconds;
                // Delta odometer m/h
                odometer = odometer + TimeUnit.MILLISECONDS.toSeconds(interval) * speed;

                final VehicleTelemetry telemetry = new VehicleTelemetry(id, System.currentTimeMillis(), getSignal(speed, seconds, odometer));
                template.send(topic, JSONConverter.toJSON(telemetry));
                final VehicleTelemetry telemetry2 = new VehicleTelemetry(id2, System.currentTimeMillis(), getSignal(speed, seconds, odometer));
                template.send(topic, JSONConverter.toJSON(telemetry2));

                iteration++;
                Thread.sleep(interval);
            }
        };
    }

    private Map<VehicleSignal, Double> getSignal(final double speed, final double time, final double odometer) {
        final Map<VehicleSignal, Double> signals = new HashMap<>();
        signals.put(VehicleSignal.CURRENT_SPEED, speed);
        signals.put(VehicleSignal.DRIVING_TIME, time);
        signals.put(VehicleSignal.IS_CHARGING, 0d);
        signals.put(VehicleSignal.ODOMETER, odometer);

        return signals;
    }
}
