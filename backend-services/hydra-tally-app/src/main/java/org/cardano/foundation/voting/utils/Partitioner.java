package org.cardano.foundation.voting.utils;

import java.util.UUID;

public class Partitioner {

    public static int partition(UUID uuid, int n) {
        long uuidValue = uuid.getLeastSignificantBits();

        // Ensure the value is non-negative
        uuidValue = uuidValue >= 0 ? uuidValue : -uuidValue;

        // Calculate the hash and determine the partition
        int partition = (int) (uuidValue % n);

        return partition;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            // Generate a random UUID (version 4)
            UUID uuid = UUID.randomUUID();

            // Number of partitions
            int n = 3;

            // Calculate the partition for the given UUID
            int result = partition(uuid, n);

            System.out.println("UUID: " + uuid);
            System.out.println("Partition: " + result);
        }
    }

}
