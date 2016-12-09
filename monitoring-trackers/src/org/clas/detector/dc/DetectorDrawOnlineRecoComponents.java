package org.clas.detector.dc;
import java.util.List;

import org.clas.detector.DetectorShapeTabView;
import org.clas.detector.DetectorShapeView2D;
import org.jlab.detector.base.DetectorType;
import org.jlab.detector.view.DetectorShape2D;
import org.jlab.geom.prim.Path3D;
import org.jlab.geom.prim.Point3D;
import org.jlab.io.evio.EvioDataBank;
import org.jlab.io.evio.EvioDataEvent;
import org.jlab.rec.dc.GeometryLoader;


public class DetectorDrawOnlineRecoComponents {

	public DetectorDrawOnlineRecoComponents() {
		// TODO Auto-generated constructor stub
	}
	
	
	
    public void PlotAllHits(EvioDataEvent event, List<DetectorShapeView2D> Modules, List<DetectorShapeTabView> Tabs) {
    	
        for(int i =1; i< Modules.size(); i++) {
        	Modules.get(i).clearHits();
        	Modules.get(i).clearPaths();
        }
        if(event.hasBank("DC::dgtz")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("DC::dgtz");
            int rows = bank.rows();
            for(int loop = 0; loop < rows; loop++){
                int sector = bank.getInt("sector", loop) ;
                int superlayer = bank.getInt("superlayer", loop) ;
                int layer  = bank.getInt("layer", loop) ;
                int wire  = bank.getInt("wire", loop) ;
                //System.out.println("superlayer "+superlayer+" layer "+layer+" wire "+wire);
                
                DetectorShape2D module = new DetectorShape2D(DetectorType.DC,superlayer,layer,wire);				
				module.createBarXY(2, 114);			
				module.getShapePath().translateXYZ((layer-1)*2+(12+2)*(superlayer-1), wire+1, 0);
				
                Path3D hit = new Path3D();               
                hit.addPoint((layer-1)*2+(superlayer-1)*(12+2)-1, wire-1-114/2, 0);
                hit.addPoint(layer*2+(superlayer-1)*(12+2)-1, wire-1-114/2, 0);
                hit.addPoint(layer*2+(superlayer-1)*(12+2)-1, wire-114/2, 0);
                hit.addPoint((layer-1)*2+(superlayer-1)*(12+2)-1, wire-114/2, 0);
                hit.addPoint((layer-1)*2+(superlayer-1)*(12+2)-1, wire-1-114/2, 0);
                Modules.get(sector).addPath(hit, 255, 50, 255);
                 
                Modules.get(sector).repaint();
             
            }
        }
        if(event.hasBank("HitBasedTrkg::HBHits")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("HitBasedTrkg::HBHits");
            int rows = bank.rows();
            for(int loop = 0; loop < rows; loop++){
                int sector = bank.getInt("sector", loop) ;
                int superlayer = bank.getInt("superlayer", loop) ;
                int layer  = bank.getInt("layer", loop) ;
                int wire  = bank.getInt("wire", loop) ;
                int clsId  = bank.getInt("clusterID", loop) ;
                //System.out.println("superlayer "+superlayer+" layer "+layer+" wire "+wire);
                
              //  DetectorShape2D module = new DetectorShape2D(DetectorType.DC,superlayer,layer,wire);				
			 //	module.createBarXY(2, 114);			
			 //	module.getShapePath().translateXYZ((layer-1)*2+(12+3)*(superlayer-1), wire+1, 0);
				
                Path3D hit = new Path3D();               
                hit.addPoint((layer-1)*2+(superlayer-1)*(12+2)-1, wire-1-114/2, 0);
                hit.addPoint(layer*2+(superlayer-1)*(12+2)-1, wire-1-114/2, 0);
                hit.addPoint(layer*2+(superlayer-1)*(12+2)-1, wire-114/2, 0);
                hit.addPoint((layer-1)*2+(superlayer-1)*(12+2)-1, wire-114/2, 0);
                hit.addPoint((layer-1)*2+(superlayer-1)*(12+2)-1, wire-1-114/2, 0);
                
                if(clsId==-1) {               	
                	Modules.get(sector).addPath(hit, 180, 255, 255);
                }  else {
                	Modules.get(sector).addPath(hit, 50, 255, 50);
                }
                
                Modules.get(sector).repaint();
             
            }
        }
        for(int i =1; i< Tabs.size(); i++) 
        	Tabs.get(i).repaint();
    }
    
    public void PlotCrosses(EvioDataEvent event, DetectorShapeView2D Module2, DetectorShapeTabView Tab2){
    	
    	
    	Module2.clearHits();
    	Module2.clearPaths();
        
    	
    	if(event.hasBank("HitBasedTrkg::HBHits")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("HitBasedTrkg::HBHits");
            int rows = bank.rows();
            for(int loop = 0; loop < rows; loop++){
            	int sector = bank.getInt("sector", loop);
            	int superlayer = bank.getInt("superlayer", loop);
            	int layer = bank.getInt("layer", loop);
            	int wire = bank.getInt("wire", loop);
            	
            	Path3D hit = new Path3D(); 
            	Point3D WireEndPoint1 = new Point3D(
            			GeometryLoader.dcDetector.getSector(sector-1).getSuperlayer(superlayer-1).getLayer(layer-1).getComponent(wire-1).getLine().origin().x(),  
		        		-GeometryLoader.dcDetector.getSector(sector-1).getSuperlayer(superlayer-1).getLayer(layer-1).getComponent(wire-1).getLine().origin().y(),  0.0);
            	Point3D WireEndPoint2 = new Point3D(
            			GeometryLoader.dcDetector.getSector(sector-1).getSuperlayer(superlayer-1).getLayer(layer-1).getComponent(wire-1).getLine().end().x(),  
		        		-GeometryLoader.dcDetector.getSector(sector-1).getSuperlayer(superlayer-1).getLayer(layer-1).getComponent(wire-1).getLine().end().y(),  0.0);
		       
            	hit.addPoint(WireEndPoint1);
            	hit.addPoint(WireEndPoint2);
            	
		        hit.translateXYZ(110.0+((int)(superlayer-1)/2)*50, 0, 0);
		        
		        if((superlayer-1)%2==1)
		        	Module2.addPath(hit, 180-(superlayer-1)*15,255,255);
				if((superlayer-1)%2==0)
					Module2.addPath(hit, 255-(superlayer-1)*15,255,229);
				
            	Module2.repaint();	
            	
            }
        } 
    	
        if(event.hasBank("HitBasedTrkg::HBCrosses")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("HitBasedTrkg::HBCrosses");
            int rows = bank.rows();
            for(int loop = 0; loop < rows; loop++){
            	int sector = bank.getInt("sector", loop);
            	int region = bank.getInt("region", loop);
            	
            	double x = bank.getDouble("x", loop);
            	double y = bank.getDouble("y", loop);
            	
            	Point3D hit = new Point3D(x, -y, 0);
            	hit.translateXYZ(110.0+((int)((region-1)/2))*50, 0, 0);
				hit.rotateZ((sector-1)*Math.toRadians(60.));
            	 
            	Module2.addHit(hit.x(), hit.y(), 0, 50,50,50);
            	Module2.repaint();	
            	
            }
        } 
       
        if(event.hasBank("TimeBasedTrkg::TBCrosses")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("TimeBasedTrkg::TBCrosses");
            int rows = bank.rows();
            for(int loop = 0; loop < rows; loop++){
            	int sector = bank.getInt("sector", loop);
            	int region = bank.getInt("region", loop);
            	
            	double x = bank.getDouble("x", loop);
            	double y = bank.getDouble("y", loop);
            	
            	Point3D hit = new Point3D(x, -y, 0);
            	hit.translateXYZ(110.0+((int)((region-1)/2))*50, 0, 0);
				hit.rotateZ((sector-1)*Math.toRadians(60.));
            	 
            	Module2.addHit(hit.x(), hit.y(), 0, 150,50,50);
            	Module2.repaint();	
            	
            }
        } 
        Tab2.repaint();

    }
}