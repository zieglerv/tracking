package org.clas.detector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jlab.detector.view.DetectorListener;
import org.jlab.detector.view.DetectorPane2D;

public class DetectorShapeTabView extends JPanel {
	private		      JTabbedPane tabbedPane;
	private final     TreeMap<String, DetectorShapeView2D>  detectorView = new TreeMap<String, DetectorShapeView2D>();
	private final     TreeMap<String, DetectorPane2D>  detectorPanes = new TreeMap<String, DetectorPane2D>();
	
	public DetectorShapeTabView(){
		super();
		this.setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();    
	}
	public DetectorShapeTabView(int sizeX, int sizeY){
        super();
        this.setPreferredSize(new Dimension(sizeX,sizeY));
        this.setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();        
        this.add(tabbedPane,BorderLayout.CENTER);
    }
	public void addDetectorLayer( DetectorShapeView2D detectorPanel){
        tabbedPane.addTab( detectorPanel.getName(), detectorPanel); 
        detectorView.put(detectorPanel.getName(), detectorPanel);
    }

	public void addDetectorListener(DetectorListener listener) {
        for(Map.Entry<String, DetectorShapeView2D> entry : this.detectorView.entrySet()){
            entry.getValue().addDetectorListener(listener);
            
        }
	}
	public void addDetectorLayer(DetectorPane2D detectorPane2D) {
		tabbedPane.addTab( detectorPane2D.getName(), detectorPane2D); 
		detectorPanes.put(detectorPane2D.getName(), detectorPane2D);
		
	}

}
