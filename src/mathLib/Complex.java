package mathLib;
import java.io.Serializable;

public class Complex implements Serializable, Scalar<Complex>{
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
    
    public static Complex real(double r) {
    	return new Complex(r, 0);
    }
    
    public void test(){
        Complex one = new Complex(1,0);
        Complex i = new Complex(0,1);
        System.out.println(one.mult(i));
        Complex oof = new Complex(1,1);
        System.out.println(oof.mult(i));
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
            if(s.contains("-")){
                return new Complex(Double.parseDouble(ssplit[0]), -Double.parseDouble(ssplit[1].substring(0, ssplit[1].length() - 1)));
            }
            return new Complex(Double.parseDouble(ssplit[0]), Double.parseDouble(ssplit[1].substring(0, ssplit[1].length() - 1)));
        }
        return new Complex(Double.parseDouble(s),0);
    }

    
    public static Complex parseInputFromFilter(String s) throws NumberFormatException {
    	if(s.equals(""))
    		return new Complex(0, 0);
    	char c;
    	String temp = "";
    	int a = 0;
    	int b = 0;
    	int index = s.length();
    	while(--index >= 0) {
    		c = s.charAt(index);
	    	temp = c + temp;
    		if(c == '+' || c == '-' || index == 0) {
    			if(temp.endsWith("i"))
    				b += Double.parseDouble(temp.substring(0, temp.length() - 1) + (temp.length() < 3? '1':""));
    			else
    				a += Double.parseDouble(temp);
    			temp = "";
    		}
    	}
    	return new Complex(a, b);
    }
    
    
    
    
    @Override
    public String toString(){
        if(b >= 0)
            return a+"+"+b+"i";
        else{
            return a+"-"+(-b)+"i";
        }
    }


	@Override
	public Complex get1() {
		return new Complex(1, 0);
	}

	@Override
	public Complex getn1() {
		return new Complex(-1, 0);
	}

	@Override
	public Complex get0() {
		return new Complex(0, 0);
	}

	@Override
	public Complex[] mkArray(int size) {
		Complex[] temp = new Complex[size];
		for(int i = 0; i < size; i++)
			temp[i] = get0();
		return temp;
	}

	@Override
	public Complex add(Complex num) {
		return new Complex(a + num.a, b + num.b);
	}

	@Override
	public Complex sub(Complex num) {
		return new Complex(a - num.a, b - num.b);
	}

	@Override
	public Complex mult(Complex num) {
		return new Complex(a * num.a - b * num.b, a * num.b + b * num.a);
	}

	@Override
	public Complex div(Complex num) {
		double magSquared = num.a * num.a + num.b * num.b;
		Complex conjugateMult = mult(num.conjugate());
		conjugateMult.a /= magSquared;
		conjugateMult.b /= magSquared;
		return conjugateMult;
	}

	
	/**
	 * This Function can ONLY take in a Real Number as an exponent and a complex base <strong> OR </strong>
	 * <br> can take a Real Number as a Base and a complex number as a Exponent. 
	 * <br> will throw an error if the base and the exponent is Complex!!!
	 * <p> ie. new Complex(K<sub>1</sub>,K<sub>2</sub>).pow(new Complex(K<sub>3</sub>, 0)); 
	 * <br>...for any value K<sub>1</sub>, K<sub>2</sub>, K<sub>3</sub> 
	 * <br><strong>OR</strong><br> ie. new Complex(K<sub>1</sub>, 0).pow(new Complex(K<sub>2</sub>, K<sub>3</sub>)); 
	 * <br>...for any value K<sub>1</sub>, K<sub>2</sub>, K<sub>3</sub> 
	 * <p>This is due to the multi-value nature of raising a complex number to a complex power
	 */
	@Override
	public Complex pow(Complex num) {
		if(b == 0) {
			double temp1 = Math.pow(a, num.a);
			double temp2 = num.b * Math.log(a);
			return new Complex(temp1 * Math.cos(temp2), temp1 * Math.sin(temp2));
		}else if(num.b == 0){
			double temp1 = num.a * Math.atan(b / a);
			double magToPower = Math.pow(a * a + b * b, num.a / 2d);
			return new Complex(magToPower * Math.cos(temp1), magToPower * Math.sin(temp1));
		}
		throw new ArithmeticException("Cannot raise a Complex power to another Complex Number");
	}

	@Override
	public Complex sqrt() {
		return pow(new Complex(.5d, 0));
	}
}
