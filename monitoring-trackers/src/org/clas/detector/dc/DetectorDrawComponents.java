package org.clas.detector.dc;

import java.util.ArrayList;
import java.util.List;

import org.clas.detector.IDetectorShapes;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.clas12.calib.IDetectorListener;
import org.jlab.rec.dc.Constants;
import org.jlab.rec.dc.GeometryLoader;

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
		
		Constants.newGeometry = true;
		GeometryLoader.Load();
		
		for(int s =0; s< 6; s++) {
					
			for(int regnum = 0; regnum <3; regnum++) {
				
				DetectorShape2D Rmodule = new DetectorShape2D(DetectorType.DC,s,regnum,7);
				
				Rmodule.createBarXY(26, 4);
				
				Rmodule.setColor(250,200,255, 150); 
				Rmodule.getShapePath().translateXYZ(12+(26+2)*regnum, -65, 0);
				
				shapeViews.get(s+1).addShape(Rmodule);
				
				for(int rslrnum = 0; rslrnum <2; rslrnum++) {
					
					int slrnum = regnum*2+rslrnum;
					DetectorShape2D Smodule = new DetectorShape2D(DetectorType.DC,s,slrnum,7);
					
					Smodule.createBarXY(12, 4);
					
					Smodule.setColor(180,200,255, 150); 
					Smodule.getShapePath().translateXYZ(5+(12+2)*slrnum, -60, 0);
					
					shapeViews.get(s+1).addShape(Smodule);
					
					for(int lrnum = 0; lrnum <6; lrnum++) {
					
						DetectorShape2D module = new DetectorShape2D(DetectorType.DC,s,slrnum,lrnum);
					
						module.createBarXY(2, 114);
					
						module.getShapePath().translateXYZ(lrnum*2+(12+2)*slrnum, 0, 0);
					
						module.setColor(180,180,255); 
					
						shapeViews.get(s+1).addShape(module);		
					}
				}		
			}
			tabViews.get(s+1).addDetectorLayer(shapeViews.get(s+1));
			tabViews.get(s+1).addDetectorListener(listener);
		}
		
		for(int s =0; s< 6; s++)
			for(int slrnum = 5; slrnum > -1; slrnum--) {
				DetectorShape2D module = new DetectorShape2D(DetectorType.DC,s,slrnum,0);
				
				module.getShapePath().addPoint(GeometryLoader.dcDetector.getSector(0).getSuperlayer(slrnum).getLayer(0).getComponent(0).getLine().origin().x(),  
		        		-GeometryLoader.dcDetector.getSector(0).getSuperlayer(slrnum).getLayer(0).getComponent(0).getLine().origin().y(),  0.0);
				module.getShapePath().addPoint(GeometryLoader.dcDetector.getSector(0).getSuperlayer(slrnum).getLayer(0).getComponent(0).getLine().end().x(),  
		        		-GeometryLoader.dcDetector.getSector(0).getSuperlayer(slrnum).getLayer(0).getComponent(0).getLine().end().y(),  0.0);
				module.getShapePath().addPoint(GeometryLoader.dcDetector.getSector(0).getSuperlayer(slrnum).getLayer(0).getComponent(111).getLine().end().x(),  
		        		-GeometryLoader.dcDetector.getSector(0).getSuperlayer(slrnum).getLayer(0).getComponent(111).getLine().end().y(),  0.0);
				module.getShapePath().addPoint(GeometryLoader.dcDetector.getSector(0).getSuperlayer(slrnum).getLayer(0).getComponent(111).getLine().origin().x(),  
		        		-GeometryLoader.dcDetector.getSector(0).getSuperlayer(slrnum).getLayer(0).getComponent(111).getLine().origin().y(),  0.0);
		    
		       
				if(slrnum%2==1)
					module.setColor(180-slrnum*15,180,255);
				if(slrnum%2==0)
					module.setColor(255-slrnum*15,182,229, 200);
				
				module.getShapePath().translateXYZ(110.0+((int)(slrnum/2))*50, 0, 0);
				module.getShapePath().rotateZ(s*Math.toRadians(60.));
				shapeViews.get(0).addShape(module);			
			}
		  
		tabViews.get(0).addDetectorLayer(shapeViews.get(0));
		
	}
	
	public void CreateViews(IDetectorListener listener) {
		List<DetectorShapeView2D> shapeViews = new ArrayList<DetectorShapeView2D>();
		shapeViews.add(0, new DetectorShapeView2D("sectors"));
		for(int s =1; s<=6; s++)
			shapeViews.add(s, new DetectorShapeView2D("superlayers"));
		
		this.set_ShapeViews(shapeViews);
		List<DetectorShapeTabView>  tabViews   = new ArrayList<DetectorShapeTabView>();
		tabViews.add(0, new DetectorShapeTabView(700,700));  	// global view
		for(int s =1; s<=6; s++)
			tabViews.add(s, new DetectorShapeTabView(650,650));  	// superlayers view
		
		this.set_TabViews(tabViews);
		
		this.CreateDetectorShapes(shapeViews, tabViews, listener);
		
		
	}

}
