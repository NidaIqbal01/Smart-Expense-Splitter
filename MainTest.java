// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
public class MainTest {
   public MainTest() {
   }

   public static void main(String[] var0) {
      ConsoleUI var1 = new ConsoleUI();
      SpendingSummary var2 = var1.collectSpendingSummary();
      if (var2 == null) {
         var1.close();
      } else {
         String var3 = System.getProperty("GEMINI_API_KEY");
         if (var3 == null || var3.trim().isEmpty()) {
            var3 = System.getenv("GEMINI_API_KEY");
         }

         AIAdvisorService var4 = new AIAdvisorService(var3);
         var1.displayLoadingBanner();
         String var5 = var4.getBudgetAdvice(var2);
         var1.streamTextOutput(var5);
         var1.close();
      }
   }
}
