package org.jlab.rec.dc;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.jlab.clas.detector.DetectorType;
import org.jlab.clasrec.utils.DataBaseLoader;
import org.jlab.geom.base.ConstantProvider;
import org.jlab.geom.detector.dc.DCDetector;
import org.jlab.geom.detector.dc.DCFactory;
import org.jlab.geom.detector.dc.DCFactoryUpdated;

/**
 * A class to load the geometry constants used in the DC reconstruction.  The coordinate system used in the Tilted Sector coordinate system.
 * @author ziegler
 *
 */
public class GeometryLoader {

	public static boolean isGeometryLoaded = false;
	public static DCDetector dcDetector;

	public static void Load() {
		// the geometry is different is hardware and geometry... until GEMC gets updated we need to run with this flag
		ConstantProvider dcDataProvider = DataBaseLoader.getDetectorConstants(DetectorType.DC);
		if(Constants.newGeometry == false)
			dcDetector = (new DCFactory()).createDetectorTilted(dcDataProvider);
		if(Constants.newGeometry == true)
			dcDetector = (new DCFactoryUpdated()).createDetectorTilted(dcDataProvider);
		if (isGeometryLoaded) return;

		// mark the geometry as loaded
		isGeometryLoaded = true;
		System.out.println("DC Geometry constants are Loaded -- MC geometry = "+Constants.newGeometry);
	}
	
	public static void main (String arg[]) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File("/Users/ziegler/testdcgeo.txt"));
		Constants.isSimulation = true;
		GeometryLoader.Load();
		System.out.println("layer 1 x; = "+GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(0).getComponent(1).getMidpoint().x());
		System.out.println("layer 2 x; = "+GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(1).getComponent(1).getMidpoint().x());
			pw.printf("%f\n",GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(1).getComponent(1).getMidpoint().x()
					-0*GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(2).getComponent(1).getMidpoint().x()
			);
		
		pw.close();
	}
}
