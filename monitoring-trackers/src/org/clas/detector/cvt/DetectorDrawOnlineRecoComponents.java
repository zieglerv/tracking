package org.clas.detector.cvt;
import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.geom.prim.Path3D;

public class DetectorDrawOnlineRecoComponents {

	public DetectorDrawOnlineRecoComponents() {
		// TODO Auto-generated constructor stub
	}
	
	
	
    public void PlotSVTStrips(EvioDataEvent event, DetectorCollection<Integer> Hits, DetectorCollection<Integer> Strips, DetectorShapeView2D Module, org.jlab.rec.cvt.svt.Geometry geo) {
    	Hits.clear();
    	Strips.clear();
    	Module.clearPaths();
        double HALFASENW = org.jlab.rec.cvt.svt.Constants.ACTIVESENWIDTH/2; 
        
        if(event.hasBank("BSTRec::Hits")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Hits");
            int rows = bank.rows();
            for(int loop = 0; loop < rows; loop++){
                int sector = bank.getInt("sector", loop) ;
                int layer  = bank.getInt("layer", loop) ;
                int strip  = bank.getInt("strip", loop) ;
                
                int sectorIdx = sector-1;
                int ll     =  layer -1;
                int regionIdx = ll/2;
                
                Hits.add(sector,layer,0, 1);
                Strips.add(sector,layer,strip, 1);
                       
                if(ll%2==0) {
                	Path3D path_L1 = new Path3D();
                	double[][] X = geo.getStripEndPoints( strip, 0);
                	path_L1.addPoint(X[0][0]-HALFASENW+ sectorIdx*(2*HALFASENW + 10),X[0][1]-org.jlab.rec.cvt.svt.Constants.MODULELENGTH/2 + regionIdx*(org.jlab.rec.cvt.svt.Constants.MODULELENGTH+10),0);
                    path_L1.addPoint(X[1][0]-HALFASENW+ sectorIdx*(2*HALFASENW + 10),X[1][1]-org.jlab.rec.cvt.svt.Constants.MODULELENGTH/2 + regionIdx*(org.jlab.rec.cvt.svt.Constants.MODULELENGTH+10),0);
                    Module.addPath(path_L1, 180, 255, 255);
                    
                }
                if(ll%2==1) {
                	Path3D path_L2 = new Path3D();
                	double[][] X = geo.getStripEndPoints( strip, 1);
                	path_L2.addPoint(X[0][0]-HALFASENW+ sectorIdx*(2*HALFASENW + 10),X[0][1]-org.jlab.rec.cvt.svt.Constants.MODULELENGTH/2 + regionIdx*(org.jlab.rec.cvt.svt.Constants.MODULELENGTH+10),0);
                    path_L2.addPoint(X[1][0]-HALFASENW+ sectorIdx*(2*HALFASENW + 10),X[1][1]-org.jlab.rec.cvt.svt.Constants.MODULELENGTH/2 + regionIdx*(org.jlab.rec.cvt.svt.Constants.MODULELENGTH+10),0);
                    Module.addPath(path_L2, 180, 180, 255);  
                }
                Module.repaint();
                
            }
        }
    }
    public void PlotBMTStrips(EvioDataEvent event, DetectorCollection<Integer> Hits, DetectorCollection<Integer> Strips, DetectorShapeView2D Module, org.jlab.rec.cvt.bmt.Geometry geo) {
        	Hits.clear();
        	Strips.clear();
        	Module.clearPaths();
        
		// BMT Transverse View
        double[] THISCRZEDGE1	=	new double[]{Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE1[2][1]), Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE1[2][0]), Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE1[2][2])};
		double[] THISCRZEDGE2	=	new double[]{360+Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE2[2][1]), Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE2[2][0]), Math.toDegrees(org.jlab.rec.cvt.bmt.Constants.CRZEDGE2[2][2])};

		
		double CRZLENGTH	=	org.jlab.rec.cvt.bmt.Constants.CRZLENGTH[2];		
		double CRCWIDTH = org.jlab.rec.cvt.bmt.Constants.CRCRADIUS[2]*Math.toRadians(THISCRZEDGE2[2]-THISCRZEDGE1[2]);
    	
        if(event.hasBank("BMT::dgtz")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("BMT::dgtz");
            int rows = bank.rows();
            for(int loop = 0; loop < rows; loop++){
                int sector = bank.getInt("sector", loop) ;
                int layer  = bank.getInt("layer", loop) ;
                int strip   = bank.getInt("strip", loop) ;

                if(layer==5) {
                	Path3D path_L1 = new Path3D();             	
                	double x = (geo.CRZStrip_GetPhi(2, 5, strip)-Math.toRadians(THISCRZEDGE1[1]))*org.jlab.rec.cvt.bmt.Constants.CRCRADIUS[2];
                	
                	path_L1.addPoint(x-CRCWIDTH/2+ (sector-1)*(CRCWIDTH + 10),-CRZLENGTH/2,0);
                	path_L1.addPoint(x-CRCWIDTH/2+ (sector-1)*(CRCWIDTH + 10),CRZLENGTH/2,0);
                	Module.addPath(path_L1, 180, 255, 255);
                }
                if(layer==6) {
                	
                	Path3D path_L2 = new Path3D();
                	double y = geo.CRCStrip_GetZ(6, strip)-geo.CRCStrip_GetZ(6, 1);
                	
                	path_L2.addPoint(-CRCWIDTH/2+ (sector-1)*(CRCWIDTH + 10), y-CRZLENGTH/2,0);
                	path_L2.addPoint(CRCWIDTH/2+ (sector-1)*(CRCWIDTH + 10), y-CRZLENGTH/2,0);
                	Module.addPath(path_L2, 180, 180, 255);         
                }
                Hits.add(sector,layer,0, 1);
                Strips.add(sector,layer,strip, 1);
                Module.repaint();
                
                
            }
        }
    }
    public void PlotCrosses(EvioDataEvent event, DetectorShapeView2D Module, DetectorShapeView2D Module2, DetectorShapeTabView Tab, DetectorShapeTabView Tab2, org.jlab.rec.cvt.svt.Geometry geo){
    	
    	Module.clearHits();
    	Module.clearPaths();
    	Module2.clearHits();
    	Module2.clearPaths();
        
        if(event.hasBank("BSTRec::Crosses")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Crosses");
            int rows = bank.rows();
            for(int loop = 0; loop < rows; loop++){
            	int sector = bank.getInt("sector", loop);
            	int layer = bank.getInt("region", loop)*2-1;
            	double x = bank.getDouble("x", loop);
            	double y = bank.getDouble("y", loop);
            	double z = bank.getDouble("z", loop);
            	double z_loc = geo.transformToFrame(sector, layer, x, y, z, "local", "middle").z();
            	if(Math.sqrt(x*x+y*y)>60 && z_loc<org.jlab.rec.cvt.svt.Constants.MODULELENGTH+1) {
            		Module.addHit(-x, -y, 0, 50,50,50);
            		Module2.addHit(z, -y, 0, 50,50,50);
            	}
            }
        }
        Tab.repaint();
        Tab2.repaint();
        
        if(event.hasBank("BMTRec::Crosses")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("BMTRec::Crosses");
            int rows = bank.rows();
            for(int loop = 0; loop < rows; loop++){
            	double x = bank.getDouble("x", loop);
            	double y = bank.getDouble("y", loop);
            	double z = bank.getDouble("z", loop);
            	if(Math.sqrt(x*x+y*y)>200) {
            		Module.addHit(-x, -y, 0, 150,50,50);
            		Module2.addHit(z, -y, 0, 150,50,50);
            	}
            }
        }
        if(event.hasBank("CVTRec::Cosmics")==true){
            EvioDataBank bank = (EvioDataBank) event.getBank("CVTRec::Cosmics");
            int rows = bank.rows();
            double y1 = -230;
        	double y2 = 230;
            for(int loop = 0; loop < rows; loop++){
            	double x1 = bank.getDouble("trkline_yx_slope", loop)*y1+bank.getDouble("trkline_yx_interc", loop);
            	double x2 = bank.getDouble("trkline_yx_slope", loop)*y2+bank.getDouble("trkline_yx_interc", loop);
            	double z1 = bank.getDouble("trkline_yz_slope", loop)*y1+bank.getDouble("trkline_yz_interc", loop);
            	double z2 = bank.getDouble("trkline_yz_slope", loop)*y2+bank.getDouble("trkline_yz_interc", loop);
            	
            	Path3D path = new Path3D();
                
                path.addPoint(-x1,-y1,0);
                path.addPoint(-x2,-y2,0);
                
                Module.addPath(path, 0, 0, 255);
                
                Path3D path2 = new Path3D();
                path2.addPoint(z1,-y1,0);
                path2.addPoint(z2,-y2,0);
               
                Module2.addPath(path2, 0, 0, 255);
            }
        }
        Tab.repaint();
        Tab2.repaint();
    }
}