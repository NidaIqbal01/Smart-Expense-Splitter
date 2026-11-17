// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;

public class AIAdvisorService {
   private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
   private final String apiKey;
   private final HttpClient httpClient;

   public AIAdvisorService(String var1) {
      this.apiKey = var1;
      this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
   }

   public String getBudgetAdvice(SpendingSummary var1) {
      if (this.apiKey != null && !this.apiKey.trim().isEmpty()) {
         try {
            String var2 = PromptBuilder.buildUserPrompt(var1);
            String var3 = this.createJsonPayload(var2);
            String var4 = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + this.apiKey.trim();
            HttpRequest var5 = HttpRequest.newBuilder().uri(URI.create(var4)).header("Content-Type", "application/json").POST(BodyPublishers.ofString(var3)).timeout(Duration.ofSeconds(10L)).build();
            HttpResponse var6 = this.httpClient.send(var5, BodyHandlers.ofString());
            return var6.statusCode() == 200 ? this.parseResponseBody((String)var6.body()) : this.generateLocalAIAnalysis(var1);
         } catch (Exception var7) {
            return this.generateLocalAIAnalysis(var1);
         }
      } else {
         return this.generateLocalAIAnalysis(var1);
      }
   }

   private String createJsonPayload(String var1) {
      String var2 = var1.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
      return "{\"contents\": [{  \"parts\": [{\"text\": \"" + var2 + "\"}]}]}";
   }

   private String parseResponseBody(String var1) {
      int var2 = var1.indexOf("\"text\": \"");
      if (var2 == -1) {
         return var1;
      } else {
         int var3 = var2 + 9;
         StringBuilder var4 = new StringBuilder();
         boolean var5 = false;

         for(int var6 = var3; var6 < var1.length(); ++var6) {
            char var7 = var1.charAt(var6);
            if (var5) {
               if (var7 == 'n') {
                  var4.append('\n');
               } else if (var7 == '"') {
                  var4.append('"');
               } else if (var7 == '\\') {
                  var4.append('\\');
               } else {
                  var4.append(var7);
               }

               var5 = false;
            } else if (var7 == '\\') {
               var5 = true;
            } else {
               if (var7 == '"') {
                  break;
               }

               var4.append(var7);
            }
         }

         return var4.toString();
      }
   }

   private String generateLocalAIAnalysis(SpendingSummary var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("=========================================\n");
      var2.append("          SPLIT SMART AI - MODULE 4        \n");
      var2.append("             AI BUDGET ADVISOR           \n");
      var2.append("=========================================\n\n");
      var2.append("Active Group: ").append(var1.getGroupName()).append("\n");
      var2.append("Total Expenditure: $").append(String.format("%.2f", var1.getTotalSpending())).append("\n\n");
      var2.append("----- CATEGORY BREAKDOWN -----\n");
      String var3 = "N/A";
      double var4 = (double)0.0F;

      for(Map.Entry var7 : var1.getCategoryTotals().entrySet()) {
         double var8 = var1.getTotalSpending() > (double)0.0F ? (Double)var7.getValue() / var1.getTotalSpending() * (double)100.0F : (double)0.0F;
         var2.append(String.format(" - %-18s: $%-8.2f (%.1f%%)%n", var7.getKey(), var7.getValue(), var8));
         if ((Double)var7.getValue() > var4) {
            var4 = (Double)var7.getValue();
            var3 = (String)var7.getKey();
         }
      }

      double var10 = var1.getTotalSpending() > (double)0.0F ? var4 / var1.getTotalSpending() * (double)100.0F : (double)0.0F;
      var2.append("\nHighest Spending Category: ").append(var3).append(" ($").append(String.format("%.2f", var4)).append(")\n\n");
      var2.append("----- AI BUDGET RECOMMENDATIONS -----\n");
      var2.append("1. Critical Focus on ").append(var3).append(":\n");
      var2.append("   This category represents ").append(String.format("%.1f%%", var10)).append(" of your overall budget. Establish a strict group limit here.\n\n");
      var2.append("2. Smart Optimization Strategy:\n");
      switch (var3.toLowerCase()) {
         case "food":
         case "dining out":
            var2.append("   Food expenses are peaking! Try group meal prepping or limiting takeaway deliveries to twice a week.\n\n");
            break;
         case "rent":
         case "rent & utilities":
         case "utilities":
            var2.append("   Utility/Housing costs dominate the budget. Audit shared electricity consumption and optimize heating/cooling settings.\n\n");
            break;
         case "shopping":
         case "entertainment":
            var2.append("   Discretionary spending is high. Implement a 24-hour waiting rule before approving shared leisure purchases.\n\n");
            break;
         default:
            var2.append("   Bulk purchase routine items for ").append(var3).append(" to reduce individual transaction overhead.\n\n");
      }

      var2.append("3. Automated Debt Settlement:\n");
      var2.append("   Encourage group members to settle up weekly using SplitSmart's debt simplification feature.\n");
      var2.append("=========================================");
      return var2.toString();
   }
}
