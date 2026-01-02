#include <iostream>
#include <chrono>
#include <thread>
#include <string>
#include <librdkafka/rdkafkacpp.h>

void ProduceMsg(RdKafka::Producer* producer, std::string topic_str, std::string key, std::string val)
{
    // Begin transaction
    if (RdKafka::Error *err = producer->begin_transaction()) {
        std::cerr << "begin_transaction failed: " << err->str() << std::endl;
        delete err;
        return;
    }

    // Produce message
    RdKafka::ErrorCode resp = producer->produce(
        topic_str,
        RdKafka::Topic::PARTITION_UA,
        RdKafka::Producer::RK_MSG_COPY,
        (void *)val.c_str(), val.size(),
        (void *)key.c_str(), key.size(),
        0, nullptr, nullptr);

    if (resp != RdKafka::ERR_NO_ERROR) {
        std::cerr << "Produce failed: " << RdKafka::err2str(resp) << std::endl;
        producer->abort_transaction(5000); // abort if something went wrong
    }else{
        std::cout << "Queued: " << val << std::endl;

        // Flush to ensure delivery
        // producer->flush(5000);

        // Commit transaction
        if (RdKafka::Error *err = producer->commit_transaction(5000))
        {
            std::cerr << "commit_transaction failed: " << err->str() << std::endl;
            producer->abort_transaction(5000);
            delete err;
            return;
        }
        else
        {
            std::cout << "Committed message successfully." << std::endl;
        }
    }
}

int main() {
    std::string brokers = "kafka_broker_1:9092";
    std::string topic_str = "test-topic";
    std::string errstr;

    // Configuration
    RdKafka::Conf* conf = RdKafka::Conf::create(RdKafka::Conf::CONF_GLOBAL);
    conf->set("bootstrap.servers", brokers, errstr);
    conf->set("security.protocol", "PLAINTEXT", errstr);
    conf->set("enable.idempotence", "true", errstr);
    conf->set("transactional.id", "producer_1", errstr);  // must be unique per producer instance
    conf->set("acks", "all", errstr);
    conf->set("max.in.flight.requests.per.connection", "1", errstr); //Guarantees message ordering?


    // Create producer
    RdKafka::Producer* producer = RdKafka::Producer::create(conf, errstr);
    if (!producer) {
        std::cerr << "Failed to create producer: " << errstr << std::endl;
        return 1;
    }

    // Initialize transactions
    RdKafka::Error *err = producer->init_transactions(5000);
    if (err) {
        std::cerr << "init_transactions failed: " << err->str() << std::endl;
        delete err;
        return 1;
    }

    int counter = 0;

    // ---- Infinite loop ----
    while (true) {
        std::string key;
        std::string msg;

        std::string key_arr[] = {"BTC_USDT", "ETH_USDC"};
        int key_arr_size = sizeof(key_arr) / sizeof(key_arr[0]);

        for(int i = 0; i < key_arr_size; i++)
        {
            key = key_arr[i];
            msg = key + ": Message " + std::to_string(counter);
            ProduceMsg(producer, topic_str, key, msg);
        }
        counter++;

        // Optional: throttle message rate
        std::this_thread::sleep_for(std::chrono::milliseconds(5000));
    }

    delete producer;
    delete conf;
    return 0;
}
