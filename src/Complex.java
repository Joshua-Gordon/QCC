public class Complex {
    double a, b;
    public Complex(double a, double b) {
        this.a = a;
        this.b = b;
    }

    public static Complex ONE(){
        return new Complex(1,0);
    }

    public static Complex ZERO(){
        return new Complex(0,0);
    }

    public static Complex I(){
        return new Complex(0,1);
    }

    public static Complex ISQRT2(){
        return new Complex(1/Math.sqrt(2),0);
    }

    public Complex multiply(Complex other) {
        return new Complex(a*other.a-b*other.b,b*other.a + a*other.b);
    }


    public void test(){
        Complex one = new Complex(1,0);
        Complex i = new Complex(0,1);
        System.out.println(one.multiply(i));
        Complex oof = new Complex(1,1);
        System.out.println(oof.multiply(i));
    }

    public Complex conjugate() {
        return new Complex(a,-b);
    }

    public Complex negative(){
        return new Complex(-a,-b);
    }

    @Override
    public String toString(){
        return a+"+"+b+"i";
    }
}
