package mathLib;

public class MathUtils {
	
	public static Matrix<DoubleScalar> translate(Vector<DoubleScalar> position){
		Matrix<DoubleScalar> temp = new Matrix<DoubleScalar>(4, 4, new DoubleScalar[16]).identity();
		for(int i = 0; i < 3; i++)
			temp.r(position.v(i), i, 3);
		return temp;
	}
	
	public static Matrix<DoubleScalar> rotate(Vector<DoubleScalar> axis, double angle){
		Vector<DoubleScalar> t = axis.mult(new DoubleScalar(Math.sin(angle / 2d))).toVector();
		double w = Math.cos(angle / 2d);
		DoubleScalar[] components = DoubleScalar.getArray(
				1 - 2 * t.v(1).d * t.v(1).d - 2 * t.v(2).d * t.v(2).d ,
				2 * t.v(0).d * t.v(1).d - 2 * t.v(2).d * w          ,
				2 * t.v(0).d * t.v(2).d + 2 * t.v(1).d * w     ,  0d,
				
				2 * t.v(0).d * t.v(1).d + 2 * t.v(2).d * w          ,
				1 - 2 * t.v(0).d * t.v(0).d - 2 * t.v(2).d * t.v(2).d ,
				2 * t.v(1).d * t.v(2).d - 2 * t.v(0).d * w     ,  0d,

				2 * t.v(0).d * t.v(2).d - 2 * t.v(1).d * w          ,
				2 * t.v(1).d * t.v(2).d + 2 * t.v(0).d * w          ,
				1 - 2 * t.v(0).d * t.v(0).d - 2 * t.v(1).d * t.v(1).d , 0d,
				
				0d, 0d, 0d, 1d
			);
		
		return new Matrix<DoubleScalar>(4, 4, components);
	}
	
	public static Matrix<DoubleScalar> rotate(double yaw, double pitch, double roll){
		Matrix<DoubleScalar> yawM = rotate(new Vector<DoubleScalar>(DoubleScalar.getArray(0d, 1d, 0d)), yaw);
		Matrix<DoubleScalar> pitchM = rotate(new Vector<DoubleScalar>(DoubleScalar.getArray(1d, 0d, 0d)), pitch);
		Matrix<DoubleScalar> rollM = rotate(new Vector<DoubleScalar>(DoubleScalar.getArray(0d, 0d, 1d)), roll);
		
		return rollM.mult(pitchM).mult(yawM);
	}
	
	public static Vector<DoubleScalar> cross3d(Vector<DoubleScalar> v1, Vector<DoubleScalar> v2){
		DoubleScalar[] components = DoubleScalar.getArray(
				v1.v(1).d * v2.v(2).d - v1.v(2).d * v2.v(1).d,
				v1.v(2).d * v2.v(0).d - v1.v(0).d * v2.v(2).d,
				v1.v(0).d * v2.v(1).d - v1.v(1).d * v2.v(0).d, 
				v1.v(3).d * v2.v(3).d);
		
		return new Vector<DoubleScalar>(components, false);
	}
	
	public static double dot3d(Vector<DoubleScalar> v1, Vector<DoubleScalar> v2){
		double dot = 0;
		for(int i = 0; i < 3; i++)
			dot += v1.v(i).d + v2.v(i).d;
		return dot;
	}
	
	
	
	
	
	
	public static class DoubleScalar implements Scalar<DoubleScalar>{
		
		public double d = 0;
		
		private static DoubleScalar[] getArray(double ... ds) {
			DoubleScalar[] values = new DoubleScalar[ds.length];
			for(int i = 0; i < ds.length; i++) {
				values[i] = new DoubleScalar(ds[i]);
			}
			return values;
		}
		
		public DoubleScalar(double d) {
			this.d = d;
		}
		
		@Override
		public String toString() {
			return Double.toString(d);
		}

		@Override
		public DoubleScalar add(DoubleScalar num) {
			return new DoubleScalar(d + num.d);
		}

		@Override
		public DoubleScalar sub(DoubleScalar num) {
			return new DoubleScalar(d - num.d);
		}

		@Override
		public DoubleScalar mult(DoubleScalar num) {
			return new DoubleScalar(d * num.d);
		}

		@Override
		public DoubleScalar div(DoubleScalar num) {
			return new DoubleScalar(d / num.d);
		}

		@Override
		public DoubleScalar pow(DoubleScalar num) {
			return new DoubleScalar(Math.pow(d, num.d));
		}

		@Override
		public DoubleScalar sqrt() {
			return new DoubleScalar(Math.sqrt(d));
		}

		@Override
		public DoubleScalar get1() {
			return new DoubleScalar(1);
		}

		@Override
		public DoubleScalar getn1() {
			return new DoubleScalar(-1);
		}

		@Override
		public DoubleScalar get0() {
			return new DoubleScalar(0);
		}

		@Override
		public DoubleScalar[] mkArray(int size) {
			DoubleScalar[] temp = new DoubleScalar[size];
			for(int i = 0; i < temp.length; i++)
				temp[i] = new DoubleScalar(0);
			
			return temp;
		}
		
	}
	
	
	
	
	
	
}
