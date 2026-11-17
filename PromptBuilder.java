// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
import java.util.Map;

public class PromptBuilder {
   public PromptBuilder() {
   }

   public static String buildUserPrompt(SpendingSummary var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append("You are a friendly financial advisor for group budget tracking.\n\n");
      var1.append("Please analyze the following group spending summary:\n");
      var1.append("- Group Name: ").append(var0.getGroupName()).append("\n");
      var1.append("- Total Spent: $").append(String.format("%.2f", var0.getTotalSpending())).append("\n");
      var1.append("- Breakdown by Category:\n");

      for(Map.Entry var3 : var0.getCategoryTotals().entrySet()) {
         var1.append("  * ").append((String)var3.getKey()).append(": $").append(String.format("%.2f", var3.getValue())).append("\n");
      }

      var1.append("\nPlease provide:\n");
      var1.append("1. A brief spending critique.\n");
      var1.append("2. Identification of the highest spending categories.\n");
      var1.append("3. Three actionable, practical tips to reduce future group costs.");
      return var1.toString();
   }
}
