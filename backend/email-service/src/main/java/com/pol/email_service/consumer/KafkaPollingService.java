package com.pol.email_service.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;

public class KafkaPollingService {

    private final KafkaConsumer<String, String> consumer;

    public KafkaPollingService(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    public void startPolling(String topic) {
        consumer.subscribe(Collections.singletonList(topic));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Key: " + record.key() + ", Value:" + record.value());
                System.out.println("Partition:" + record.partition() + ", Offset:" + record.offset());
            }

            // Sleep for 2 minutes after processing the records
            try {
                Thread.sleep(120_000); // 120,000 milliseconds = 2 minutes
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Reset the interrupt status
                System.err.println("Polling interrupted: " + e.getMessage());
            }
        }
    }
}
