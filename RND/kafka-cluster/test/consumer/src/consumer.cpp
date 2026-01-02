#include <iostream>
#include <vector>
#include <librdkafka/rdkafkacpp.h>
#include <ctime>

int main() {
    std::string brokers = "kafka_broker_1:9092";
    std::string topic = "test-topic";
    std::string errstr;

    auto conf = RdKafka::Conf::create(RdKafka::Conf::CONF_GLOBAL);
    conf->set("bootstrap.servers", brokers, errstr);
    conf->set("group.id", "test-group", errstr);
    conf->set("auto.offset.reset", "earliest", errstr);     // Start from earliest if no offset exists
    conf->set("enable.auto.commit", "false", errstr);       // EOS requires manual offset handling
    conf->set("isolation.level", "read_committed", errstr); // Only read committed messages
    conf->set("max_poll_records", "1", errstr);               // Process one message at a time (optional)

    RdKafka::KafkaConsumer* consumer = RdKafka::KafkaConsumer::create(conf, errstr);
    if (!consumer) {
        std::cerr << "Failed to create consumer: " << errstr << std::endl;
        return 1;
    }

    RdKafka::ErrorCode resp = consumer->subscribe({topic});
    if (resp != RdKafka::ERR_NO_ERROR) {
        std::cerr << "Failed to subscribe: " << RdKafka::err2str(resp) << std::endl;
        return 1;
    }
    std::cout << "Consumer started, waiting for messages..." << std::endl;

    // ---- Infinite loop ----
    while (true) {
        RdKafka::Message *msg = consumer->consume(1000); // timeout 1s

        switch (msg->err()) {
            case RdKafka::ERR_NO_ERROR:
                std::cout << "Partition: " << msg->partition() << ", Consumed message: " 
                          << std::string(static_cast<const char *>(msg->payload()), msg->len()) 
                          << std::endl;

                consumer->commitSync(msg);  // Manual commit after processing
                break;

            case RdKafka::ERR__TIMED_OUT:
                // poll timeout, just continue
                break;

            case RdKafka::ERR__PARTITION_EOF:
                // reached end of partition, continue
                break;

            default:
                std::cerr << "Consumer error: " << msg->errstr() << std::endl;
                break;
        }

        delete msg;
    }

    consumer->close();
    delete consumer;
    delete conf;

    return 0;
}
