package org.clas.detector.cvt;

import java.util.ArrayList;
import java.util.List;

import org.clas.detector.IDetectorShapes;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.clas12.calib.IDetectorListener;

public class DetectorDrawComponents implements IDetectorShapes {

	
	public DetectorDrawComponents() {
		// TODO Auto-generated constructor stub
	}
	
	private List<DetectorShapeView2D> _ShapeViews;
	private List<DetectorShapeTabView> _TabViews;
	
	public List<DetectorShapeView2D> get_ShapeViews() {
		return _ShapeViews;
	}
	public List<DetectorShapeTabView> get_TabViews() {
		return _TabViews;
	}
	public void set_TabViews(List<DetectorShapeTabView> _TabViews) {
		this._TabViews = _TabViews;
	}
	public void set_ShapeViews(List<DetectorShapeView2D> _ShapeViews) {
		this._ShapeViews = _ShapeViews;
	}
	@Override
	public void CreateDetectorShapes(List<DetectorShapeView2D> shapeViews,
			List<DetectorShapeTabView> tabViews, IDetectorListener listener) {
		// Modules
		// SVT module
		
		for(int r =0; r< 4; r++)
			for(int num = 0; num < org.jlab.rec.cvt.svt.Constants.NSECT[2*r+1]; num++) {
				DetectorShape2D module = new DetectorShape2D(DetectorType.BST,0,0,0);
				module.createBarXY(org.jlab.rec.cvt.svt.Constants.ACTIVESENWIDTH, org.jlab.rec.cvt.svt.Constants.MODULELENGTH);
				module.setColor(70, 70, 70);
				module.getShapePath().translateXYZ((org.jlab.rec.cvt.svt.Constants.ACTIVESENWIDTH+10)*num, (org.jlab.rec.cvt.svt.Constants.MODULELENGTH+10)*r, 0);
				shapeViews.get(2).addShape(module);			
			}		
		tabViews.get(2).addDetectorLayer(shapeViews.get(2));

		// BMT module
		double CRCWIDTH = org.jlab.rec.cvt.bmt.Constants.CRCRADIUS[2]*(org.jlab.rec.cvt.bmt.Constants.CRZEDGE2[2][2]-org.jlab.rec.cvt.bmt.Constants.CRZEDGE1[2][2]);
		for(int num = 0; num < 3; num++) {
			DetectorShape2D module = new DetectorShape2D(DetectorType.BMT,0,0,0);		
			module.createBarXY(CRCWIDTH, org.jlab.rec.cvt.bmt.Constants.CRZLENGTH[2]);
			module.setColor(70, 70, 70);
			module.getShapePath().translateXYZ((CRCWIDTH+10)*num, 0, 0);
			shapeViews.get(3).addShape(module);		
		}
		tabViews.get(3).addDetectorLayer(shapeViews.get(3));

       // BMT Transverse View
        double[] THISCRZEDGE1	=	new double[]{Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE1[2][1]), Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE1[2][0]), Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE1[2][2])};
		double[] THISCRZEDGE2	=	new double[]{360+Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE2[2][1]), Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE2[2][0]), Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE2[2][2])};

        for(int sector = 0; sector < 3; sector++){      
        	int slyr=0; // inner ring
        	DetectorShape2D  shape = new DetectorShape2D(DetectorType.BMT,sector+1,slyr+1,0);
        	shape.createArc(org.jlab.rec.cvt.bmt.Constants.CRZRADIUS[2]+slyr*(org.jlab.rec.cvt.bmt.Constants.CRCRADIUS[2]-org.jlab.rec.cvt.bmt.Constants.CRZRADIUS[2]), org.jlab.rec.cvt.bmt.Constants.CRZRADIUS[2]+slyr*(org.jlab.rec.cvt.bmt.Constants.CRCRADIUS[2]-205.8)+4, THISCRZEDGE1[sector], THISCRZEDGE2[sector]);
            shape.getShapePath().rotateZ(Math.toRadians(180.0));
            shape.setColor(200, 140, 200);
            
            shapeViews.get(0).addShape(shape); 
            
            slyr=1; // outer ring
            DetectorShape2D  shape2 = new DetectorShape2D(DetectorType.BMT,sector+1,slyr+1,0);
            shape2.createArc(org.jlab.rec.cvt.bmt.Constants.CRZRADIUS[2]+slyr*(org.jlab.rec.cvt.bmt.Constants.CRCRADIUS[2]-org.jlab.rec.cvt.bmt.Constants.CRZRADIUS[2]), org.jlab.rec.cvt.bmt.Constants.CRZRADIUS[2]+slyr*(org.jlab.rec.cvt.bmt.Constants.CRCRADIUS[2]-205.8)+4, THISCRZEDGE1[sector], THISCRZEDGE2[sector]);
            shape2.getShapePath().rotateZ(Math.toRadians(180.0));
            shape2.setColor(200, 140, 200);
            
            shapeViews.get(0).addShape(shape2);  
        }
        
        // BMT longitudinal view
        double x0 = org.jlab.rec.cvt.bmt.Constants.CRZZMIN[2] + org.jlab.rec.cvt.bmt.Constants.CRZOFFSET[2];
        double x1 = org.jlab.rec.cvt.bmt.Constants.CRZLENGTH[2] + x0 ;
        double y0 = -221;
        double y1 =  221;
       
        DetectorShape2D  shape3 = new DetectorShape2D(DetectorType.BMT,1,0,0); 
   		shape3.createBarXY(x1-x0, y1-y0);
   		shape3.getShapePath().translateXYZ(x0+(x1-x0)/2., 0, 0);
   		shape3.setColor(255,182,229, 60);
   		shapeViews.get(1).addShape(shape3);
   		
   		//SVT views
        int[] sectors = new int[]{10,14,18,24};
        double[] distances = new double[]{org.jlab.rec.cvt.svt.Constants.MODULERADIUS[0][0],
        		org.jlab.rec.cvt.svt.Constants.MODULERADIUS[2][0],
        		org.jlab.rec.cvt.svt.Constants.MODULERADIUS[4][0],
        		org.jlab.rec.cvt.svt.Constants.MODULERADIUS[6][0]};
        double[] Z0 = new double[]{org.jlab.rec.cvt.svt.Constants.Z0[0]-org.jlab.rec.cvt.svt.Constants.DEADZNLEN/2., 
        		org.jlab.rec.cvt.svt.Constants.Z0[2]-org.jlab.rec.cvt.svt.Constants.DEADZNLEN/2.,
        		org.jlab.rec.cvt.svt.Constants.Z0[4]-org.jlab.rec.cvt.svt.Constants.DEADZNLEN/2.,
        		org.jlab.rec.cvt.svt.Constants.Z0[6]-org.jlab.rec.cvt.svt.Constants.DEADZNLEN/2.};
       
        for(int ring = 0; ring < 4; ring++){
            for(int sector = 0; sector < sectors[ring]; sector++){
                double rotation = sector*360.0/sectors[ring];
                for(int layer = 0; layer < 2; layer++){
                    
                    //longitudinal view                  
                    if(sector<sectors[ring]/2+1) {
	                    DetectorShape2D  shape2 = new DetectorShape2D(DetectorType.BST,sector+1,(ring*2+1),0);  
	                    double thickn = 4;
	                    if(sector> 0 && sector<sectors[ring]/2) {
	                    	thickn = distances[ring]*Math.abs(Math.cos(Math.toRadians(rotation+180.0/sectors[ring])) - Math.cos(Math.toRadians(rotation-180.0/sectors[ring])) );
	                    	shape2.createBarXY(org.jlab.rec.cvt.svt.Constants.MODULELENGTH, thickn);
	                    	shape2.getShapePath().translateXYZ(0,(distances[ring])*Math.cos(Math.toRadians(rotation)),0);
	                    } else {
	                    	shape2.createBarXY(org.jlab.rec.cvt.svt.Constants.MODULELENGTH, thickn);
	                    	shape2.getShapePath().translateXYZ(0,(distances[ring]+2)*Math.cos(Math.toRadians(rotation)),0);
	                    }
	                    
	                    shape2.getShapePath().translateXYZ(org.jlab.rec.cvt.svt.Constants.MODULELENGTH/2.+Z0[ring],0,0);	                    
	                    shape2.setColor(50+50*ring, 10, 100, 80-10*ring);
	                    shapeViews.get(1).addShape(shape2);	                    
                    }
                    // transverse view 
                    DetectorShape2D  shape = new DetectorShape2D(DetectorType.BST,sector+1,(ring*2+layer+1),0);                  
                    shape.createBarXY(org.jlab.rec.cvt.svt.Constants.ACTIVESENWIDTH, 4.5);
                    shape.getShapePath().translateXYZ(0, distances[ring]-2 + 6*layer, 0.0); 
                    if(layer%2==0){
                        shape.setColor(180, 180, 255);
                    } else {
                        shape.setColor(180, 255, 255);
                    }
                    shape.getShapePath().rotateZ(Math.toRadians(rotation+180.*0)); // +180 for the old geometry where sector 1 is on top
                    shapeViews.get(0).addShape(shape);
                }
            }
        }
        tabViews.get(0).addDetectorLayer(shapeViews.get(0));
        tabViews.get(1).addDetectorLayer(shapeViews.get(1));
        tabViews.get(0).addDetectorListener(listener);
	}
	
	public void CreateViews(IDetectorListener listener) {
		List<DetectorShapeView2D> shapeViews = new ArrayList<DetectorShapeView2D>();
		shapeViews.add(0, new DetectorShapeView2D("CVT XY View"));
		shapeViews.add(1, new DetectorShapeView2D("CVT XZ View"));
		shapeViews.add(2, new DetectorShapeView2D("SVT Modules"));
		shapeViews.add(3, new DetectorShapeView2D("BMT Modules"));
		this.set_ShapeViews(shapeViews);
		List<DetectorShapeTabView>  tabViews   = new ArrayList<DetectorShapeTabView>();
		tabViews.add(0, new DetectorShapeTabView(580,580));  	// XY view
		tabViews.add(1, new DetectorShapeTabView(580,580));  	// YZ view
		tabViews.add(2, new DetectorShapeTabView(1800,1000)); 	// SVT modules
		tabViews.add(3, new DetectorShapeTabView(500,550));  	// BMT modules
		this.set_TabViews(tabViews);
		
		this.CreateDetectorShapes(shapeViews, tabViews, listener);
		
		
	}

}
