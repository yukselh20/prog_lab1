public class labOne {
    public static void main(String[] args){
        // Static Method: Class itself
        // Non-Static Method: Instance of class (Class.name(lab1) name = new Class.name()(lab1) ile object oluşturup çağırılır.)

        int[] z = new int[13];
        int value = 17;

        System.out.println("**************** Elements of z Array *************");
        for(int i = 0; i < z.length; i ++) {
            z[i] = value;
            value --;
            System.out.println(z[i] + " ");
        }

        int arraySize = 19;
        System.out.println("******Elements of x array******");
        float[] x = new float[arraySize];
        for(int i = 0; i < x.length; i++){
            x[i] = (float) ((Math.random() * 9.0) - (6.0));
            System.out.println(x[i] + " ");
        }
        int rows = 13;
        int colms = 19;
        float[][] z2 = new float[rows][colms];
        int[] valuesToCheck = {8,9,10,12,14,17};

        createArray(z,x,z2,valuesToCheck,rows,colms);
        System.out.println("******Elements of z2 array******");
        printArray(z2, rows, colms);
    }

    public static void createArray(int[] z, float[] x, float[][] z2, int[] valuesToCheck, int rows, int colms){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colms; j++) {
                if (z[i] == 16) {
                    z2[i][j] = (float) Math.cbrt(Math.tan(Math.exp(x[j])));
                } 
                else if (contains(valuesToCheck, z[i])) {
                    z2[i][j] = (float) Math.cos(Math.cbrt(Math.pow((x[j] + 1) / x[j], 3)));
                } 
                else {
                    z2[i][j] = (float) Math.atan(Math.pow((Math.exp(Math.cbrt(Math.pow(-((Math.PI / 2) * Math.abs(x[j])), x[j])))), 2));
                }
            }      
        }
    }

    public static void printArray(float[][] z2, int rows, int colms) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colms; j++) {
                System.out.printf("%.3f ", z2[i][j]);
            }
            System.out.println();
        }
    }

    public static boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;

    }
}
