// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
import java.util.Map;

public class SpendingSummary {
   private final String groupName;
   private final double totalSpending;
   private final Map<String, Double> categoryTotals;

   public SpendingSummary(String var1, double var2, Map<String, Double> var4) {
      this.groupName = var1;
      this.totalSpending = var2;
      this.categoryTotals = var4;
   }

   public String getGroupName() {
      return this.groupName;
   }

   public double getTotalSpending() {
      return this.totalSpending;
   }

   public Map<String, Double> getCategoryTotals() {
      return this.categoryTotals;
   }
}
