package com.liboshuai.starlink.slr.engine.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class RuleEngineDataMock {

    public static void main(String[] args) throws InterruptedException {

        for(int i=0;i<3;i++) {
            new Thread(new MyRunnable()).start();
            System.out.println("线程 -- " + i + "--------------------------");
            Thread.sleep(500);
        }
    }


    public static class MyRunnable implements Runnable{

        @Override
        public void run() {
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"doitedu:9092");
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.ACKS_CONFIG,"1");

            KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

            String[] events =  new String[]{"A","C","D","W","P","T","U","h","j","Z","p","a","b","c","d","e","f","g","h","k","X","Y"};
            //String[] events =  new String[]{"WW","AA","XX","a","b","c","d","e","f","g","h","j","w","p"};
            //String[] events =  new String[]{"A","C","D","W","P","T","U"};
            int[] uids = {1,3,5};

            for(int i=0; i< 1000000; i++) {
                //String eventId = events[RandomUtils.nextInt(0,events.length)];

                String eventId = RandomStringUtils.randomAlphabetic(1,3);

                //int uid = RandomUtils.nextInt(1,100);
                int uid = uids[RandomUtils.nextInt(0,3)];
                long actionTime = System.currentTimeMillis();

                //System.out.println("{\"user_id\":"+uid+",\"event_id\":\" "+eventId+" \",\"action_time\":"+actionTime+",\"properties\":{}}");
                producer.send(new ProducerRecord<>("user-action-log", "{\"user_id\":"+uid+",\"event_id\":\" "+eventId+" \",\"action_time\":"+actionTime+",\"properties\":{}}"));
                if(i%500 == 0) {producer.flush();}
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            producer.flush();
            producer.close();

        }
    }

}
