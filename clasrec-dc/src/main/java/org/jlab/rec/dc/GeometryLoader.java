package org.jlab.rec.dc;


import org.jlab.clasrec.utils.DataBaseLoader;
import org.jlab.geom.base.ConstantProvider;
import org.jlab.geom.detector.dc.DCDetector;
import org.jlab.geom.detector.dc.DCFactory;

/**
 * A class to load the geometry constants used in the DC reconstruction.  The coordinate system used in the Tilted Sector coordinate system.
 * @author ziegler
 *
 */
public class GeometryLoader {

	public static boolean isGeometryLoaded = false;
	public static DCDetector dcDetector;

	public static void Load() {

		ConstantProvider dcDataProvider = DataBaseLoader.getDriftChamberConstants();
		
		dcDetector = (new DCFactory()).createDetectorTilted(dcDataProvider);
	    
		if (isGeometryLoaded) return;

		// mark the geometry as loaded
		isGeometryLoaded = true;
		System.out.println("DC Geometry constants are Loaded");
	}
	
	public static void main (String arg[]) {
		
		GeometryLoader.Load();
		for(int i =0; i<3; i++) {
			System.out.println(GeometryLoader.dcDetector.getSector(0).getRegionMiddlePlane(i).point().z());
			
		}
	}
}
