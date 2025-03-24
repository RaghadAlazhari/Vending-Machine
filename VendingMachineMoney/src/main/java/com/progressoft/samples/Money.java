package com.progressoft.samples;

import java.util.Arrays;

public class Money {

    private static final int[] DENOMINATIONS = {5000, 2000, 1000, 500, 100, 50, 25, 10, 5, 1};
    private final int[] counts;

    public static final Money Zero = new Money(new int[10]);
    public static final Money OnePiaster = createMoney(9, 1);
    public static final Money FivePiasters = createMoney(8, 1);
    public static final Money TenPiasters = createMoney(7, 1);
    public static final Money TwentyFivePiasters = createMoney(6, 1);
    public static final Money FiftyPiasters = createMoney(5, 1);
    public static final Money OneDinar = createMoney(4, 1);
    public static final Money FiveDinars = createMoney(3, 1);
    public static final Money TenDinars = createMoney(2, 1);
    public static final Money TwentyDinars = createMoney(1, 1);
    public static final Money FiftyDinars = createMoney(0, 1);

    private Money(int[] counts) {
        if (counts.length != DENOMINATIONS.length) {
            throw new IllegalArgumentException("Invalid counts length");
        }
        this.counts = counts.clone();
    }

    public Money(double amount) {
        this.counts = convertAmountToCounts(amount);
    }

    private int[] convertAmountToCounts(double amount) {
        int totalPiasters = (int) Math.round(amount * 100);
        int[] counts = new int[DENOMINATIONS.length];

        for (int i = 0; i < DENOMINATIONS.length; i++) {
            int denom = DENOMINATIONS[i];
            counts[i] = totalPiasters / denom;
            totalPiasters %= denom;
        }

        return counts;
    }

    private static Money createMoney(int index, int count) {
        int[] counts = new int[10];
        counts[index] = count;
        return new Money(counts);
    }

    public double amount() {
        int total = 0;
        for (int i = 0; i < counts.length; i++) {
            total += counts[i] * DENOMINATIONS[i];
        }
        return total / 100.0;
    }

    public Money times(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        int[] newCounts = new int[counts.length];
        for (int i = 0; i < counts.length; i++) {
            newCounts[i] = counts[i] * count;
        }
        return new Money(newCounts);
    }

    public static Money sum(Money... items) {
        int[] newCounts = new int[10];
        for (Money item : items) {
            for (int i = 0; i < newCounts.length; i++) {
                newCounts[i] += item.counts[i];
            }
        }
        return new Money(newCounts);
    }

    public Money plus(Money other) {
        int[] newCounts = new int[counts.length];
        for (int i = 0; i < counts.length; i++) {
            newCounts[i] = this.counts[i] + other.counts[i];
        }
        return new Money(newCounts);
    }

    public Money minus(Money other) {
        int desired = (int) Math.round(other.amount() * 100);
        int current = (int) Math.round(this.amount() * 100);

        if (desired < 0) {
            throw new IllegalArgumentException("Negative amount subtraction not allowed.");
        }
        if (desired > current) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        int[] newCounts = counts.clone();
        int remaining = desired;

        for (int i = 0; i < DENOMINATIONS.length; i++) {
            int denom = DENOMINATIONS[i];
            if (denom > remaining) {
                continue;
            }
            int available = newCounts[i];
            int take = Math.min(available, remaining / denom);
            newCounts[i] -= take;
            remaining -= take * denom;
            if (remaining == 0) {
                break;
            }
        }

        if (remaining != 0) {
            throw new IllegalArgumentException("Cannot make change");
        }

        return new Money(newCounts);
    }

    public Money getChange(Money amountToReturn) {
        int desired = (int) Math.round(amountToReturn.amount() * 100);
        int[] changeCounts = new int[counts.length];
        int remaining = desired;

        for (int i = 0; i < DENOMINATIONS.length; i++) {
            int denom = DENOMINATIONS[i];
            if (denom > remaining) {
                continue;
            }
            int available = this.counts[i];
            int take = Math.min(available, remaining / denom);
            changeCounts[i] = take;
            remaining -= take * denom;
            if (remaining == 0) {
                break;
            }
        }

        if (remaining != 0) {
            throw new IllegalArgumentException("Cannot make change");
        }

        return new Money(changeCounts);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Money)) {
            return false;
        }
        Money other = (Money) obj;
        return Math.abs(this.amount() - other.amount()) < 0.001;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(counts);
    }

    @Override
    public String toString() {
        return String.format("%.2f", amount());
    }

    public int[] getCounts() {
        return counts.clone();
    }
}
