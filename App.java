import java.lang.*;
import java.util.*;

// A credit card class
class CreditCard {
  // Fields
  private double limit = 1000;
  private double APR = 0.30;
  private int openDay = 0;
  private int lastTransactionDay = 0;
  private double outstandingBalance = 0;
  private double dailyInterestRate = 0;

  private PriorityQueue<Integer> transactionDays = new PriorityQueue<>();
  private HashMap<Integer, ArrayList<Double>> transactionRecords = new HashMap<>();
  private HashMap<Integer, Double> balanceHistory = new HashMap<>();

  // Constructor
  CreditCard(double limit, double APR) {
    this.limit = limit;
    this.APR = APR;
    this.dailyInterestRate = computeDailyInterestRate();
    System.out.printf("A credit with $%.2f limit and %d%% APR is created\n", this.limit, (int)(this.APR*100));
  }


  // Getters
  double getLimit() {
    return this.limit;
  }

  double getAPR() {
    return this.APR;
  }
  double getOpenDay() {
    return this.openDay;
  }

  double getOutStandingBalance() {
    return this.outstandingBalance;
  }

  int getlastTransactionDay() {
    return this.lastTransactionDay;
  }


  // Make a purchase using card
  boolean purchase(double amount, int day) {
    if (this.outstandingBalance + amount > getLimit()) {
      System.out.println("Credit limit will be exceeded, purchased failed");
      return false;
    }

    // update balance
    this.outstandingBalance += amount;

    // add transaction to to transaction history
    if (transactionRecords.containsKey(day) == false) {
      this.transactionDays.offer(day);
      ArrayList<Double> transactions = new ArrayList<>();
      this.transactionRecords.put(day, transactions);
    }
    ArrayList<Double> transactions = this.transactionRecords.get(day);
    transactions.add(amount);
    this.transactionRecords.put(day, transactions);
    this.balanceHistory.put(day, this.outstandingBalance);
    // update last transaction day
    this.lastTransactionDay = Math.max(this.lastTransactionDay, day);

    System.out.println("Charged $" + amount + " to the card on day " + day);
    return true;
  }

  // Make payment to the card
  boolean payback(double amount, int day) {
    System.out.println("Paid back $" + amount + " to the card on day " + day);

    // update balance
    amount = -1 * amount;
    this.outstandingBalance += amount;

    // add transaction to to transaction history
    if (transactionRecords.containsKey(day) == false) {
      this.transactionDays.offer(day);
      ArrayList<Double> transactions = new ArrayList<>();
      this.transactionRecords.put(day, transactions);
    }
    ArrayList<Double> transactions = this.transactionRecords.get(day);
    transactions.add(amount);
    this.transactionRecords.put(day, transactions);
    this.balanceHistory.put(day, this.outstandingBalance);

    // update last transaction day
    this.lastTransactionDay = Math.max(this.lastTransactionDay, day);

    return true;
  }

  // Print the balance of card given a date
  void printBalance(int day) {
    double balance = 0;
    if (day >= 30) {
      balance = this.outstandingBalance + computeAccuredInterest(day);
    } else {
      balance = this.outstandingBalance;
    }
    System.out.printf("Outstanding Balance at day %d: $%.2f\n", day, balance);
  }

  double computeDailyInterestRate(){
    return (this.APR / (double) 365);
  }

  double computeAccuredInterest(int lastDay) {
    double res = 0;
    PriorityQueue<Integer> days = new PriorityQueue<>(this.transactionDays);
    int prevDay = days.peek();

    while(!days.isEmpty()){
      int curDay = days.poll();
      if(curDay >= lastDay){
        break;
      }
      if(curDay > prevDay){
        res+= (curDay - prevDay) * this.dailyInterestRate * this.balanceHistory.get(prevDay);
      }
      prevDay = curDay;
    }
    res+= (lastDay - prevDay) * this.dailyInterestRate * this.balanceHistory.get(prevDay);
    return res;
  }

  void printTransactionRecords() {
    System.out.println("\n--Transaction Records--");
    System.out.print("Day \t\t Transaction\n");
    for (Integer day : this.transactionDays) {
      ArrayList<Double> transactionList = this.transactionRecords.get(day);
      for (double transaction : transactionList) {
        System.out.print(day + " \t\t "
            + "$" + transaction + "\n");
      }
    }
  }

  void printBalanceHistory() {
    System.out.println("\n--Balance History--");
    System.out.print("Day \t\t Balance\n");
    for (Integer day : this.transactionDays) {
      double balance = this.balanceHistory.get(day);
      System.out.print(day + " \t\t "
          + "$" + balance + "\n");
    }
  }
}

public class App {
  public static void test1() {
    System.out.println("Test Scenario 1");
    double limit = 1000;
    double APR = 0.35;

    CreditCard cc = new CreditCard(limit, APR);
    cc.purchase(500, 0);
    cc.printBalance(30);
    cc.printTransactionRecords();
    cc.printBalanceHistory();
  }

  public static void test2() {
    System.out.println("\n\n\nTest Scenario 2");
    double limit = 1000;
    double APR = 0.35;

    CreditCard cc = new CreditCard(limit, APR);
    cc.purchase(500, 0);
    cc.payback(200, 15);
    cc.purchase(100, 25);
    cc.printBalance(30);
    cc.printTransactionRecords();
    cc.printBalanceHistory();
  }

  public static void main(String[] args) {
    test1();
    test2();
  }
}
