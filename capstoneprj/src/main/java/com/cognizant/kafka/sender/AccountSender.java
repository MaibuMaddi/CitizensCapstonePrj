package com.cognizant.kafka.sender;

import java.util.Properties;

import com.cognizant.kafka.domain.Account;
import com.cognizant.kafka.partitioner.AccountTypePartitioner;
import com.cognizant.kafka.serializer.AccountSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class AccountSender {
    public static void main(String[] args) throws Exception{
        String bootstrapServers="localhost:9092";
        String topic="accounts-topic";

        Properties props=new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AccountSerializer.class);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, AccountTypePartitioner.class);

        Producer<String, Account> producer=new KafkaProducer<>(props);

        for (int i = 0; i < 100; i++) {
            Account account = new Account();
            account.setAccountNumber(i);
            account.setCustomerId(i + 1000);
            account.setAccountType(i % 4 == 0 ? "CA" : i % 4 == 1 ? "SB" : i % 4 == 2 ? "RD" : "LOAN");
            account.setBranch("Branch-" + (i % 10));

            producer.send(new ProducerRecord<>(topic, String.valueOf(account.getAccountNumber()), account));
        }
        System.out.println("Meassage sent.");
    }
}