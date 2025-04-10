package io.zipcoder;

public class Main {
    public static void main(String[] args) {
        GroceryReporter reporter = new GroceryReporter("RawInput.JerkSON");
        System.out.println(reporter.toString());
    }
}
