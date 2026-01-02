package org.example;
import redis.clients.jedis.Jedis;

public class Main {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis_node", 6379);

        jedis.set("name", "John Doe");

        // Get the value of the key
        String value = jedis.get("name");

        // Print the value retrieved from Redis
        System.out.println("The value of 'name' is: " + value);
    }
}