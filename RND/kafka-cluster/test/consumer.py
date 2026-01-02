from confluent_kafka import Consumer, TopicPartition

conf = {
    'bootstrap.servers': '192.168.0.51:9092',
    'group.id': 'debug-group',
    'auto.offset.reset': 'earliest',
    #'debug': 'all',
    'security.protocol': 'PLAINTEXT'
}

consumer = Consumer(conf)

# Directly assign partition 0 of 'test-topic'
consumer.assign([TopicPartition('test-topic', 0)])

print("Assigned partition 0 directly. Polling for messages...")

try:
    while True:
        msg = consumer.poll(1.0)
        if msg is None:
            continue
        if msg.error():
            print("Consumer error:", msg.error())
            continue
        print("Received message:", msg.value().decode('utf-8'))
finally:
    consumer.close()
