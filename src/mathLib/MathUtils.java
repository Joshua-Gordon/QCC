package mathLib;

public class MathUtils {
	
	public static Matrix<Double> translate(Vector<Double> position){
		Matrix<Double> temp = Matrix.identity(0d,4);
		for(int i = 0; i < 3; i++)
			temp.r(position.v(i), i, 3);
		return temp;
	}
	
	public static Matrix<Double> rotate(Vector<Double> axis, double angle){
		Vector<Double> t = axis.mult(Math.sin(angle / 2d)).toVector();
		double w = Math.cos(angle / 2d);
		Double[] components = new Double[] {
				1 - 2 * t.v(1) * t.v(1) - 2 * t.v(2) * t.v(2) ,
				2 * t.v(0) * t.v(1) - 2 * t.v(2) * w          ,
				2 * t.v(0) * t.v(2) + 2 * t.v(1) * w     ,  0d,
				
				2 * t.v(0) * t.v(1) + 2 * t.v(2) * w          ,
				1 - 2 * t.v(0) * t.v(0) - 2 * t.v(2) * t.v(2) ,
				2 * t.v(1) * t.v(2) - 2 * t.v(0) * w     ,  0d,

				2 * t.v(0) * t.v(2) - 2 * t.v(1) * w          ,
				2 * t.v(1) * t.v(2) + 2 * t.v(0) * w          ,
				1 - 2 * t.v(0) * t.v(0) - 2 * t.v(1) * t.v(1) , 0d,
				
				0d, 0d, 0d, 1d
		};
		
		return new Matrix<Double>(0d, 4, 4, components);
	}
	
	public static Matrix<Double> rotate(double yaw, double pitch, double roll){
		Matrix<Double> yawM = rotate(new Vector<Double>(0d, 1d, 0d), yaw);
		Matrix<Double> pitchM = rotate(new Vector<Double>(1d, 0d, 0d), pitch);
		Matrix<Double> rollM = rotate(new Vector<Double>(0d, 0d, 1d), roll);
		
		return rollM.mult(pitchM).mult(yawM);
	}
	
	public static Vector<Double> cross3d(Vector<Double> v1, Vector<Double> v2){
		Double[] components = new Double[] {
				v1.v(1) * v2.v(2) - v1.v(2) * v2.v(1),
				v1.v(2) * v2.v(0) - v1.v(0) * v2.v(2),
				v1.v(0) * v2.v(1) - v1.v(1) * v2.v(0), 
				v1.v(3) * v2.v(3)};
		
		return new Vector<Double>(components);
	}
	
	public static double dot3d(Vector<Double> v1, Vector<Double> v2){
		double dot = 0;
		for(int i = 0; i < 3; i++)
			dot += v1.v(i) + v2.v(i);
		return dot;
	}
			
	
}
