package org.jlab.rec.fmt;

import org.jlab.geom.prim.Point3D;

public class Geometry {

	public Geometry() {
		
	}
	


	public static Point3D getStripsIntersection(double x0_inner,
			double x1_inner,
			double x0_outer,
			double x1_outer,
			double y0_inner,
			double y1_inner,
			double y0_outer,
			double y1_outer,
			double z0_inner,
			double z0_outer) {


				
		double denom = (x1_inner-x0_inner)*(y0_outer-y1_outer) - (y1_inner-y0_inner)*(x0_outer-x1_outer);
		if(denom==0)
			return null;
		
		double X = ((x1_inner*y0_inner-y1_inner*x0_inner)*(x0_outer-x1_outer) - (x1_inner-x0_inner)*(x0_outer*y1_outer-y0_outer*x1_outer))/denom;
		double Y = ((x1_inner*y0_inner-y1_inner*x0_inner)*(y0_outer-y1_outer) - (y1_inner-y0_inner)*(x0_outer*y1_outer-y0_outer*x1_outer))/denom;
		double Z = (z0_outer+z0_inner)/2.; 
		
		return new Point3D(X,Y,Z);

	}	
	
	
	
	
	public static void main (String arg[])  {
		
		Geometry fmt = new Geometry();
		if(Constants.FVT_Zlayer[0]==0)
			Constants.Load();
		
		
		
	}

}
