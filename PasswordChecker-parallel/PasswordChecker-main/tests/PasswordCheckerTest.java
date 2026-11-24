public class PasswordCheckerTest {
    public static void main(String[] args) throws Exception {
        expect(PasswordChecker.checkPassword("").score == 0, "Empty password should score 0");
        expect(PasswordChecker.checkPassword("abc").score == 1, "Lowercase-only short password should score 1");
        expect(PasswordChecker.checkPassword("Abcdef12!").score == 5, "Password meeting all rules should score 5");
        expect(PasswordChecker.checkPassword("alllowercase").score == 2, "Long lowercase password should score 2");
        testBatch();
        System.out.println("All PasswordChecker tests passed.");
    }

    private static void testBatch() throws Exception {
        java.util.List<String> pws = java.util.Arrays.asList("abc", "Abcdef12!", "alllowercase", "", "Hello123");
        java.util.List<BatchChecker.Entry> par = BatchChecker.checkParallel(pws, 4, 200);
        expect(par.size() == pws.size(), "Batch should return one result per password");
        for (int i = 0; i < pws.size(); i++) {
            expect(par.get(i).password.equals(pws.get(i)), "Batch results should keep input order");
            expect(par.get(i).result.score == PasswordChecker.checkPassword(pws.get(i)).score,
                "Parallel score should match single check");
        }
        long t1 = System.nanoTime();
        BatchChecker.checkParallel(pws, 5, 200);
        long parMs = (System.nanoTime() - t1) / 1_000_000;
        long t2 = System.nanoTime();
        BatchChecker.checkSequential(pws, 200);
        long seqMs = (System.nanoTime() - t2) / 1_000_000;
        expect(parMs < seqMs, "Parallel should be faster than sequential with simulated work");
    }

    private static void expect(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
