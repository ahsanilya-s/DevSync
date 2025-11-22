public class TestDetectors {
    
    // Test for MissingDefaultDetector
    public void testSwitchWithoutDefault(int value) {
        switch (value) {
            case 1:
                System.out.println("One");
                break;
            case 2:
                System.out.println("Two");
                break;
            // Missing default case
        }
    }
    
    // Test for EmptyCatchDetector
    public void testEmptyCatch() {
        try {
            riskyOperation();
        } catch (Exception e) {
            // Empty catch block
        }
    }
    
    // Test for LongMethodDetector
    public void veryLongMethodWithManyStatements() {
        int a = 1;
        int b = 2;
        int c = 3;
        int d = 4;
        int e = 5;
        int f = 6;
        int g = 7;
        int h = 8;
        int i = 9;
        int j = 10;
        int k = 11;
        int l = 12;
        int m = 13;
        int n = 14;
        int o = 15;
        int p = 16;
        int q = 17;
        int r = 18;
        int s = 19;
        int t = 20;
        int u = 21;
        int v = 22;
        int w = 23;
        int x = 24;
        int y = 25;
        int z = 26;
        System.out.println("This method has too many statements");
    }
    
    // Test for MagicNumberDetector
    public void calculatePrice() {
        double price = 100.0;
        double tax = price * 0.08; // Magic number 0.08
        double shipping = 15.99;   // Magic number 15.99
        double total = price + tax + shipping;
    }
    
    // Test for LongParameterListDetector
    public void methodWithTooManyParameters(String firstName, String lastName, 
            String email, String phone, String address, String city, 
            String state, String zipCode, String country) {
        // Method with 9 parameters
    }
    
    // Test for LongIdentifierDetector
    public void thisIsAnExtremelyLongMethodNameThatShouldBeDetectedByTheLongIdentifierDetector() {
        String thisIsAnExtremelyLongVariableNameThatExceedsReasonableLimits = "test";
    }
    
    private void riskyOperation() throws Exception {
        throw new Exception("Test exception");
    }
}