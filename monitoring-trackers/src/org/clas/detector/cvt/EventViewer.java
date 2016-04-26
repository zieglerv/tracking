package org.clas.detector.cvt;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.basic.IDetectorModule;
import org.jlab.clas12.basic.IDetectorProcessor;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.IDetectorListener;
import org.jlab.clas12.detector.EventDecoder;
import org.jlab.clas12.detector.FADCMaxFinder;
import org.jlab.clasrec.main.DetectorEventProcessorPane;
import org.jlab.clasrec.utils.ServiceConfiguration;
import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.cvt.services.CVTCosmicsReconstruction;
import org.root.histogram.H1D;
import org.root.pad.EmbeddedCanvas;

public class EventViewer implements IDetectorProcessor,IDetectorListener,IDetectorModule, ActionListener {
	// Initialize Cosmics Rec
	CVTCosmicsReconstruction reco = new CVTCosmicsReconstruction();
	EventDecoder decoder = new EventDecoder();
	Decoder deco = new Decoder();
	
	DetectorDrawOnlineRecoComponents displays 	= new DetectorDrawOnlineRecoComponents();
	org.jlab.rec.cvt.svt.Geometry svt_geo 		= new org.jlab.rec.cvt.svt.Geometry();
	org.jlab.rec.cvt.bmt.Geometry bmt_geo 		= new org.jlab.rec.cvt.bmt.Geometry();
	
	 // Define the panel and views
    JPanel  detectorPanel  = null;
    JPanel  detectorPanel2 = null;
    JPanel  detectorPanel3 = null;
    JPanel  plotsPanel 	   = null;
    
    DetectorDrawComponents detFrm 		= new DetectorDrawComponents();
    OnlineRecoFillHistograms histos 	= new OnlineRecoFillHistograms();
    DetectorEventProcessorPane evPane 	= new DetectorEventProcessorPane();
    EmbeddedCanvas canvas 				= new EmbeddedCanvas();
    
	public EventViewer() {
		// configure reconstruction
		decoder.addFitter(DetectorType.BMT,
    	        new FADCMaxFinder());
		reco.init();   	
    	ServiceConfiguration config = new ServiceConfiguration();
    	config.addItem("DAQ", "data", "true");
    	config.addItem("SVT", "newGeometry", "true");
    	reco.configure(config);
    	
    	// configure views	   
        this.plotsPanel = new JPanel();
        this.plotsPanel.setLayout(new BorderLayout());
        this.plotsPanel.add(this.canvas);
        histos.CreateHistos();
	    
        this.detectorPanel = new JPanel();
	    detFrm.CreateViews(this);
	    histos.CreateDetectorShapes(new ArrayList<DetectorCollection<H1D>>());
	    this.detectorPanel.setLayout(new BorderLayout());
	    
	    this.detectorPanel2 = new JPanel();
	    this.detectorPanel2.setLayout(new BorderLayout());
	    
	    this.detectorPanel3 = new JPanel();
	    this.detectorPanel3.setLayout(new BorderLayout());
	    
	    this.evPane.addProcessor(this);
	    JPanel topView = new JPanel();
	    topView.setLayout(new FlowLayout());
	    
	    JPanel ModView = new JPanel();
	    ModView.setLayout(new FlowLayout());
	    JPanel ModView2 = new JPanel();
	    ModView2.setLayout(new FlowLayout());
	   
	    topView.add(detFrm.get_TabViews().get(0));	 		 	// transverse view
	    topView.add(detFrm.get_TabViews().get(1)); 	 	 		// longitudinal view
	    ModView.add(detFrm.get_TabViews().get(2)); 		 		// SVT modules
	    ModView2.add(detFrm.get_TabViews().get(3)); 		 	// BMT modules
	    // add borders
	    this.detectorPanel.add(topView,BorderLayout.PAGE_START);
	    this.detectorPanel2.add(ModView,BorderLayout.PAGE_START);
	    this.detectorPanel3.add(ModView2,BorderLayout.PAGE_START);
	    this.detectorPanel.add(this.evPane,BorderLayout.PAGE_END);
	   
	    
	}

	@Override
	public void detectorSelected(DetectorDescriptor arg0) {
		// TODO Auto-generated method stub
		
	}

	DetectorCollection<Integer> SVTHits 	= new DetectorCollection<Integer>();
    DetectorCollection<Integer> BMTHits 	= new DetectorCollection<Integer>();
    DetectorCollection<Integer> SVTStrips  	= new DetectorCollection<Integer>();
    DetectorCollection<Integer> BMTStrips	= new DetectorCollection<Integer>();
    
	@Override
    public void update(DetectorShape2D shape) {
    	if(shape.getDescriptor().getType()==DetectorType.BST){
	        if(this.SVTHits.hasEntry(shape.getDescriptor().getSector(), 
	        		shape.getDescriptor().getLayer(),shape.getDescriptor().getComponent())==true){
	            shape.setColor(255, 180, 180);
	        } else {	
	           
	        	if(shape.getDescriptor().getLayer()%2==0){
                    shape.setColor(180, 180, 255);
                } else {
                    shape.setColor(180, 255, 255);
                }
	        }
    	}
    	if(shape.getDescriptor().getType()==DetectorType.BMT){
	        if(this.BMTHits.hasEntry(shape.getDescriptor().getSector(), 
	        		shape.getDescriptor().getLayer(),shape.getDescriptor().getComponent())==true){
	            shape.setColor(255, 180, 180);
	        } else {	
	           
	        	if(shape.getDescriptor().getLayer()%2==0){
	        		shape.setColor(255, 180, 255);
                } else {
                    shape.setColor(200, 140, 200);
                }
	        }
    	}
    }

	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
		return "ziegler";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getDetectorPanel() {	
		return this.detectorPanel;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ziegler";
	}

	@Override
	public DetectorType getType() {
		// TODO Auto-generated method stub
		return DetectorType.SVT;
	}

	@Override
	public void processEvent(DataEvent de) {
        EvioDataEvent event = (EvioDataEvent) de;
        EvioDataEvent decodedEvent = deco.DecodeEvent(event, decoder);
        //decodedEvent.show();
        
        reco.processEvent(decodedEvent);
        displays.PlotCrosses(decodedEvent, detFrm.get_ShapeViews().get(0), detFrm.get_ShapeViews().get(1), detFrm.get_TabViews().get(0), detFrm.get_TabViews().get(1), svt_geo);
        displays.PlotSVTStrips(decodedEvent, SVTHits, SVTStrips,detFrm.get_ShapeViews().get(2), svt_geo);
        displays.PlotBMTStrips(decodedEvent, BMTHits, BMTStrips,detFrm.get_ShapeViews().get(3), bmt_geo);    
        histos.FillHistos(decodedEvent);
	}

	public static void main(String[] args){
    	
        EventViewer module = new EventViewer();
        JFrame frame = new JFrame();
        frame.add(module.detectorPanel);
        frame.pack();
        frame.setVisible(true);
        JFrame frame2 = new JFrame();
        frame2.add(module.detectorPanel2);
        frame2.pack();
        frame2.setVisible(true);
        JFrame frame3 = new JFrame();
        frame3.add(module.detectorPanel3);
        frame3.pack();
        frame3.setVisible(true);
        JFrame frame4 = new JFrame();
        frame4.add(module.plotsPanel);
        frame4.pack();
        frame4.setVisible(true);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
