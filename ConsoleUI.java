// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
import java.util.HashMap;
import java.util.Scanner;

public class ConsoleUI {
   private final Scanner scanner;

   public ConsoleUI() {
      this.scanner = new Scanner(System.in);
   }

   public SpendingSummary collectSpendingSummary() {
      this.printHeader();
      System.out.print("Enter Group Name: ");
      String var1 = this.readValidString("Group Name");
      System.out.print("How many categories? ");
      int var2 = this.readValidCategoryCount();
      System.out.println("\n-------------------------------------------------");
      System.out.println(" ENTER EXPENSES");
      System.out.println("-------------------------------------------------");
      HashMap var3 = new HashMap();
      double var4 = (double)0.0F;

      for(int var6 = 1; var6 <= var2; ++var6) {
         System.out.printf("[%d/%d] Category Name : ", var6, var2);
         String var7 = this.readValidString("Category Name");
         System.out.printf("[%d/%d] Amount ($)    : ", var6, var2);
         double var8 = this.readValidDouble();
         var3.put(var7, var8);
         var4 += var8;
         System.out.println();
      }

      return new SpendingSummary(var1, var4, var3);
   }

   public void displayLoadingBanner() {
      System.out.println("\nAnalyzing budget with AI Advisor...");
   }

   public void streamTextOutput(String var1) {
      for(char var5 : var1.toCharArray()) {
         System.out.print(var5);

         try {
            Thread.sleep(12L);
         } catch (InterruptedException var7) {
            Thread.currentThread().interrupt();
         }
      }

      System.out.println();
   }

   public void close() {
      this.scanner.close();
   }

   private void printHeader() {
      System.out.println("=================================================");
      System.out.println("         SPLIT SMART AI - BUDGET INPUT          ");
      System.out.println("=================================================");
   }

   private String readValidString(String var1) {
      while(true) {
         String var2 = this.scanner.nextLine().trim();
         if (var2.isEmpty()) {
            System.out.printf("Invalid entry! %s cannot be blank.\n", var1);
            System.out.printf("Please enter a valid %s: ", var1);
         } else {
            if (!var2.matches("-?\\d+(\\.\\d+)?")) {
               return var2;
            }

            System.out.printf("Invalid entry! %s cannot be just a number.\n", var1);
            System.out.printf("Please enter a valid %s: ", var1);
         }
      }
   }

   private int readValidCategoryCount() {
      while(true) {
         try {
            int var1 = Integer.parseInt(this.scanner.nextLine().trim());
            if (var1 > 0) {
               return var1;
            }

            System.out.println("Category count must be at least 1.");
            System.out.print("Enter Category Count Again: ");
         } catch (NumberFormatException var2) {
            System.out.print("Invalid input! Enter a valid number: ");
         }
      }
   }

   private double readValidDouble() {
      while(true) {
         try {
            double var1 = Double.parseDouble(this.scanner.nextLine().trim());
            if (!(var1 <= (double)0.0F)) {
               return var1;
            }

            System.out.println("Amount must be greater than $0.00.");
            System.out.print("Enter amount again ($): ");
         } catch (NumberFormatException var3) {
            System.out.print("Invalid amount! Enter a valid dollar number ($): ");
         }
      }
   }
}
