package framework;
import java.io.Serializable;

public class Complex implements Serializable{
	private static final long serialVersionUID = -9099395460757557732L;
	
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

    public static Complex parseComplex(String s) {
        String[] ssplit = s.split("[+-]");
        if(s.equals("i")) {
            return Complex.I();
        }
        if(ssplit.length == 1 && s.contains("i")) {
            return new Complex(0,Double.parseDouble(s.substring(0,s.length()-1)));
        }
        if(s.contains("i")) {
            if(s.contains("+i")) {
                return new Complex(Double.parseDouble(s.substring(0,s.length()-2)),1);
            } else if (s.contains("-i")) {
                return new Complex(Double.parseDouble(s.substring(0,s.length()-2)),-1);
            }
            return new Complex(Double.parseDouble(ssplit[0]), Double.parseDouble(ssplit[1].substring(0, ssplit[1].length() - 1)));
        }
        return new Complex(Double.parseDouble(s),0);
    }

    @Override
    public String toString(){
        return a+"+"+b+"i";
    }
}
